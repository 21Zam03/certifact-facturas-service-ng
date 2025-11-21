package com.certicom.certifact_facturas_service_ng.feign.rest;

import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherFileData;
import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherFileModel;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Slf4j
@Service
public class PaymentVoucherFileDataRestImpl implements PaymentVoucherFileData {

    private final RestTemplate restTemplate;

    @Value("${external.services.factura-service-sp.base-url}")
    private String baseUrl;

    @Value("${external.services.factura-service-sp.endpoints.api-paymentvoucherfile-endpoint}")
    private String apiPaymentVoucherFileEndpoint;

    public PaymentVoucherFileDataRestImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getUrlEndpoint() {
        return this.baseUrl+this.apiPaymentVoucherFileEndpoint;
    }

    @Override
    public Integer save(PaymentVoucherFileModel paymentVoucherFileModel) {
        String url = getUrlEndpoint();
        try {
            ResponseEntity<Integer> response = restTemplate.postForEntity(
                    url,
                    paymentVoucherFileModel,
                    Integer.class
            );
            LogHelper.infoLog(LogMessages.currentMethod(), "Se registro el paymentVoucherFileModel de manera exitosa");
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
    public Long findActiveXMLIdRegisterFileSendByIdPaymentVoucher(Long id) {

        return 0L;
    }

}
