package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResCompanyDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller public — không yêu cầu đăng nhập.
 * Base URL: /api/v1/companies
 */
@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class PublicCompanyController {

    private final CompanyService companyService;

    /**
     * GET /companies
     * GET /companies?keyword=abc&provinceId=1&industryId=5&page=0&size=10
     * Lấy danh sách công ty active, hỗ trợ tìm kiếm + lọc + phân trang.
     */
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getPublicCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer provinceId,
            @RequestParam(required = false) Long industryId,
            @RequestParam(required = false) Boolean isBanner,
            @RequestParam(required = false) Boolean isTopEmployer,
            @RequestParam(required = false) Boolean isBrandVerified,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(
                companyService.getPublicCompanies(keyword, provinceId, industryId, isBanner, isTopEmployer,
                        isBrandVerified, pageable));
    }

    /**
     * GET /companies/{slug}
     * Lấy thông tin công ty theo slug (trang chi tiết công ty).
     * Chỉ trả về công ty đang active.
     */
    @GetMapping("/{slug}")
    public ResponseEntity<ResCompanyDTO> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(companyService.getBySlug(slug));
    }

    /**
     * GET /companies/id/{id}
     * Lấy thông tin công ty theo id.
     * Chỉ trả về công ty đang active.
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<ResCompanyDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }
}