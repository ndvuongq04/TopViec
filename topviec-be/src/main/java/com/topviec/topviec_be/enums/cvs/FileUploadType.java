package com.topviec.topviec_be.enums.cvs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileUploadType {
    CV("files/cvs", "user"),
    AVATAR("images/avatars", "user"),
    CV_TEMPLATE_THUMBNAIL("images/cv-template-thumbnails", "admin"),
    COMPANY_LOGO("images/company-logos", "company"),
    COMPANY_COVER("images/company-covers", "company"),
    BUSINESS_LICENSE("images/business-licenses", "company");

    private final String subDir;
    private final String ownerPrefix;

    public String resolveOwnerDir(Long ownerId) {
        return ownerPrefix + "_" + ownerId;
    }
}
