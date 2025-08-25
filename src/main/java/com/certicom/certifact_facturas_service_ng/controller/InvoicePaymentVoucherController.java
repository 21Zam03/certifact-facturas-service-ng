package com.certicom.certifact_facturas_service_ng.controller;

import com.certicom.certifact_facturas_service_ng.converter.PaymentVoucherConverter;
import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.request.IdentificadorPaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.dto.request.PaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
import com.certicom.certifact_facturas_service_ng.validation.business.PaymentVoucherValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(InvoicePaymentVoucherController.API_PATH)
@RequiredArgsConstructor
@Slf4j
public class InvoicePaymentVoucherController {

    public static final String API_PATH = "/api/invoice-ng";
    private final PaymentVoucherService paymentVoucherService;
    private final ComunicationSunatService comunicationSunatService;
    private final PaymentVoucherValidator paymentVoucherValidator;

    @GetMapping("/payment-voucher")
    public ResponseEntity<?> listarComprobantesConFiltros(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "pageNumber", required = true) Integer pageNumber,
            @RequestParam(name = "perPage", required = true) Integer perPage,
            @RequestParam(name = "estadoSunat", required = false) Integer estadoSunat,
            @RequestParam(name = "idUsuario", required = true) Long idUsuario
    ) {
        log.info("ComprobanteController - listarComprobantesConfiltros - [filtroDesde={}, filtroHasta={}, filtroTipoComprobante={}, fltroRuc={}, " +
                        "filtroSerie={}, filtroNumero={}, pageNumber={}, perPage={}, estadoSunat={}, idUsuario={}]",
                filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat, idUsuario);
        Map<String, Object> paginacionComprobantes = paymentVoucherService.obtenerComprobantesEstadoPorFiltro(
                filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat, idUsuario);
        return new ResponseEntity<>(paginacionComprobantes, HttpStatus.OK);
    }

    @PostMapping("/payment-voucher")
    public ResponseEntity<?> savePaymentVoucher(@RequestBody @Valid PaymentVoucherRequest paymentVoucherRequest) {
        log.info("ComprobanteController - registrarComprobante - [comprobanteRequest={}]", paymentVoucherRequest.toString());
        PaymentVoucherDto paymentVoucherDto = PaymentVoucherConverter.requestToDto(paymentVoucherRequest);
        paymentVoucherValidator.validate(paymentVoucherDto, false);
        //Map<String, Object> result = paymentVoucherService.generatePaymentVoucher(paymentVoucherDto, false, 2L);
        //return new ResponseEntity<>(result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE), HttpStatus.CREATED);
        return new ResponseEntity<>("TEST", HttpStatus.OK);
    }

    @PutMapping("/payment-voucher")
    public ResponseEntity<?> editPaymentVoucher(@RequestBody @Valid PaymentVoucherRequest paymentVoucherRequest) {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping("send-sunat")
    public ResponseEntity<?> sendPaymentVoucherToSunat(
            @RequestBody @Valid IdentificadorPaymentVoucherRequest paymentVoucher
            ) {
        PaymentVoucherDto paymentVoucherDto = paymentVoucherService.prepareComprobanteForEnvioSunatInter("20204040303", paymentVoucher.getTipo(), paymentVoucher.getSerie(), paymentVoucher.getNumero());
        Map<String, Object> result = comunicationSunatService.sendDocumentBill(paymentVoucherDto.getRucEmisor(), paymentVoucherDto.getIdPaymentVoucher());


        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
