package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleRequest {

    @NotBlank(message = "Name không được để trống")
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;
}
