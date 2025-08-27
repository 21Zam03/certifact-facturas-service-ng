package com.certicom.certifact_facturas_service_ng.controller;

import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.request.IdentificadorPaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping(SunatController.API_PATH)
@RestController
@RequiredArgsConstructor
public class SunatController {

    public static final String API_PATH = "/api/sunat";
    private final ComunicationSunatService comunicationSunatService;
    private final PaymentVoucherService paymentVoucherService;


    @PostMapping("send")
    public ResponseEntity<?> sendPaymentVoucherToSunat(
            @RequestBody @Valid IdentificadorPaymentVoucherRequest paymentVoucher
    ) {
        PaymentVoucherDto paymentVoucherDto = paymentVoucherService.prepareComprobanteForEnvioSunatInter("20204040303", paymentVoucher.getTipo(), paymentVoucher.getSerie(), paymentVoucher.getNumero());
        Map<String, Object> result = comunicationSunatService.sendDocumentBill(paymentVoucherDto.getRucEmisor(), paymentVoucherDto.getIdPaymentVoucher());

        return new ResponseEntity<>("TEST", HttpStatus.OK);
    }

    @PostMapping("/avoid")
    public ResponseEntity<?> anularPaymentVoucher() {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
