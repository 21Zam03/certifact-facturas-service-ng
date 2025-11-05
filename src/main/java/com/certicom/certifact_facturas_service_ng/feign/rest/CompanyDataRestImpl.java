package com.certicom.certifact_facturas_service_ng.feign.rest;

import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.CompanyData;
import com.certicom.certifact_facturas_service_ng.model.CompanyModel;
import com.certicom.certifact_facturas_service_ng.model.OseModel;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
public class CompanyDataRestImpl implements CompanyData {

    private final RestTemplate restTemplate;

    @Value("${external.services.factura-service-sp.base-url}")
    private String baseUrl;

    @Value("${external.services.factura-service-sp.endpoints.api-company-endpoint}")
    private String apiCompanyEndpoint;

    public CompanyDataRestImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getStateFromCompanyByRuc(String ruc) {
        String url = getUrlEndpoint()+"/"+ruc+"/state";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LogHelper.warnLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+e.getMessage());
            return null;
        } catch (HttpServerErrorException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+ e.getMessage());
            throw new ServiceException(LogMessages.ERROR_HTTP_SERVER, e);
        } catch (ResourceAccessException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error de conexión con el servicio externo", e);
            throw new ServiceException(LogMessages.ERROR_HTTP_RED, e);
        } catch (RestClientException ex) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error inesperado al comunicarse con el servicio externo", ex);
            throw new ServiceException(LogMessages.ERROR_HTTP, ex);
        }
    }

    @Override
    public CompanyModel findCompanyByRuc(String ruc) {
        String url = getUrlEndpoint()+"/"+ruc;
        try {
            ResponseEntity<CompanyModel> response = restTemplate.getForEntity(url, CompanyModel.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LogHelper.warnLog(LogMessages.currentMethod(),
                    "Error 4xx al comunicarse con el servicio externo, "+e.getMessage());
            return null;
        } catch (HttpServerErrorException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error 5xx al comunicarse con el servicio externo", e);
            throw new ServiceException(LogMessages.ERROR_HTTP_SERVER, e);
        } catch (ResourceAccessException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error de conexión con el servicio externo", e);
            throw new ServiceException(LogMessages.ERROR_HTTP_RED, e);
        } catch (RestClientException ex) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error inesperado al comunicarse con el servicio externo", ex);
            throw new ServiceException(LogMessages.ERROR_HTTP, ex);
        }
    }

    @Override
    public OseModel findOseByRucInter(String ruc) {
        String url = getUrlEndpoint()+"/ose?ruc="+ruc;
        try {
            ResponseEntity<OseModel> response = restTemplate.getForEntity(url, OseModel.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LogHelper.warnLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+e.getMessage());
            return null;
        } catch (HttpServerErrorException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error 5xx al comunicarse con el servicio externo", e);
            throw new ServiceException(LogMessages.ERROR_HTTP_SERVER, e);
        } catch (ResourceAccessException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error de conexión con el servicio externo", e);
            throw new ServiceException(LogMessages.ERROR_HTTP_RED, e);
        } catch (RestClientException ex) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error inesperado al comunicarse con el servicio externo", ex);
            throw new ServiceException(LogMessages.ERROR_HTTP, ex);
        }
    }

    private String getUrlEndpoint() {
        return this.baseUrl+this.apiCompanyEndpoint;
    }

}
