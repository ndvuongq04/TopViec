package com.topviec.topviec_be.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.service.PdfGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    private static final List<String> FONT_CANDIDATES = List.of(
            "C:/Windows/Fonts/arial.ttf",
            "C:/Windows/Fonts/tahoma.ttf",
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf");

    @Override
    public byte[] generatePdfFromHtml(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(outputStream);
            registerFonts(builder);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception ex) {
            log.error("Khong the generate PDF tu HTML", ex);
            throw AppException.badRequest("Khong the render PDF tu template CV");
        }
    }

    private void registerFonts(PdfRendererBuilder builder) throws IOException {
        boolean fontRegistered = false;
        for (String candidate : FONT_CANDIDATES) {
            Path path = Path.of(candidate);
            if (!Files.exists(path)) {
                continue;
            }
            builder.useFont(path.toFile(), "CVUnicodeFallback");
            fontRegistered = true;
            break;
        }

        if (!fontRegistered) {
            log.warn("Khong tim thay font Unicode fallback cho PDF, ky tu tieng Viet co the bi lech");
        }
    }
}
