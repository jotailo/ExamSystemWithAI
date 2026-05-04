package com.liuxuanhui.aicodehelper.exam.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuxuanhui.aicodehelper.exam.exception.CommonErrorCode;
import com.liuxuanhui.aicodehelper.exam.exception.RestErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class MyAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        RestErrorResponse apiError = new RestErrorResponse(
                String.valueOf(CommonErrorCode.UNAUTHORIZED.getCode()), authenticationException.getMessage());
        new ObjectMapper().writeValue(response.getWriter(), apiError);
    }
}
