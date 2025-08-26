package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.entity.ErrorEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "errorCatalogo")
public interface ErrorCatalogFeign {

    @GetMapping("/api/invoice-sp/error-catalog")
    public ErrorEntity findFirst1ByCodeAndDocument(@RequestParam String codigoRespuesta, @RequestParam String tipoDocumento);

}
