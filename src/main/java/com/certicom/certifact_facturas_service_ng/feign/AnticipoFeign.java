package com.certicom.certifact_facturas_service_ng.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "anticipoPaymentVoucher")
public interface AnticipoFeign {

    @DeleteMapping("/api/anticipo/{id}")
    public int deleteAnticipoById(@PathVariable("id") Long anticipoId);

}
