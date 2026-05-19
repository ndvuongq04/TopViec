package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.cvonline.CvOnlineTemplateRenderer;
import com.topviec.topviec_be.cvonline.CvTemplateCssAdvisor;
import com.topviec.topviec_be.cvonline.CvTemplatePlaceholderParser;
import com.topviec.topviec_be.cvonline.CvTemplateSampleDataFactory;
import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.dto.request.ReqCreateCvTemplateDTO;
import com.topviec.topviec_be.dto.request.ReqPreviewCvTemplateDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCvTemplateContentDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCvTemplateDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplateDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplateDetailDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplatePreviewDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.CvTemplate;
import com.topviec.topviec_be.enums.cvs.CvType;
import com.topviec.topviec_be.enums.cvs.FileUploadType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CvTemplateRepository;
import com.topviec.topviec_be.repository.CvsRepository;
import com.topviec.topviec_be.service.CvTemplateService;
import com.topviec.topviec_be.service.FileStorageService;
import com.topviec.topviec_be.util.ChangeTracker;
import com.topviec.topviec_be.util.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CvTemplateServiceImpl implements CvTemplateService {

    private final CvTemplateRepository cvTemplateRepository;
    private final CvsRepository cvsRepository;
    private final CvTemplateSampleDataFactory sampleDataFactory;
    private final CvTemplateCssAdvisor cssAdvisor;
    private final CvOnlineTemplateRenderer templateRenderer;
    private final FileStorageService fileStorageService;
    private final FileValidator fileValidator;

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAdminTemplates(String keyword, Pageable pageable) {
        Page<CvTemplate> page = cvTemplateRepository.searchAll(keyword, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        List<ResCvTemplateDTO> results = page.getContent().stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO response = new ResultPaginationDTO();
        response.setMeta(meta);
        response.setResult(results);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ResCvTemplateDetailDTO getAdminTemplateDetail(Long id) {
        return toDetailDTO(findTemplateOrThrow(id));
    }

    @Override
    @Transactional
    public ResCvTemplateDetailDTO createTemplate(Long adminUserId, ReqCreateCvTemplateDTO request, MultipartFile thumbnail) {
        validateSlugUnique(request.getSlug(), null);
        validateContent(request.getHtmlContent(), request.getCssContent());

        boolean isActive = request.getIsActive() == null || request.getIsActive();
        boolean shouldBeDefault = Boolean.TRUE.equals(request.getIsDefault())
                || cvTemplateRepository.findDefaultTemplate().isEmpty();

        if (shouldBeDefault && !isActive) {
            throw AppException.badRequest("Template mac dinh phai o trang thai active");
        }

        if (shouldBeDefault) {
            clearCurrentDefault();
        }

        String thumbnailUrl = resolveThumbnailUrl(adminUserId, request.getThumbnailUrl(), thumbnail);

        CvTemplate template = CvTemplate.builder()
                .name(request.getName().trim())
                .slug(request.getSlug().trim())
                .description(trimToNull(request.getDescription()))
                .thumbnailUrl(thumbnailUrl)
                .htmlContent(request.getHtmlContent())
                .cssContent(request.getCssContent())
                .isActive(isActive)
                .isDefault(shouldBeDefault)
                .createdBy(adminUserId)
                .updatedBy(adminUserId)
                .build();

        return toDetailDTO(cvTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public ResCvTemplateDetailDTO updateTemplateMetadata(Long adminUserId, Long id, ReqUpdateCvTemplateDTO request) {
        CvTemplate template = findTemplateOrThrow(id);
        validateSlugUnique(request.getSlug(), id);

        ChangeTracker tracker = ChangeTracker.of(template);

        template.setName(request.getName().trim());
        template.setSlug(request.getSlug().trim());
        template.setDescription(trimToNull(request.getDescription()));
        template.setThumbnailUrl(trimToNull(request.getThumbnailUrl()));
        template.setUpdatedBy(adminUserId);

        CvTemplate saved = cvTemplateRepository.save(template);
        // HTML/CSS template đổi thì toàn bộ PDF đã render từ template này đều trở thành bản cũ.
        cvsRepository.markPdfDirtyByTemplateId(saved.getId(), CvType.online);
        tracker.compare(saved).apply();
        return toDetailDTO(saved);
    }

    @Override
    @Transactional
    public ResCvTemplateDetailDTO updateTemplateContent(Long adminUserId, Long id, ReqUpdateCvTemplateContentDTO request) {
        CvTemplate template = findTemplateOrThrow(id);
        validateContent(request.getHtmlContent(), request.getCssContent());

        ChangeTracker tracker = ChangeTracker.of(template);

        template.setHtmlContent(request.getHtmlContent());
        template.setCssContent(request.getCssContent());
        template.setUpdatedBy(adminUserId);

        CvTemplate saved = cvTemplateRepository.save(template);
        tracker.compare(saved).apply();
        return toDetailDTO(saved);
    }

    @Override
    @Transactional
    public ResCvTemplateDetailDTO activateTemplate(Long adminUserId, Long id) {
        CvTemplate template = findTemplateOrThrow(id);
        if (Boolean.TRUE.equals(template.getIsActive())) {
            return toDetailDTO(template);
        }

        template.setIsActive(true);
        template.setUpdatedBy(adminUserId);

        if (cvTemplateRepository.findDefaultTemplate().isEmpty()) {
            template.setIsDefault(true);
        }

        return toDetailDTO(cvTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public ResCvTemplateDetailDTO deactivateTemplate(Long adminUserId, Long id) {
        CvTemplate template = findTemplateOrThrow(id);
        if (Boolean.TRUE.equals(template.getIsDefault())) {
            throw AppException.badRequest("Khong the deactivate template mac dinh");
        }
        if (!Boolean.TRUE.equals(template.getIsActive())) {
            return toDetailDTO(template);
        }

        template.setIsActive(false);
        template.setUpdatedBy(adminUserId);
        return toDetailDTO(cvTemplateRepository.save(template));
    }

    @Override
    @Transactional
    public ResCvTemplateDetailDTO setDefaultTemplate(Long adminUserId, Long id) {
        CvTemplate template = findTemplateOrThrow(id);
        if (!Boolean.TRUE.equals(template.getIsActive())) {
            throw AppException.badRequest("Chi co the dat template active lam mac dinh");
        }
        if (Boolean.TRUE.equals(template.getIsDefault())) {
            return toDetailDTO(template);
        }

        clearCurrentDefault();
        template.setIsDefault(true);
        template.setUpdatedBy(adminUserId);
        return toDetailDTO(cvTemplateRepository.save(template));
    }

    @Override
    @Transactional(readOnly = true)
    public CvOnlineExtraDataDTO getSampleData() {
        return sampleDataFactory.createSampleData();
    }

    @Override
    @Transactional(readOnly = true)
    public ResCvTemplatePreviewDTO previewTemplate(Long adminUserId, ReqPreviewCvTemplateDTO request) {
        CvTemplatePlaceholderParser.TemplateValidationResult validation = CvTemplatePlaceholderParser
                .validate(request.getHtmlContent());
        List<String> cssWarnings = cssAdvisor.analyze(request.getCssContent());
        CvOnlineExtraDataDTO sampleData = sampleDataFactory.createSampleData();

        String renderedHtml = null;
        String renderedXhtml = null;
        if (validation.isValid()) {
            try {
                renderedHtml = templateRenderer.renderToHtmlDocument(
                        request.getHtmlContent(),
                        request.getCssContent(),
                        sampleData);
                renderedXhtml = templateRenderer.renderToXhtml(request.getHtmlContent(), request.getCssContent(), sampleData);
            } catch (Exception ex) {
                Long templateId = request.getTemplateId();
                log.error("Preview template that bai - templateId: {}, adminUserId: {}", templateId, adminUserId, ex);
                throw AppException.badRequest("Khong the preview template voi sample data");
            }
        }

        CvTemplate template = request.getTemplateId() != null
                ? cvTemplateRepository.findActiveOrInactiveById(request.getTemplateId()).orElse(null)
                : null;

        return ResCvTemplatePreviewDTO.builder()
                .templateId(request.getTemplateId())
                .versionTag(template != null ? buildVersionTag(template) : null)
                .renderedHtml(renderedHtml)
                .renderedXhtml(renderedXhtml)
                .sampleData(sampleData)
                .valid(validation.isValid())
                .rootPlaceholders(validation.rootPlaceholders())
                .sections(validation.sections())
                .placeholderErrors(validation.errors())
                .cssWarnings(cssWarnings)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResCvTemplateDTO> getActiveTemplates() {
        return cvTemplateRepository.findAllActive()
                .stream()
                .map(this::toSummaryDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResCvTemplateDetailDTO getActiveTemplateDetail(Long id) {
        CvTemplate template = cvTemplateRepository.findActiveById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay template dang active"));
        return toDetailDTO(template);
    }

    private CvTemplate findTemplateOrThrow(Long id) {
        return cvTemplateRepository.findActiveOrInactiveById(id)
                .orElseThrow(() -> AppException.notFound("Khong tim thay CV template"));
    }

    private void clearCurrentDefault() {
        cvTemplateRepository.findDefaultTemplate().ifPresent(currentDefault -> {
            currentDefault.setIsDefault(false);
            cvTemplateRepository.save(currentDefault);
        });
    }

    private void validateSlugUnique(String slug, Long currentId) {
        boolean exists = currentId == null
                ? cvTemplateRepository.existsBySlug(slug.trim())
                : cvTemplateRepository.existsBySlugAndIdNot(slug.trim(), currentId);
        if (exists) {
            throw AppException.conflict("Slug template da ton tai");
        }
    }

    private void validateContent(String htmlContent, String cssContent) {
        if (cssContent == null || cssContent.isBlank()) {
            throw AppException.badRequest("CSS content khong duoc de trong");
        }

        CvTemplatePlaceholderParser.TemplateValidationResult validation = CvTemplatePlaceholderParser.validate(htmlContent);
        if (!validation.isValid()) {
            throw AppException.badRequest("Template HTML khong hop le: " + String.join("; ", validation.errors()));
        }
    }

    private String buildVersionTag(CvTemplate template) {
        if (template == null) {
            return null;
        }
        if (template.getUpdatedAt() != null) {
            return "v" + Timestamp.valueOf(template.getUpdatedAt()).getTime();
        }
        if (template.getCreatedAt() != null) {
            return "v" + Timestamp.valueOf(template.getCreatedAt()).getTime();
        }
        return "v0";
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String resolveThumbnailUrl(Long adminUserId, String thumbnailUrl, MultipartFile thumbnail) {
        if (thumbnail == null || thumbnail.isEmpty()) {
            return trimToNull(thumbnailUrl);
        }

        fileValidator.validate(thumbnail, FileUploadType.CV_TEMPLATE_THUMBNAIL);
        return fileStorageService.uploadFile(thumbnail, adminUserId, FileUploadType.CV_TEMPLATE_THUMBNAIL);
    }

    private ResCvTemplateDTO toSummaryDTO(CvTemplate template) {
        return ResCvTemplateDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .slug(template.getSlug())
                .description(template.getDescription())
                .thumbnailUrl(template.getThumbnailUrl())
                .versionTag(buildVersionTag(template))
                .isActive(template.getIsActive())
                .isDefault(template.getIsDefault())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }

    private ResCvTemplateDetailDTO toDetailDTO(CvTemplate template) {
        return ResCvTemplateDetailDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .slug(template.getSlug())
                .description(template.getDescription())
                .thumbnailUrl(template.getThumbnailUrl())
                .htmlContent(template.getHtmlContent())
                .cssContent(template.getCssContent())
                .versionTag(buildVersionTag(template))
                .isActive(template.getIsActive())
                .isDefault(template.getIsDefault())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
