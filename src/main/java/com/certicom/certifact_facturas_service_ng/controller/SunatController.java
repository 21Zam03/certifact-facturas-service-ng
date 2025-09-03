package com.certicom.certifact_facturas_service_ng.controller;

import com.certicom.certifact_facturas_service_ng.model.PaymentVoucher;
import com.certicom.certifact_facturas_service_ng.dto.request.VoucherAnnularRequest;
import com.certicom.certifact_facturas_service_ng.dto.request.IdentificadorPaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
import com.certicom.certifact_facturas_service_ng.service.DocumentsVoidedService;
import com.certicom.certifact_facturas_service_ng.service.SendSunatService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping(SunatController.API_PATH)
@RestController
@RequiredArgsConstructor
public class SunatController {

    public static final String API_PATH = "/api/sunat";
    private final ComunicationSunatService comunicationSunatService;
    private final SendSunatService sendSunatService;
    private final DocumentsVoidedService documentsVoidedService;

    @PostMapping("send")
    public ResponseEntity<?> sendPaymentVoucherToSunat(
            @RequestBody @Valid IdentificadorPaymentVoucherRequest paymentVoucher
    ) {
        PaymentVoucher paymentVoucherDto = sendSunatService.prepareComprobanteForEnvioSunatInter("20204040303", paymentVoucher.getTipo(), paymentVoucher.getSerie(), paymentVoucher.getNumero());
        Map<String, Object> result = comunicationSunatService.sendDocumentBill(paymentVoucherDto.getRucEmisor(), paymentVoucherDto.getIdPaymentVoucher());
        ResponsePSE resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        if (resp.getEstado()) {
            System.out.println("ENVIAR correo");
            //messageProducer.produceEnviarCorreo(EmailSendDTO.builder().id(paymentVoucherEntity.getIdPaymentVoucher()).build());
        }
        if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {
            System.out.println("producir la cdr");
            //GetStatusCdrDTO dataGetStatusCDR = (GetStatusCdrDTO) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
            //messageProducer.produceGetStatusCDR(dataGetStatusCDR);
        }
        System.out.println("RESPUESTA: "+resp);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @PostMapping("/avoid")
    public ResponseEntity<?> anularPaymentVoucher(@RequestBody List<VoucherAnnularRequest> documentosToAnular) {
        List<String> ticketsVoidedProcess = new ArrayList<>();
        ResponsePSE resp = documentsVoidedService.anularDocuments(
                documentosToAnular,
                "20204040303",
                "demo@certifakt.com.pe", ticketsVoidedProcess);


        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
