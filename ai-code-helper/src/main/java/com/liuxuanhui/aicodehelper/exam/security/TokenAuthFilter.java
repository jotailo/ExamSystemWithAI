package com.liuxuanhui.aicodehelper.exam.security;

import com.liuxuanhui.aicodehelper.exam.utils.JwtUtils;
import com.liuxuanhui.aicodehelper.exam.vo.TokenVo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenAuthFilter extends OncePerRequestFilter {

    private final Map<Integer, String> roleMap = new ConcurrentHashMap<>(4);

    public TokenAuthFilter() {
        roleMap.put(1, "student");
        roleMap.put(2, "teacher");
        roleMap.put(3, "admin");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader("authorization");
        if (!StringUtils.hasLength(token)) {
            // 没有 token，直接放行，由 Security 的 permitAll/authenticated 规则决定
            chain.doFilter(request, response);
            return;
        }
        try {
            TokenVo userInfo = JwtUtils.getUserInfoByToken(request);
            String role = roleMap.get(userInfo.getRoleId());
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userInfo.getUsername(), token,
                            List.of(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            // token 无效，清空认证，让 Security 规则决定后续处理
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }
}
