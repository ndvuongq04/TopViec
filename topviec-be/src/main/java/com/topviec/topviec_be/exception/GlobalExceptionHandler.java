package com.topviec.topviec_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.topviec.topviec_be.dto.response.RestResponse;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<RestResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
                var errors = ex.getBindingResult().getFieldErrors().stream()
                                .collect(Collectors.toMap(
                                                e -> e.getField(),
                                                e -> e.getDefaultMessage() != null ? e.getDefaultMessage()
                                                                : "Invalid"));

                RestResponse<Object> res = new RestResponse<>();
                res.setStatusCode(HttpStatus.BAD_REQUEST.value());
                res.setError("Validation failed");
                res.setMessage(errors);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        @ExceptionHandler(AppException.class)
        public ResponseEntity<RestResponse<Object>> handleAppException(AppException ex) {
                RestResponse<Object> res = new RestResponse<>();
                res.setStatusCode(ex.getStatus().value());
                res.setError(ex.getStatus().getReasonPhrase());
                res.setMessage(ex.getMessage());

                return ResponseEntity.status(ex.getStatus()).body(res);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<RestResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
                RestResponse<Object> res = new RestResponse<>();
                res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                res.setError("Unauthorized");
                res.setMessage("Email hoặc mật khẩu không chính xác");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        // Bắt lỗi @PreAuthorize từ chối truy cập — trả 403 thay vì 500
        @ExceptionHandler(AuthorizationDeniedException.class)
        public ResponseEntity<RestResponse<Object>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
                RestResponse<Object> res = new RestResponse<>();
                res.setStatusCode(HttpStatus.FORBIDDEN.value());
                res.setError("Forbidden");
                res.setMessage("Bạn không có quyền thực hiện thao tác này");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<RestResponse<Object>> handleException(Exception ex) {
                // If the exception is wrapped (e.g., by Spring's Transactional proxy), unwrap it
                Throwable cause = ex;
                while (cause != null) {
                        if (cause instanceof AppException) {
                                return handleAppException((AppException) cause);
                        }
                        // Also check for common spring security or data integrity exceptions if needed
                        cause = cause.getCause();
                }

                // If it's truly an unexpected exception, print it
                ex.printStackTrace();
                RestResponse<Object> res = new RestResponse<>();
                res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                res.setError("Internal Server Error");
                // Provide a safe message, maybe the actual exception message for development, but secure for production
                res.setMessage("Lỗi hệ thống, vui lòng thử lại sau (" + ex.getMessage() + ")");

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
}