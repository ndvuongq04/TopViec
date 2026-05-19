package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResEmployerProfileDTO;
import com.topviec.topviec_be.service.CompanyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employer/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerProfileController {

    private final CompanyMemberService companyMemberService;

    @GetMapping("/me")
    public ResponseEntity<ResEmployerProfileDTO> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        ResEmployerProfileDTO profile = companyMemberService.getMyProfile(userId);
        return ResponseEntity.ok(profile);
    }
}
