package com.certicom.certifact_facturas_service_ng.controller.web;

import com.certicom.certifact_facturas_service_ng.converter.PaymentVoucherConverter;
import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.request.IdentificadorPaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.request.PaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
import com.certicom.certifact_facturas_service_ng.service.SendSunatService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.validation.business.PaymentVoucherValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public static final String API_PATH = "/api";
    private final PaymentVoucherService paymentVoucherService;
    private final PaymentVoucherValidator paymentVoucherValidator;
    private final ComunicationSunatService comunicationSunatService;
    private final SendSunatService sendSunatService;

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

    @PostMapping("/v1/factura/comprobantes-pago")
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

    @PutMapping("/v1/factura/editar-comprobante")
    public ResponseEntity<?> editPaymentVoucher(@RequestBody @Valid PaymentVoucherRequest paymentVoucherRequest) {
        //Id usuario por defecto va ir en duro hasta saber como identificar al usuario que haral a peticion desde el gateway
        Long idUsuario = 2L;
        String rucEmisor = "20204040303";
        paymentVoucherRequest.setRucEmisor(rucEmisor);

        PaymentVoucherDto paymentVoucherDto = PaymentVoucherConverter.requestToModel(paymentVoucherRequest);
        paymentVoucherValidator.validate(paymentVoucherDto, true);
        Map<String, Object> result = paymentVoucherService.updatePaymentVoucher(paymentVoucherDto,  idUsuario);
        return new ResponseEntity<>(result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE), HttpStatus.CREATED);
    }

    @PostMapping("/v1/factura/comprobantes/enviar-sunat")
    public ResponseEntity<?> sendPaymentVoucherToSunat(
            @RequestBody IdentificadorPaymentVoucherRequest paymentVoucher
    ) {
        PaymentVoucherDto paymentVoucherDtoDto = sendSunatService.prepareComprobanteForEnvioSunatInter("20204040303", paymentVoucher.getTipo(), paymentVoucher.getSerie(), paymentVoucher.getNumero());
        Map<String, Object> result = comunicationSunatService.sendDocumentBill(paymentVoucherDtoDto.getRucEmisor(), paymentVoucherDtoDto.getIdPaymentVoucher());
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

    @GetMapping("/v1/factura/siguienteNumero/{tipoDocumento}/{serie}")
    public ResponseEntity<?> ultimoComprobante(
            @PathVariable String tipoDocumento,
            @PathVariable String serie) {
        String ruc = "20204040303";
        return new ResponseEntity<Object>(paymentVoucherService.getSiguienteNumeroComprobante(tipoDocumento, serie, ruc), HttpStatus.OK);
    }

    @GetMapping("/v1/factura/comprobantes-anticipo")
    public ResponseEntity<List<PaymentVoucherDto>> comprobantesAnticipo(
            @RequestParam(name = "filtroNumDoc", required = true) String filtroNumDoc) {
        String ruc = "20204040303";
        return new ResponseEntity<List<PaymentVoucherDto>>(paymentVoucherService.findComprobanteByAnticipo(filtroNumDoc, ruc), HttpStatus.OK);
    }

    @GetMapping("/v1/factura/comprobantes-credito")
    public ResponseEntity<List<PaymentVoucherDto>> comprobantesCredito(
            @RequestParam(name = "filtroNumDoc", required = true) String filtroNumDoc) {
        String ruc = "20204040303";
        return new ResponseEntity<List<PaymentVoucherDto>>(paymentVoucherService.findComprobanteByCredito(filtroNumDoc, ruc), HttpStatus.OK);
    }

    @PostMapping("/getEstadosSunat")
    public ResponseEntity<?> getEstadosSunat(@RequestBody List<Long> idsPaymentVouchers) {

        return new ResponseEntity<Object>(paymentVoucherService.getEstadoSunatByListaIdsInter(idsPaymentVouchers), HttpStatus.OK);
    }

}
