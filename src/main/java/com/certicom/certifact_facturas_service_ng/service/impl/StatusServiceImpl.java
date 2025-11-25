package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.UserDto;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponseSunat;
import com.certicom.certifact_facturas_service_ng.entity.DetailsPaymentVoucherEntity;
import com.certicom.certifact_facturas_service_ng.enums.*;
import com.certicom.certifact_facturas_service_ng.feign.CompanyData;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherData;
import com.certicom.certifact_facturas_service_ng.feign.UserData;
import com.certicom.certifact_facturas_service_ng.feign.VoidedDocumentsFeign;
import com.certicom.certifact_facturas_service_ng.model.*;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.SendSunatService;
import com.certicom.certifact_facturas_service_ng.service.StatusService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.util.*;

@Service
public class StatusServiceImpl implements StatusService {

    private final SendSunatService sendSunatService;
    private final CompanyData companyData;
    private final VoidedDocumentsFeign voidedDocumentsFeign;
    private final AmazonS3ClientService amazonS3ClientService;
    private final PaymentVoucherData paymentVoucherData;
    private final UserData userData;

    @Autowired
    public StatusServiceImpl(SendSunatService sendSunatService, CompanyData companyData, AmazonS3ClientService amazonS3ClientService,
                             VoidedDocumentsFeign voidedDocumentsFeign,
                             PaymentVoucherData paymentVoucherData, UserData userData) {
        this.sendSunatService = sendSunatService;
        this.companyData = companyData;
        this.amazonS3ClientService = amazonS3ClientService;
        this.voidedDocumentsFeign = voidedDocumentsFeign;
        this.paymentVoucherData = paymentVoucherData;
        this.userData = userData;
    }

    @Override
    public ResponsePSE getStatus(String numeroTicket, String tipoResumen, String userName, String rucEmisor) {
        ResponseSunat respSunat;
        ResponsePSE resp = null;
        OperacionLogEnum operacionLog = null;

        try {
            operacionLog = (tipoResumen.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) ?
                    OperacionLogEnum.STATUS_SUNAT_SUMMARY : OperacionLogEnum.STATUS_SUNAT_VOIDED;

            resp = new ResponsePSE();
            respSunat = sendSunatService.getStatus(numeroTicket, tipoResumen, rucEmisor);
            System.out.println(respSunat);
            /*
            Logger.register(TipoLogEnum.INFO, rucEmisor, tipoResumen, operacionLog,
                    SubOperacionLogEnum.SEND_SUNAT, respSunat.toString());*/

            switch (respSunat.getEstadoComunicacionSunat()) {
                case SUCCESS:
                    comunicacionSuccess(
                            tipoResumen,
                            numeroTicket,
                            respSunat.getStatusCode(),
                            respSunat.getMessage(),
                            respSunat.getNameDocument(),
                            respSunat.getRucEmisor(),
                            respSunat.getContentBase64(),
                            userName,
                            EstadoComprobanteEnum.ACEPTADO
                    );

                    resp.setEstado(true);
                    resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK);
                    resp.setMensaje("[" + respSunat.getStatusCode() + "] " + respSunat.getMessage());
                    break;
                case SUCCESS_WITH_WARNING:
                    comunicacionSuccess(
                            tipoResumen,
                            numeroTicket,
                            respSunat.getStatusCode(),
                            respSunat.getMessage(),
                            respSunat.getNameDocument(),
                            rucEmisor,
                            respSunat.getContentBase64(),
                            userName,
                            EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA
                    );
                    resp.setEstado(true);
                    resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK);
                    resp.setMensaje("[" + respSunat.getStatusCode() + "] " + respSunat.getMessage());
                    break;
                case SUCCESS_WITH_ERROR_CONTENT:
                    comunicacionError(
                            tipoResumen,
                            numeroTicket,
                            respSunat.getStatusCode(),
                            respSunat.getMessage(),
                            rucEmisor,
                            userName,
                            EstadoComprobanteEnum.ERROR);
                    resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_ERROR);
                    resp.setMensaje("[" + respSunat.getStatusCode() + "] " + respSunat.getMessage());
                    resp.setEstado(false);
                    break;
                case SUCCESS_WITHOUT_CONTENT_CDR:
                    resp.setMensaje(respSunat.getMessage());
                    resp.setEstado(false);
                    break;
                case WITHOUT_CONNECTION:
                case PENDING:
                    comunicacionPendiente(tipoResumen, numeroTicket, resp);
                    resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO);
                    resp.setMensaje(respSunat.getMessage());
                    resp.setEstado(false);
                    break;
                default:
//					resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO);
                    resp.setMensaje(respSunat.getMessage());
                    resp.setEstado(false);
                    break;
            }

        } catch (IOException e) {

            resp.setEstado(false);
            resp.setMensaje(e.getMessage());

            /*
            Logger.register(TipoLogEnum.ERROR, rucEmisor, numeroTicket, operacionLog,
                    SubOperacionLogEnum.IN_PROCESS, e.getMessage(), numeroTicket, e);
            * */

        } catch (Exception e) {

            resp.setEstado(false);
            resp.setMensaje(e.getMessage());

            /*
            Logger.register(TipoLogEnum.ERROR, rucEmisor, numeroTicket, operacionLog,
                    SubOperacionLogEnum.IN_PROCESS, e.getMessage(), numeroTicket, e);
            * */

        }

        return resp;
    }

    @Override
    public String getEstadoDocumentoResumenInBD(String tipoDocumento, String numeroTicket) {
        String estado;
        estado = voidedDocumentsFeign.getEstadoByNumeroTicket(numeroTicket);
        return estado;
    }

    private void comunicacionPendiente(String tipoDocumentoResumen, String numeroTicket, ResponsePSE responsePSE) {
        //**SOLAMENTE PARA RESUMENES DIARIOS - BOLETAS POR ESO NO SE PUSO EL CODIGO DEL PROYECTO ANTERIOR/
    }

    private void comunicacionError(String tipoDocumentoResumen, String numeroTicket,
                                   String codeResponse, String messageResponse, String rucEmisor,
                                   String userName, EstadoComprobanteEnum estadoComprobanteError) throws Exception {

        Map<String, String> params;
        String estadoDocumentInBD;

        estadoDocumentInBD = getEstadoDocumentoResumenInBD(tipoDocumentoResumen, numeroTicket);
        if (estadoDocumentInBD.equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO)) {

            params = new HashMap<>();

            params.put(ConstantesParameter.PARAM_NUM_TICKET, numeroTicket);
            params.put(ConstantesParameter.PARAM_ESTADO, ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_ERROR);
            params.put(ConstantesParameter.PARAM_RESPONSE_CODE, codeResponse);
            params.put(ConstantesParameter.PARAM_DESCRIPTION, messageResponse);
            params.put(ConstantesParameter.PARAM_USER_NAME, userName);

            actualizarDocumentoResumenByTicket(params, tipoDocumentoResumen, null, estadoComprobanteError);
        }
    }

    private void comunicacionSuccess(String tipoDocumentoResumen, String numeroTicket,
                                     String codeResponse, String messageResponse, String nameDocument,
                                     String rucEmisor, String fileBase64, String userName, EstadoComprobanteEnum aceptado) throws Exception {

        Map<String, String> params = new HashMap<>();
        String estadoDocumentInBD;
        OperacionLogEnum operacionLog = (tipoDocumentoResumen.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) ?
                OperacionLogEnum.STATUS_SUNAT_SUMMARY : OperacionLogEnum.STATUS_SUNAT_VOIDED;

        estadoDocumentInBD = getEstadoDocumentoResumenInBD(tipoDocumentoResumen, numeroTicket);
        if (estadoDocumentInBD.equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO)) {
            System.out.println("INGRESO VOIDED 98");
            CompanyModel companyEntity = companyData.findCompanyByRuc(rucEmisor);
            RegisterFileUploadModel file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileBase64),
                    nameDocument, "summary", companyEntity);
            /*
            Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, operacionLog,
                    SubOperacionLogEnum.STORAGE_FILE, "{" + ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "}{" + params + "}");
            * */

            params.put(ConstantesParameter.PARAM_NUM_TICKET, numeroTicket);
            params.put(ConstantesParameter.PARAM_ESTADO, ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK);
            params.put(ConstantesParameter.PARAM_RESPONSE_CODE, codeResponse);
            params.put(ConstantesParameter.PARAM_DESCRIPTION, messageResponse);
            params.put(ConstantesParameter.PARAM_USER_NAME, userName);

            actualizarDocumentoResumenByTicket(params, tipoDocumentoResumen, file.getIdRegisterFileSend(), aceptado);
            System.out.println("INGRESO VOIDED 99"+tipoDocumentoResumen);
        }
    }

    public void actualizarDocumentoResumenByTicket(Map<String, String> params,
                                                   String tipoDocumento, Long idRegisterFile,
                                                   EstadoComprobanteEnum estadoComprobanteEnum) {
        System.out.println("estadoCOMPROBANTE: "+estadoComprobanteEnum);
        Timestamp fechaModificacion = new Timestamp(Calendar.getInstance().getTime().getTime());
        String numeroTicket = params.get(ConstantesParameter.PARAM_NUM_TICKET);
        String estado = params.get(ConstantesParameter.PARAM_ESTADO);
        String codeResponse = params.get(ConstantesParameter.PARAM_RESPONSE_CODE);
        String description = params.get(ConstantesParameter.PARAM_DESCRIPTION);
        String usuario = params.get(ConstantesParameter.PARAM_USER_NAME);
        List<String> identificadoresComprobantes;
        List<String> comprobantesByAnular = null;
        List<String> comprobantesByAceptar = null;
        String rucEmisor;
        StringBuilder msgLog = new StringBuilder();

        VoidedDocumentsModel voided;
        identificadoresComprobantes = new ArrayList<>();
        voided = voidedDocumentsFeign.getVoidedByTicket(numeroTicket);
        rucEmisor = voided.getRucEmisor();

        voided.setEstado(estado);
        voided.setCodigoRespuesta(codeResponse);
        voided.setDescripcionRespuesta(description);
        voided.setUserNameModify(usuario);
        voided.setFechaModificacion(fechaModificacion);
        voided.setEstadoComprobante(estadoComprobanteEnum.getCodigo());

        //AGREGANDO ARCHIVO
        if (idRegisterFile != null) {
            voided.addVoidFile(VoidedFileModel.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                    .idRegisterFileSend(idRegisterFile)
                    .tipoArchivo(TipoArchivoEnum.CDR.name())
                    .build());
        }

        System.out.println("VOIDED A GUARDAR: "+voided);
        VoidedDocumentsModel voidedDocumentsModel = voidedDocumentsFeign.update(voided);
        System.out.println("RESPUESTA DE GUARDAR VOIDED: "+voidedDocumentsModel);

        msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                append("{numeroTicket:").append(numeroTicket).append("}{estado:").append(estado).
                append("}{codeResponse:").append(codeResponse).append("}{description:").append(description).
                append("}{fechaModificacion:").append(fechaModificacion).append("}{estadoComprobante:").
                append(estadoComprobanteEnum.getCodigo()).append("}");

        /*
        Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_VOIDED,
                SubOperacionLogEnum.UPDATE_BD_VOIDED, msgLog.toString());
        * */

        for (DetailsDocsVoidedModel detail : voided.getOrCreateDocumentos()) {
            identificadoresComprobantes.add(rucEmisor + "-" + detail.getTipoComprobante() + "-" +
                    detail.getSerieDocumento() + "-" + detail.getNumeroDocumento());
        }

        switch (estadoComprobanteEnum) {
            case ACEPTADO:
            case ACEPTADO_ADVERTENCIA:
                if (tipoDocumento.equals(ConstantesSunat.COMUNICACION_BAJA)) {
                    paymentVoucherData.updateComprobantesByBajaDocumentos(
                            identificadoresComprobantes, usuario, fechaModificacion);

                    /*
                    for(String comprobante : identificadoresComprobantes) {
                        PaymentVoucherDto paymentVoucher = paymentVoucherData.getIdentificadorDocument(comprobante);
                        UserDto user = userData.findUserByUsername(paymentVoucher.getUserName());
                        for (DetailsPaymentVoucherEntity paymentVoucherItem : paymentVoucher.getItems()) {
                            Optional<Long> optionalProductoId = productRepository.findProductIdByCodigoOrDescripcion(
                                    paymentVoucherItem.getCodigoProducto(), paymentVoucherItem.getDescripcion());
                            ProductEntity producto = productRepository.findById(optionalProductoId.get())
                                    .orElseThrow(() -> new ServiceException("Producto no encontrado con ID. "));
                            Long stockAntes = producto.getStock();
                            producto.setStock(producto.getStock() + paymentVoucherItem.getCantidad().longValue());
                            productRepository.save(producto);
                            historialStockService.registrarHistorialStock(
                                    producto,
                                    user,
                                    stockAntes,
                                    stockAntes + paymentVoucherItem.getCantidad().longValue(),
                                    null,
                                    paymentVoucherItem,
                                    paymentVoucher,
                                    "ANULACION"
                            );
                        }
                    }
                    * */

                    msgLog.setLength(0);
                    msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                            append("{identificadoresComprobantes:").append(identificadoresComprobantes).
                            append("}{fechaModificacion:").append(fechaModificacion).append("}");

                    /*
                    Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_VOIDED,
                            SubOperacionLogEnum.UPDATE_BD_PAYMENT_VOUCHER, msgLog.toString());
                    * */
                }
                break;
            case ERROR:
                paymentVoucherData.updateComprobantesOnResumenError(
                        identificadoresComprobantes, usuario, fechaModificacion);

                msgLog.setLength(0);
                msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                        append("{identificadoresComprobantesRechazados:").append(identificadoresComprobantes).
                        append("}{fechaModificacion").append(fechaModificacion).append("}");

                /*
                Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_SUMMARY,
                        SubOperacionLogEnum.UPDATE_BD_PAYMENT_VOUCHER, msgLog.toString());
                * */
                break;
            default:
                break;
        }
    }



}
