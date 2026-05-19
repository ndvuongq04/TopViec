package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqIndustryDTO;
import com.topviec.topviec_be.dto.response.ResIndustryDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;

public interface IndustryService {

    ResultPaginationDTO getIndustries(String keyword, int page, int size);

    ResIndustryDTO getIndustryById(Long id);

    ResIndustryDTO createIndustry(ReqIndustryDTO request);

    ResIndustryDTO updateIndustry(Long id, ReqIndustryDTO request);

    void deleteIndustry(Long id);
}