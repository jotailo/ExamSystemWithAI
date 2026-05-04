package com.liuxuanhui.aicodehelper.exam.utils;

import com.liuxuanhui.aicodehelper.exam.entity.User;
import com.liuxuanhui.aicodehelper.exam.exception.BusinessException;
import com.liuxuanhui.aicodehelper.exam.exception.CommonErrorCode;
import com.liuxuanhui.aicodehelper.exam.vo.TokenVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtils {

    public static final long EXPIRE = 1000 * 60 * 60 * 24;

    public static final String APP_SECRET = "saseessrtkookppijhfewewsadhuutresxvhjkk_extended_for_256bit";

    private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(APP_SECRET.getBytes(StandardCharsets.UTF_8));

    public static String createToken(User user) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("exam-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .claim("roleId", user.getRoleId())
                .claim("username", user.getUsername())
                .claim("password", user.getPassword())
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean checkToken(String jwtToken) {
        if (!StringUtils.hasLength(jwtToken)) {
            return false;
        }
        try {
            Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(jwtToken);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("Authorization");
            if (!StringUtils.hasLength(jwtToken)) {
                return false;
            }
            Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(jwtToken);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static TokenVo getUserInfoByToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("authorization");
        if (!StringUtils.hasLength(jwtToken)) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(jwtToken);
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.E_200001);
        }
        Claims claims = claimsJws.getBody();
        return TokenVo.builder()
                .roleId(claims.get("roleId", Integer.class))
                .username(claims.get("username", String.class))
                .password(claims.get("password", String.class))
                .build();
    }

    public static Integer getRoleByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("Authorization");
        if (!StringUtils.hasLength(jwtToken)) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return claims.get("roleId", Integer.class);
    }
}
