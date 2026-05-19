package com.topviec.topviec_be.cvonline;

import com.topviec.topviec_be.dto.cvonline.CvOnlineExtraDataDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CvOnlineTemplateRenderer {

    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "\\{\\{#([a-zA-Z][a-zA-Z0-9]*)\\}\\}(.*?)\\{\\{/\\1\\}\\}",
            Pattern.DOTALL);
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{\\s*([a-zA-Z][a-zA-Z0-9]*)\\s*\\}\\}");

    private final CvOnlineRenderModelMapper renderModelMapper;

    public CvOnlineTemplateRenderer(CvOnlineRenderModelMapper renderModelMapper) {
        this.renderModelMapper = renderModelMapper;
    }

    public String renderToXhtml(String htmlContent, String cssContent, CvOnlineExtraDataDTO extraData) {
        String renderedHtml = renderHtml(htmlContent, extraData);
        return normalizeToDocument(renderedHtml, cssContent, Document.OutputSettings.Syntax.xml);
    }

    public String renderToHtmlDocument(String htmlContent, String cssContent, CvOnlineExtraDataDTO extraData) {
        String renderedHtml = renderHtml(htmlContent, extraData);
        return normalizeToDocument(renderedHtml, cssContent, Document.OutputSettings.Syntax.html);
    }

    public String renderHtml(String htmlContent, CvOnlineExtraDataDTO extraData) {
        Map<String, Object> model = renderModelMapper.toRenderModel(extraData);
        return renderTemplate(htmlContent, model);
    }

    private String renderTemplate(String htmlContent, Map<String, Object> model) {
        String result = htmlContent == null ? "" : htmlContent;
        result = renderSections(result, model);
        return replaceRootPlaceholders(result, model);
    }

    private String renderSections(String template, Map<String, Object> model) {
        Matcher matcher = SECTION_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String sectionName = matcher.group(1);
            String blockTemplate = matcher.group(2);
            String replacement = renderSectionBlock(sectionName, blockTemplate, model);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    @SuppressWarnings("unchecked")
    private String renderSectionBlock(String sectionName, String blockTemplate, Map<String, Object> model) {
        Object sectionValue = model.get(sectionName);
        if (!(sectionValue instanceof List<?> items)) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (Object item : items) {
            if (!(item instanceof Map<?, ?> rawMap)) {
                continue;
            }
            Map<String, Object> itemMap = (Map<String, Object>) rawMap;
            builder.append(replaceItemPlaceholders(blockTemplate, itemMap));
        }
        return builder.toString();
    }

    private String replaceItemPlaceholders(String template, Map<String, Object> itemMap) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = formatValue(itemMap.get(key));
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String replaceRootPlaceholders(String template, Map<String, Object> model) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = model.get(key);
            String replacement = value instanceof List<?> ? "" : formatValue(value);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String normalizeToDocument(
            String htmlContent,
            String cssContent,
            Document.OutputSettings.Syntax syntax) {
        Document document = Jsoup.parse(htmlContent == null ? "" : htmlContent);
        document.outputSettings()
                .syntax(syntax)
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset("UTF-8")
                .prettyPrint(false);
        ensureCharsetMeta(document);
        appendCss(document, cssContent);
        return document.outerHtml();
    }

    private void ensureCharsetMeta(Document document) {
        if (document.head().selectFirst("meta[charset]") == null) {
            document.head().prependElement("meta").attr("charset", "UTF-8");
        }
    }

    private void appendCss(Document document, String cssContent) {
        Element style = document.head().appendElement("style").attr("type", "text/css");
        style.appendChild(new DataNode(cssContent == null ? "" : cssContent));
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        String safe = HtmlUtils.htmlEscape(String.valueOf(value));
        return safe.replace("\r\n", "\n").replace("\n", "<br/>");
    }
}
