package com.emiteai.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class AuditingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        long startTime = System.currentTimeMillis();

        // Log de método HTTP, URI e endereço IP
        log.info("Request Info: Method={}, URI={}, IP={}", req.getMethod(), req.getRequestURI(), req.getRemoteAddr());

        // Continua o filter chain
        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;

        // Log da duração das requisições
        log.info("Request duration: {} ms", duration);
    }
}