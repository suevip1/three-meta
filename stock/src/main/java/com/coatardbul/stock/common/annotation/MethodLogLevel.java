package com.coatardbul.stock.common.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
/**
 * 注解的生命周期，表示注解会被保留到什么阶段，可以选择编译阶段、类加载阶段，或运行阶段
 */
@Retention(RetentionPolicy.RUNTIME)
/**
 * 注解作用的位置
 */
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MethodLogLevel {
    String value() default "";
}
