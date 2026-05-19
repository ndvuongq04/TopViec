package com.topviec.topviec_be.aspect;

import com.topviec.topviec_be.annotation.LogAction;
import com.topviec.topviec_be.dto.internal.LogContext;
import com.topviec.topviec_be.enums.logging.LogActionType;
import com.topviec.topviec_be.service.LoggingService;
import com.topviec.topviec_be.util.IpUtil;
import com.topviec.topviec_be.util.LogContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * AOP Aspect — chặn method có @LogAction, tự động thu thập thông tin
 * và ghi log async sau khi method thực thi xong.
 *
 * Luồng: @Before → method chạy → @After → async save
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final LoggingService loggingService;
    private final IpUtil ipUtil;

    @Around("@annotation(logAction)")
    public Object aroundLogAction(ProceedingJoinPoint joinPoint, LogAction logAction) throws Throwable {

        LogActionType actionType = logAction.value();

        // ── BEFORE: Thu thập thông tin ──
        LogContext context = LogContext.builder()
                .actionType(actionType)
                .userId(extractUserId())
                .ipAddress(extractIpAddress())
                .userAgent(extractUserAgent())
                .targetId(extractTargetId(joinPoint))
                .description(logAction.description())
                .startTime(System.currentTimeMillis())
                .build();

        // Đặt vào ThreadLocal để service layer có thể bổ sung metadata
        LogContextHolder.init(context);

        Object result;
        try {
            // ── Method thực thi ──
            result = joinPoint.proceed();

            // ── AFTER SUCCESS ──
            context.setStatus("SUCCESS");
            context.setDurationMs(System.currentTimeMillis() - context.getStartTime());

            // Nếu method POST tạo mới mà targetId chưa có → thử lấy từ response
            if (context.getTargetId() == null) {
                context.setTargetId(extractTargetIdFromResponse(result));
            }

        } catch (Throwable ex) {
            // ── AFTER FAILURE ──
            context.setStatus("FAILURE");
            context.setDurationMs(System.currentTimeMillis() - context.getStartTime());
            context.setErrorMessage(truncate(ex.getMessage(), 2000));

            throw ex; // Re-throw để exception handler xử lý bình thường
        } finally {
            // Dispatch async save — không block response
            loggingService.saveLogAsync(context);

            // Dọn dẹp ThreadLocal
            LogContextHolder.clear();
        }

        return result;
    }

    // ══════════════════════════════════════════════
    // Helper methods
    // ══════════════════════════════════════════════

    /**
     * Lấy userId từ JWT trong SecurityContext.
     * Trả về null nếu không authenticated (VD: public endpoint, scheduler).
     */
    private Long extractUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return null;
            }

            Object principal = auth.getPrincipal();
            if (principal instanceof Jwt jwt) {
                return Long.parseLong(jwt.getSubject());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /** Lấy IP address từ HttpServletRequest */
    private String extractIpAddress() {
        HttpServletRequest request = getRequest();
        return request != null ? ipUtil.getClientIp(request) : null;
    }

    /** Lấy User-Agent header */
    private String extractUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null)
            return null;

        String ua = request.getHeader("User-Agent");
        return ua != null ? truncate(ua, 500) : null;
    }

    /**
     * Tự động lấy targetId từ @PathVariable.
     * Ưu tiên: param tên "id" → param Long đầu tiên có @PathVariable.
     */
    private Long extractTargetId(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Parameter[] parameters = method.getParameters();
            Object[] args = joinPoint.getArgs();
            String[] paramNames = signature.getParameterNames();

            // Ưu tiên 1: Tìm @PathVariable có tên "id"
            for (int i = 0; i < parameters.length; i++) {
                if (hasPathVariable(parameters[i]) && "id".equals(paramNames[i])) {
                    return toLong(args[i]);
                }
            }

            // Ưu tiên 2: @PathVariable kiểu Long đầu tiên
            for (int i = 0; i < parameters.length; i++) {
                if (hasPathVariable(parameters[i]) && args[i] instanceof Long) {
                    return (Long) args[i];
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Thử lấy targetId từ response body (cho POST create).
     * Hỗ trợ ResponseEntity chứa object có method getId().
     */
    private Long extractTargetIdFromResponse(Object result) {
        try {
            if (result == null)
                return null;

            Object body = result;
            // Unwrap ResponseEntity
            if (result instanceof ResponseEntity<?> re) {
                body = re.getBody();
            }
            if (body == null)
                return null;

            // Tìm method getId()
            java.lang.reflect.Method getIdMethod = body.getClass().getMethod("getId");
            Object id = getIdMethod.invoke(body);
            return toLong(id);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean hasPathVariable(Parameter parameter) {
        for (Annotation ann : parameter.getAnnotations()) {
            if (ann instanceof PathVariable) {
                return true;
            }
        }
        return false;
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Long toLong(Object value) {
        if (value instanceof Long l)
            return l;
        if (value instanceof Number n)
            return n.longValue();
        if (value instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String truncate(String text, int maxLength) {
        if (text == null)
            return null;
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }
}
