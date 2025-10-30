package com.certicom.certifact_facturas_service_ng.controller.api;

import com.certicom.certifact_facturas_service_ng.controller.web.VoidedDocumentsController;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.request.VoucherAnnularRequest;
import com.certicom.certifact_facturas_service_ng.service.DocumentsVoidedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(VoidedDocumentsControllerApi.API_PATH)
@RequiredArgsConstructor
public class VoidedDocumentsControllerApi {

    public final static String API_PATH = "/api/v1/facturas";

    private final DocumentsVoidedService documentsVoidedService;

    @PostMapping("/anulacion-comprobantes")
    public ResponseEntity<?> anularPaymentVoucher(@RequestBody List<VoucherAnnularRequest> documentosToAnular) {
        List<String> ticketsVoidedProcess = new ArrayList<>();
        ResponsePSE resp = documentsVoidedService.anularDocuments(
                documentosToAnular,
                "20204040303",
                "demo@certifakt.com.pe", ticketsVoidedProcess);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}
