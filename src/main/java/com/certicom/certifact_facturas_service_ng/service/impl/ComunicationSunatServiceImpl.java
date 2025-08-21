package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.RegisterFileUploadDto;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponseSunat;
import com.certicom.certifact_facturas_service_ng.entity.TmpVoucherSendBillEntity;
import com.certicom.certifact_facturas_service_ng.enums.EstadoArchivoEnum;
import com.certicom.certifact_facturas_service_ng.enums.EstadoVoucherTmpEnum;
import com.certicom.certifact_facturas_service_ng.enums.TipoArchivoEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.FacturaComprobanteFeign;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
import com.certicom.certifact_facturas_service_ng.service.SendSunatService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParametro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComunicationSunatServiceImpl implements ComunicationSunatService {

    private final FacturaComprobanteFeign facturaComprobanteFeign;
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
            voucherPendiente = facturaComprobanteFeign.findTmpVoucherByIdPaymentVoucher(idPaymentVoucher);

            if (voucherPendiente != null) {

                facturaComprobanteFeign.updateStatusVoucherTmp(
                        voucherPendiente.getIdTmpSendBill(),
                        EstadoVoucherTmpEnum.BLOQUEO.getEstado()
                );

                RegisterFileUploadDto registerFileUploadDto = facturaComprobanteFeign
                        .findFirst1ByPaymentVoucherIdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(idPaymentVoucher, TipoArchivoEnum.XML.name(),
                                EstadoArchivoEnum.ACTIVO.name());

                if (registerFileUploadDto == null)
                    throw new ServiceException("No se encuentra el archivo XML a enviar, por favor edite o regenere el comprobante.");

                fileXMLZipBase64 = amazonS3ClientService.downloadFileStorageInB64(registerFileUploadDto);


                nombreCompleto = voucherPendiente.getNombreDocumento() + "." + ConstantesParametro.TYPE_FILE_ZIP;

                responseSunat = sendSunatService.sendBillPaymentVoucher(
                        nombreCompleto,
                        fileXMLZipBase64,ruc
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

                        GetStatusCdrDTO dataGetStatusCDR;
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
                            PaymentVoucherEntity paymentVoucherEntity = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);
                            GetStatusCdrDTO statusCdrDTO = new GetStatusCdrDTO(ruc,paymentVoucherEntity.getTipoComprobante(),paymentVoucherEntity.getSerie(),paymentVoucherEntity.getNumero(),idPaymentVoucher);
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
                                paymentVoucherEntity.addFile(PaymentVoucherFileEntity.builder()
                                        .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                                        .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(responseStorage.getIdRegisterFileSend()).build())
                                        .tipoArchivo(TipoArchivoEnum.CDR)
                                        .build());
                            }
                            paymentVoucherRepository.save(paymentVoucherEntity);

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
                            sendEmailRechazo(voucherPendiente.getIdPaymentVoucher(),messageResponse);
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
                            PaymentVoucherEntity paymentVoucherEntity = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);

                            GetStatusCdrDTO statusCdrDTO = new GetStatusCdrDTO(ruc,paymentVoucherEntity.getTipoComprobante(),paymentVoucherEntity.getSerie(),paymentVoucherEntity.getNumero(),idPaymentVoucher);

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
                                paymentVoucherEntity.addFile(PaymentVoucherFileEntity.builder()
                                        .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                                        .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(responseStorage.getIdRegisterFileSend()).build())
                                        .tipoArchivo(TipoArchivoEnum.CDR)
                                        .build());
                            }
                            paymentVoucherRepository.save(paymentVoucherEntity);

                            status = true;
                        }else{
                            System.out.println("Error: Dio error al reenviar");
                            comunicacionWithoutConnectionSendBill(
                                    voucherPendiente.getIdTmpSendBill()
                            );
                            result.put(ConstantesParameter.PARAM_BEAN_SEND_BILL, SendBillDTO.builder().ruc(ruc).idPaymentVoucher(idPaymentVoucher).nameDocument(voucherPendiente.getNombreDocumento()).envioAutomaticoSunat(true).build());
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

            Logger.register(TipoLogEnum.ERROR, ruc, voucherPendiente.getNombreDocumento(),
                    OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.IN_PROCESS,
                    messageResponse, idPaymentVoucher.toString(), e);
            log.error("Error en sendDocumentBill: {}", messageResponse, e);
        }

        resp.setMensaje(messageResponse);
        resp.setEstado(status);
        resp.setNombre(voucherPendiente.getNombreDocumento());

        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, resp);

        Logger.register(TipoLogEnum.INFO, ruc, voucherPendiente.getNombreDocumento(),
                OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.COMPLETED, result.toString());

        return result;
    }
}
