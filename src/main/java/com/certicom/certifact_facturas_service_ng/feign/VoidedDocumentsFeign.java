package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.VoidedDocumentsModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "facturas-service-sp", url = "${external.services.factura-service-sp.base-url}", contextId = "voidedDocuments")
public interface VoidedDocumentsFeign {

    @GetMapping("/api/voided-documents/correlativo")
    public Integer getCorrelativoGeneracionByDiaInVoidedDocuments(
            @RequestParam String ruc,
            @RequestParam String fechaGeneracionBaja);

    @PostMapping("/api/voided-documents")
    public VoidedDocumentsModel save(@RequestBody VoidedDocumentsModel voidedDocumentsModel);

    @PutMapping("/api/voided-documents")
    public VoidedDocumentsModel update(@RequestBody VoidedDocumentsModel voidedDocumentsModel);

    @GetMapping("/api/voided-documents/ticket")
    VoidedDocumentsModel getVoidedByTicket(@RequestParam String ticket);

    @GetMapping("/api/voided-documents/ticket/state")
    String getEstadoByNumeroTicket(@RequestParam String ticket);

}
