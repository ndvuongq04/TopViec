package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqCreateCandidateProfileDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCandidateProfileDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCandidateProfileVisibilityDTO;
import com.topviec.topviec_be.dto.response.ResCandidateProfileDTO;
import com.topviec.topviec_be.entity.CandidateProfile;
import com.topviec.topviec_be.enums.candidateProfile.PreferredWorkType;
import com.topviec.topviec_be.enums.cvs.FileUploadType;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.CandidateProfileRepository;
import com.topviec.topviec_be.service.CandidateProfileService;
import com.topviec.topviec_be.service.FileStorageService;
import com.topviec.topviec_be.util.ChangeTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public void createDefaultProfile(Long userId, String email) {
        CandidateProfile profile = CandidateProfile.builder()
                .userId(userId)
                .fullName(email)
                .createdBy(userId)
                .build();

        candidateProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public ResCandidateProfileDTO createProfile(Long userId, ReqCreateCandidateProfileDTO request) {
        if (candidateProfileRepository.existsByUserId(userId)) {
            throw AppException.conflict("Ho so ca nhan da ton tai");
        }

        validateSalaryRange(request.getExpectedSalaryMin(), request.getExpectedSalaryMax(),
                request.getSalaryNegotiable());

        CandidateProfile profile = CandidateProfile.builder()
                .userId(userId)
                .fullName(request.getFullName())
                .avatarUrl(request.getAvatarUrl())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phoneDisplay(request.getPhoneDisplay())
                .bio(request.getBio())
                .linkedinUrl(request.getLinkedinUrl())
                .githubUrl(request.getGithubUrl())
                .personalWebsite(request.getPersonalWebsite())
                .expectedSalaryMin(request.getExpectedSalaryMin())
                .expectedSalaryMax(request.getExpectedSalaryMax())
                .salaryNegotiable(Boolean.TRUE.equals(request.getSalaryNegotiable()))
                .jobSeekingStatus(request.getJobSeekingStatus() != null
                        ? request.getJobSeekingStatus().getValue()
                        : "active")
                .preferredWorkType(request.getPreferredWorkType() != null
                        ? request.getPreferredWorkType().getValue()
                        : null)
                .preferredJobTitle(request.getPreferredJobTitle())
                .preferredLocationId(request.getPreferredLocationId())
                .profileCompletionPct(0)
                .cvPublic(Boolean.TRUE.equals(request.getIsCvPublic()) || request.getIsCvPublic() == null)
                .hidePhone(Boolean.TRUE.equals(request.getHidePhone()))
                .hideEmail(Boolean.TRUE.equals(request.getHideEmail()))
                .hideDateOfBirth(Boolean.TRUE.equals(request.getHideDateOfBirth()))
                .hideExpectedSalary(Boolean.TRUE.equals(request.getHideExpectedSalary()))
                .createdBy(userId)
                .build();

        profile = candidateProfileRepository.save(profile);

        int pct = calculateCompletionPct(profile);
        candidateProfileRepository.updateProfileCompletionPct(userId, pct);
        profile.setProfileCompletionPct(pct);

        return toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ResCandidateProfileDTO getMyProfile(Long userId) {
        CandidateProfile profile = findByUserIdOrThrow(userId);
        return toResponse(profile);
    }

    @Override
    @Transactional
    public ResCandidateProfileDTO updateProfile(Long userId, ReqUpdateCandidateProfileDTO request) {
        CandidateProfile profile = findByUserIdOrThrow(userId);
        String oldAvatarUrl = profile.getAvatarUrl();

        Double salaryMin = request.getExpectedSalaryMin() != null
                ? request.getExpectedSalaryMin()
                : profile.getExpectedSalaryMin();
        Double salaryMax = request.getExpectedSalaryMax() != null
                ? request.getExpectedSalaryMax()
                : profile.getExpectedSalaryMax();
        Boolean salaryNegotiable = request.getSalaryNegotiable() != null
                ? request.getSalaryNegotiable()
                : profile.getSalaryNegotiable();

        validateSalaryRange(salaryMin, salaryMax, salaryNegotiable);

        ChangeTracker tracker = ChangeTracker.of(profile);

        if (request.getFullName() != null)
            profile.setFullName(request.getFullName());
        if (request.getAvatarUrl() != null)
            profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getDateOfBirth() != null)
            profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null)
            profile.setGender(request.getGender());
        if (request.getPhoneDisplay() != null)
            profile.setPhoneDisplay(request.getPhoneDisplay());
        if (request.getBio() != null)
            profile.setBio(request.getBio());
        if (request.getLinkedinUrl() != null)
            profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null)
            profile.setGithubUrl(request.getGithubUrl());
        if (request.getPersonalWebsite() != null)
            profile.setPersonalWebsite(request.getPersonalWebsite());
        if (request.getExpectedSalaryMin() != null)
            profile.setExpectedSalaryMin(request.getExpectedSalaryMin());
        if (request.getExpectedSalaryMax() != null)
            profile.setExpectedSalaryMax(request.getExpectedSalaryMax());
        if (request.getSalaryNegotiable() != null)
            profile.setSalaryNegotiable(request.getSalaryNegotiable());
        if (request.getJobSeekingStatus() != null)
            profile.setJobSeekingStatus(request.getJobSeekingStatus().getValue());
        if (request.getPreferredWorkType() != null)
            profile.setPreferredWorkType(request.getPreferredWorkType().getValue());
        if (request.getPreferredJobTitle() != null)
            profile.setPreferredJobTitle(request.getPreferredJobTitle());
        if (request.getPreferredLocationId() != null)
            profile.setPreferredLocationId(request.getPreferredLocationId());
        if (request.getCvPublic() != null)
            profile.setCvPublic(request.getCvPublic());
        if (request.getHidePhone() != null)
            profile.setHidePhone(request.getHidePhone());
        if (request.getHideEmail() != null)
            profile.setHideEmail(request.getHideEmail());
        if (request.getHideDateOfBirth() != null)
            profile.setHideDateOfBirth(request.getHideDateOfBirth());
        if (request.getHideExpectedSalary() != null)
            profile.setHideExpectedSalary(request.getHideExpectedSalary());

        profile.setUpdatedBy(userId);
        profile = candidateProfileRepository.save(profile);

        tracker.compare(profile).apply();

        int pct = calculateCompletionPct(profile);
        candidateProfileRepository.updateProfileCompletionPct(userId, pct);
        profile.setProfileCompletionPct(pct);

        deleteReplacedFile(oldAvatarUrl, profile.getAvatarUrl(), FileUploadType.AVATAR);

        return toResponse(profile);
    }

    @Override
    @Transactional
    public ResCandidateProfileDTO updateVisibility(Long userId, ReqUpdateCandidateProfileVisibilityDTO request) {
        CandidateProfile profile = findByUserIdOrThrow(userId);

        ChangeTracker tracker = ChangeTracker.of(profile);

        profile.setHidePhone(request.getHidePhone());
        profile.setHideEmail(request.getHideEmail());
        profile.setHideDateOfBirth(request.getHideDateOfBirth());
        profile.setHideExpectedSalary(request.getHideExpectedSalary());
        profile.setUpdatedBy(userId);

        profile = candidateProfileRepository.save(profile);
        tracker.compare(profile).apply();

        return toResponse(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long userId) {
        CandidateProfile profile = findByUserIdOrThrow(userId);
        profile.setDeletedAt(LocalDateTime.now());
        profile.setUpdatedBy(userId);
        candidateProfileRepository.save(profile);
    }

    private CandidateProfile findByUserIdOrThrow(Long userId) {
        return candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> AppException.notFound("Ho so ca nhan khong ton tai"));
    }

    private void deleteReplacedFile(String oldFileUrl, String newFileUrl, FileUploadType type) {
        if (oldFileUrl == null || oldFileUrl.isBlank()) {
            return;
        }
        if (oldFileUrl.equals(newFileUrl)) {
            return;
        }
        fileStorageService.deleteFile(oldFileUrl, type);
    }

    private void validateSalaryRange(Double min, Double max, Boolean salaryNegotiable) {
        boolean isNegotiable = Boolean.TRUE.equals(salaryNegotiable);

        if (!isNegotiable) {
            if (min == null || max == null) {
                throw AppException.badRequest(
                        "Vui long dien day du muc luong toi thieu va toi da, hoac chon 'Thoa thuan'");
            }
            if (min > max) {
                throw AppException.badRequest("Muc luong toi thieu khong duoc lon hon muc luong toi da");
            }
        }
    }

    private int calculateCompletionPct(CandidateProfile profile) {
        int score = 0;
        if (profile.getFullName() != null && !profile.getFullName().isBlank())
            score++;
        if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isBlank())
            score++;
        if (profile.getDateOfBirth() != null)
            score++;
        if (profile.getGender() != null && !profile.getGender().isBlank())
            score++;
        if (profile.getPhoneDisplay() != null && !profile.getPhoneDisplay().isBlank())
            score++;
        if (profile.getBio() != null && !profile.getBio().isBlank())
            score++;
        if (profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().isBlank())
            score++;
        if (profile.getExpectedSalaryMin() != null || Boolean.TRUE.equals(profile.getSalaryNegotiable()))
            score++;
        if (profile.getPreferredWorkType() != null && !profile.getPreferredWorkType().isBlank())
            score++;
        if (profile.getPreferredLocationId() != null)
            score++;

        return (score * 100) / 10;
    }

    private ResCandidateProfileDTO toResponse(CandidateProfile profile) {
        return ResCandidateProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .fullName(profile.getFullName())
                .avatarUrl(profile.getAvatarUrl())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .phoneDisplay(profile.getPhoneDisplay())
                .bio(profile.getBio())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .personalWebsite(profile.getPersonalWebsite())
                .expectedSalaryMin(profile.getExpectedSalaryMin())
                .expectedSalaryMax(profile.getExpectedSalaryMax())
                .salaryNegotiable(profile.getSalaryNegotiable())
                .jobSeekingStatus(profile.getJobSeekingStatus() != null
                        ? com.topviec.topviec_be.enums.candidateProfile.JobSeekingStatus
                                .fromValue(profile.getJobSeekingStatus())
                        : null)
                .preferredWorkType(profile.getPreferredWorkType() != null
                        ? PreferredWorkType.fromValue(profile.getPreferredWorkType())
                        : null)
                .preferredJobTitle(profile.getPreferredJobTitle())
                .preferredLocationId(profile.getPreferredLocationId())
                .profileCompletionPct(profile.getProfileCompletionPct())
                .isCvPublic(profile.getCvPublic())
                .hidePhone(profile.getHidePhone())
                .hideEmail(profile.getHideEmail())
                .hideDateOfBirth(profile.getHideDateOfBirth())
                .hideExpectedSalary(profile.getHideExpectedSalary())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
