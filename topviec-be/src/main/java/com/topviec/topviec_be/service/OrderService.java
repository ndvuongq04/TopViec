package com.topviec.topviec_be.service;

import com.topviec.topviec_be.dto.request.ReqCreateOrderDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateOrderStatusDTO;
import com.topviec.topviec_be.dto.response.ResAdminOrderStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResOrderDTO;
import com.topviec.topviec_be.dto.response.ResPaymentUrlDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.enums.services.OrderStatus;
import com.topviec.topviec_be.enums.services.OrderType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface OrderService {
        ResOrderDTO createOrder(Long userId, ReqCreateOrderDTO request);

        ResPaymentUrlDTO getPaymentUrl(Long userId, Long orderId, HttpServletRequest request);

        Map<String, String> handleVnpayIpn(Map<String, String> params);

        ResOrderDTO handleVnpayReturn(Map<String, String> params);

        ResOrderDTO requestRefund(Long userId, Long orderId, String reason, HttpServletRequest request);

        ResultPaginationDTO getMyOrders(
                        Long userId,
                        String keyword, OrderType type, OrderStatus status,
                        String dateFilter, String startDate, String endDate,
                        Pageable pageable);

        ResOrderDTO getMyOrderById(Long userId, Long orderId);

        ResOrderDTO cancelOrder(Long userId, Long orderId);

        ResultPaginationDTO getAllOrders(
                        String keyword, OrderType type, OrderStatus status,
                        String dateFilter, String startDate, String endDate,
                        Pageable pageable);

        ResOrderDTO getOrderById(Long orderId);

        ResOrderDTO updateOrderStatus(Long adminId, Long orderId, ReqUpdateOrderStatusDTO request);

        ResAdminOrderStatisticsDTO getOrderStatistics();

        ResultPaginationDTO getOrdersByCompanyId(Long companyId, Pageable pageable);
}
