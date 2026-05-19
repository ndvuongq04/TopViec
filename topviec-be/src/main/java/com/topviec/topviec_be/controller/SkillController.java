package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.request.ReqSkillDTO;
import com.topviec.topviec_be.dto.response.ResSkillDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.SkillService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getSkills(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(skillService.getSkills(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResSkillDTO> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PostMapping
    public ResponseEntity<List<ResSkillDTO>> createSkills(@RequestBody List<ReqSkillDTO> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkills(requests));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResSkillDTO> updateSkill(
            @PathVariable Long id,
            @RequestBody ReqSkillDTO request) {
        return ResponseEntity.ok(skillService.updateSkill(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}