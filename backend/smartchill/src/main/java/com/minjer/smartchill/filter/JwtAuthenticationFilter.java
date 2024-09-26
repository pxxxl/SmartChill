package com.minjer.smartchill.filter;

import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.utils.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebFilter(filterName = "JwtAuthenticationFilter", urlPatterns = "/*")
public class JwtAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 1. 强转
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 2. 判断是否为需要校验的请求
        log.info("拦截请求: {}", request.getRequestURI());

        // 打印请求头
        log.info("Authorization: {}", request.getHeader("Authorization"));

        if (!request.getRequestURI().startsWith("/admin")
                || request.getRequestURI().startsWith("/admin/login")
                || request.getRequestURI().startsWith("/admin/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 获取请求头中的token
        String token = request.getHeader("Authorization");
        if (token == null) {
            // 重写response
            response.setStatus(ResultEnum.AUTH_ERROR.getCode());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\": 401, \"message\": \"请先登录再操作\"}");
            return;
        }

        token = token.replace("Bearer ", "");
        log.info("token: {}", token);

        // 4. 提取token并校验
        try {
            if (JwtUtil.isTokenExpired(token)) {
                response.setStatus(ResultEnum.AUTH_ERROR.getCode());
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\": 401, \"message\": \"Token过期\"}");
                return;
            }
        } catch (Exception e) {
            response.setStatus(ResultEnum.AUTH_ERROR.getCode());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\": 401, \"message\": \"Token不存在\"}");
            return;
        }

        // 5. 放行
        filterChain.doFilter(request, response);
    }
}
