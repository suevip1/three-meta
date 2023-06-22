package com.coatardbul.sail.interceptor;

import com.coatardbul.baseCommon.interceptor.InterceptorConfiguration;
import com.coatardbul.baseCommon.interceptor.InterfaceURLInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 拦截器的属性配置
 *
 */
@Configuration
public class SailConfiguration extends InterceptorConfiguration {


    @Bean
    public InterfaceURLInterceptor getInterfaceURLInterceptor() {
        return new SailInterceptor();
    }
    /**
     * 重写addCorsMappings()解决跨域问题
     * 配置：允许http请求进行跨域访问
     *
     * @param registry
     */


    /**
     * 重写addInterceptors()实现拦截器
     * 配置：要拦截的路径以及不拦截的路径
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册Interceptor拦截器(Interceptor这个类是我们自己写的拦截器类)
        InterceptorRegistration registration = registry.addInterceptor(getInterfaceURLInterceptor());
        //addPathPatterns()方法添加需要拦截的路径
        //所有路径都被拦截
//        registration.addPathPatterns("/**");
        //excludePathPatterns()方法添加不拦截的路径
        //添加不拦截路径
        registration.excludePathPatterns(
                //策略
                "/**"
        );
    }
}