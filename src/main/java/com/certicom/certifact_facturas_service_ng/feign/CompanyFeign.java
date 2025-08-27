package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.dto.model.CompanyDto;
import com.certicom.certifact_facturas_service_ng.dto.model.OseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "company")
public interface CompanyFeign {

    @GetMapping("/api/company/state")
    public String getStateFromCompanyByRuc(@RequestParam String rucEmisor);

    @GetMapping("/api/company/{ruc}")
    public CompanyDto findCompanyByRuc(@PathVariable String ruc);

    @GetMapping("/api/company/ose")
    public OseDto findOseByRucInter(@RequestParam String ruc);

}
