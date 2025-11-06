package com.certicom.certifact_facturas_service_ng.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DataFilter implements Filter {

    public static final String RUC_CLIENT = "X-User-Ruc";
    public static final String X_ID_USER = "X-User-Id";
    public static final String USER_AGENT = "User-Agent";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String ruc = request.getHeader(RUC_CLIENT);
        String id = request.getHeader(X_ID_USER);
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader(USER_AGENT);
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        if (ruc != null) {
            MDC.put("x_user_ruc", ruc);
        }

        if (id != null) {
            MDC.put("x_user_id", id);
        }

        if (ip != null) {
            MDC.put("ip_address", ip);
        }

        if (userAgent != null) {
            MDC.put("user_agent", userAgent);
        }

        if (endpoint != null) {
            MDC.put("endpoint", endpoint);
        }

        if (method != null) {
            MDC.put("method", method);
        }

        try {
            //LogHelper.infoLog(LogTitle.INFO.getType(), LogMessages.currentMethod(), "Incoming request ["+request.getMethod()+" "+request.getRequestURI()+"]");
            filterChain.doFilter(servletRequest, servletResponse);
            //LogHelper.infoLog(LogTitle.INFO.getType(), LogMessages.currentMethod(), "Completed request: duration="+(System.currentTimeMillis() - startTime)+"ms");
        } finally {
            MDC.clear();
        }

    }

}
