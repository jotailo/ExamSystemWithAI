package com.liuxuanhui.aicodehelper.exam.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

public class AnnotationUtils {

    public static Object LogAndInvokeTargetMethod(ProceedingJoinPoint joinPoint, Logger logger,
                                                   String startLogMsg, String endLogMsg) throws Throwable {
        long startTime = System.currentTimeMillis();
        logger.info(startLogMsg);
        Object result = joinPoint.proceed(joinPoint.getArgs());
        long endTime = System.currentTimeMillis();
        logger.info(endLogMsg + ", 耗时: {}ms", endTime - startTime);
        return result;
    }

    public static String parseSpel(String key, Method method, Object[] args) {
        if (!StringUtils.hasLength(key)) {
            return "";
        }
        StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();
        String[] paraNameArr = discoverer.getParameterNames(method);

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < Objects.requireNonNull(paraNameArr).length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }
}
