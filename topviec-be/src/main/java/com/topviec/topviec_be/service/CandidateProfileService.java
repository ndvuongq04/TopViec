package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqCreateCandidateProfileDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCandidateProfileDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCandidateProfileVisibilityDTO;
import com.topviec.topviec_be.dto.response.ResCandidateProfileDTO;

public interface CandidateProfileService {

    ResCandidateProfileDTO createProfile(Long userId, ReqCreateCandidateProfileDTO request);

    ResCandidateProfileDTO getMyProfile(Long userId);

    ResCandidateProfileDTO updateProfile(Long userId, ReqUpdateCandidateProfileDTO request);

    ResCandidateProfileDTO updateVisibility(Long userId, ReqUpdateCandidateProfileVisibilityDTO request);

    void deleteProfile(Long userId);

    void createDefaultProfile(Long userId, String email);
}
