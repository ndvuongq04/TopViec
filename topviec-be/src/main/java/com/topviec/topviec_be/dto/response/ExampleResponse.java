package com.topviec.topviec_be.dto.response;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleResponse {

    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
}
