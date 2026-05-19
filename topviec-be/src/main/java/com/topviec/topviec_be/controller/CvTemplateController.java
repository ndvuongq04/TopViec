package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResCvTemplateDTO;
import com.topviec.topviec_be.dto.response.ResCvTemplateDetailDTO;
import com.topviec.topviec_be.service.CvTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cv-templates")
@RequiredArgsConstructor
public class CvTemplateController {

    private final CvTemplateService cvTemplateService;

    @GetMapping
    public ResponseEntity<List<ResCvTemplateDTO>> getActiveTemplates() {
        return ResponseEntity.ok(cvTemplateService.getActiveTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResCvTemplateDetailDTO> getActiveTemplateDetail(@PathVariable Long id) {
        return ResponseEntity.ok(cvTemplateService.getActiveTemplateDetail(id));
    }
}
