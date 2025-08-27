package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.CompanyDto;
import com.certicom.certifact_facturas_service_ng.dto.model.GetStatusCdrDto;
import com.certicom.certifact_facturas_service_ng.dto.model.RegisterFileUploadDto;
import com.certicom.certifact_facturas_service_ng.dto.model.SendBillDto;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponseSunat;
import com.certicom.certifact_facturas_service_ng.entity.PaymentVoucherEntity;
import com.certicom.certifact_facturas_service_ng.entity.PaymentVoucherFileEntity;
import com.certicom.certifact_facturas_service_ng.entity.RegisterFileUploadEntity;
import com.certicom.certifact_facturas_service_ng.entity.TmpVoucherSendBillEntity;
import com.certicom.certifact_facturas_service_ng.enums.*;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.CompanyFeign;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherFeign;
import com.certicom.certifact_facturas_service_ng.feign.RegisterFileUploadFeign;
import com.certicom.certifact_facturas_service_ng.feign.TmpVoucherFeign;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
import com.certicom.certifact_facturas_service_ng.service.SendSunatService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComunicationSunatServiceImpl implements ComunicationSunatService {

    private static final String RECHA = "RECHA";

    private final PaymentVoucherFeign paymentVoucherFeign;
    private final CompanyFeign companyFeign;
    private final TmpVoucherFeign tmpVoucherFeign;
    private final RegisterFileUploadFeign registerFileUploadFeign;

    private final AmazonS3ClientService amazonS3ClientService;
    private final SendSunatService sendSunatService;

    @Override
    public Map<String, Object> sendDocumentBill(String ruc, Long idPaymentVoucher) {
        TmpVoucherSendBillEntity voucherPendiente = null;
        Map<String, Object> result = new HashMap<>();
        ResponsePSE resp = new ResponsePSE();
        ResponseSunat responseSunat;
        ResponseSunat responseSunatCdr;
        String fileXMLZipBase64;
        String messageResponse = null;
        String nombreCompleto;
        Boolean status = null;
        StringBuilder msgLog = new StringBuilder();

        try {
            voucherPendiente = tmpVoucherFeign.findTmpVoucherByIdPaymentVoucher(idPaymentVoucher);

            if (voucherPendiente != null) {
                System.out.println("voucher pendiente existe "+voucherPendiente);

                tmpVoucherFeign.updateStatusVoucherTmp(
                        voucherPendiente.getIdTmpSendBill(),
                        EstadoVoucherTmpEnum.BLOQUEO.getEstado()
                );

                RegisterFileUploadDto registerFileUploadDto = registerFileUploadFeign
                        .findFirst1ByPaymentVoucherIdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(idPaymentVoucher, TipoArchivoEnum.XML.name(),
                                EstadoArchivoEnum.ACTIVO.name());
                System.out.println("REGISTER FILE UPLOAD: "+registerFileUploadDto);

                if (registerFileUploadDto == null)
                    throw new ServiceException("No se encuentra el archivo XML a enviar, por favor edite o regenere el comprobante.");

                fileXMLZipBase64 = amazonS3ClientService.downloadFileStorageInB64(registerFileUploadDto);


                nombreCompleto = voucherPendiente.getNombreDocumento() + "." + ConstantesParameter.TYPE_FILE_ZIP;
                System.out.println("VARIABLES: "+fileXMLZipBase64);
                System.out.println("VARIABLES: "+nombreCompleto);

                responseSunat = sendSunatService.sendBillPaymentVoucher(
                        nombreCompleto,
                        fileXMLZipBase64,
                        ruc
                );

                switch (responseSunat.getEstadoComunicacionSunat()) {
                    case SUCCESS:
                        messageResponse = responseSunat.getMessage();
                        comunicacionSuccess(
                                ruc,
                                voucherPendiente.getTipoComprobante(),
                                voucherPendiente.getIdTmpSendBill(),
                                voucherPendiente.getIdPaymentVoucher(),
                                responseSunat.getContentBase64(),
                                responseSunat.getMessage(),
                                EstadoComprobanteEnum.ACEPTADO.getCodigo(),
                                responseSunat.getNameDocument(),
                                responseSunat.getStatusCode()
                        );
                        status = true;
                        break;
                    case SUCCESS_WITH_WARNING:
                        messageResponse = responseSunat.getMessage();
                        comunicacionSuccess(
                                ruc,
                                voucherPendiente.getTipoComprobante(),
                                voucherPendiente.getIdTmpSendBill(),
                                voucherPendiente.getIdPaymentVoucher(),
                                responseSunat.getContentBase64(),
                                responseSunat.getMessage(),
                                EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA.getCodigo(),
                                responseSunat.getNameDocument(),
                                responseSunat.getStatusCode()
                        );
                        status = true;
                        break;

                    case SUCCESS_WITHOUT_CONTENT_CDR:

                        GetStatusCdrDto dataGetStatusCDR;
                        messageResponse = responseSunat.getMessage();
                        dataGetStatusCDR = comunicacionWithoutContentCDRSendBill(
                                voucherPendiente.getIdTmpSendBill(),
                                voucherPendiente.getIdPaymentVoucher(),
                                messageResponse,
                                voucherPendiente.getNombreDocumento(),
                                responseSunat.getStatusCode());
                        result.put(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR, dataGetStatusCDR);
                        status = false;
                        break;
                    case SUCCESS_WITH_ERROR_CONTENT:
                        if(Integer.parseInt(responseSunat.getStatusCode())==1033){
                            PaymentVoucherEntity paymentVoucherEntity = paymentVoucherFeign.findPaymentVoucherById(idPaymentVoucher);
                            GetStatusCdrDto statusCdrDTO = new GetStatusCdrDto(ruc,paymentVoucherEntity.getTipoComprobante(),paymentVoucherEntity.getSerie(),paymentVoucherEntity.getNumero(),idPaymentVoucher);
                            responseSunatCdr = sendSunatService.getStatusCDR(statusCdrDTO, ruc);

                            messageResponse = "La Factura numero "+paymentVoucherEntity.getSerie()+"-"+paymentVoucherEntity.getNumero()+", ha sido aceptada";

                            RegisterFileUploadEntity responseStorage = uploadFileCdr(ruc, voucherPendiente.getNombreDocumento(), voucherPendiente.getTipoComprobante(),
                                    ConstantesParameter.REGISTRO_STATUS_NUEVO, responseSunatCdr.getContentBase64());

                            paymentVoucherEntity.setEstado(EstadoComprobanteEnum.ACEPTADO.getCodigo());
                            paymentVoucherEntity.setEstadoSunat(EstadoSunatEnum.ACEPTADO.getAbreviado());
                            paymentVoucherEntity.setMensajeRespuesta(messageResponse);
                            paymentVoucherEntity.setCodigosRespuestaSunat("0");
                            System.out.println("SEGUIMIENTO 020");
                            if (responseStorage.getIdRegisterFileSend() != null) {
                                paymentVoucherEntity.getPaymentVoucherFileEntityList().add(
                                        PaymentVoucherFileEntity.builder()
                                                .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                                                .idRegisterFileSend(responseStorage.getIdRegisterFileSend())
                                                .tipoArchivo(TipoArchivoEnum.CDR.name())
                                                .build()
                                );
                            }
                            //ANALIZAR YA QUE SE CAMBIO EL OBJETO PAYMENTCOUCHERENTITY A DTO
                            //paymentVoucherFeign.savePaymentVoucher(paymentVoucherEntity);

                            status = true;
                        }else if(Integer.parseInt(responseSunat.getStatusCode())==140){
                            System.out.println("VUELVA INTENTARLO "+responseSunat.getMessage());
                            comunicacionVolverIntentarSendBill(
                                    idPaymentVoucher,
                                    voucherPendiente.getIdTmpSendBill(),
                                    responseSunat.getMessage(),
                                    responseSunat.getStatusCode()
                            );
                            status = false;
                            messageResponse = responseSunat.getMessage();
                        }else if(Integer.parseInt(responseSunat.getStatusCode())==109 || Integer.parseInt(responseSunat.getStatusCode())==130 ||
                                Integer.parseInt(responseSunat.getStatusCode())==132 || Integer.parseInt(responseSunat.getStatusCode())==133){
                            System.out.println("SERVICIO SUNAT INESTABLE ");
                            comunicacionVolverIntentarSendBill(
                                    idPaymentVoucher,
                                    voucherPendiente.getIdTmpSendBill(),
                                    responseSunat.getMessage(),
                                    responseSunat.getStatusCode()
                            );
                            status = false;
                            messageResponse = responseSunat.getMessage();
                        }else if(responseSunat.getMessage().contains("A security error was encountered")){
                            System.out.println("SERVICIO SUNAT INESTABLE ");
                            comunicacionVolverIntentarSendBill(
                                    idPaymentVoucher,
                                    voucherPendiente.getIdTmpSendBill(),
                                    responseSunat.getMessage(),
                                    responseSunat.getStatusCode()
                            );
                            status = false;
                            messageResponse = responseSunat.getMessage();
                        }else if(Integer.parseInt(responseSunat.getStatusCode())==2800 || Integer.parseInt(responseSunat.getStatusCode())==1083){
                            System.out.println("RECHAZADO POR RECEPTOR "+responseSunat.getMessage());
                            comunicacionReceptorSendBill(
                                    idPaymentVoucher,
                                    voucherPendiente.getIdTmpSendBill(),
                                    responseSunat.getMessage(),
                                    responseSunat.getStatusCode()
                            );
                            status = false;
                            messageResponse = responseSunat.getMessage();

                            //SE ENVIARA CORREO
                            //sendEmailRechazo(voucherPendiente.getIdPaymentVoucher(),messageResponse);
                        }else{
                            comunicacionWithErrorSendBill(
                                    idPaymentVoucher,
                                    voucherPendiente.getIdTmpSendBill(),
                                    responseSunat.getMessage(),
                                    responseSunat.getStatusCode()
                            );
                            status = false;
                            messageResponse = responseSunat.getMessage();
                        }
                        break;
                    case WITHOUT_CONNECTION:
                        if(responseSunat.getMessage().contains("1033")){
                            PaymentVoucherEntity paymentVoucherEntity = paymentVoucherFeign.findPaymentVoucherById(idPaymentVoucher);

                            GetStatusCdrDto statusCdrDTO = new GetStatusCdrDto(ruc,paymentVoucherEntity.getTipoComprobante(),paymentVoucherEntity.getSerie(),paymentVoucherEntity.getNumero(),idPaymentVoucher);

                            responseSunatCdr = sendSunatService.getStatusCDR(statusCdrDTO, ruc);

                            messageResponse = "La Factura numero "+paymentVoucherEntity.getSerie()+"-"+paymentVoucherEntity.getNumero()+", ha sido aceptada";


                            RegisterFileUploadEntity responseStorage = uploadFileCdr(ruc, voucherPendiente.getNombreDocumento(), voucherPendiente.getTipoComprobante(), ConstantesParameter.REGISTRO_STATUS_NUEVO,
                                    responseSunatCdr.getContentBase64());

                            paymentVoucherEntity.setEstado(EstadoComprobanteEnum.ACEPTADO.getCodigo());
                            paymentVoucherEntity.setEstadoSunat(EstadoSunatEnum.ACEPTADO.getAbreviado());
                            paymentVoucherEntity.setMensajeRespuesta(messageResponse);
                            paymentVoucherEntity.setCodigosRespuestaSunat("0");
                            System.out.println("SEGUIMIENTO 021");
                            if (responseStorage.getIdRegisterFileSend() != null) {
                                paymentVoucherEntity.getPaymentVoucherFileEntityList().add(
                                    PaymentVoucherFileEntity.builder()
                                            .estadoArchivo(EstadoArchivoEnum.INACTIVO.name())
                                            .idRegisterFileSend(responseStorage.getIdRegisterFileSend())
                                            .tipoArchivo(TipoArchivoEnum.CDR.name())
                                            .build()
                                );
                            }
                            //ANALIZAR YA QUE SE CAMBIO EL OBJETO PAYMENTCOUCHERENTITY A DTO
                            //paymentVoucherFeign.savePaymentVoucher(paymentVoucherEntity);
                            status = true;
                        }else{
                            System.out.println("Error: Dio error al reenviar");
                            comunicacionWithoutConnectionSendBill(
                                    voucherPendiente.getIdTmpSendBill()
                            );
                            result.put(ConstantesParameter.PARAM_BEAN_SEND_BILL, SendBillDto.builder().ruc(ruc).idPaymentVoucher(idPaymentVoucher).nameDocument(voucherPendiente.getNombreDocumento()).envioAutomaticoSunat(true).build());
                            status = false;
                            messageResponse = responseSunat.getMessage();
                        }
                        break;
                    default:

                }
            } else {
                throw new Exception(
                        "No se pudo entontrar en la tabla temporal id_payment_voucher[" + idPaymentVoucher + "]");
            }
        } catch (Exception e) {

            status = false;
            messageResponse = e.getMessage();

            log.error("Error en sendDocumentBill: {}", messageResponse, e);
        }

        resp.setMensaje(messageResponse);
        resp.setEstado(status);
        resp.setNombre(voucherPendiente.getNombreDocumento());

        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, resp);


        return result;
    }

    private void comunicacionSuccess(String ruc, String tipoComprobante, Long idTmpVoucher, Long idPaymentVoucher,
                                     String contenidoBase64, String mensajeRespuesta, String estadoComprobante,
                                     String nombreDocumento, String codigosRespuesta) throws Exception {


        RegisterFileUploadEntity responseStorage = uploadFileCdr(ruc, nombreDocumento, tipoComprobante, ConstantesParameter.REGISTRO_STATUS_NUEVO,
                contenidoBase64);

        tmpVoucherFeign.deleteTmpVoucherById(idTmpVoucher);

        paymentVoucherFeign.updateStatePaymentVoucher(
                idPaymentVoucher,
                estadoComprobante,
                EstadoSunatEnum.ACEPTADO.getAbreviado(),
                mensajeRespuesta,
                codigosRespuesta
        );
        //AGREGANDO ARCHIVO
        if (responseStorage.getIdRegisterFileSend() != null) {
            PaymentVoucherFileEntity.builder()
                    .orden(2)
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                    .idRegisterFileSend(responseStorage.getIdRegisterFileSend())
                    .tipoArchivo(TipoArchivoEnum.CDR.name())
                    .build();

        }
        //AJUSTE DE STOCK
        //************

    }

    public RegisterFileUploadEntity uploadFileCdr(String rucEmisor, String nameDocument, String tipoComprobante, String estadoRegistro,
                                                  String fileXMLZipBase64) throws Exception {

        CompanyDto companyEntity = companyFeign.findCompanyByRuc(rucEmisor);
        RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64),
                nameDocument, "cdr", companyEntity);
        return file;
    }

    private GetStatusCdrDto comunicacionWithoutContentCDRSendBill(
            Long idTmpSendBill,
            Long idPaymentVoucher,
            String messageResponse,
            String nameDocument,
            String codesResponse) {

        GetStatusCdrDto dataGetStatus;
        String ruc;
        String tipoComprobante;
        String serie;
        Integer numero;

        tmpVoucherFeign.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.VERIFICAR.getEstado());
        paymentVoucherFeign.updateStatePaymentVoucher(
                idPaymentVoucher,
                EstadoComprobanteEnum.ACEPTADO_POR_VERIFICAR.getCodigo(),
                messageResponse,
                codesResponse);

        String[] datosComprobante = nameDocument.split("-");
        ruc = datosComprobante[0];
        tipoComprobante = datosComprobante[1];
        serie = datosComprobante[2];
        numero = Integer.valueOf(datosComprobante[3]);

        dataGetStatus = new GetStatusCdrDto();
        dataGetStatus.setRuc(ruc);
        dataGetStatus.setTipoComprobante(tipoComprobante);
        dataGetStatus.setSerie(serie);
        dataGetStatus.setNumero(numero);
        dataGetStatus.setIdPaymentVoucher(idPaymentVoucher);

        return dataGetStatus;
    }

    private void comunicacionVolverIntentarSendBill(
            Long idPaymentVoucher,
            Long idTmpSendBill,
            String messageResponse,
            String codesResponse) {

        tmpVoucherFeign.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.PENDIENTE.getEstado());
        paymentVoucherFeign.updateStatePaymentVoucher(
                idPaymentVoucher,
                EstadoComprobanteEnum.REGISTRADO.getCodigo(),
                messageResponse,
                codesResponse);
    }

    private void comunicacionReceptorSendBill(Long idPaymentVoucher, Long idTmpSendBill, String message, String statusCode) {
        tmpVoucherFeign.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.ERROR.getEstado());
        paymentVoucherFeign.updateStatePaymentVoucher(
                idPaymentVoucher,
                EstadoComprobanteEnum.RECHAZADO.getCodigo(),
                RECHA,
                message,
                statusCode);

    }

    private void comunicacionWithErrorSendBill(
            Long idPaymentVoucher,
            Long idTmpSendBill,
            String messageResponse,
            String codesResponse) {

        tmpVoucherFeign.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.ERROR.getEstado());
        paymentVoucherFeign.updateStatePaymentVoucher(
                idPaymentVoucher,
                EstadoComprobanteEnum.ERROR.getCodigo(),
                messageResponse,
                codesResponse);
    }

    private void comunicacionWithoutConnectionSendBill(Long idTmpSendBill) {

        tmpVoucherFeign.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.PENDIENTE.getEstado()
        );
    }

}
