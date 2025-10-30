package com.certicom.certifact_facturas_service_ng.controller.api;

import com.certicom.certifact_facturas_service_ng.converter.PaymentVoucherConverter;
import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.request.PaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.validation.business.PaymentVoucherValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(PaymentVoucherApi.API_PATH)
@RequiredArgsConstructor
@Slf4j
public class PaymentVoucherApi {

    public final static String API_PATH = "/api/v1/facturas";

    private final PaymentVoucherService paymentVoucherService;
    private final PaymentVoucherValidator paymentVoucherValidator;

    @PostMapping("/comprobantes-pago")
    public ResponseEntity<?> savePaymentVoucher(@RequestBody @Valid PaymentVoucherRequest paymentVoucherRequest) {
        //Id usuario por defecto va ir en duro hasta saber como identificar al usuario que haral a peticion desde el gateway
        Long idUsuario = 2L;
        String rucEmisor = "20204040303";
        paymentVoucherRequest.setRucEmisor(rucEmisor);

        PaymentVoucherDto paymentVoucherDto = PaymentVoucherConverter.requestToModel(paymentVoucherRequest);
        paymentVoucherValidator.validate(paymentVoucherDto, false);
        Map<String, Object> result = paymentVoucherService.createPaymentVoucher(paymentVoucherDto, idUsuario);
        return new ResponseEntity<>(result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE), HttpStatus.CREATED);
    }

}
