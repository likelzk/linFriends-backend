package com.lin.linfriends.Interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CorsInterceptor implements HandlerInterceptor {
    public static final String FRONT_HOST="http://localhost:3000";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin",FRONT_HOST);
        response.setHeader("Access-Control-Allow-Methods","GET,HEAD,OPTIONS,POST,PUT");
        response.setHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Allow-Credentials","true");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}