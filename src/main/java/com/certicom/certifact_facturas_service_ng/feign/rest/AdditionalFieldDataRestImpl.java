package com.certicom.certifact_facturas_service_ng.feign.rest;

import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.AdditionalFieldData;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
public class AdditionalFieldDataRestImpl implements AdditionalFieldData {

    private final RestTemplate restTemplate;

    @Value("${external.services.factura-service-sp.base-url}")
    private String baseUrl;

    @Value("${external.services.factura-service-sp.endpoints.api-aditionalfieldpayment-endpoint}")
    private String apiAdditionalFieldEndpoint;

    public AdditionalFieldDataRestImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getUrlEndpoint() {
        return this.baseUrl+this.apiAdditionalFieldEndpoint;
    }

    @Override
    public int deleteAditionalFieldPaymentById(Long aditionalPaymentId) {
        String url = getUrlEndpoint()+"/"+aditionalPaymentId;
        try {
            restTemplate.delete(url);
            return 1;
        } catch (HttpClientErrorException e) {
            LogHelper.warnLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+e.getMessage());
            return 0;
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

}
