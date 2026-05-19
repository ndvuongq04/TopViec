package com.topviec.topviec_be.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topviec.topviec_be.cvonline.CvOnlineTemplateRenderer;
import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.dto.request.ReqChangeOnlineCvTemplateDTO;
import com.topviec.topviec_be.dto.request.ReqCreateOnlineCvDTO;
import com.topviec.topviec_be.dto.request.ReqCreateShareTokenDTO;
import com.topviec.topviec_be.dto.request.ReqShareCvDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateOnlineCvDTO;
import com.topviec.topviec_be.dto.request.ReqUploadCvDTO;
import com.topviec.topviec_be.dto.response.ResCvDTO;
import com.topviec.topviec_be.dto.response.ResCvOnlineEditorPayloadDTO;
import com.topviec.topviec_be.dto.response.ResCvOnlineDetailDTO;
import com.topviec.topviec_be.dto.response.ResCvPdfExportDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplateDetailDTO;
import com.topviec.topviec_be.dto.response.ResShareTokenDTO;
import com.topviec.topviec_be.entity.CandidateProfile;
import com.topviec.topviec_be.entity.CvTemplate;
import com.topviec.topviec_be.entity.Cvs;
import com.topviec.topviec_be.entity.User;
import com.topviec.topviec_be.enums.cvs.CvParseStatus;
import com.topviec.topviec_be.enums.cvs.CvType;
import com.topviec.topviec_be.enums.cvs.CvVisibility;
import com.topviec.topviec_be.enums.cvs.FileUploadType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CandidateProfileRepository;
import com.topviec.topviec_be.repository.CvTemplateRepository;
import com.topviec.topviec_be.repository.CvsRepository;
import com.topviec.topviec_be.repository.UserRepository;
import com.topviec.topviec_be.service.CvSectionSyncService;
import com.topviec.topviec_be.service.CvService;
import com.topviec.topviec_be.service.FileStorageService;
import com.topviec.topviec_be.service.PdfGenerationService;
import com.topviec.topviec_be.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class CvServiceImpl implements CvService {

    private static final Logger log = LoggerFactory.getLogger(CvServiceImpl.class);

    private final CvsRepository cvsRepository;
    private final CvTemplateRepository cvTemplateRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final CvOnlineTemplateRenderer cvOnlineTemplateRenderer;
    private final FileStorageService fileStorageService;
    private final PdfGenerationService pdfGenerationService;
    private final CvSectionSyncService cvSectionSyncService;
    private final FileValidator fileValidator;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final int MAX_CV_PER_USER = 5;

    @Override
    @Transactional
    public ResCvDTO uploadCv(Long userId, MultipartFile file, ReqUploadCvDTO request) {

        fileValidator.validate(file, FileUploadType.CV);

        long currentCount = cvsRepository.countByUserId(userId);
        if (currentCount >= MAX_CV_PER_USER) {
            throw AppException.badRequest(
                    "Đã đạt giới hạn " + MAX_CV_PER_USER + " CV, vui lòng xóa CV cũ trước");
        }

        if (cvsRepository.existsByUserIdAndTitle(userId, request.getTitle())) {
            throw AppException.conflict(
                    "Tên CV '" + request.getTitle() + "' đã tồn tại");
        }

        String fileUrl = fileStorageService.uploadFile(file, userId, FileUploadType.CV);

        boolean shouldBeDefault = request.isDefault() || currentCount == 0;
        if (shouldBeDefault) {
            clearCurrentDefaultCv(userId);
        }

        Cvs cv = Cvs.builder()
                .userId(userId)
                .title(request.getTitle())
                .cvType(CvType.uploaded)
                .fileUrl(fileUrl)
                .isDefault(shouldBeDefault)
                .visibility(CvVisibility.private_cv)
                .parseStatus(CvParseStatus.skipped)
                .viewCount(0)
                .createdBy(userId)
                .build();

        Cvs saved = cvsRepository.save(cv);
        log.info("Upload CV thành công - userId: {}, cvId: {}", userId, saved.getId());

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO createOnlineCv(Long userId, ReqCreateOnlineCvDTO request) {
        long currentCount = cvsRepository.countByUserId(userId);
        if (currentCount >= MAX_CV_PER_USER) {
            throw AppException.badRequest("Đã đạt giới hạn " + MAX_CV_PER_USER + " CV");
        }

        String title = request.getTitle().trim();
        if (cvsRepository.existsByUserIdAndTitle(userId, title)) {
            throw AppException.conflict("Tên CV '" + title + "' đã tồn tại");
        }

        CvTemplate template = findActiveTemplateOrThrow(request.getTemplateId());
        boolean shouldBeDefault = Boolean.TRUE.equals(request.getIsDefault()) || currentCount == 0;
        if (shouldBeDefault) {
            clearCurrentDefaultCv(userId);
        }

        CvOnlineExtraDataDTO extraData = request.getExtraData() == null
                ? CvOnlineExtraDataDTO.empty()
                : request.getExtraData();

        Cvs cv = Cvs.builder()
                .userId(userId)
                .title(title)
                .cvType(CvType.online)
                .templateId(template.getId())
                .extraData(toExtraDataJson(extraData))
                .pdfUrl(null)
                .pdfDirty(true)
                .isDefault(shouldBeDefault)
                .visibility(CvVisibility.private_cv)
                .parseStatus(CvParseStatus.skipped)
                .viewCount(0)
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        Cvs saved = cvsRepository.save(cv);
        cvSectionSyncService.syncSectionsIfChanged(saved, extraData);
        saved = cvsRepository.save(saved);
        return mapToOnlineDetail(saved, template);
    }

    @Override
    public List<ResCvDTO> getMyCvs(Long userId) {
        return cvsRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ResCvDTO getCvById(Long userId, Long id) {
        Cvs cv = cvsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));
        if (cv.getCvType() == CvType.online) {
            cv = ensureFreshOnlineCvPdf(cv, findTemplateByIdOrThrow(cv.getTemplateId()), userId);
        }
        return mapToDTO(cv);
    }

    @Override
    @Transactional(readOnly = true)
    public CvOnlineExtraDataDTO getOnlineCvPrefill(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Khong tim thay nguoi dung"));
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId).orElse(null);
        return toPrefillExtraData(user, profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ResCvOnlineEditorPayloadDTO getOnlineCvEditorPayloadByTemplate(Long userId, Long templateId) {
        CvTemplate template = findActiveTemplateOrThrow(templateId);
        return ResCvOnlineEditorPayloadDTO.builder()
                .cvId(null)
                .persisted(false)
                .title(null)
                .cvType(CvType.online)
                .templateId(template.getId())
                .template(mapTemplateDetail(template))
                .extraData(getOnlineCvPrefill(userId))
                .pdfDirty(true)
                .isDefault(false)
                .visibility(CvVisibility.private_cv)
                .parseStatus(CvParseStatus.skipped)
                .viewCount(0)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResCvOnlineEditorPayloadDTO getOnlineCvEditorPayloadById(Long userId, Long id) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        return mapToEditorPayload(cv, template);
    }

    @Override
    @Transactional
    public ResCvPdfExportDTO exportOnlineCvPdf(Long userId, Long id) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        Cvs saved = ensureFreshOnlineCvPdf(cv, template, userId);

        return ResCvPdfExportDTO.builder()
                .cvId(saved.getId())
                .pdfUrl(saved.getPdfUrl())
                .pdfDirty(saved.getPdfDirty())
                .generatedAt(saved.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public Resource downloadOnlineCvPdf(Long userId, Long id) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        Cvs saved = ensureFreshOnlineCvPdf(cv, template, userId);
        return fileStorageService.loadFile(saved.getPdfUrl());
    }

    @Override
    @Transactional(readOnly = true)
    public ResCvOnlineDetailDTO getOnlineCvById(Long userId, Long id) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        Cvs saved = ensureFreshOnlineCvPdf(cv, template, userId);
        return mapToOnlineDetail(saved, template);
    }

    @Override
    @Transactional
    public ResCvDTO renameCv(Long userId, Long id, String newTitle) {
        Cvs cv = cvsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));

        if (!cv.getTitle().equals(newTitle) && cvsRepository.existsByUserIdAndTitle(userId, newTitle)) {
            throw AppException.conflict("Tên CV '" + newTitle + "' đã tồn tại");
        }

        cv.setTitle(newTitle);
        cv.setUpdatedBy(userId);
        return mapToDTO(cvsRepository.save(cv));
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO updateOnlineCv(Long userId, Long id, ReqUpdateOnlineCvDTO request) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        String title = request.getTitle().trim();

        if (!cv.getTitle().equals(title) && cvsRepository.existsByUserIdAndTitle(userId, title)) {
            throw AppException.conflict("Tên CV '" + title + "' đã tồn tại");
        }

        CvOnlineExtraDataDTO extraData = request.getExtraData() == null
                ? CvOnlineExtraDataDTO.empty()
                : request.getExtraData();
        String nextExtraDataJson = toExtraDataJson(extraData);
        boolean contentChanged = !Objects.equals(cv.getExtraData(), nextExtraDataJson);

        cv.setTitle(title);
        cv.setExtraData(nextExtraDataJson);
        cv.setUpdatedBy(userId);
        if (contentChanged) {
            // PDF cũ vẫn giữ lại để user xem tạm; khi xem/tải BE sẽ render lại nếu cờ này đang bật.
            cv.setPdfDirty(true);
        }

        Cvs saved = cvsRepository.save(cv);
        CvTemplate template = findTemplateByIdOrThrow(saved.getTemplateId());
        cvSectionSyncService.syncSectionsIfChanged(saved, extraData);
        saved = cvsRepository.save(saved);
        return mapToOnlineDetail(saved, template);
    }

    @Override
    @Transactional
    public ResCvDTO setDefaultCv(Long userId, Long id) {
        Cvs cv = cvsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));

        if (Boolean.TRUE.equals(cv.getIsDefault())) {
            return mapToDTO(cv);
        }

        clearCurrentDefaultCv(userId);

        cv.setIsDefault(true);
        cv.setUpdatedBy(userId);
        return mapToDTO(cvsRepository.save(cv));
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO changeOnlineCvTemplate(Long userId, Long id, ReqChangeOnlineCvTemplateDTO request) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findActiveTemplateOrThrow(request.getTemplateId());

        cv.setTemplateId(template.getId());
        cv.setUpdatedBy(userId);
        // Đổi template làm HTML/CSS render khác dù dữ liệu CV không đổi.
        cv.setPdfDirty(true);

        Cvs saved = cvsRepository.save(cv);
        return mapToOnlineDetail(saved, template);
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO updateOnlineCvPersonalInfo(
            Long userId,
            Long id,
            CvOnlineExtraDataDTO.PersonalInfo personalInfo) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        CvOnlineExtraDataDTO extraData = parseExtraData(cv.getExtraData());
        extraData.setPersonalInfo(personalInfo == null ? new CvOnlineExtraDataDTO.PersonalInfo() : personalInfo);
        return savePartialOnlineCv(userId, cv, template, extraData);
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO updateOnlineCvExperiences(
            Long userId,
            Long id,
            List<CvOnlineExtraDataDTO.ExperienceItem> experiences) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        CvOnlineExtraDataDTO extraData = parseExtraData(cv.getExtraData());
        List<CvOnlineExtraDataDTO.ExperienceItem> safeExperiences = safeList(experiences);
        extraData.setExperiences(safeExperiences);
        return saveSearchableOnlineCvSection(
                userId,
                cv,
                template,
                extraData,
                safeExperiences,
                cvSectionSyncService::syncExperiences);
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO updateOnlineCvEducations(
            Long userId,
            Long id,
            List<CvOnlineExtraDataDTO.EducationItem> educations) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        CvOnlineExtraDataDTO extraData = parseExtraData(cv.getExtraData());
        List<CvOnlineExtraDataDTO.EducationItem> safeEducations = safeList(educations);
        extraData.setEducations(safeEducations);
        return saveSearchableOnlineCvSection(
                userId,
                cv,
                template,
                extraData,
                safeEducations,
                cvSectionSyncService::syncEducations);
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO updateOnlineCvSkills(
            Long userId,
            Long id,
            List<CvOnlineExtraDataDTO.SkillItem> skills) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        CvOnlineExtraDataDTO extraData = parseExtraData(cv.getExtraData());
        List<CvOnlineExtraDataDTO.SkillItem> safeSkills = safeList(skills);
        extraData.setSkills(safeSkills);
        return saveSearchableOnlineCvSection(
                userId,
                cv,
                template,
                extraData,
                safeSkills,
                cvSectionSyncService::syncSkills);
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO updateOnlineCvCertifications(
            Long userId,
            Long id,
            List<CvOnlineExtraDataDTO.CertificationItem> certifications) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        CvOnlineExtraDataDTO extraData = parseExtraData(cv.getExtraData());
        List<CvOnlineExtraDataDTO.CertificationItem> safeCertifications = safeList(certifications);
        extraData.setCertifications(safeCertifications);
        return saveSearchableOnlineCvSection(
                userId,
                cv,
                template,
                extraData,
                safeCertifications,
                cvSectionSyncService::syncCertifications);
    }

    @Override
    @Transactional
    public ResCvOnlineDetailDTO updateOnlineCvLanguages(
            Long userId,
            Long id,
            List<CvOnlineExtraDataDTO.LanguageItem> languages) {
        Cvs cv = findOwnedOnlineCvOrThrow(userId, id);
        CvTemplate template = findTemplateByIdOrThrow(cv.getTemplateId());
        CvOnlineExtraDataDTO extraData = parseExtraData(cv.getExtraData());
        List<CvOnlineExtraDataDTO.LanguageItem> safeLanguages = safeList(languages);
        extraData.setLanguages(safeLanguages);
        return saveSearchableOnlineCvSection(
                userId,
                cv,
                template,
                extraData,
                safeLanguages,
                cvSectionSyncService::syncLanguages);
    }

    @Override
    @Transactional
    public ResCvDTO duplicateCv(Long userId, Long id) {
        Cvs original = cvsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));

        long currentCount = cvsRepository.countByUserId(userId);
        if (currentCount >= MAX_CV_PER_USER) {
            throw AppException.badRequest("Đã đạt giới hạn " + MAX_CV_PER_USER + " CV");
        }

        String newTitle = original.getTitle() + " (Copy)";
        int i = 1;
        while (cvsRepository.existsByUserIdAndTitle(userId, newTitle)) {
            newTitle = original.getTitle() + " (Copy " + (i++) + ")";
        }

        Cvs copy = Cvs.builder()
                .userId(userId)
                .title(newTitle)
                .cvType(original.getCvType())
                .templateId(original.getTemplateId())
                .fileUrl(original.getFileUrl())
                .pdfUrl(original.getPdfUrl())
                .pdfDirty(original.getCvType() == CvType.online
                        && (Boolean.TRUE.equals(original.getPdfDirty())
                                || original.getPdfUrl() == null
                                || original.getPdfUrl().isBlank()))
                .extraData(original.getExtraData())
                .isDefault(false)
                .visibility(original.getVisibility())
                .parseStatus(original.getParseStatus())
                .viewCount(0)
                .createdBy(userId)
                .build();

        Cvs saved = cvsRepository.save(copy);
        if (saved.getCvType() == CvType.online) {
            cvSectionSyncService.syncSectionsIfChanged(saved, parseExtraData(saved.getExtraData()));
            saved = cvsRepository.save(saved);
        }
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteCv(Long userId, Long id) {
        Cvs cv = cvsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));

        if (Boolean.TRUE.equals(cv.getIsDefault())) {
            throw AppException.badRequest("Không thể xóa CV mặc định, vui lòng đổi CV mặc định khác trước");
        }

        cv.setDeletedAt(LocalDateTime.now());
        cv.setUpdatedBy(userId);
        cvsRepository.save(cv);
        if (cv.getCvType() == CvType.online) {
            cvSectionSyncService.deleteSections(cv.getId());
        }

        if (cv.getFileUrl() != null && cvsRepository.countActiveByFileUrl(cv.getFileUrl()) == 0) {
            fileStorageService.deleteFile(cv.getFileUrl(), FileUploadType.CV);
        }
        if (cv.getPdfUrl() != null && cvsRepository.countActiveByPdfUrl(cv.getPdfUrl()) == 0) {
            fileStorageService.deleteFile(cv.getPdfUrl(), FileUploadType.CV);
        }
    }

    @Override
    @Transactional
    public ResCvDTO shareCv(Long userId, Long id, ReqShareCvDTO request) {
        Cvs cv = cvsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));

        cv.setVisibility(request.getVisibility());
        cv.setUpdatedBy(userId);
        return mapToDTO(cvsRepository.save(cv));
    }

    @Override
    public ResShareTokenDTO createShareToken(Long userId, Long id, ReqCreateShareTokenDTO request) {
        Cvs cv = cvsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));

        long minutes = 0;
        if (request.getDays() != null) {
            minutes += request.getDays() * 24 * 60;
        }
        if (request.getHours() != null) {
            minutes += request.getHours() * 60;
        }
        if (request.getMinutes() != null) {
            minutes += request.getMinutes();
        }

        if (minutes <= 0) {
            throw AppException.badRequest("Thời gian phải lớn hơn 0");
        }

        String token = java.util.UUID.randomUUID().toString();
        String key = "cv-share:" + token;

        redisTemplate.opsForValue().set(key, String.valueOf(cv.getId()), java.time.Duration.ofMinutes(minutes));

        return ResShareTokenDTO.builder()
                .shareToken(token)
                .expiresAt(LocalDateTime.now().plusMinutes(minutes))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResCvDTO getPublicCv(String shareToken) {
        String key = "cv-share:" + shareToken;
        String cvIdStr = redisTemplate.opsForValue().get(key);

        if (cvIdStr == null) {
            throw AppException.notFound("Không tìm thấy CV hoặc link đã hết hạn");
        }

        Long cvId = Long.parseLong(cvIdStr);
        Cvs cv = cvsRepository.findActiveById(cvId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));

        if (cv.getVisibility() != CvVisibility.public_cv) {
            throw AppException.notFound("Không tìm thấy CV hoặc link đã hết hạn");
        }

        if (cv.getCvType() == CvType.online) {
            cv = ensureFreshOnlineCvPdf(cv, findTemplateByIdOrThrow(cv.getTemplateId()), cv.getUserId());
        }
        return mapToDTO(cv);
    }

    private void clearCurrentDefaultCv(Long userId) {
        cvsRepository.findDefaultByUserId(userId).ifPresent(oldDefault -> {
            oldDefault.setIsDefault(false);
            cvsRepository.save(oldDefault);
        });
    }

    private ResCvOnlineDetailDTO savePartialOnlineCv(
            Long userId,
            Cvs cv,
            CvTemplate template,
            CvOnlineExtraDataDTO extraData) {
        String nextExtraDataJson = toExtraDataJson(extraData);
        if (!Objects.equals(cv.getExtraData(), nextExtraDataJson)) {
            cv.setPdfDirty(true);
        }
        cv.setExtraData(nextExtraDataJson);
        cv.setUpdatedBy(userId);
        return mapToOnlineDetail(cvsRepository.save(cv), template);
    }

    private <T> ResCvOnlineDetailDTO saveSearchableOnlineCvSection(
            Long userId,
            Cvs cv,
            CvTemplate template,
            CvOnlineExtraDataDTO extraData,
            List<T> sectionItems,
            BiConsumer<Long, List<T>> sectionSyncAction) {
        String nextExtraDataJson = toExtraDataJson(extraData);
        String nextSectionHash = cvSectionSyncService.calculateSectionHash(extraData);
        boolean contentChanged = !Objects.equals(cv.getExtraData(), nextExtraDataJson);
        boolean sectionChanged = !Objects.equals(cv.getCvSectionHash(), nextSectionHash);

        if (contentChanged) {
            // Chỉ đánh dấu PDF cũ, không render lại ngay để thao tác lưu section vẫn nhanh.
            cv.setPdfDirty(true);
        }
        cv.setExtraData(nextExtraDataJson);
        cv.setUpdatedBy(userId);
        if (sectionChanged) {
            cv.setCvSectionHash(nextSectionHash);
        }

        Cvs saved = cvsRepository.save(cv);
        if (sectionChanged) {
            // Endpoint section chỉ rebuild đúng bảng liên quan, không đụng các cv_section khác.
            sectionSyncAction.accept(saved.getId(), sectionItems);
        }
        return mapToOnlineDetail(saved, template);
    }

    private <T> List<T> safeList(List<T> items) {
        return items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    private Cvs findOwnedOnlineCvOrThrow(Long userId, Long id) {
        Cvs cv = cvsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy CV"));
        if (cv.getCvType() != CvType.online) {
            throw AppException.badRequest("CV này không phải CV online");
        }
        return cv;
    }

    private CvTemplate findActiveTemplateOrThrow(Long templateId) {
        return cvTemplateRepository.findActiveById(templateId)
                .orElseThrow(() -> AppException.badRequest("Template không tồn tại hoặc không active"));
    }

    private CvTemplate findTemplateByIdOrThrow(Long templateId) {
        if (templateId == null) {
            throw AppException.badRequest("CV online chưa có template");
        }
        return cvTemplateRepository.findActiveOrInactiveById(templateId)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy template của CV"));
    }

    private String toExtraDataJson(CvOnlineExtraDataDTO extraData) {
        try {
            return objectMapper.writeValueAsString(extraData == null ? CvOnlineExtraDataDTO.empty() : extraData);
        } catch (JsonProcessingException ex) {
            throw AppException.badRequest("Không thể serialize dữ liệu CV online");
        }
    }

    private CvOnlineExtraDataDTO parseExtraData(String extraDataJson) {
        if (extraDataJson == null || extraDataJson.isBlank()) {
            return CvOnlineExtraDataDTO.empty();
        }
        try {
            return objectMapper.readValue(extraDataJson, CvOnlineExtraDataDTO.class);
        } catch (JsonProcessingException ex) {
            throw AppException.badRequest("Dữ liệu CV online không đúng schema JSON");
        }
    }

    private CvOnlineExtraDataDTO toPrefillExtraData(User user, CandidateProfile profile) {
        CvOnlineExtraDataDTO.PersonalInfo personalInfo = CvOnlineExtraDataDTO.PersonalInfo.builder()
                .fullName(profile != null ? trimToNull(profile.getFullName()) : null)
                .headline(profile != null ? trimToNull(profile.getPreferredJobTitle()) : null)
                .email(resolveVisibleEmail(user, profile))
                .phone(resolveVisiblePhone(profile))
                .website(profile != null ? trimToNull(profile.getPersonalWebsite()) : null)
                .linkedin(profile != null ? trimToNull(profile.getLinkedinUrl()) : null)
                .github(profile != null ? trimToNull(profile.getGithubUrl()) : null)
                .build();

        return CvOnlineExtraDataDTO.builder()
                .personalInfo(personalInfo)
                .careerObjective(profile != null ? trimToNull(profile.getBio()) : null)
                .build();
    }

    private String resolveVisibleEmail(User user, CandidateProfile profile) {
        if (profile != null && Boolean.TRUE.equals(profile.getHideEmail())) {
            return null;
        }
        return trimToNull(user.getEmail());
    }

    private String resolveVisiblePhone(CandidateProfile profile) {
        if (profile == null || Boolean.TRUE.equals(profile.getHidePhone())) {
            return null;
        }
        return trimToNull(profile.getPhoneDisplay());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String buildPdfFileName(Cvs cv) {
        String baseName = trimToNull(cv.getTitle());
        if (baseName == null) {
            return "cv-online-" + cv.getId() + ".pdf";
        }
        String normalized = baseName
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        if (normalized.isBlank()) {
            normalized = "cv-online-" + cv.getId();
        }
        return normalized + ".pdf";
    }

    private Cvs ensureFreshOnlineCvPdf(Cvs cv, CvTemplate template, Long userId) {
        if (cv.getCvType() != CvType.online) {
            return cv;
        }
        if (cv.getPdfUrl() != null && !cv.getPdfUrl().isBlank() && !Boolean.TRUE.equals(cv.getPdfDirty())) {
            return cv;
        }
        return generateAndPersistOnlineCvPdf(cv, template, userId);
    }

    private Cvs generateAndPersistOnlineCvPdf(Cvs cv, CvTemplate template, Long userId) {
        String oldPdfUrl = cv.getPdfUrl();
        String xhtmlContent = cvOnlineTemplateRenderer.renderToXhtml(
                template.getHtmlContent(),
                template.getCssContent(),
                parseExtraData(cv.getExtraData()));
        byte[] pdfBytes = pdfGenerationService.generatePdfFromHtml(xhtmlContent);
        String pdfUrl = fileStorageService.uploadBytes(pdfBytes, buildPdfFileName(cv), userId, FileUploadType.CV);

        // Chỉ xóa PDF cũ sau khi upload PDF mới thành công để tránh mất file nếu render lỗi giữa chừng.
        cv.setPdfUrl(pdfUrl);
        cv.setPdfDirty(false);
        cv.setUpdatedBy(userId);
        Cvs saved = cvsRepository.save(cv);

        deleteUnusedPdf(oldPdfUrl, pdfUrl);
        return saved;
    }

    private void deleteUnusedPdf(String oldPdfUrl, String currentPdfUrl) {
        if (oldPdfUrl == null || oldPdfUrl.isBlank()) {
            return;
        }
        if (oldPdfUrl.equals(currentPdfUrl)) {
            return;
        }
        if (cvsRepository.countActiveByPdfUrl(oldPdfUrl) == 0) {
            fileStorageService.deleteFile(oldPdfUrl, FileUploadType.CV);
        }
    }

    private ResCvDTO mapToDTO(Cvs cv) {
        return ResCvDTO.builder()
                .id(cv.getId())
                .title(cv.getTitle())
                .cvType(cv.getCvType())
                .templateId(cv.getTemplateId())
                .fileUrl(cv.getFileUrl())
                .pdfUrl(cv.getPdfUrl())
                .pdfDirty(cv.getPdfDirty())
                .isDefault(cv.getIsDefault())
                .visibility(cv.getVisibility())
                .shareToken(cv.getShareToken())
                .shareExpiresAt(cv.getShareExpiresAt())
                .parseStatus(cv.getParseStatus())
                .viewCount(cv.getViewCount())
                .createdBy(cv.getCreatedBy())
                .createdAt(cv.getCreatedAt())
                .updatedAt(cv.getUpdatedAt())
                .build();
    }

    private ResCvOnlineDetailDTO mapToOnlineDetail(Cvs cv, CvTemplate template) {
        return ResCvOnlineDetailDTO.builder()
                .id(cv.getId())
                .title(cv.getTitle())
                .cvType(cv.getCvType())
                .templateId(cv.getTemplateId())
                .template(mapTemplateDetail(template))
                .extraData(parseExtraData(cv.getExtraData()))
                .pdfUrl(cv.getPdfUrl())
                .pdfDirty(cv.getPdfDirty())
                .isDefault(cv.getIsDefault())
                .visibility(cv.getVisibility())
                .parseStatus(cv.getParseStatus())
                .viewCount(cv.getViewCount())
                .createdAt(cv.getCreatedAt())
                .updatedAt(cv.getUpdatedAt())
                .build();
    }

    private ResCvOnlineEditorPayloadDTO mapToEditorPayload(Cvs cv, CvTemplate template) {
        return ResCvOnlineEditorPayloadDTO.builder()
                .cvId(cv.getId())
                .persisted(true)
                .title(cv.getTitle())
                .cvType(cv.getCvType())
                .templateId(cv.getTemplateId())
                .template(mapTemplateDetail(template))
                .extraData(parseExtraData(cv.getExtraData()))
                .pdfDirty(cv.getPdfDirty())
                .isDefault(cv.getIsDefault())
                .visibility(cv.getVisibility())
                .parseStatus(cv.getParseStatus())
                .viewCount(cv.getViewCount())
                .createdAt(cv.getCreatedAt())
                .updatedAt(cv.getUpdatedAt())
                .build();
    }

    private ResCvTemplateDetailDTO mapTemplateDetail(CvTemplate template) {
        return ResCvTemplateDetailDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .slug(template.getSlug())
                .description(template.getDescription())
                .thumbnailUrl(template.getThumbnailUrl())
                .htmlContent(template.getHtmlContent())
                .cssContent(template.getCssContent())
                .isActive(template.getIsActive())
                .isDefault(template.getIsDefault())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
