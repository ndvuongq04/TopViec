package com.topviec.topviec_be.service;

import java.util.List;

import com.topviec.topviec_be.dto.request.ReqSkillDTO;
import com.topviec.topviec_be.dto.response.ResSkillDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;

public interface SkillService {

    ResultPaginationDTO getSkills(String keyword, int page, int size);

    ResSkillDTO getSkillById(Long id);

    List<ResSkillDTO> createSkills(List<ReqSkillDTO> requests);

    ResSkillDTO updateSkill(Long id, ReqSkillDTO request);

    void deleteSkill(Long id);
}