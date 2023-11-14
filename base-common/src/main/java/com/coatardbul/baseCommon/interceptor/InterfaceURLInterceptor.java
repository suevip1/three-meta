package com.coatardbul.baseCommon.interceptor;

import com.coatardbul.baseCommon.constants.Constant;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 */
public class InterfaceURLInterceptor implements HandlerInterceptor {


    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String token = request.getHeader("token");
            if(Constant.INVINCIBLE_TOKEN.equals(token)){
               return true;
            }
            Boolean valid = verifyUserValid(token);
            if (!valid) {
                response.setStatus(401);
                return false;
            }
//            logLoginInfo(request);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public Boolean verifyUserValid(String token) {
        return true;
    }

    public void logLoginInfo(HttpServletRequest request) {

    }


//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//        System.out.println("执行业务逻辑后，视图渲染之前");
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
//        System.out.println("视图渲染后");
//    }


}