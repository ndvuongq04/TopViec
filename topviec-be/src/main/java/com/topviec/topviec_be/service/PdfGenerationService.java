package com.topviec.topviec_be.service;

public interface PdfGenerationService {

    byte[] generatePdfFromHtml(String htmlContent);
}
