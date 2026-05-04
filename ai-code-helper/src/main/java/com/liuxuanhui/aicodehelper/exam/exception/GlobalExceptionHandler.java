package com.liuxuanhui.aicodehelper.exam.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestErrorResponse handle(MethodArgumentNotValidException ex) {
        String field = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getField();
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return new RestErrorResponse(HttpStatus.BAD_REQUEST.toString(), field + message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public RestErrorResponse handle(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> constraint : violations) {
            message.append(constraint.getMessage()).append(";");
        }
        return new RestErrorResponse(HttpStatus.BAD_REQUEST.toString(), message.toString());
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse processException(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Exception e) {
        if (e instanceof BusinessException businessException) {
            LOGGER.info(e.getMessage(), e);
            ErrorCode errorCode = businessException.getErrorCode();
            return new RestErrorResponse(String.valueOf(errorCode.getCode()), errorCode.getDesc());
        }
        LOGGER.error("系统未知异常：", e);
        return new RestErrorResponse(String.valueOf(CommonErrorCode.UNKNOWN.getCode()),
                e.getMessage() != null ? e.getMessage() : CommonErrorCode.UNKNOWN.getDesc());
    }
}
