package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.request.ReqLevelDTO;
import com.topviec.topviec_be.dto.response.ResLevelDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getLevels(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(levelService.getLevels(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResLevelDTO> getLevelById(@PathVariable Long id) {
        return ResponseEntity.ok(levelService.getLevelById(id));
    }

    @PostMapping
    public ResponseEntity<List<ResLevelDTO>> createLevels(@RequestBody List<ReqLevelDTO> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(levelService.createLevels(requests));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResLevelDTO> updateLevel(
            @PathVariable Long id,
            @RequestBody ReqLevelDTO request) {
        return ResponseEntity.ok(levelService.updateLevel(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLevel(@PathVariable Long id) {
        levelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }
}