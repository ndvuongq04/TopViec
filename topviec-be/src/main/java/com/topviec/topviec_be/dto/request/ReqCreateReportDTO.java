package com.topviec.topviec_be.dto.request;

import com.topviec.topviec_be.enums.complaints.ComplaintType;
import com.topviec.topviec_be.enums.complaints.EvidenceFileType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqCreateReportDTO {

    @NotNull(message = "jobPostId không được để trống")
    private Long jobPostId;

    @NotNull(message = "Loại vi phạm không được để trống")
    private ComplaintType complaintType;

    @Size(max = 500, message = "Mô tả khiếu nại tối đa 500 ký tự")
    private String description;

    @Valid
    @Builder.Default
    private List<EvidenceItem> evidences = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenceItem {

        @NotBlank(message = "fileUrl không được để trống")
        private String fileUrl;

        @NotNull(message = "fileType không được để trống")
        private EvidenceFileType fileType;
    }
}
