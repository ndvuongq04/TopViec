package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import com.topviec.topviec_be.entity.Cvs;

import java.util.List;

public interface CvSectionSyncService {

    boolean syncSectionsIfChanged(Cvs cv, CvOnlineExtraDataDTO extraData);

    String calculateSectionHash(CvOnlineExtraDataDTO extraData);

    void syncExperiences(Long cvId, List<CvOnlineExtraDataDTO.ExperienceItem> items);

    void syncEducations(Long cvId, List<CvOnlineExtraDataDTO.EducationItem> items);

    void syncSkills(Long cvId, List<CvOnlineExtraDataDTO.SkillItem> items);

    void syncCertifications(Long cvId, List<CvOnlineExtraDataDTO.CertificationItem> items);

    void syncLanguages(Long cvId, List<CvOnlineExtraDataDTO.LanguageItem> items);

    void deleteSections(Long cvId);
}
