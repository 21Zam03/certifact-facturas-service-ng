package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.VoidedDocumentsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "voidedDocuments")
public interface VoidedDocumentsFeign {

    @GetMapping("/api/voided-documents/correlativo")
    public Integer getCorrelativoGeneracionByDiaInVoidedDocuments(
            @RequestParam String ruc,
            @RequestParam String fechaGeneracionBaja);

    @PostMapping("/api/voided-documents")
    public VoidedDocumentsDto save(VoidedDocumentsDto voidedDocumentsDto);
}
