package com.certicom.certifact_facturas_service_ng.controller.web;

import com.certicom.certifact_facturas_service_ng.converter.PaymentVoucherConverter;
import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.EmailSendDto;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.request.IdentificadorPaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.request.PaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
import com.certicom.certifact_facturas_service_ng.service.SendSunatService;
import com.certicom.certifact_facturas_service_ng.sqs.SqsProducer;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.validation.business.PaymentVoucherValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PaymentVoucherController.API_PATH)
@RequiredArgsConstructor
@Slf4j
public class PaymentVoucherController {

    public static final String API_PATH = "/api/web/facturas";
    private final PaymentVoucherService paymentVoucherService;
    private final PaymentVoucherValidator paymentVoucherValidator;
    private final ComunicationSunatService comunicationSunatService;
    private final SendSunatService sendSunatService;
    private final SqsProducer sqsProducer;

    @GetMapping("/comprobantes")
    public ResponseEntity<?> findPaymentVoucherWithFilter(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "pageNumber", required = true) Integer pageNumber,
            @RequestParam(name = "perPage", required = true) Integer perPage,
            @RequestParam(name = "estadoSunat", required = false) Integer estadoSunat,
            @RequestHeader(name = "X-User-Ruc", required = true) String userRuc,
            @RequestHeader(name = "X-User-Id", required = true) Long userId,
            @RequestHeader(name = "X-User-Roles", required = true) List<String> rol
    ) {
        Map<String, Object> paginacionComprobantes = paymentVoucherService.findPaymentVoucherWithFilter(
                filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat, userId
        );
        return new ResponseEntity<>(paginacionComprobantes, HttpStatus.OK);
    }

    @PostMapping("/comprobantes-pago")
    public ResponseEntity<?> savePaymentVoucher(
            @RequestBody @Valid PaymentVoucherRequest paymentVoucherRequest,
            @RequestHeader(name = "X-User-Ruc", required = true) String userRuc,
            @RequestHeader(name = "X-User-Id", required = true) String userId,
            @RequestHeader(name = "X-User-Roles", required = true) String rol
    ) {
        MDC.put("payment_voucher",  paymentVoucherRequest.getSerie()+"-"+paymentVoucherRequest.getNumero());

        paymentVoucherRequest.setRucEmisor(userRuc);

        PaymentVoucherDto paymentVoucherDto = PaymentVoucherConverter.requestToModel(paymentVoucherRequest);

        paymentVoucherValidator.validate(paymentVoucherDto, false);

        Map<String, Object> result = paymentVoucherService.createPaymentVoucher(paymentVoucherDto, Long.valueOf(userId));

        return new ResponseEntity<>(result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE), HttpStatus.CREATED);
    }

    @PutMapping("/editar-comprobante")
    public ResponseEntity<?> editPaymentVoucher(
            @RequestBody @Valid PaymentVoucherRequest paymentVoucherRequest,
            @RequestHeader(name = "X-User-Ruc", required = true) String ruc,
            @RequestHeader(name = "X-User-Id", required = true) Long usuarioId,
            @RequestHeader(name = "X-User-Roles", required = true) List<String> roles
    ) {
        MDC.put("payment_voucher",  paymentVoucherRequest.getSerie()+"-"+paymentVoucherRequest.getNumero());

        paymentVoucherRequest.setRucEmisor(ruc);

        PaymentVoucherDto paymentVoucherDto = PaymentVoucherConverter.requestToModel(paymentVoucherRequest);

        paymentVoucherValidator.validate(paymentVoucherDto, true);

        Map<String, Object> result = paymentVoucherService.updatePaymentVoucher(paymentVoucherDto,  usuarioId);

        return new ResponseEntity<>(result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE), HttpStatus.CREATED);
    }

    @PostMapping("/comprobantes/enviar-sunat")
    public ResponseEntity<?> sendPaymentVoucherToSunat(
            @RequestBody IdentificadorPaymentVoucherRequest paymentVoucher,
            @RequestHeader(name = "X-User-Ruc", required = true) String ruc,
            @RequestHeader(name = "X-User-Id", required = true) Long usuarioId,
            @RequestHeader(name = "X-User-Roles", required = true) List<String> roles
    ) {
        PaymentVoucherDto paymentVoucherDtoDto = sendSunatService.prepareComprobanteForEnvioSunatInter(ruc, paymentVoucher.getTipo(), paymentVoucher.getSerie(), paymentVoucher.getNumero());
        Map<String, Object> result = comunicationSunatService.sendDocumentBill(paymentVoucherDtoDto.getRucEmisor(), paymentVoucherDtoDto.getIdPaymentVoucher());
        ResponsePSE resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

        if (resp.getEstado()) {
            sqsProducer.produceEnviarCorreo(EmailSendDto.builder().id(paymentVoucherDtoDto.getIdPaymentVoucher()).build());
        }

        if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {
            System.out.println("producir la cdr");
            //GetStatusCdrDTO dataGetStatusCDR = (GetStatusCdrDTO) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
            //messageProducer.produceGetStatusCDR(dataGetStatusCDR);
        }

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping("/siguienteNumero/{tipoDocumento}/{serie}")
    public ResponseEntity<?> ultimoComprobante(
            @PathVariable String tipoDocumento,
            @PathVariable String serie,
            @RequestHeader(name = "X-User-Ruc", required = true) String ruc,
            @RequestHeader(name = "X-User-Id", required = true) Long usuarioId,
            @RequestHeader(name = "X-User-Roles", required = true) List<String> roles) {
        return new ResponseEntity<Object>(paymentVoucherService.getSiguienteNumeroComprobante(tipoDocumento, serie, ruc), HttpStatus.OK);
    }

    @GetMapping("/comprobantes-anticipo")
    public ResponseEntity<List<PaymentVoucherDto>> comprobantesAnticipo(
            @RequestParam(name = "filtroNumDoc", required = true) String filtroNumDoc,
            @RequestHeader(name = "X-User-Ruc", required = true) String ruc,
            @RequestHeader(name = "X-User-Id", required = true) Long usuarioId,
            @RequestHeader(name = "X-User-Roles", required = true) List<String> roles) {
        return new ResponseEntity<List<PaymentVoucherDto>>(paymentVoucherService.findComprobanteByAnticipo(filtroNumDoc, ruc), HttpStatus.OK);
    }

    @GetMapping("/comprobantes-credito")
    public ResponseEntity<List<PaymentVoucherDto>> comprobantesCredito(
            @RequestParam(name = "filtroNumDoc", required = true) String filtroNumDoc,
            @RequestHeader(name = "X-User-Ruc", required = true) String ruc,
            @RequestHeader(name = "X-User-Id", required = true) Long usuarioId,
            @RequestHeader(name = "X-User-Roles", required = true) List<String> roles) {
        return new ResponseEntity<List<PaymentVoucherDto>>(paymentVoucherService.findComprobanteByCredito(filtroNumDoc, ruc), HttpStatus.OK);
    }

    @PostMapping("/getEstadosSunat")
    public ResponseEntity<?> getEstadosSunat(
            @RequestBody List<Long> idsPaymentVouchers,
            @RequestHeader(name = "X-User-Ruc", required = true) String ruc,
            @RequestHeader(name = "X-User-Id", required = true) Long usuarioId,
            @RequestHeader(name = "X-User-Roles", required = true) List<String> roles) {
        return new ResponseEntity<Object>(paymentVoucherService.getEstadoSunatByListaIdsInter(idsPaymentVouchers), HttpStatus.OK);
    }

    @GetMapping("/comprobanteById")
    public ResponseEntity<?> comprobanteById(
            @RequestParam(name = "id", required = true) Long id,
            @RequestHeader(name = "X-User-Ruc", required = true) String ruc,
            @RequestHeader(name = "X-User-Id", required = true) Long usuarioId,
            @RequestHeader(name = "X-User-Roles", required = true) List<String> roles) {
        return new ResponseEntity<Object>(paymentVoucherService.getComprobanteById(id), HttpStatus.OK);
    }

}
