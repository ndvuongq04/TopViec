package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.request.ReqIndustryDTO;
import com.topviec.topviec_be.dto.response.ResIndustryDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.IndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/industries")
@RequiredArgsConstructor
public class IndustryController {

    private final IndustryService industryService;

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getIndustries(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(industryService.getIndustries(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResIndustryDTO> getIndustryById(@PathVariable Long id) {
        return ResponseEntity.ok(industryService.getIndustryById(id));
    }

    @PostMapping
    public ResponseEntity<ResIndustryDTO> createIndustry(@RequestBody ReqIndustryDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(industryService.createIndustry(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResIndustryDTO> updateIndustry(
            @PathVariable Long id,
            @RequestBody ReqIndustryDTO request) {
        return ResponseEntity.ok(industryService.updateIndustry(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIndustry(@PathVariable Long id) {
        industryService.deleteIndustry(id);
        return ResponseEntity.noContent().build();
    }
}
