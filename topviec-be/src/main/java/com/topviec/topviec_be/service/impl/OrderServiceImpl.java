package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqCreateOrderDTO;
import com.topviec.topviec_be.dto.request.ReqUpdateOrderStatusDTO;
import com.topviec.topviec_be.dto.response.ResAdminOrderStatisticsDTO;
import com.topviec.topviec_be.dto.response.ResCompanyDTO;
import com.topviec.topviec_be.dto.response.ResOrderDTO;
import com.topviec.topviec_be.dto.response.ResOrderItemDTO;
import com.topviec.topviec_be.dto.response.ResPaymentUrlDTO;
import com.topviec.topviec_be.dto.response.ResServicePackageDetailDTO;
import com.topviec.topviec_be.dto.response.ResultPaginationDTO;
import com.topviec.topviec_be.entity.AddonService;
import com.topviec.topviec_be.entity.CompanyAddon;
import com.topviec.topviec_be.entity.CompanySubscription;
import com.topviec.topviec_be.entity.Order;
import com.topviec.topviec_be.entity.OrderItem;
import com.topviec.topviec_be.entity.ServicePackage;
import com.topviec.topviec_be.entity.ServicePackageDetail;
import com.topviec.topviec_be.entity.Services;
import com.topviec.topviec_be.entity.SubscriptionUsage;
import com.topviec.topviec_be.enums.services.BillingCycle;
import com.topviec.topviec_be.enums.services.OrderItemType;
import com.topviec.topviec_be.enums.services.OrderStatus;
import com.topviec.topviec_be.enums.services.OrderType;
import com.topviec.topviec_be.enums.services.PaymentMethod;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.AddonServiceRepository;
import com.topviec.topviec_be.repository.CompanyAddonRepository;
import com.topviec.topviec_be.repository.CompanySubscriptionRepository;
import com.topviec.topviec_be.repository.OrderItemRepository;
import com.topviec.topviec_be.repository.OrderRepository;
import com.topviec.topviec_be.repository.ServicePackageDetailRepository;
import com.topviec.topviec_be.repository.ServicePackageRepository;
import com.topviec.topviec_be.repository.ServiceRepository;
import com.topviec.topviec_be.repository.SubscriptionUsageRepository;
import com.topviec.topviec_be.service.CompanyService;
import com.topviec.topviec_be.service.OrderService;
import com.topviec.topviec_be.specification.OrderSpecification;
import com.topviec.topviec_be.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final ServicePackageDetailRepository servicePackageDetailRepository;
    private final ServiceRepository serviceRepository;
    private final AddonServiceRepository addonServiceRepository;
    private final CompanyService companyService;
    private final CompanySubscriptionRepository companySubscriptionRepository;
    private final SubscriptionUsageRepository subscriptionUsageRepository;
    private final CompanyAddonRepository companyAddonRepository;
    private final VnpayUtil vnpayUtil;

    @Override
    @Transactional
    public ResOrderDTO createOrder(Long userId, ReqCreateOrderDTO request) {
        Long companyId = getCompanyId(userId);
        if (Boolean.TRUE.equals(request.getPayNow()) && request.getPaymentMethod() != PaymentMethod.VNPAY) {
            throw AppException.badRequest("He thong hien chi ho tro thanh toan ngay qua VNPay.");
        }

        List<NormalizedOrderItem> normalizedItems = normalizeOrderItems(request);
        BigDecimal totalAmount = normalizedItems.stream()
                .map(NormalizedOrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .companyId(companyId)
                .orderCode("ORD-" + System.currentTimeMillis())
                .type(request.getType())
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .createdBy(userId)
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> savedItems = orderItemRepository.saveAll(normalizedItems.stream()
                .map(item -> OrderItem.builder()
                        .orderId(savedOrder.getId())
                        .itemType(item.itemType())
                        .servicePackageId(item.servicePackage() != null ? item.servicePackage().getId() : null)
                        .addonServiceId(item.addonService() != null ? item.addonService().getId() : null)
                        .quantity(item.quantity())
                        .unitPrice(item.unitPrice())
                        .totalPrice(item.totalPrice())
                        .billingCycle(item.servicePackage() != null ? item.servicePackage().getBillingCycle() : null)
                        .durationDays(item.addonService() != null ? item.addonService().getDurationDays() : null)
                        .build())
                .collect(Collectors.toList()));
        savedOrder.setOrderItems(savedItems);

        return mapToDTO(savedOrder);
    }

    @Override
    @Transactional
    public ResPaymentUrlDTO getPaymentUrl(Long userId, Long orderId, HttpServletRequest request) {
        Order order = getOwnedOrder(userId, orderId);
        validateVnpayOrder(order);

        if (order.getStatus() == OrderStatus.FAILED) {
            order.setStatus(OrderStatus.PENDING);
            orderRepository.save(order);
        }

        String paymentUrl = vnpayUtil.buildPaymentUrl(
                order.getOrderCode(),
                order.getTotalAmount(),
                vnpayUtil.getClientIp(request),
                buildReturnUrl(request));

        return ResPaymentUrlDTO.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    @Transactional
    public Map<String, String> handleVnpayIpn(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return ipnResponse("99", "Invalid request");
        }
        if (!vnpayUtil.verifySignature(params)) {
            return ipnResponse("97", "Invalid signature");
        }

        Order order = orderRepository.findByOrderCode(params.get("vnp_TxnRef")).orElse(null);
        if (order == null) {
            return ipnResponse("01", "Order not found");
        }
        if (!isValidVnpayAmount(order, params.get("vnp_Amount"))) {
            return ipnResponse("04", "Invalid amount");
        }
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.REFUNDED) {
            return ipnResponse("02", "Order already confirmed");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return ipnResponse("02", "Order already cancelled");
        }

        applyPaymentResult(order, params);
        return ipnResponse("00", "Confirm Success");
    }

    @Override
    @Transactional
    public ResOrderDTO handleVnpayReturn(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            throw AppException.badRequest("Khong nhan duoc du lieu tra ve tu VNPay.");
        }
        if (!vnpayUtil.verifySignature(params)) {
            throw AppException.badRequest("Chu ky VNPay khong hop le.");
        }

        Order order = orderRepository.findByOrderCode(params.get("vnp_TxnRef"))
                .orElseThrow(() -> AppException.notFound("Khong tim thay don hang."));

        if (!isValidVnpayAmount(order, params.get("vnp_Amount"))) {
            throw AppException.badRequest("So tien thanh toan khong khop voi don hang.");
        }

        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.FAILED) {
            applyPaymentResult(order, params);
        }

        return mapToDTO(orderRepository.findById(order.getId()).orElse(order));
    }

    @Override
    @Transactional
    public ResOrderDTO requestRefund(Long userId, Long orderId, String reason, HttpServletRequest request) {
        Order order = getOwnedOrder(userId, orderId);
        validateRefundableOrder(order);

        CompanySubscription subscription = companySubscriptionRepository.findByOrderId(order.getId())
                .orElseThrow(() -> AppException.notFound("Khong tim thay goi dich vu can thu hoi."));
        List<SubscriptionUsage> usages = subscriptionUsageRepository.findByCompanySubscriptionId(subscription.getId());

        boolean hasUsage = usages.stream().anyMatch(usage -> usage.getQuantityRemaining() == null
                || usage.getQuantityTotal() == null
                || !usage.getQuantityRemaining().equals(usage.getQuantityTotal()));
        if (hasUsage) {
            throw AppException.badRequest("Goi dich vu da duoc su dung, khong du dieu kien hoan tien.");
        }

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> refundResponse = vnpayUtil.refund(
                order.getOrderCode(),
                order.getVnpayTransactionNo(),
                order.getTotalAmount(),
                order.getVnpayPayDate(),
                "user-" + userId,
                vnpayUtil.getClientIp(request),
                "Refund order " + order.getOrderCode(),
                "02");

        String responseCode = stringValue(refundResponse.get("vnp_ResponseCode"));
        if (!"00".equals(responseCode)) {
            order.setStatus(OrderStatus.REFUND_REJECTED);
            order.setRefundReason(reason);
            order.setRefundRequestedAt(now);
            order.setRefundVnpayResponseCode(responseCode);
            orderRepository.save(order);
            throw AppException.badRequest("VNPay tu choi yeu cau hoan tien.");
        }

        order.setStatus(OrderStatus.REFUNDED);
        order.setRefundAmount(order.getTotalAmount());
        order.setRefundReason(reason);
        order.setRefundRequestedAt(now);
        order.setRefundApprovedAt(now);
        order.setRefundVnpayResponseCode(responseCode);
        order.setRefundVnpayTransactionNo(stringValue(refundResponse.get("vnp_TransactionNo")));
        orderRepository.save(order);

        subscription.setStatus(SubscriptionStatus.REVOKED);
        subscription.setExpiredAt(now);
        companySubscriptionRepository.save(subscription);

        for (SubscriptionUsage usage : usages) {
            usage.setQuantityTotal(0);
            usage.setQuantityRemaining(0);
            usage.setResetAt(now);
        }
        subscriptionUsageRepository.saveAll(usages);

        return mapToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getMyOrders(
            Long userId,
            String keyword, OrderType type, OrderStatus status,
            String dateFilter, String startDate, String endDate,
            Pageable pageable) {
        Long companyId = getCompanyId(userId);

        LocalDateTime startDt = parseDateFilter(dateFilter, startDate, true);
        LocalDateTime endDt = parseDateFilter(dateFilter, endDate, false);

        Specification<Order> spec = OrderSpecification.withFilter(keyword, type, status, startDt, endDt)
                .and(OrderSpecification.hasCompanyId(companyId));

        Page<Order> page = orderRepository.findAll(spec, pageable);
        return buildPaginationResult(page, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResOrderDTO getMyOrderById(Long userId, Long orderId) {
        return mapToDTO(getOwnedOrder(userId, orderId));
    }

    @Override
    @Transactional
    public ResOrderDTO cancelOrder(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.FAILED) {
            throw AppException.badRequest("Chi co the huy don hang dang cho thanh toan.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return mapToDTO(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getAllOrders(
            String keyword, OrderType type, OrderStatus status,
            String dateFilter, String startDate, String endDate,
            Pageable pageable) {

        LocalDateTime startDt = parseDateFilter(dateFilter, startDate, true);
        LocalDateTime endDt = parseDateFilter(dateFilter, endDate, false);

        Specification<Order> spec = OrderSpecification.withFilter(keyword, type, status, startDt, endDt);
        Page<Order> page = orderRepository.findAll(spec, pageable);
        return buildPaginationResult(page, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ResOrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> AppException.notFound("Khong tim thay don hang."));
        return mapToDTO(order);
    }

    @Override
    @Transactional
    public ResOrderDTO updateOrderStatus(Long adminId, Long orderId, ReqUpdateOrderStatusDTO request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> AppException.notFound("Khong tim thay don hang."));

        if (request.getStatus() == OrderStatus.PAID && order.getStatus() != OrderStatus.PAID) {
            order.setPaidAt(LocalDateTime.now());
        }

        order.setStatus(request.getStatus());
        return mapToDTO(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public ResAdminOrderStatisticsDTO getOrderStatistics() {
        long totalOrders = orderRepository.count();
        long paidOrders = orderRepository.countByStatus(OrderStatus.PAID);
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        BigDecimal totalRevenue = orderRepository.sumTotalAmountByStatus(OrderStatus.PAID);

        return ResAdminOrderStatisticsDTO.builder()
                .totalOrders(totalOrders)
                .paidOrders(paidOrders)
                .pendingOrders(pendingOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResultPaginationDTO getOrdersByCompanyId(Long companyId, Pageable pageable) {
        Page<Order> page = orderRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
        return buildPaginationResult(page, pageable);
    }

    private List<NormalizedOrderItem> normalizeOrderItems(ReqCreateOrderDTO request) {
        List<ReqCreateOrderDTO.Item> requestItems = new ArrayList<>();
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            requestItems.addAll(request.getItems());
        }

        if (requestItems.isEmpty() && request.getPackageId() != null) {
            ReqCreateOrderDTO.Item legacyItem = new ReqCreateOrderDTO.Item();
            legacyItem.setPackageId(request.getPackageId());
            legacyItem.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
            requestItems.add(legacyItem);
        }

        if (requestItems.isEmpty()) {
            throw AppException.badRequest("Don hang phai co it nhat 1 san pham.");
        }

        if (request.getType() == OrderType.SUBSCRIPTION && requestItems.size() > 1) {
            throw AppException.badRequest("Don hang goi dich vu chi duoc mua 1 goi moi lan.");
        }

        List<NormalizedOrderItem> normalizedItems = new ArrayList<>();
        for (ReqCreateOrderDTO.Item requestItem : requestItems) {
            if (requestItem.getPackageId() == null) {
                throw AppException.badRequest("ID goi khong duoc de trong.");
            }
            if (requestItem.getQuantity() == null || requestItem.getQuantity() < 1) {
                throw AppException.badRequest("So luong toi thieu la 1.");
            }

            if (request.getType() == OrderType.SUBSCRIPTION) {
                ServicePackage servicePackage = servicePackageRepository.findById(requestItem.getPackageId())
                        .orElseThrow(() -> AppException.notFound("Khong tim thay goi dich vu."));
                if (servicePackage.getIsActive() == null || !servicePackage.getIsActive()) {
                    throw AppException.badRequest("Goi dich vu nay khong con hoat dong.");
                }
                BigDecimal unitPrice = servicePackage.getPrice();
                normalizedItems.add(new NormalizedOrderItem(
                        OrderItemType.SUBSCRIPTION,
                        requestItem.getQuantity(),
                        unitPrice,
                        unitPrice.multiply(BigDecimal.valueOf(requestItem.getQuantity())),
                        servicePackage,
                        null));
                continue;
            }

            AddonService addonService = addonServiceRepository.findById(requestItem.getPackageId())
                    .orElseThrow(() -> AppException.notFound("Khong tim thay dich vu le."));
            if (addonService.getIsActive() == null || !addonService.getIsActive()) {
                throw AppException.badRequest("Dich vu le nay khong con hoat dong.");
            }
            BigDecimal unitPrice = addonService.getPrice();
            normalizedItems.add(new NormalizedOrderItem(
                    OrderItemType.ADDON,
                    requestItem.getQuantity(),
                    unitPrice,
                    unitPrice.multiply(BigDecimal.valueOf(requestItem.getQuantity())),
                    null,
                    addonService));
        }

        return normalizedItems;
    }

    private void applyPaymentResult(Order order, Map<String, String> params) {
        boolean successful = "00".equals(params.get("vnp_ResponseCode"))
                && ("00".equals(params.get("vnp_TransactionStatus"))
                        || params.get("vnp_TransactionStatus") == null
                        || params.get("vnp_TransactionStatus").isBlank());

        order.setVnpayResponseCode(params.get("vnp_ResponseCode"));
        order.setVnpayTransactionNo(params.get("vnp_TransactionNo"));
        order.setVnpayPayDate(params.get("vnp_PayDate"));
        order.setPaymentTransactionId(params.get("vnp_TransactionNo"));

        if (successful) {
            order.setStatus(OrderStatus.PAID);
            if (order.getPaidAt() == null) {
                order.setPaidAt(LocalDateTime.now());
            }
            activateOrder(order);
        } else {
            order.setStatus(OrderStatus.FAILED);
        }
        orderRepository.save(order);
    }

    private void activateOrder(Order order) {
        List<OrderItem> items = order.getOrderItems();
        if (items == null || items.isEmpty()) {
            items = orderItemRepository.findByOrderId(order.getId());
            order.setOrderItems(items);
        }

        for (OrderItem item : items) {
            if (item.getItemType() == OrderItemType.SUBSCRIPTION && item.getServicePackageId() != null) {
                boolean alreadyActivated = companySubscriptionRepository.findByOrderId(order.getId()).isPresent();
                if (!alreadyActivated) {
                    ServicePackage servicePackage = servicePackageRepository.findById(item.getServicePackageId())
                            .orElseThrow(() -> AppException.notFound("Khong tim thay goi dich vu."));
                    activateSubscription(order.getCompanyId(), order.getId(), servicePackage);
                }
            }
            if (item.getItemType() == OrderItemType.ADDON && item.getAddonServiceId() != null) {
                boolean alreadyActivated = companyAddonRepository
                        .findByOrderIdAndAddonServiceId(order.getId(), item.getAddonServiceId()).isPresent();
                if (!alreadyActivated) {
                    AddonService addonService = addonServiceRepository.findById(item.getAddonServiceId())
                            .orElseThrow(() -> AppException.notFound("Khong tim thay dich vu le."));
                    activateAddon(order.getCompanyId(), order.getId(), addonService, item.getQuantity());
                }
            }
        }
    }

    private void activateSubscription(Long companyId, Long orderId, ServicePackage servicePackage) {
        LocalDateTime startedAt = LocalDateTime.now();
        CompanySubscription subscription = CompanySubscription.builder()
                .companyId(companyId)
                .servicePackageId(servicePackage.getId())
                .orderId(orderId)
                .status(SubscriptionStatus.ACTIVE)
                .billingCycle(servicePackage.getBillingCycle())
                .startedAt(startedAt)
                .expiredAt(servicePackage.getBillingCycle() == BillingCycle.MONTHLY
                        ? startedAt.plusMonths(1)
                        : startedAt.plusYears(1))
                .build();

        CompanySubscription savedSubscription = companySubscriptionRepository.save(subscription);
        List<ServicePackageDetail> details = servicePackageDetailRepository
                .findByServicePackageId(servicePackage.getId());

        for (ServicePackageDetail detail : details) {
            Services service = serviceRepository.findById(detail.getServiceId()).orElse(null);
            if (service == null) {
                continue;
            }

            subscriptionUsageRepository.save(SubscriptionUsage.builder()
                    .companySubscriptionId(savedSubscription.getId())
                    .companyId(companyId)
                    .featureCode(service.getCode())
                    .quantityTotal(detail.getQuantity())
                    .quantityRemaining(detail.getQuantity())
                    .resetAt(savedSubscription.getExpiredAt())
                    .build());
        }
    }

    private void activateAddon(Long companyId, Long orderId, AddonService addonService, int quantity) {
        LocalDateTime startedAt = LocalDateTime.now();
        companyAddonRepository.save(CompanyAddon.builder()
                .companyId(companyId)
                .addonServiceId(addonService.getId())
                .orderId(orderId)
                .status(SubscriptionStatus.ACTIVE)
                .quantityTotal(quantity * addonService.getQuantity())
                .quantityRemaining(quantity * addonService.getQuantity())
                .startedAt(startedAt)
                .expiredAt(addonService.getDurationDays() != null ? startedAt.plusDays(addonService.getDurationDays())
                        : null)
                .build());
    }

    private void validateVnpayOrder(Order order) {
        if (order.getPaymentMethod() != PaymentMethod.VNPAY) {
            throw AppException.badRequest("He thong hien chi ho tro tao payment URL cho VNPay.");
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.FAILED) {
            throw AppException.badRequest("Don hang nay khong o trang thai cho thanh toan.");
        }
    }

    private void validateRefundableOrder(Order order) {
        if (order.getType() != OrderType.SUBSCRIPTION) {
            throw AppException.badRequest("Chi goi dich vu moi duoc hoan tien.");
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw AppException.badRequest("Chi don hang da thanh toan moi duoc hoan tien.");
        }
        if (order.getPaidAt() == null || order.getPaidAt().isBefore(LocalDateTime.now().minusDays(7))) {
            throw AppException.badRequest("Don hang da qua thoi han hoan tien 7 ngay.");
        }
        if (order.getVnpayTransactionNo() == null || order.getVnpayPayDate() == null) {
            throw AppException.badRequest("Don hang khong du thong tin giao dich VNPay de hoan tien.");
        }
    }

    private boolean isRefundEligible(Order order) {
        if (order.getType() != OrderType.SUBSCRIPTION || order.getStatus() != OrderStatus.PAID
                || order.getPaidAt() == null) {
            return false;
        }
        if (order.getPaidAt().isBefore(LocalDateTime.now().minusDays(7))) {
            return false;
        }

        CompanySubscription subscription = companySubscriptionRepository.findByOrderId(order.getId()).orElse(null);
        if (subscription == null) {
            return false;
        }

        List<SubscriptionUsage> usages = subscriptionUsageRepository.findByCompanySubscriptionId(subscription.getId());
        return usages.stream().allMatch(usage -> usage.getQuantityTotal() != null
                && usage.getQuantityRemaining() != null
                && usage.getQuantityTotal().equals(usage.getQuantityRemaining()));
    }

    private boolean isValidVnpayAmount(Order order, String rawAmount) {
        try {
            long vnpayAmount = Long.parseLong(rawAmount);
            long expected = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();
            return vnpayAmount == expected;
        } catch (Exception e) {
            return false;
        }
    }

    private String buildReturnUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(request.getContextPath() + "/payment/vnpay/return")
                .replaceQuery(null)
                .build()
                .toUriString();
    }

    private Order getOwnedOrder(Long userId, Long orderId) {
        Long companyId = getCompanyId(userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> AppException.notFound("Khong tim thay don hang."));
        if (!order.getCompanyId().equals(companyId)) {
            throw AppException.forbidden("Ban khong co quyen thao tac tren don hang nay.");
        }
        return order;
    }

    private Long getCompanyId(Long userId) {
        Long companyId = companyService.getCompanyIdByUserId(userId);
        if (companyId == null) {
            throw AppException.badRequest("Chua co ho so cong ty.");
        }
        return companyId;
    }

    private Map<String, String> ipnResponse(String code, String message) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("RspCode", code);
        response.put("Message", message);
        return response;
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private LocalDateTime parseDateFilter(String dateFilter, String rawDate, boolean isStart) {
        if (dateFilter != null && !dateFilter.isBlank()) {
            LocalDateTime now = LocalDateTime.now();
            switch (dateFilter.toLowerCase()) {
                case "today":
                    return isStart ? now.toLocalDate().atStartOfDay() : now.toLocalDate().atTime(23, 59, 59);
                case "last7days":
                    return isStart ? now.minusDays(7).toLocalDate().atStartOfDay()
                            : now.toLocalDate().atTime(23, 59, 59);
                case "thismonth":
                    return isStart ? now.withDayOfMonth(1).toLocalDate().atStartOfDay()
                            : now.toLocalDate().atTime(23, 59, 59);
                default:
                    break;
            }
        }
        if (rawDate != null && !rawDate.isBlank()) {
            try {
                return LocalDateTime.parse(rawDate);
            } catch (Exception e) {
                try {
                    return isStart
                            ? java.time.LocalDate.parse(rawDate).atStartOfDay()
                            : java.time.LocalDate.parse(rawDate).atTime(23, 59, 59);
                } catch (Exception ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private ResultPaginationDTO buildPaginationResult(Page<Order> page, Pageable pageable) {
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        List<ResOrderDTO> results = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO response = new ResultPaginationDTO();
        response.setMeta(meta);
        response.setResult(results);
        return response;
    }

    private ResOrderDTO mapToDTO(Order entity) {
        List<ResOrderItemDTO> itemDTOs = new ArrayList<>();
        if (entity.getOrderItems() != null) {
            itemDTOs = entity.getOrderItems().stream().map(item -> {
                String packageName = null;
                List<ResServicePackageDetailDTO> detailDTOs = new ArrayList<>();

                if (item.getServicePackageId() != null) {
                    ServicePackage servicePackage = item.getServicePackage();
                    if (servicePackage == null) {
                        servicePackage = servicePackageRepository.findById(item.getServicePackageId()).orElse(null);
                    }
                    if (servicePackage != null) {
                        packageName = servicePackage.getName();
                        List<ServicePackageDetail> details = servicePackage.getDetails();
                        if (details == null || details.isEmpty()) {
                            details = servicePackageDetailRepository.findByServicePackageId(servicePackage.getId());
                        }
                        detailDTOs = details.stream().map(detail -> {
                            Services service = serviceRepository.findById(detail.getServiceId()).orElse(null);
                            return ResServicePackageDetailDTO.builder()
                                    .id(detail.getId())
                                    .serviceId(detail.getServiceId())
                                    .serviceCode(service != null ? service.getCode() : null)
                                    .serviceName(service != null ? service.getName() : null)
                                    .serviceCategory(service != null ? service.getCategory() : null)
                                    .serviceCategoryName(service != null && service.getCategory() != null
                                            ? service.getCategory().getValue()
                                            : null)
                                    .serviceUnit(service != null ? service.getUnit() : null)
                                    .quantity(detail.getQuantity())
                                    .build();
                        }).collect(Collectors.toList());
                    }
                }

                if (packageName == null && item.getAddonServiceId() != null) {
                    AddonService addon = item.getAddonService();
                    if (addon == null) {
                        addon = addonServiceRepository.findById(item.getAddonServiceId()).orElse(null);
                    }
                    if (addon != null) {
                        packageName = addon.getName();
                    }
                }

                return ResOrderItemDTO.builder()
                        .id(item.getId())
                        .itemType(item.getItemType())
                        .servicePackageId(item.getServicePackageId())
                        .addonServiceId(item.getAddonServiceId())
                        .packageName(packageName)
                        .details(detailDTOs)
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .billingCycle(item.getBillingCycle())
                        .durationDays(item.getDurationDays())
                        .build();
            }).collect(Collectors.toList());
        }

        ResOrderDTO.CompanyInfo companyInfo = null;
        if (entity.getCompany() != null) {
            companyInfo = ResOrderDTO.CompanyInfo.builder()
                    .name(entity.getCompany().getName())
                    .logoUrl(entity.getCompany().getLogoUrl())
                    .email(entity.getCompany().getEmail())
                    .phone(entity.getCompany().getPhone())
                    .build();
        } else if (entity.getCompanyId() != null) {
            try {
                ResCompanyDTO dto = companyService.getById(entity.getCompanyId());
                companyInfo = ResOrderDTO.CompanyInfo.builder()
                        .name(dto.getName())
                        .logoUrl(dto.getLogoUrl())
                        .email(dto.getEmail())
                        .phone(dto.getPhone())
                        .build();
            } catch (Exception ignored) {
                log.debug("Company info not available for order {}", entity.getId());
            }
        }

        return ResOrderDTO.builder()
                .id(entity.getId())
                .orderCode(entity.getOrderCode())
                .type(entity.getType())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .paymentTransactionId(entity.getPaymentTransactionId())
                .vnpayTransactionNo(entity.getVnpayTransactionNo())
                .vnpayResponseCode(entity.getVnpayResponseCode())
                .paidAt(entity.getPaidAt())
                .refundEligible(isRefundEligible(entity))
                .refundReason(entity.getRefundReason())
                .refundRequestedAt(entity.getRefundRequestedAt())
                .refundApprovedAt(entity.getRefundApprovedAt())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .items(itemDTOs)
                .company(companyInfo)
                .build();
    }

    private record NormalizedOrderItem(
            OrderItemType itemType,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice,
            ServicePackage servicePackage,
            AddonService addonService) {
    }
}
