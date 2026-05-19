package com.topviec.topviec_be.controller;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.dto.request.ReqCreateOrderDTO;
import com.topviec.topviec_be.dto.request.ReqRefundOrderDTO;
import com.topviec.topviec_be.dto.response.ResOrderDTO;
import com.topviec.topviec_be.dto.response.ResPaymentUrlDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.logging.LogActionType;
import com.topviec.topviec_be.enums.services.OrderStatus;
import com.topviec.topviec_be.enums.services.OrderType;
import com.topviec.topviec_be.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employer/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerOrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:invoice')")
    public ResponseEntity<ResultPaginationDTO> getMyOrders(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderType type,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getMyOrders(
                extractUserId(jwt), search, type, status, dateFilter, startDate, endDate, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:invoice')")
    public ResponseEntity<ResOrderDTO> getOrderById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getMyOrderById(extractUserId(jwt), id));
    }

    @PostMapping
    @LogAction(LogActionType.CREATE_ORDER)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:purchase')")
    public ResponseEntity<ResOrderDTO> createOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReqCreateOrderDTO request,
            HttpServletRequest httpRequest) {
        ResOrderDTO order = orderService.createOrder(extractUserId(jwt), request);
        if (Boolean.TRUE.equals(request.getPayNow())) {
            ResPaymentUrlDTO payment = orderService.getPaymentUrl(extractUserId(jwt), order.getId(), httpRequest);
            order.setPaymentUrl(payment.getPaymentUrl());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:purchase')")
    public ResponseEntity<ResPaymentUrlDTO> getPaymentUrl(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            HttpServletRequest request) {
        return ResponseEntity.ok(orderService.getPaymentUrl(extractUserId(jwt), id, request));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:invoice')")
    public ResponseEntity<ResOrderDTO> refundOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody ReqRefundOrderDTO request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(orderService.requestRefund(
                extractUserId(jwt), id, request.getReason(), httpRequest));
    }

    @PatchMapping("/{id}/cancel")
    @LogAction(LogActionType.CANCEL_ORDER)
    @PreAuthorize("@companyPerm.hasPermission(authentication, 'service:invoice')")
    public ResponseEntity<ResOrderDTO> cancelOrder(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(extractUserId(jwt), id));
    }

    private Long extractUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
