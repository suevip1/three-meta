package com.coatardbul.stock.common.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 将请求的参数
 */
@Component
@Aspect
@Slf4j
public class MethodLogLevelAspect {
    @Pointcut(value = "@within(com.coatardbul.stock.common.annotation.MethodLogLevel)")
    private void pointCut() {
    }

    //    @Before(value = "pointCut() && @annotation(logger)")
//    public void before(JoinPoint joinPoint, KtLog logger){
//        System.out.println("注解作用的方法名: " + joinPoint.getSignature().getName());
//
//        System.out.println("所在类的简单类名: " + joinPoint.getSignature().getDeclaringType().getSimpleName());
//
//        System.out.println("所在类的完整类名: " + joinPoint.getSignature().getDeclaringType());
//
//        System.out.println("目标方法的声明类型: " + Modifier.toString(joinPoint.getSignature().getModifiers()));
//
//    }
    @Around("pointCut()")
    public Object advice(ProceedingJoinPoint joinPoint) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info("--------" +request.getMethod()+ request.getRequestURI() + "----开始");
        log.info("--------" +request.getQueryString() + "----开始");




        Object result=null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
           log.error(throwable.getMessage(),throwable);
        }
        log.info("--------" + request.getRequestURI() + "----结束");
        return result;

    }
}
