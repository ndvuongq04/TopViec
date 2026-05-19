package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqSkillDTO;
import com.topviec.topviec_be.dto.response.ResSkillDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Skill;
import com.topviec.topviec_be.repository.SkillRepository;
import com.topviec.topviec_be.service.SkillService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public ResultPaginationDTO getSkills(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Skill> skillPage = skillRepository.searchSkills(keyword, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setPageSize(size);
        meta.setPages(skillPage.getTotalPages());
        meta.setTotals(skillPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(skillPage.getContent().stream().map(this::toResponse).toList());

        return result;
    }

    @Override
    public ResSkillDTO getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));
        return toResponse(skill);
    }

    @Override
    public List<ResSkillDTO> createSkills(List<ReqSkillDTO> requests) {
        List<Skill> skills = requests.stream()
                .map(request -> {
                    Skill skill = new Skill();
                    mapRequestToEntity(request, skill);
                    return skill;
                })
                .toList();
        return skillRepository.saveAll(skills)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ResSkillDTO updateSkill(Long id, ReqSkillDTO request) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));
        mapRequestToEntity(request, skill);
        return toResponse(skillRepository.save(skill));
    }

    @Override
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found with id: " + id));
        skillRepository.delete(skill);
    }

    // ========== PRIVATE HELPERS ==========

    private void mapRequestToEntity(ReqSkillDTO request, Skill skill) {
        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        skill.setAliases(request.getAliases());
        skill.setUsageCount(request.getUsageCount() != null ? request.getUsageCount() : 0);
        skill.setIsActive(request.getIsActive());
    }

    private ResSkillDTO toResponse(Skill s) {
        return ResSkillDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .category(s.getCategory())
                .aliases(s.getAliases())
                .usageCount(s.getUsageCount())
                .isActive(s.getIsActive())
                .build();
    }
}