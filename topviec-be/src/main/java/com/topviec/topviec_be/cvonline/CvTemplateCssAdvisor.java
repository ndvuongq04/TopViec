package com.topviec.topviec_be.cvonline;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class CvTemplateCssAdvisor {

    public List<String> analyze(String cssContent) {
        List<String> warnings = new ArrayList<>();
        if (cssContent == null || cssContent.isBlank()) {
            warnings.add("CSS content không được để trống.");
            return warnings;
        }

        String normalized = cssContent.toLowerCase(Locale.ROOT);
        if (normalized.contains("display:flex")) {
            warnings.add("Template đang dùng `display:flex`; PDF có thể lệch layout.");
        }
        if (normalized.contains("display: grid") || normalized.contains("display:grid")) {
            warnings.add("Template đang dùng `display:grid`; PDF có thể không ổn định.");
        }
        if (normalized.contains("position:sticky") || normalized.contains("position: sticky")) {
            warnings.add("Template đang dùng `position: sticky`; PDF có thể bỏ qua thuộc tính này.");
        }
        if (normalized.contains("position:fixed") || normalized.contains("position: fixed")) {
            warnings.add("Template đang dùng `position: fixed`; PDF có thể hiển thị khác preview.");
        }
        if (normalized.contains("transform:")) {
            warnings.add("Template đang dùng `transform`; PDF có thể render không đúng như trình duyệt.");
        }
        if (normalized.contains("filter:")) {
            warnings.add("Template đang dùng `filter`; PDF có thể không hỗ trợ đầy đủ.");
        }
        return warnings;
    }
}
