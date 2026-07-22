package divar.aut.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts exceptions thrown anywhere in controller/service layers into
 * structured JSON errors instead of raw stack traces or framework HTML.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Persian labels for request fields, used to build a fully-Persian
    // validation message ("<field> <message>") instead of leaking the raw
    // English field name. Falls back to the field name itself if a field
    // isn't listed here.
    private static final Map<String, String> FIELD_LABELS = Map.ofEntries(
            Map.entry("fullName", "نام و نام خانوادگی"),
            Map.entry("fullname", "نام و نام خانوادگی"),
            Map.entry("username", "نام کاربری"),
            Map.entry("password", "رمز عبور"),
            Map.entry("email", "ایمیل"),
            Map.entry("phone", "شماره موبایل"),
            Map.entry("name", "نام"),
            Map.entry("title", "عنوان"),
            Map.entry("description", "توضیحات"),
            Map.entry("price", "قیمت"),
            Map.entry("itemCondition", "وضعیت کالا"),
            Map.entry("categoryId", "دسته‌بندی"),
            Map.entry("cityId", "شهر"),
            Map.entry("content", "متن پیام"),
            Map.entry("comment", "نظر"),
            Map.entry("score", "امتیاز"),
            Map.entry("reason", "دلیل"),
            Map.entry("strategy", "استراتژی")
    );

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        ErrorResponse body = new ErrorResponse(exception.getMessage(), exception.getStatus().value());
        return ResponseEntity.status(exception.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> FIELD_LABELS.getOrDefault(fieldError.getField(), fieldError.getField())
                        + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("، "));
        ErrorResponse body = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        ErrorResponse body = new ErrorResponse("خطای غیرمنتظره سرور: " + exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
