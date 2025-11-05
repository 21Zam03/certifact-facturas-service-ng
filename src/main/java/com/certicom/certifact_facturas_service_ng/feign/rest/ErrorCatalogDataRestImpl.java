package com.certicom.certifact_facturas_service_ng.feign.rest;

import com.certicom.certifact_facturas_service_ng.entity.ErrorEntity;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.ErrorCatalogData;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ErrorCatalogDataRestImpl implements ErrorCatalogData {

    private final RestTemplate restTemplate;

    @Value("${external.services.factura-service-sp.base-url}")
    private String baseUrl;

    @Value("${external.services.factura-service-sp.endpoints.api-error-catalog}")
    private String apiErrorCatalog;

    public ErrorCatalogDataRestImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ErrorEntity findFirst1ByCodeAndDocument(String codigoRespuesta, String tipoDocumento) {
        String url = UriComponentsBuilder
                .fromHttpUrl(getUrlEndpoint())
                .queryParam("codigoRespuesta", codigoRespuesta)
                .queryParam("tipoDocumento", tipoDocumento)
                .toUriString();
        try {
            return restTemplate.getForObject(url, ErrorEntity.class);
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

    private String getUrlEndpoint() {
        return this.baseUrl+this.apiErrorCatalog;
    }

}
