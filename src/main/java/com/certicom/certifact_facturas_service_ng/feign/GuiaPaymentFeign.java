package com.certicom.certifact_facturas_service_ng.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "guiaPayment")
public interface GuiaPaymentFeign {

    @DeleteMapping("/api/guia-payment-voucher/{id}")
    public int deleteGuiaPaymentById(@PathVariable("id") Long guiaPaymentId);

}
