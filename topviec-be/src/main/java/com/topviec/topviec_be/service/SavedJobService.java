package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.response.ResSavedJobDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;

public interface SavedJobService {

    ResSavedJobDTO toggleSave(Long userId, Long jobPostId);

    boolean isSaved(Long userId, Long jobPostId);

    ResultPaginationDTO getSavedJobs(Long userId, int page, int size);

    void unsave(Long userId, Long jobPostId);
}