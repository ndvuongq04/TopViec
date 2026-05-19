package com.topviec.topviec_be.service.impl;

import com.topviec.topviec_be.dto.request.ReqApplyAddonDTO;
import com.topviec.topviec_be.dto.request.ReqRenewSubscriptionDTO;
import com.topviec.topviec_be.dto.response.ResCompanyAddonDTO;
import com.topviec.topviec_be.dto.response.ResCompanyBrandingDTO;
import com.topviec.topviec_be.dto.response.ResCompanySubscriptionDTO;
import com.topviec.topviec_be.dto.response.ResCompanySubscriptionDTO.ResSubscriptionUsageDTO;
import com.topviec.topviec_be.dto.response.ResJobPostAddonDTO;
import com.topviec.topviec_be.dto.response.ResSubscriptionRenewalDTO;
import com.topviec.topviec_be.entity.AddonService;
import com.topviec.topviec_be.entity.CompanyAddon;
import com.topviec.topviec_be.entity.CompanySubscription;
import com.topviec.topviec_be.entity.JobPostAddon;
import com.topviec.topviec_be.entity.JobPosting;
import com.topviec.topviec_be.entity.Order;
import com.topviec.topviec_be.entity.OrderItem;
import com.topviec.topviec_be.entity.Services;
import com.topviec.topviec_be.entity.ServicePackage;
import com.topviec.topviec_be.entity.ServicePackageDetail;
import com.topviec.topviec_be.entity.SubscriptionRenewalLog;
import com.topviec.topviec_be.entity.SubscriptionUsage;
import com.topviec.topviec_be.enums.services.BillingCycle;
import com.topviec.topviec_be.enums.services.JobPostAddonStatus;
import com.topviec.topviec_be.enums.services.OrderItemType;
import com.topviec.topviec_be.enums.services.OrderStatus;
import com.topviec.topviec_be.enums.services.OrderType;
import com.topviec.topviec_be.enums.services.ServiceCategory;
import com.topviec.topviec_be.enums.services.SubscriptionStatus;
import com.topviec.topviec_be.exception.AppException;
import com.topviec.topviec_be.repository.AddonServiceRepository;
import com.topviec.topviec_be.repository.CompanyAddonRepository;
import com.topviec.topviec_be.repository.CompanySubscriptionRepository;
import com.topviec.topviec_be.repository.JobPostAddonRepository;
import com.topviec.topviec_be.repository.JobPostingRepository;
import com.topviec.topviec_be.repository.OrderRepository;
import com.topviec.topviec_be.repository.ServicePackageDetailRepository;
import com.topviec.topviec_be.repository.ServicePackageRepository;
import com.topviec.topviec_be.repository.ServiceRepository;
import com.topviec.topviec_be.repository.SubscriptionRenewalLogRepository;
import com.topviec.topviec_be.repository.SubscriptionUsageRepository;
import com.topviec.topviec_be.service.CompanyService;
import com.topviec.topviec_be.service.EmployerServiceManagementService;
import com.topviec.topviec_be.service.activation.BrandingActivationService;
import com.topviec.topviec_be.service.activation.ServiceActivationRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class EmployerServiceManagementServiceImpl implements EmployerServiceManagementService {

        private final CompanyService companyService;
        private final CompanySubscriptionRepository companySubscriptionRepository;
        private final SubscriptionUsageRepository subscriptionUsageRepository;
        private final CompanyAddonRepository companyAddonRepository;
        private final AddonServiceRepository addonServiceRepository;
        private final ServiceRepository serviceRepository;
        private final ServicePackageRepository servicePackageRepository;
        private final ServicePackageDetailRepository servicePackageDetailRepository;
        private final JobPostingRepository jobPostingRepository;
        private final JobPostAddonRepository jobPostAddonRepository;
        private final ServiceActivationRouter serviceActivationRouter;
        private final BrandingActivationService brandingActivationService;
        private final OrderRepository orderRepository;
        private final SubscriptionRenewalLogRepository subscriptionRenewalLogRepository;

        @Override
        @Transactional(readOnly = true)
        public ResCompanySubscriptionDTO getMySubscription(Long userId) {
                Long companyId = getCompanyId(userId);

                CompanySubscription subscription = companySubscriptionRepository
                                .findFirstByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, SubscriptionStatus.ACTIVE)
                                .orElseThrow(() -> AppException.notFound("Công ty chưa đăng ký gói dịch vụ nào."));

                if (subscription.getExpiredAt() != null
                                && subscription.getExpiredAt().isBefore(LocalDateTime.now())) {
                        throw AppException.notFound("Gói dịch vụ đã hết hạn. Vui lòng gia hạn hoặc mua gói mới.");
                }

                ServicePackage servicePackage = servicePackageRepository.findById(subscription.getServicePackageId())
                                .orElse(null);

                List<SubscriptionUsage> usages = subscriptionUsageRepository
                                .findByCompanySubscriptionId(subscription.getId());

                List<ResSubscriptionUsageDTO> usageDTOs = usages.stream()
                                .map(u -> {
                                        Services svc = serviceRepository.findByCode(u.getFeatureCode()).orElse(null);
                                        return ResSubscriptionUsageDTO.builder()
                                                        .id(u.getId())
                                                        .featureCode(u.getFeatureCode())
                                                        .featureName(svc != null ? svc.getName() : null)
                                                        .quantityTotal(u.getQuantityTotal())
                                                        .quantityRemaining(u.getQuantityRemaining())
                                                        .resetAt(u.getResetAt())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return ResCompanySubscriptionDTO.builder()
                                .id(subscription.getId())
                                .servicePackageId(subscription.getServicePackageId())
                                .packageName(servicePackage != null ? servicePackage.getName() : null)
                                .packageCode(servicePackage != null ? servicePackage.getCode() : null)
                                .billingCycle(subscription.getBillingCycle())
                                .status(subscription.getStatus())
                                .startedAt(subscription.getStartedAt())
                                .expiredAt(subscription.getExpiredAt())
                                .createdAt(subscription.getCreatedAt())
                                .usages(usageDTOs)
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public List<ResCompanyAddonDTO> getMyAddons(Long userId) {
                Long companyId = getCompanyId(userId);

                List<CompanyAddon> addons = companyAddonRepository
                                .findByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, SubscriptionStatus.ACTIVE);

                // Gộp theo addon code: cùng code thì cộng dồn số lượng, khác code thì giữ riêng
                Map<String, List<CompanyAddon>> grouped = new LinkedHashMap<>();
                Map<String, AddonService> addonSvcMap = new LinkedHashMap<>();

                for (CompanyAddon addon : addons) {
                        AddonService addonSvc = addonServiceRepository.findById(addon.getAddonServiceId())
                                        .orElse(null);
                        if (addonSvc == null) continue;

                        String addonCode = addonSvc.getCode();
                        grouped.computeIfAbsent(addonCode, k -> new ArrayList<>()).add(addon);
                        addonSvcMap.putIfAbsent(addonCode, addonSvc);
                }

                return grouped.entrySet().stream().map(entry -> {
                        String addonCode = entry.getKey();
                        List<CompanyAddon> group = entry.getValue();
                        AddonService addonSvc = addonSvcMap.get(addonCode);
                        Services svc = serviceRepository.findById(addonSvc.getServiceId()).orElse(null);

                        int totalQty = group.stream().mapToInt(CompanyAddon::getQuantityTotal).sum();
                        int remainingQty = group.stream().mapToInt(CompanyAddon::getQuantityRemaining).sum();
                        List<Long> ids = group.stream().map(CompanyAddon::getId).collect(Collectors.toList());

                        CompanyAddon representative = group.get(0);

                        LocalDateTime latestExpiry = group.stream()
                                        .map(CompanyAddon::getExpiredAt)
                                        .filter(e -> e != null)
                                        .max(LocalDateTime::compareTo)
                                        .orElse(null);

                        LocalDateTime earliestStart = group.stream()
                                        .map(CompanyAddon::getStartedAt)
                                        .filter(s -> s != null)
                                        .min(LocalDateTime::compareTo)
                                        .orElse(null);

                        return ResCompanyAddonDTO.builder()
                                        .id(representative.getId())
                                        .addonServiceId(representative.getAddonServiceId())
                                        .companyAddonIds(ids)
                                        .addonName(addonSvc.getName())
                                        .addonCode(addonCode)
                                        .addonQuantity(addonSvc.getQuantity())
                                        .serviceId(svc != null ? svc.getId() : null)
                                        .serviceCode(svc != null ? svc.getCode() : null)
                                        .serviceName(svc != null ? svc.getName() : null)
                                        .serviceCategory(svc != null ? svc.getCategory() : null)
                                        .serviceCategoryName(svc != null && svc.getCategory() != null
                                                        ? svc.getCategory().getValue()
                                                        : null)
                                        .status(SubscriptionStatus.ACTIVE)
                                        .quantityTotal(totalQty)
                                        .quantityRemaining(remainingQty)
                                        .startedAt(earliestStart)
                                        .expiredAt(latestExpiry)
                                        .createdAt(representative.getCreatedAt())
                                        .build();
                }).collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ResJobPostAddonDTO applyAddonToJobPost(Long userId, Long jobPostingId, ReqApplyAddonDTO request) {
                Long companyId = getCompanyId(userId);

                // 1. Validate tin tuyển dụng
                JobPosting jobPosting = jobPostingRepository.findByIdAndDeletedAtIsNull(jobPostingId)
                                .orElseThrow(() -> AppException.notFound("Không tìm thấy tin tuyển dụng."));

                if (!jobPosting.getCompanyId().equals(companyId)) {
                        throw AppException.forbidden("Bạn không có quyền thao tác trên tin tuyển dụng này.");
                }

                // 2. Validate dịch vụ lẻ đã mua
                CompanyAddon companyAddon = companyAddonRepository.findById(request.getCompanyAddonId())
                                .orElseThrow(() -> AppException.notFound("Không tìm thấy dịch vụ lẻ."));

                if (!companyAddon.getCompanyId().equals(companyId)) {
                        throw AppException.forbidden("Dịch vụ lẻ này không thuộc công ty của bạn.");
                }

                if (companyAddon.getStatus() != SubscriptionStatus.ACTIVE) {
                        throw AppException.badRequest("Dịch vụ lẻ này đã hết hiệu lực.");
                }

                if (companyAddon.getExpiredAt() != null && companyAddon.getExpiredAt().isBefore(LocalDateTime.now())) {
                        throw AppException.badRequest("Dịch vụ lẻ này đã hết hạn sử dụng.");
                }

                if (companyAddon.getQuantityRemaining() <= 0) {
                        throw AppException.badRequest("Dịch vụ lẻ này đã hết số lượng sử dụng.");
                }

                // 3. Lấy thông tin cấu hình dịch vụ lẻ
                AddonService addonService = addonServiceRepository.findById(companyAddon.getAddonServiceId())
                                .orElseThrow(() -> AppException.notFound("Không tìm thấy thông tin dịch vụ lẻ."));

                // 4. Tìm service category and code để route
                Services service = serviceRepository.findById(addonService.getServiceId()).orElse(null);
                ServiceCategory serviceCategory = service != null ? service.getCategory() : null;
                String serviceCode = service != null ? service.getCode() : null;

                // 5. Nếu có handler trong Router → delegate hoàn toàn
                if (serviceCategory != null && serviceCode != null
                                && serviceActivationRouter.isSupported(serviceCategory, serviceCode)) {
                        log.info("[applyAddon] Routing to handler: {} for job #{}", serviceCode, jobPostingId);
                        return serviceActivationRouter.activate(serviceCategory, serviceCode, jobPosting, companyAddon,
                                        addonService);
                }

                // 6. Fallback: xử lý generic cho các dịch vụ chưa có handler riêng
                log.info("[applyAddon] Generic fallback for service: {} on job #{}", serviceCode, jobPostingId);
                return applyGenericAddon(jobPostingId, companyAddon, addonService);
        }

        /**
         * Xử lý generic cho các dịch vụ chưa có handler riêng.
         * Tạo JobPostAddon record và trừ số lượng.
         */
        private ResJobPostAddonDTO applyGenericAddon(Long jobPostingId, CompanyAddon companyAddon,
                        AddonService addonService) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expiredAt = addonService.getDurationDays() != null
                                ? now.plusDays(addonService.getDurationDays())
                                : null;

                JobPostAddon jobPostAddon = JobPostAddon.builder()
                                .jobPostingId(jobPostingId)
                                .companyAddonId(companyAddon.getId())
                                .addonServiceId(addonService.getId())
                                .startedAt(now)
                                .expiredAt(expiredAt)
                                .status(JobPostAddonStatus.ACTIVE)
                                .build();

                JobPostAddon saved = jobPostAddonRepository.save(jobPostAddon);

                companyAddon.setQuantityRemaining(companyAddon.getQuantityRemaining() - 1);
                companyAddonRepository.save(companyAddon);

                return ResJobPostAddonDTO.builder()
                                .id(saved.getId())
                                .jobPostingId(saved.getJobPostingId())
                                .companyAddonId(saved.getCompanyAddonId())
                                .addonServiceId(saved.getAddonServiceId())
                                .addonName(addonService.getName())
                                .status(saved.getStatus())
                                .startedAt(saved.getStartedAt())
                                .expiredAt(saved.getExpiredAt())
                                .createdAt(saved.getCreatedAt())
                                .build();
        }

        @Override
        @Transactional
        public ResCompanyBrandingDTO applyBrandingToCompany(Long userId, ReqApplyAddonDTO request) {
                Long companyId = getCompanyId(userId);

                CompanyAddon companyAddon = companyAddonRepository.findById(request.getCompanyAddonId())
                                .orElseThrow(() -> AppException.notFound("Không tìm thấy dịch vụ lẻ."));

                if (!companyAddon.getCompanyId().equals(companyId)) {
                        throw AppException.forbidden("Dịch vụ lẻ này không thuộc công ty của bạn.");
                }
                if (companyAddon.getStatus() != SubscriptionStatus.ACTIVE) {
                        throw AppException.badRequest("Dịch vụ lẻ này đã hết hiệu lực.");
                }
                if (companyAddon.getExpiredAt() != null && companyAddon.getExpiredAt().isBefore(LocalDateTime.now())) {
                        throw AppException.badRequest("Dịch vụ lẻ này đã hết hạn sử dụng.");
                }
                if (companyAddon.getQuantityRemaining() <= 0) {
                        throw AppException.badRequest("Dịch vụ lẻ này đã hết số lượng sử dụng.");
                }

                AddonService addonService = addonServiceRepository.findById(companyAddon.getAddonServiceId())
                                .orElseThrow(() -> AppException.notFound("Không tìm thấy thông tin dịch vụ lẻ."));

                Services service = serviceRepository.findById(addonService.getServiceId()).orElse(null);
                String serviceCode = service != null ? service.getCode() : null;

                if (!BrandingActivationService.isSupported(serviceCode)) {
                        throw AppException.badRequest("Dịch vụ lẻ này không thuộc nhóm dịch vụ BRANDING.");
                }

                return brandingActivationService.activate(serviceCode, companyId, companyAddon, addonService);
        }

        @Override
        @Transactional
        public ResSubscriptionRenewalDTO renewSubscription(Long userId, ReqRenewSubscriptionDTO request) {
                Long companyId = getCompanyId(userId);

                // 1. Tìm subscription ACTIVE hiện tại
                CompanySubscription subscription = companySubscriptionRepository
                                .findFirstByCompanyIdAndStatusOrderByCreatedAtDesc(companyId, SubscriptionStatus.ACTIVE)
                                .orElseThrow(() -> AppException.badRequest(
                                                "Không tìm thấy gói dịch vụ đang hoạt động. Vui lòng mua gói mới."));

                // 2. Lấy thông tin gói (cùng gói hiện tại)
                ServicePackage servicePackage = servicePackageRepository.findById(subscription.getServicePackageId())
                                .orElseThrow(() -> AppException.notFound("Không tìm thấy thông tin gói dịch vụ."));

                if (servicePackage.getIsActive() == null || !servicePackage.getIsActive()) {
                        throw AppException.badRequest("Gói dịch vụ này không còn hoạt động. Không thể gia hạn.");
                }

                if (subscription.getExpiredAt() == null) {
                        throw AppException.badRequest("Gói dịch vụ không có ngày hết hạn, không thể gia hạn.");
                }

                // 3. Tạo Order gia hạn
                Order order = Order.builder()
                                .companyId(companyId)
                                .orderCode("ORD-RN-" + System.currentTimeMillis())
                                .type(OrderType.SUBSCRIPTION)
                                .totalAmount(servicePackage.getPrice())
                                .status(OrderStatus.PAID)
                                .paymentMethod(request.getPaymentMethod())
                                .paidAt(LocalDateTime.now())
                                .createdBy(userId)
                                .build();
                Order savedOrder = orderRepository.save(order);

                OrderItem item = OrderItem.builder()
                                .orderId(savedOrder.getId())
                                .itemType(OrderItemType.SUBSCRIPTION)
                                .servicePackageId(servicePackage.getId())
                                .quantity(1)
                                .unitPrice(servicePackage.getPrice())
                                .totalPrice(servicePackage.getPrice())
                                .billingCycle(servicePackage.getBillingCycle())
                                .build();
                savedOrder.setOrderItems(new ArrayList<>(List.of(item)));

                // 4. Kéo dài expiredAt (nối tiếp, không mất thời gian còn lại)
                LocalDateTime oldExpiredAt = subscription.getExpiredAt();
                LocalDateTime newExpiredAt = subscription.getBillingCycle() == BillingCycle.MONTHLY
                                ? oldExpiredAt.plusMonths(1)
                                : oldExpiredAt.plusYears(1);
                subscription.setExpiredAt(newExpiredAt);
                subscription.setReminderSentAt(null); // Reset để chu kỳ mới có thể nhắc lại
                companySubscriptionRepository.save(subscription);

                // 5. Cộng dồn quota cho mỗi feature
                List<ServicePackageDetail> details = servicePackageDetailRepository
                                .findByServicePackageId(servicePackage.getId());
                List<SubscriptionUsage> usages = subscriptionUsageRepository
                                .findByCompanySubscriptionId(subscription.getId());

                int totalQuotaAdded = 0;
                for (ServicePackageDetail detail : details) {
                        Services svc = serviceRepository.findById(detail.getServiceId()).orElse(null);
                        if (svc == null) continue;

                        SubscriptionUsage usage = usages.stream()
                                        .filter(u -> u.getFeatureCode().equals(svc.getCode()))
                                        .findFirst()
                                        .orElse(null);

                        if (usage != null) {
                                // Cộng dồn: giữ lại phần chưa dùng + thêm quota mới
                                usage.setQuantityTotal(usage.getQuantityTotal() + detail.getQuantity());
                                usage.setQuantityRemaining(usage.getQuantityRemaining() + detail.getQuantity());
                                usage.setResetAt(newExpiredAt);
                                subscriptionUsageRepository.save(usage);
                        } else {
                                // Feature mới chưa có — tạo usage mới
                                SubscriptionUsage newUsage = SubscriptionUsage.builder()
                                                .companySubscriptionId(subscription.getId())
                                                .companyId(companyId)
                                                .featureCode(svc.getCode())
                                                .quantityTotal(detail.getQuantity())
                                                .quantityRemaining(detail.getQuantity())
                                                .resetAt(newExpiredAt)
                                                .build();
                                subscriptionUsageRepository.save(newUsage);
                        }
                        totalQuotaAdded += detail.getQuantity();
                }

                // 6. Ghi log gia hạn
                SubscriptionRenewalLog renewalLog = SubscriptionRenewalLog.builder()
                                .companySubscriptionId(subscription.getId())
                                .orderId(savedOrder.getId())
                                .oldExpiredAt(oldExpiredAt)
                                .newExpiredAt(newExpiredAt)
                                .quotaAdded(totalQuotaAdded)
                                .renewedBy(userId)
                                .build();
                SubscriptionRenewalLog savedLog = subscriptionRenewalLogRepository.save(renewalLog);

                // 7. Build response
                List<SubscriptionUsage> updatedUsages = subscriptionUsageRepository
                                .findByCompanySubscriptionId(subscription.getId());

                List<ResSubscriptionRenewalDTO.UsageInfo> usageInfos = updatedUsages.stream()
                                .map(u -> {
                                        Services svc = serviceRepository.findByCode(u.getFeatureCode()).orElse(null);
                                        return ResSubscriptionRenewalDTO.UsageInfo.builder()
                                                        .featureCode(u.getFeatureCode())
                                                        .featureName(svc != null ? svc.getName() : null)
                                                        .quantityTotal(u.getQuantityTotal())
                                                        .quantityRemaining(u.getQuantityRemaining())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return ResSubscriptionRenewalDTO.builder()
                                .renewalLogId(savedLog.getId())
                                .orderId(savedOrder.getId())
                                .orderCode(savedOrder.getOrderCode())
                                .totalAmount(savedOrder.getTotalAmount())
                                .subscription(ResSubscriptionRenewalDTO.SubscriptionInfo.builder()
                                                .id(subscription.getId())
                                                .packageName(servicePackage.getName())
                                                .packageCode(servicePackage.getCode())
                                                .billingCycle(subscription.getBillingCycle())
                                                .status(subscription.getStatus())
                                                .oldExpiredAt(oldExpiredAt)
                                                .newExpiredAt(newExpiredAt)
                                                .usages(usageInfos)
                                                .build())
                                .build();
        }

        private Long getCompanyId(Long userId) {
                Long companyId = companyService.getCompanyIdByUserId(userId);
                if (companyId == null) {
                        throw AppException.badRequest("Chưa có hồ sơ công ty.");
                }
                return companyId;
        }
}
