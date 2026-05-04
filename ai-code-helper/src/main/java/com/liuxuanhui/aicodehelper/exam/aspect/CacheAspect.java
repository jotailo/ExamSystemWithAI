package com.liuxuanhui.aicodehelper.exam.aspect;

import com.liuxuanhui.aicodehelper.exam.annotation.Cache;
import com.liuxuanhui.aicodehelper.exam.exception.BusinessException;
import com.liuxuanhui.aicodehelper.exam.exception.CommonErrorCode;
import com.liuxuanhui.aicodehelper.exam.utils.AnnotationUtils;
import com.liuxuanhui.aicodehelper.exam.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    private final RedisUtil redisUtil;

    @Around("@annotation(com.liuxuanhui.aicodehelper.exam.annotation.Cache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Cache cache = method.getAnnotation(Cache.class);

        String cacheKey = cache.prefix();
        if (StringUtils.hasLength(cache.suffix())) {
            String suffix = AnnotationUtils.parseSpel(cache.suffix(), method, joinPoint.getArgs());
            cacheKey += ":" + suffix;
        }

        TimeUnit timeUnit = cache.timeUnit();
        long ttl = cache.ttl();
        int randomTime = cache.randomTime();
        long randomTtl = timeUnit.convert(new Random().nextInt(randomTime), timeUnit);

        Class<?> clazz = joinPoint.getTarget().getClass();
        Logger logger = LoggerFactory.getLogger(clazz);

        if (cache.resetCache()) {
            Object result = AnnotationUtils.LogAndInvokeTargetMethod(joinPoint, logger,
                    String.format("%s中的%s方法, 准备reset cache: %s", clazz.getName(), method.getName(), cacheKey),
                    String.format("%s中的%s方法执行结束", clazz.getName(), method.getName()));
            if (result == null) {
                throw new BusinessException(CommonErrorCode.E_800001);
            }
            redisUtil.set(cacheKey, result, ttl + randomTtl, timeUnit);
            return result;
        }

        Object cacheValue = redisUtil.get(cacheKey);
        if (cacheValue == null) {
            Object result = AnnotationUtils.LogAndInvokeTargetMethod(joinPoint, logger,
                    String.format("%s中的%s方法, 查询了cache: %s", clazz.getName(), method.getName(), cacheKey),
                    String.format("%s中的%s方法执行结束", clazz.getName(), method.getName()));
            redisUtil.set(cacheKey, result, ttl + randomTtl, timeUnit);
            return result;
        }
        return cacheValue;
    }
}
