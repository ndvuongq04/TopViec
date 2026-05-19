package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.dto.request.ReqCreateCvTemplateDTO;
import com.topviec.topviec_be.dto.request.ReqPreviewCvTemplateDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCvTemplateContentDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateCvTemplateDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplateDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplateDetailDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplatePreviewDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CvTemplateService {

    ResultPaginationDTO getAdminTemplates(String keyword, Pageable pageable);

    ResCvTemplateDetailDTO getAdminTemplateDetail(Long id);

    ResCvTemplateDetailDTO createTemplate(Long adminUserId, ReqCreateCvTemplateDTO request, MultipartFile thumbnail);

    ResCvTemplateDetailDTO updateTemplateMetadata(Long adminUserId, Long id, ReqUpdateCvTemplateDTO request);

    ResCvTemplateDetailDTO updateTemplateContent(Long adminUserId, Long id, ReqUpdateCvTemplateContentDTO request);

    ResCvTemplateDetailDTO activateTemplate(Long adminUserId, Long id);

    ResCvTemplateDetailDTO deactivateTemplate(Long adminUserId, Long id);

    ResCvTemplateDetailDTO setDefaultTemplate(Long adminUserId, Long id);

    CvOnlineExtraDataDTO getSampleData();

    ResCvTemplatePreviewDTO previewTemplate(Long adminUserId, ReqPreviewCvTemplateDTO request);

    List<ResCvTemplateDTO> getActiveTemplates();

    ResCvTemplateDetailDTO getActiveTemplateDetail(Long id);
}
