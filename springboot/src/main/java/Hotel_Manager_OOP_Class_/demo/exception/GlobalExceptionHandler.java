package Hotel_Manager_OOP_Class_.demo.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import Hotel_Manager_OOP_Class_.demo.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Dữ liệu đang bị trùng hoặc đang được liên kết nên không thể thao tác",
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện chức năng này", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");

        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, vui lòng thử lại sau", request);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(response);
    }
}
