package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.enums.logging.LogActionType;

import com.topviec.topviec_be.dto.response.ResSavedJobDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.SavedJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/{jobPostId}/toggle")
    @LogAction(LogActionType.SAVE_JOB)
    public ResponseEntity<ResSavedJobDTO> toggle(
            @PathVariable Long jobPostId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(savedJobService.toggleSave(extractUserId(jwt), jobPostId));
    }

    @GetMapping("/{jobPostId}/status")
    public ResponseEntity<Map<String, Boolean>> isSaved(
            @PathVariable Long jobPostId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of("isSaved", savedJobService.isSaved(extractUserId(jwt), jobPostId)));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getSavedJobs(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(savedJobService.getSavedJobs(extractUserId(jwt), page, size));
    }

    @DeleteMapping("/{jobPostId}")
    public ResponseEntity<Void> unsave(
            @PathVariable Long jobPostId,
            @AuthenticationPrincipal Jwt jwt) {
        savedJobService.unsave(extractUserId(jwt), jobPostId);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}