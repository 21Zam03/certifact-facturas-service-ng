package com.certicom.certifact_facturas_service_ng.controller.web;

import com.certicom.certifact_facturas_service_ng.request.VoucherAnnularRequest;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.service.DocumentsVoidedService;
import com.certicom.certifact_facturas_service_ng.sqs.SqsProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(VoidedDocumentsController.API_PATH)
@RequiredArgsConstructor
public class VoidedDocumentsController {

    public final static String API_PATH = "/api/web/facturas";

    private final DocumentsVoidedService documentsVoidedService;
    private final SqsProducer sqsProducer;

    @PostMapping("/anulacion-comprobantes")
    public ResponseEntity<?> anularPaymentVoucher(
            @RequestBody List<VoucherAnnularRequest> documentosToAnular,
            @RequestHeader(name = "X-User-Ruc", required = true) String userRuc,
            @RequestHeader(name = "X-User-Id", required = true) String userId,
            @RequestHeader(name = "X-User-Roles", required = true) String rol
            ) {
        List<String> ticketsVoidedProcess = new ArrayList<>();
        ResponsePSE resp = documentsVoidedService.anularDocuments(
                documentosToAnular,
                "20204040303",
                "demo@certifakt.com.pe", ticketsVoidedProcess);

        ticketsVoidedProcess.forEach(s -> sqsProducer.produceProcessVoided(s, userRuc));
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

}
