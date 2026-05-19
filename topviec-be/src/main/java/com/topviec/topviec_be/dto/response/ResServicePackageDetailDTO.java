package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.services.ServiceCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResServicePackageDetailDTO {
    private Long id;
    private Long serviceId;
    private String serviceCode;
    private String serviceName;
    private ServiceCategory serviceCategory;
    private String serviceCategoryName;
    private String serviceUnit;
    private Integer quantity;
}
