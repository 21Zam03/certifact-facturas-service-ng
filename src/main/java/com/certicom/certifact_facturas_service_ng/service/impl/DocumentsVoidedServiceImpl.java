package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.dto.others.VoucherAnnular;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponseSunat;
import com.certicom.certifact_facturas_service_ng.entity.RegisterFileUploadEntity;
import com.certicom.certifact_facturas_service_ng.enums.ComunicationSunatEnum;
import com.certicom.certifact_facturas_service_ng.enums.EstadoArchivoEnum;
import com.certicom.certifact_facturas_service_ng.enums.EstadoComprobanteEnum;
import com.certicom.certifact_facturas_service_ng.enums.TipoArchivoEnum;
import com.certicom.certifact_facturas_service_ng.feign.CompanyFeign;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherFeign;
import com.certicom.certifact_facturas_service_ng.feign.VoidedDocumentsFeign;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.DocumentsVoidedService;
import com.certicom.certifact_facturas_service_ng.service.SendSunatService;
import com.certicom.certifact_facturas_service_ng.service.TemplateService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.certicom.certifact_facturas_service_ng.validation.business.VoucherAnnularValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentsVoidedServiceImpl implements DocumentsVoidedService {

    private final TemplateService templateService;
    private final VoidedDocumentsFeign voidedDocumentsFeign;
    private final CompanyFeign companyFeign;
    private final SendSunatService sendSunatService;
    private final PaymentVoucherFeign paymentVoucherFeign;
    private final AmazonS3ClientService amazonS3ClientService;
    private final VoucherAnnularValidator voucherAnnularValidator;

    @Override
    public VoidedDocumentsDto registrarVoidedDocuments(Voided voided, Long idRegisterFile, String usuario, String ticket) {
        Date fechaActual = Calendar.getInstance().getTime();
        Timestamp fechaEjecucion = new Timestamp(fechaActual.getTime());
        VoidedDocumentsDto documentSummary = new VoidedDocumentsDto();
        List<String> identificadorComprobantes = new ArrayList<>();

        documentSummary.setEstado(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO);
        documentSummary.setFechaBajaDocs(voided.getFechaBaja());
        documentSummary.setFechaGeneracionBaja(voided.getFechaGeneracion());
        documentSummary.setCorrelativoGeneracionDia(voided.getCorrelativoGeneracionDia());
        documentSummary.setIdDocument(voided.getId());
        documentSummary.setRucEmisor(voided.getRucEmisor());
        documentSummary.setTicketSunat(ticket);
        documentSummary.setFechaGeneracionResumen(fechaEjecucion);
        documentSummary.setUserName(usuario);
        documentSummary.setEstadoComprobante(voided.getEstadoComprobante());

        //AGREGANDO ARCHIVO
        /*
        if (idRegisterFile != null) {
            documentSummary.addFile(VoidedFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                    .tipoArchivo(TipoArchivoEnum.XML)
                    .build());
        }

        * */

        for (VoidedLine item : voided.getLines()) {

            DetailsDocsVoidedDto detail = new DetailsDocsVoidedDto();

            detail.setSerieDocumento(item.getSerieDocumento());
            detail.setNumeroDocumento(item.getNumeroDocumento());
            detail.setTipoComprobante(item.getTipoComprobante());
            detail.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
            detail.setMotivoBaja(item.getRazon());
            detail.setNumeroItem(item.getNumeroItem());
            documentSummary.addDetailDocsVoided(detail);

            identificadorComprobantes.add(voided.getRucEmisor() + "-" + item.getTipoComprobante() + "-" +
                    item.getSerieDocumento() + "-" + item.getNumeroDocumento());
        }

        documentSummary = voidedDocumentsFeign.save(documentSummary);

        paymentVoucherFeign.updateStateToSendSunatForVoidedDocuments(
                identificadorComprobantes,
                EstadoComprobanteEnum.PENDIENTE_ANULACION.getCodigo(),
                usuario,
                fechaEjecucion);

        return documentSummary;
    }

    @Override
    public ResponsePSE anularDocuments(List<VoucherAnnular> documents, String rucEmisor, String userName, List<String> ticketsVoidedProcess) {
        ResponsePSE respuesta = new ResponsePSE();
        Map<String, List<VoucherAnnular>> documentosBajaByFechaEmisionFacturasMap = new HashMap<>();
        Map<String, List<VoucherAnnular>> documentosBajaByFechaEmisionGuiasMap = new HashMap<>();
        Map<String, List<VoucherAnnular>> documentosBajaByFechaEmisionOtrosMap = new HashMap<>();
        List<VoucherAnnular> documentosVoidedByFechaEmision;

        List<VoucherAnnular> documentosSummary = new ArrayList<>();
        StringBuilder messageBuilder = null;
        List<VoucherAnnular> documentsanular = new ArrayList<>();
        try {

            for (VoucherAnnular documento : documents) {
                String identificadorDocumento = rucEmisor + "-" + documento.getTipoComprobante() + "-" +
                        documento.getSerie().toUpperCase() + "-" + documento.getNumero();
                System.out.println(identificadorDocumento);
                PaymentVoucherDto entity = paymentVoucherFeign.getIdentificadorDocument(identificadorDocumento);
                System.out.println(entity);
                if (documento.getRucEmisor()==null){
                    documento.setRucEmisor(rucEmisor);
                }
                if(entity==null){
                    System.out.println("Document not found");
                    //throw new Exception("Documento no encontrado en los registros");
                }else if (!entity.getEstado().equals("08") ){
                    documentsanular.add(documento);
                }
            }

            voucherAnnularValidator.validateVoucherAnnular(documentsanular, rucEmisor);

            for (VoucherAnnular document : documents) {
                String identificadorDocumento = rucEmisor + "-" + document.getTipoComprobante() + "-" +
                        document.getSerie().toUpperCase() + "-" + document.getNumero();
                System.out.println(identificadorDocumento);
                boolean noExiste = false;
                PaymentVoucherDto entity = paymentVoucherFeign.getIdentificadorDocument(identificadorDocumento);
                if (entity==null){
                    noExiste=true;
                }

                if (noExiste){
                    if (messageBuilder == null) {
                        messageBuilder = new StringBuilder();
                    }
                    messageBuilder.append("500");
                    messageBuilder.append("No existe documento de referencia");
                }else{
                    switch (document.getTipoComprobante()) {
                        case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                            if (documentosBajaByFechaEmisionFacturasMap.get(document.getFechaEmision()) != null) {
                                documentosVoidedByFechaEmision = documentosBajaByFechaEmisionFacturasMap
                                        .get(document.getFechaEmision());
                            } else {
                                documentosVoidedByFechaEmision = new ArrayList<>();
                            }
                            documentosVoidedByFechaEmision.add(document);
                            documentosBajaByFechaEmisionFacturasMap.put(document.getFechaEmision(), documentosVoidedByFechaEmision);
                            break;
                        case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                        case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:
                            if (document.getTipoComprobanteRelacionado().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
                                if (documentosBajaByFechaEmisionFacturasMap.get(document.getFechaEmision()) != null) {
                                    documentosVoidedByFechaEmision = documentosBajaByFechaEmisionFacturasMap
                                            .get(document.getFechaEmision());
                                } else {
                                    documentosVoidedByFechaEmision = new ArrayList<>();
                                }
                                documentosVoidedByFechaEmision.add(document);
                                documentosBajaByFechaEmisionFacturasMap.put(
                                        document.getFechaEmision(),
                                        documentosVoidedByFechaEmision);
                            } else {
                                documentosSummary.add(document);
                            };
                            break;
                    }
                }
            }
            for (String fechaEmision : documentosBajaByFechaEmisionFacturasMap.keySet()) {
                Voided voided = new Voided();
                List<VoidedLine> lines = new ArrayList<>();
                voided.setFechaBaja(fechaEmision);
                voided.setRucEmisor(rucEmisor);

                List<VoucherAnnular> anulados = documentosBajaByFechaEmisionFacturasMap.get(fechaEmision);
                for (VoucherAnnular document : anulados) {
                    String identificadorDocumento = rucEmisor + "-" + document.getTipoComprobante() + "-" +
                            document.getSerie().toUpperCase() + "-" + document.getNumero();
                    PaymentVoucherDto entity = paymentVoucherFeign.getIdentificadorDocument(identificadorDocumento);
                    if (entity.getEstado().equals("08") ){
                        if (messageBuilder == null) {
                            messageBuilder = new StringBuilder();
                        }
                        messageBuilder.append("[").append("200").append("]");
                        messageBuilder.append("[").append("El comprobante ya ha sido anulado").append("]");
                    }else{
                        VoidedLine item = new VoidedLine();
                        item.setTipoComprobante(document.getTipoComprobante());
                        item.setSerieDocumento(document.getSerie());
                        item.setNumeroDocumento(document.getNumero());
                        item.setRazon(document.getMotivoAnulacion());
                        lines.add(item);
                    }
                }
                voided.setLines(lines);

                Map<String, String> resp = annularDocumentSendVoidedDocuments(voided, userName,false);
                if (!ComunicationSunatEnum.getEnum(resp.get(ConstantesParameter.PARAM_ESTADO)).equals(
                        ComunicationSunatEnum.SUCCESS)) {
                    if (messageBuilder == null) {
                        messageBuilder = new StringBuilder();
                    }
                    messageBuilder.append("[").append(resp.get(ConstantesParameter.PARAM_RESPONSE_CODE)).append("]");
                    messageBuilder.append("[").append(resp.get(ConstantesParameter.PARAM_DESCRIPTION)).append("]");
                } else {
                    ticketsVoidedProcess.add(resp.get(ConstantesParameter.PARAM_NUM_TICKET));
                }
            }
            respuesta.setEstado(true);
            if (messageBuilder != null) {
                respuesta.setMensaje(messageBuilder.toString());
            } else {
                respuesta.setMensaje(ConstantesParameter.MSG_RESP_OK);
            }
        } catch (Exception e) {
            respuesta.setEstado(false);
            respuesta.setMensaje(e.getMessage());
        }
        return respuesta;
    }

    public Map<String, String> annularDocumentSendVoidedDocuments(Voided voided, String userName , boolean esRetencion)
            throws Exception {
        ResponseSunat responseSunat;
        String nameDocumentComplete;
        String fileXMLZipBase64;
        String nameDocument = null;
        Map<String, String> params;
        Map<String, String> resp;
        Map<String, String> templateGenerated;
        StringBuilder messageBuilder = new StringBuilder();

        CompanyDto companyEntity = completarDatosVoided(voided,esRetencion);

        if (companyEntity.getOseId() != null && companyEntity.getOseId()==2) {
            templateGenerated = templateService.buildVoidedDocumentsSign(voided);
        } else if (companyEntity.getOseId() != null && (companyEntity.getOseId()==10||companyEntity.getOseId()==12)) {
            templateGenerated = templateService.buildVoidedDocumentsSignCerti(voided);
        } else {
            templateGenerated = templateService.buildVoidedDocumentsSign(voided);
        }

        System.out.println("ANULACIONES SUNAT 1 ");
        System.out.println(companyEntity);

        fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
        nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
        nameDocumentComplete = nameDocument + "." + ConstantesParameter.TYPE_FILE_ZIP;
        System.out.println(nameDocument);
        responseSunat = sendSunatService.sendSummary(nameDocumentComplete, fileXMLZipBase64,companyEntity.getRuc());

        System.out.println("ANULACIONES SUNAT 2 ");
        System.out.println(responseSunat);
        messageBuilder.append("[").append(voided.getRucEmisor()).append("]");
        messageBuilder.append("[").append(voided.getFechaBaja()).append("]");
        messageBuilder.append("[").append(voided.getId()).append("]");

        resp = new HashMap<>();
        resp.put(ConstantesParameter.PARAM_ESTADO, responseSunat.getEstadoComunicacionSunat().getEstado());

        switch (responseSunat.getEstadoComunicacionSunat()) {
            case SUCCESS_WITH_ERROR_CONTENT:
                resp.put(ConstantesParameter.PARAM_RESPONSE_CODE, responseSunat.getStatusCode());
                messageBuilder.append(responseSunat.getMessage());
                resp.put(ConstantesParameter.PARAM_DESCRIPTION, messageBuilder.toString());
                break;
            case WITHOUT_CONNECTION:
                resp.put(ConstantesParameter.PARAM_RESPONSE_CODE, ComunicationSunatEnum.WITHOUT_CONNECTION.name());
                messageBuilder.append(responseSunat.getMessage());
                resp.put(ConstantesParameter.PARAM_DESCRIPTION, messageBuilder.toString());
                break;
            default:
        }
        if (!responseSunat.isSuccess()) {
            return resp;
        }
        RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64),
                nameDocument, "voided", companyEntity);

        voided.setEstadoComprobante(EstadoComprobanteEnum.PROCESO_ENVIO.getCodigo());
        VoidedDocumentsDto voidedDocumentsEntity = registrarVoidedDocuments(voided, file.getIdRegisterFileSend(), userName, responseSunat.getTicket());
        resp.put(ConstantesParameter.PARAM_NUM_TICKET, voidedDocumentsEntity.getTicketSunat());
        resp.put(ConstantesParameter.PARAM_DESCRIPTION, "Se registro correctamente el documento: " + voided.getId());
        return resp;
    }

    private CompanyDto completarDatosVoided(Voided voided, boolean esRetencion) {

        Date fechaActual = Calendar.getInstance().getTime();
        Integer correlativo;
        String baja = ConstantesSunat.COMUNICACION_BAJA;
        if (esRetencion){
            baja = ConstantesSunat.COMUNICACION_BAJA_PER;
        }

        voided.setFechaGeneracion(UtilFormat.fecha(fechaActual, "yyyy-MM-dd"));
        correlativo = voidedDocumentsFeign.getCorrelativoGeneracionByDiaInVoidedDocuments(
                voided.getRucEmisor(), voided.getFechaGeneracion());
        correlativo++;
        voided.setCorrelativoGeneracionDia(correlativo);
        CompanyDto company = companyFeign.findCompanyByRuc(voided.getRucEmisor());
        voided.setDenominacionEmisor(company.getRazon());
        voided.setTipoDocumentoEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        voided.setId(baja + "-" +
                voided.getFechaGeneracion().replace("-", "") +
                "-" + voided.getCorrelativoGeneracionDia());

        return company;
    }
}
