package com.topviec.topviec_be.dto.response;

import com.topviec.topviec_be.enums.cvs.FileUploadType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResFileUploadDTO {
    private String fileUrl;
    private FileUploadType type;
}
