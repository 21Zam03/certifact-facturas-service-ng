package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.Parameter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "parameter")
public interface ParameterFeign {

    @GetMapping("/api/parameter/name")
    Parameter findByName(@RequestParam String name);

}
