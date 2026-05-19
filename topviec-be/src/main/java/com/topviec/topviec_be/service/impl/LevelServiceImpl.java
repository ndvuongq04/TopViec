package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqLevelDTO;
import com.topviec.topviec_be.dto.response.ResLevelDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.Level;
import com.topviec.topviec_be.repository.LevelRepository;
import com.topviec.topviec_be.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;

    @Override
    public ResultPaginationDTO getLevels(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Level> levelPage = levelRepository.searchLevels(keyword, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(page);
        meta.setPageSize(size);
        meta.setPages(levelPage.getTotalPages());
        meta.setTotals(levelPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(levelPage.getContent().stream().map(this::toResponse).toList());

        return result;
    }

    @Override
    public ResLevelDTO getLevelById(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found with id: " + id));
        return toResponse(level);
    }

    @Override
    public List<ResLevelDTO> createLevels(List<ReqLevelDTO> requests) {
        List<Level> levels = requests.stream()
                .map(request -> {
                    Level level = new Level();
                    mapRequestToEntity(request, level);
                    return level;
                })
                .toList();
        return levelRepository.saveAll(levels)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ResLevelDTO updateLevel(Long id, ReqLevelDTO request) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found with id: " + id));
        mapRequestToEntity(request, level);
        return toResponse(levelRepository.save(level));
    }

    @Override
    public void deleteLevel(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Level not found with id: " + id));
        levelRepository.delete(level);
    }

    // ========== PRIVATE HELPERS ==========

    private void mapRequestToEntity(ReqLevelDTO request, Level level) {
        level.setName(request.getName());
        level.setRank(request.getRank());
    }

    private ResLevelDTO toResponse(Level l) {
        return ResLevelDTO.builder()
                .id(l.getId())
                .name(l.getName())
                .rank(l.getRank())
                .build();
    }
}