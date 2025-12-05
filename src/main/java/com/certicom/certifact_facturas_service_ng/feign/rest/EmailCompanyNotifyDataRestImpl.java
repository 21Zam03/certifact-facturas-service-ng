package com.certicom.certifact_facturas_service_ng.feign.rest;

import com.certicom.certifact_facturas_service_ng.dto.others.EmailCompanyNotifyDto;
import com.certicom.certifact_facturas_service_ng.feign.EmailCompanyNotifyData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EmailCompanyNotifyDataRestImpl implements EmailCompanyNotifyData {

    private final RestTemplate restTemplate;

    @Value("${external.services.factura-service-sp.base-url}")
    private String baseUrl;

    @Value("${external.services.factura-service-sp.endpoints.api-emailcompanynotify-endpoint}")
    private String apiEmailCompanyNotifyEndpoint;

    public EmailCompanyNotifyDataRestImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<EmailCompanyNotifyDto> findAllByCompanyRucAndEstadoIsTrue(String rucEmisor) {
        String url = getUrlEndpoint() + "/join/ruc?ruc=" + rucEmisor;
        ResponseEntity<List<EmailCompanyNotifyDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<EmailCompanyNotifyDto>>() {}
        );

        return response.getBody();
    }

    private String getUrlEndpoint() {
        return this.baseUrl+this.apiEmailCompanyNotifyEndpoint;
    }
}
