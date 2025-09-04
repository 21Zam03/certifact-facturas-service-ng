package com.certicom.certifact_facturas_service_ng.controller;

import com.certicom.certifact_facturas_service_ng.converter.PaymentVoucherConverter;
import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherModel;
import com.certicom.certifact_facturas_service_ng.dto.request.PaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.validation.business.PaymentVoucherValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(PaymentVoucherController.API_PATH)
@RequiredArgsConstructor
@Slf4j
public class PaymentVoucherController {

    public static final String API_PATH = "/api/v1/facturas";
    private final PaymentVoucherService paymentVoucherService;
    private final PaymentVoucherValidator paymentVoucherValidator;

    @GetMapping
    public ResponseEntity<?> findPaymentVoucherWithFilter(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "pageNumber", required = true) Integer pageNumber,
            @RequestParam(name = "perPage", required = true) Integer perPage,
            @RequestParam(name = "estadoSunat", required = false) Integer estadoSunat
    ) {
        Long idUsuario = 2L;
        log.info("ComprobanteController - listarComprobantesConfiltros - [filtroDesde={}, filtroHasta={}, filtroTipoComprobante={}, fltroRuc={}, " +
                        "filtroSerie={}, filtroNumero={}, pageNumber={}, perPage={}, estadoSunat={}, idUsuario={}]",
                filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat, idUsuario);
        Map<String, Object> paginacionComprobantes = paymentVoucherService.findPaymentVoucherWithFilter(
                filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat, idUsuario);
        return new ResponseEntity<>(paginacionComprobantes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> savePaymentVoucher(@RequestBody @Valid PaymentVoucherRequest paymentVoucherRequest) {
        Long idUsuario = 2L;
        watchLog(paymentVoucherRequest);
        PaymentVoucherModel paymentVoucherModel = PaymentVoucherConverter.requestToModel(paymentVoucherRequest);
        paymentVoucherValidator.validate(paymentVoucherModel, false);
        Map<String, Object> result = paymentVoucherService.createPaymentVoucher(paymentVoucherModel, idUsuario);
        return new ResponseEntity<>(result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE), HttpStatus.CREATED);
        //return new ResponseEntity<>("TEST", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> editPaymentVoucher(@RequestBody @Valid PaymentVoucherRequest paymentVoucherRequest) {
        Long idUsuario = 2L;
        watchLog(paymentVoucherRequest);
        PaymentVoucherModel paymentVoucherModel = PaymentVoucherConverter.requestToModel(paymentVoucherRequest);
        paymentVoucherValidator.validate(paymentVoucherModel, true);
        Map<String, Object> result = paymentVoucherService.updatePaymentVoucher(paymentVoucherModel,  idUsuario);
        return new ResponseEntity<>(result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE), HttpStatus.CREATED);
    }

    private void watchLog(Object object) {
        log.info("ComprobanteController - watchLog - [object={}]", object.toString());
    }

}
