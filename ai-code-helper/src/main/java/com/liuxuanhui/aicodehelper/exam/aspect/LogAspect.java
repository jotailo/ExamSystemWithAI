package com.liuxuanhui.aicodehelper.exam.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final ObjectMapper objectMapper;

    @Pointcut("execution(public * com.liuxuanhui.aicodehelper.exam.controller.*.*(..))")
    public void logPoint() {
    }

    @Around("logPoint()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        log.info("[Request] {} url: {}, method: {}#{}",
                request.getMethod(),
                String.format("%s?%s", request.getServletPath(), Optional.ofNullable(request.getQueryString()).orElse("")),
                point.getSignature().getDeclaringTypeName(),
                point.getSignature().getName());
        Long start = System.currentTimeMillis();
        Object result = point.proceed();
        Long end = System.currentTimeMillis();
        HttpServletResponse response = attributes.getResponse();
        log.info("[Response] {} {}ms, body: {}",
                Objects.requireNonNull(response).getStatus(),
                end - start,
                objectMapper.writeValueAsString(result));
        return result;
    }
}
