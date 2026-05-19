package com.topviec.topviec_be.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateCvTemplateDTO {

    @NotBlank(message = "Ten template khong duoc de trong")
    @Size(max = 255, message = "Ten template toi da 255 ky tu")
    private String name;

    @NotBlank(message = "Slug khong duoc de trong")
    @Size(max = 100, message = "Slug toi da 100 ky tu")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug chi duoc gom chu thuong, so va dau gach ngang")
    private String slug;

    @Size(max = 2000, message = "Mo ta toi da 2000 ky tu")
    private String description;

    @Size(max = 512, message = "Thumbnail URL toi da 512 ky tu")
    private String thumbnailUrl;
}
