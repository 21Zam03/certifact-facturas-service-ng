package com.certicom.certifact_facturas_service_ng.interceptor;

import com.certicom.certifact_facturas_service_ng.enums.LogTitle;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String user = request.getHeader("X-User-Id");
        LogHelper.infoLog(LogTitle.INFO.getType(), LogMessages.currentMethod(),
                "[INTERCEPTOR] user="+user+" endpoint="+request.getRequestURI()+" method="+request.getMethod());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        LogHelper.infoLog(LogTitle.INFO.getType(), LogMessages.currentMethod(),
                "[INTERCEPTOR-END] endpoint="+request.getRequestURI()+" method="+request.getMethod());
    }

}
