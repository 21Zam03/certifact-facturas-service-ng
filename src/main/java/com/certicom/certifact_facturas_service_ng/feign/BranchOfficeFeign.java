package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.BranchOfficesModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "branchOffices")
public interface BranchOfficeFeign {

    @GetMapping("/api/office")
    public BranchOfficesModel obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
            @RequestParam Integer empresaId, @RequestParam String serie, @RequestParam String tipoComprobante
    );

}
