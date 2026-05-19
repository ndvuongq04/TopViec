package com.topviec.topviec_be.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Component
@Slf4j
public class VnpayUtil {

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.pay-url}")
    private String payUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    @Value("${vnpay.api-url}")
    private String apiUrl;

    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String buildPaymentUrl(String orderCode, BigDecimal amount, String ipAddress) {
        return buildPaymentUrl(orderCode, amount, ipAddress, returnUrl);
    }

    public String buildPaymentUrl(String orderCode, BigDecimal amount, String ipAddress, String callbackUrl) {
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(amount.multiply(BigDecimal.valueOf(100)).longValue()));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", orderCode);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + orderCode);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", callbackUrl);
        params.put("vnp_IpAddr", ipAddress);

        LocalDateTime now = LocalDateTime.now();
        params.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        params.put("vnp_ExpireDate", now.plusMinutes(15).format(VNPAY_DATE_FORMAT));

        String queryString = buildQueryString(params);
        String secureHash = hmacSha512(hashSecret, queryString);

        return payUrl + "?" + queryString + "&vnp_SecureHash=" + secureHash;
    }

    public boolean verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }

        Map<String, String> filtered = new TreeMap<>(params);
        filtered.remove("vnp_SecureHash");
        filtered.remove("vnp_SecureHashType");

        String queryString = buildQueryString(filtered);
        String computed = hmacSha512(hashSecret, queryString);
        return computed.equalsIgnoreCase(receivedHash);
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private String hmacSha512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA512 error", e);
        }
    }

    public Map<String, Object> refund(String txnRef, String transactionNo,
            BigDecimal amount, String transactionDate,
            String createdBy, String ipAddress,
            String orderInfo, String transactionType) {
        String requestId = UUID.randomUUID().toString().replace("-", "");
        String vnpVersion = "2.1.0";
        String vnpCommand = "refund";
        String vnpAmount = String.valueOf(amount.multiply(BigDecimal.valueOf(100)).longValue());
        String createDate = LocalDateTime.now().format(VNPAY_DATE_FORMAT);

        String hashData = String.join("|",
                requestId, vnpVersion, vnpCommand, tmnCode, transactionType,
                txnRef, vnpAmount, transactionNo, transactionDate, createdBy,
                createDate, ipAddress, orderInfo);
        String secureHash = hmacSha512(hashSecret, hashData);

        String jsonBody = String.format(
                "{" +
                        "\"vnp_RequestId\":\"%s\"," +
                        "\"vnp_Version\":\"%s\"," +
                        "\"vnp_Command\":\"%s\"," +
                        "\"vnp_TmnCode\":\"%s\"," +
                        "\"vnp_TransactionType\":\"%s\"," +
                        "\"vnp_TxnRef\":\"%s\"," +
                        "\"vnp_Amount\":%s," +
                        "\"vnp_OrderInfo\":\"%s\"," +
                        "\"vnp_TransactionNo\":\"%s\"," +
                        "\"vnp_TransactionDate\":\"%s\"," +
                        "\"vnp_CreateBy\":\"%s\"," +
                        "\"vnp_CreateDate\":\"%s\"," +
                        "\"vnp_IpAddr\":\"%s\"," +
                        "\"vnp_SecureHash\":\"%s\"" +
                        "}",
                requestId, vnpVersion, vnpCommand, tmnCode, transactionType,
                txnRef, vnpAmount, orderInfo, transactionNo, transactionDate,
                createdBy, createDate, ipAddress, secureHash);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseJsonResponse(response.body());
        } catch (Exception e) {
            log.error("VNPay refund error", e);
            throw new RuntimeException("Loi ket noi VNPay khi hoan tien: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> parseJsonResponse(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (Exception e) {
            log.error("Failed to parse VNPay response JSON: {}", json, e);
            return Map.of("vnp_ResponseCode", "99", "vnp_Message", "Parse error");
        }
    }
}
