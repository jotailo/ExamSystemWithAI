package com.liuxuanhui.aicodehelper.exam.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuxuanhui.aicodehelper.exam.exception.CommonErrorCode;
import com.liuxuanhui.aicodehelper.exam.exception.RestErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(403);
        RestErrorResponse apiError = new RestErrorResponse(
                String.valueOf(CommonErrorCode.FORBIDDEN.getCode()), accessDeniedException.getMessage());
        new ObjectMapper().writeValue(response.getWriter(), apiError);
    }
}
