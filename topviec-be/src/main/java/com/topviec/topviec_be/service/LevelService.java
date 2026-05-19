package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqLevelDTO;
import com.topviec.topviec_be.dto.response.ResLevelDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;

import java.util.List;

public interface LevelService {

    ResultPaginationDTO getLevels(String keyword, int page, int size);

    ResLevelDTO getLevelById(Long id);

    List<ResLevelDTO> createLevels(List<ReqLevelDTO> requests);

    ResLevelDTO updateLevel(Long id, ReqLevelDTO request);

    void deleteLevel(Long id);
}