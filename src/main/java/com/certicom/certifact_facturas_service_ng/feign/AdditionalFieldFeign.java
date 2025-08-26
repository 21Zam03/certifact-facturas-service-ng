package com.certicom.certifact_facturas_service_ng.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "additionalField")
public interface AdditionalFieldFeign {

    @GetMapping("/api/invoice-sp/additional-field")
    public Integer obtenerCampoAdicionalIdPorNombre(@RequestParam String nombreCampo);

}
