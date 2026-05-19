package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.dto.response.ResOrderDTO;
import com.topviec.topviec_be.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/payment/vnpay")
@RequiredArgsConstructor
public class VnpayController {

    private final OrderService orderService;

    @Value("${vnpay.frontend-return-url}")
    private String frontendReturnUrl;

    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> handapileIpn(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(orderService.handleVnpayIpn(params));
    }

    @GetMapping("/return")
    public ResponseEntity<Void> handleReturn(@RequestParam Map<String, String> params) {
        try {
            ResOrderDTO order = orderService.handleVnpayReturn(params);
            URI redirect = ServletUriComponentsBuilder.fromUriString(frontendReturnUrl)
                    .queryParam("orderId", order.getId())
                    .queryParam("orderCode", order.getOrderCode())
                    .queryParam("status", order.getStatus().getValue())
                    .queryParam("responseCode", params.get("vnp_ResponseCode"))
                    .build()
                    .toUri();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirect.toString())
                    .build();
        } catch (Exception ex) {
            URI redirect = ServletUriComponentsBuilder.fromUriString(frontendReturnUrl)
                    .queryParam("status", "error")
                    .queryParam("message", ex.getMessage())
                    .queryParam("responseCode", params.get("vnp_ResponseCode"))
                    .build()
                    .toUri();
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirect.toString())
                    .build();
        }
    }
}
