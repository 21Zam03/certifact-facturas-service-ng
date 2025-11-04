package com.certicom.certifact_facturas_service_ng.interceptor;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    public static final String X_RUC_CLIENT = "X-RUC-Client";
    public static final String X_ID_USER = "X-ID-User";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String ruc = MDC.get("ruc");
        String id =  MDC.get("id");

        if (ruc != null) {
            request.getHeaders().add(X_RUC_CLIENT, ruc);
        }

        if(id != null) {
            request.getHeaders().add(X_ID_USER, id);
        }

        return execution.execute(request, body);
    }

}
