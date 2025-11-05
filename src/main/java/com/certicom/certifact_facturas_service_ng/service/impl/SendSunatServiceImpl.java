package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.others.ResponseServer;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponseSunat;
import com.certicom.certifact_facturas_service_ng.entity.ErrorEntity;
import com.certicom.certifact_facturas_service_ng.enums.ComunicationSunatEnum;
import com.certicom.certifact_facturas_service_ng.enums.EstadoSunatEnum;
import com.certicom.certifact_facturas_service_ng.enums.TyperErrorEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.CompanyData;
import com.certicom.certifact_facturas_service_ng.feign.ErrorCatalogData;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherData;
import com.certicom.certifact_facturas_service_ng.feign.VoidedDocumentsFeign;
import com.certicom.certifact_facturas_service_ng.dto.others.GetStatusCdrDto;
import com.certicom.certifact_facturas_service_ng.model.OseModel;
import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.service.SendSunatService;
import com.certicom.certifact_facturas_service_ng.service.TemplateService;
import com.certicom.certifact_facturas_service_ng.templates.sunat.RequestSunatTemplate;
import com.certicom.certifact_facturas_service_ng.util.*;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.certicom.certifact_facturas_service_ng.validation.business.VoucherAnnularValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendSunatServiceImpl implements SendSunatService {

    private final PaymentVoucherData paymentVoucherData;
    private final CompanyData companyData;
    private final ErrorCatalogData errorCatalogData;
    private final RequestSunatTemplate requestSunatTemplate;
    private final VoucherAnnularValidator voucherAnnularValidator;
    private final VoidedDocumentsFeign voidedDocumentsFeign;
    private final TemplateService templateService;

    @Value("${sunat.endpoint}")
    private String endPointSunat;
    @Value("${sunat.endpointOtrosCpe}")
    private String endPointSunatOtrosCpe;
    @Value("${sunat.endpointGuiaRemision}")
    private String endPointSunatGuiaRemision;
    @Value("${sunat.endpointGuiaRemisionRest}")
    private String endPointSunatGuiaRemisionRest;
    @Value("${sunat.endpointConsultaCDR}")
    private String endPointConsultaCDR;

    @Override
    public PaymentVoucherDto prepareComprobanteForEnvioSunatInter(String finalRucEmisor, String tipoComprobante, String serieDocumento, Integer numeroDocumento) throws ServiceException {
        PaymentVoucherDto paymentVoucherDto = paymentVoucherData.
                findPaymentVoucherByRucAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento(finalRucEmisor, tipoComprobante, serieDocumento, numeroDocumento);

        if (paymentVoucherDto == null)
            throw new ServiceException(String.format("%s [%s-%s-%s-%s]", "El comprobante que desea enviar a la Sunat, no existe: ", finalRucEmisor, tipoComprobante, serieDocumento, numeroDocumento != null ? numeroDocumento.toString() : ""));
        System.out.println("RESULTA: "+ paymentVoucherDto.toString());
        if (paymentVoucherDto.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()))
            throw new ServiceException("Este comprobante ya se encuentra aceptado en Sunat.");

        if (paymentVoucherDto.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado()))
            throw new ServiceException("Este comprobante se encuentra anulado en Sunat.");

        if (paymentVoucherDto.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA))
            throw new ServiceException("Por este metodo solo se permite enviar Facturas, Notas de crédito y Débito.");


        if (paymentVoucherDto.getTipoComprobanteAfectado() != null) {
            if (
                    (paymentVoucherDto.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO))
            ) {
                throw new ServiceException("Por este metodo solo se permite enviar Notas de crédito y Débito asociadas a Facturas, las notas asociadas a boletas se deben enviar por resumen diario.");
            }
        }
        return paymentVoucherDto;
    }

    @Override
    public ResponseSunat sendBillPaymentVoucher(String fileName, String contentFileBase64, String rucEmisor) {
        ResponseSunat responseSunat = new ResponseSunat();
        try {
            String formatSoap = obtenerFormat(rucEmisor,fileName,contentFileBase64);
            System.out.println("PREPARANDO XML");
            System.out.println(formatSoap);
            System.out.println("--------------");

            ResponseServer responseServer = null;
            OseModel ose = companyData.findOseByRucInter(rucEmisor);
            System.out.println("OSE: "+ose);
            if (ose != null) {
                if (ose.getId() == 1) {
                    responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                            ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
                }else if (ose.getId()==2||ose.getId()==12) {
                    RestTemplate template = new RestTemplate();
                    URI uriget = new URI(ose.getUrlFacturas()+ ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
                    HttpHeaders requestHeaders = new HttpHeaders();
                    HttpEntity<String> requestEntity = new HttpEntity<>(formatSoap, requestHeaders);
                    ResponseEntity<ResponseServer> entity = template.exchange(uriget, HttpMethod.POST, requestEntity, ResponseServer.class);
                    System.out.println("PUENTE OSE BLIZ");
                    System.out.println(entity);

                    if (entity.getStatusCode() == HttpStatus.OK) {
                        responseServer = entity.getBody();
                        System.out.println("user response retrieved ");
                    }
                }else if (ose.getId()==10) {
                    responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                            ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
                }
            } else {
                responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                        ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
            }
            log.info("*** CONSUMIENDO  WEB SERVICE - SUNAT ***");
            buildResponseSendBillStatus(responseSunat, responseServer, ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
        } catch (IOException e) {

            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.WITHOUT_CONNECTION);

            //new SentryExceptionResolver().resolveException(null, null, e, e);

        } catch (Exception ex) {

            responseSunat.setMessage(ex.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.ERROR_INTERNO_WS_API);

            //new SentryExceptionResolver().resolveException(null, null, ex, ex);
        }

        return responseSunat;
    }

    @Override
    public ResponseSunat getStatusCDR(GetStatusCdrDto statusDto, String rucEmisor) {
        ResponseSunat responseSunat = new ResponseSunat();
        try {

            String formatSoap = obtenerStatusCdr(statusDto,rucEmisor);

            ResponseServer responseServer = null;
            OseModel ose = companyData.findOseByRucInter(rucEmisor);
            if (ose != null) {
                if (ose.getId()==1) {
                    responseServer = send(
                            formatSoap,
                            obtenerEndPointConsultaCdr(rucEmisor),
                            ConstantesParameter.TAG_GET_STATUS_CONTENT
                    );
                }else if (ose.getId()==2) {
                    RestTemplate template = new RestTemplate();
                    URI uriget = new URI(ose.getUrlFacturas()+ConstantesParameter.TAG_GET_STATUS_CONTENT);
                    HttpHeaders requestHeaders = new HttpHeaders();
                    HttpEntity<String> requestEntity = new HttpEntity<>(formatSoap, requestHeaders);
                    ResponseEntity<ResponseServer> entity = template.exchange(uriget, HttpMethod.POST, requestEntity, ResponseServer.class);

                    if (entity.getStatusCode() == HttpStatus.OK) {
                        responseServer = entity.getBody();
                        System.out.println("user response retrieved ");
                    }
                } else if (ose.getId() == 10) {
                    responseServer = send(
                            formatSoap,
                            obtenerEndPointConsultaCdr(rucEmisor),
                            ConstantesParameter.TAG_GET_STATUS_CONTENT
                    );
                } else if (ose.getId() == 12) {
                    responseServer = send(
                            formatSoap,
                            obtenerEndPointConsultaCdr(rucEmisor),
                            ConstantesParameter.TAG_GET_STATUS_CONTENT
                    );
                }
            } else {
                responseServer = send(formatSoap, obtenerEndPointConsultaCdr(rucEmisor),
                        ConstantesParameter.TAG_GET_STATUS_CONTENT);
            }
            System.out.println("responseServerCdr: "+responseServer.toString());
            buildResponseGetStatusCDR(responseSunat, responseServer);

        } catch (IOException e) {

            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.WITHOUT_CONNECTION);
        } catch (Exception ex) {
            responseSunat.setMessage(ex.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.ERROR_INTERNO_WS_API);
        }

        return responseSunat;
    }

    @Override
    public ResponseSunat sendSummary(String fileName, String contentFileBase64, String rucEmisor) {
        ResponseSunat responseSunat = new ResponseSunat();
        String formatSoap;
        Document document;
        NodeList nodeFaultcode;
        NodeList nodeFaultstring;
        Node node;

        try {
            System.out.println("ANTES FORMAT SOAT");
            formatSoap = obtenerFormatBuildSendSumary(rucEmisor,fileName, contentFileBase64);

            System.out.println("FORMAT SOAT 2 SEGUIMIENTO");
            /*ResponseServer responseServer = send(
                    formatSoap,
                    obtenerEndPointSunat(rucEmisor),
                    ConstantesParameter.TAG_SEND_SUMMARY_TICKET
            );*/
            ResponseServer responseServer = null;
            OseModel ose = companyData.findOseByRucInter(rucEmisor);
            if (ose != null) {
                if (ose.getId()==1){

                    responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                            ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
                }else if (ose.getId()==2 || ose.getId()==12) {
                    RestTemplate template = new RestTemplate();
                    URI uriget = new URI(ose.getUrlFacturas()+ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
                    HttpHeaders requestHeaders = new HttpHeaders();
                    System.out.println("SOAP ANULACION");
                    System.out.println(formatSoap);
                    HttpEntity<String> requestEntity = new HttpEntity<>(formatSoap, requestHeaders);
                    ResponseEntity<ResponseServer> entity = template.exchange(uriget, HttpMethod.POST, requestEntity, ResponseServer.class);
                    System.out.println(entity);
                    if (entity.getStatusCode() == HttpStatus.OK) {
                        responseServer = entity.getBody();
                        System.out.println("user response retrieved ");
                    }
                }else if (ose.getId()==10 ) {
                    System.out.println("Enviar boleta resumen 10");
                    System.out.println(formatSoap);
                    responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                            ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
                }
            } else {
                System.out.println("Enviar anulacion 0");
                responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                        ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
            }
            document = UtilXml.parseXmlFile(responseServer.getContent());

            if (responseServer.isSuccess()) {

                NodeList nodeTicket = document.getElementsByTagName(ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
                String valueTicket = nodeTicket.item(0).getTextContent();
                responseSunat.setSuccess(true);
                responseSunat.setTicket(valueTicket);
                responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS);
            } else {

                String valueFaultcode = null;
                String valueFaultstring = null;

                nodeFaultcode = document.getElementsByTagName("faultcode");
                nodeFaultstring = document.getElementsByTagName("faultstring");
                node = nodeFaultcode.item(0);

                if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                    valueFaultcode = (node.getTextContent()).replaceAll("[^0-9]", "");
                    valueFaultstring = nodeFaultstring.item(0).getTextContent();
                    if (valueFaultcode.equals("")) {
                        responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.WITHOUT_CONNECTION);
                    } else {
                        responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                    }
                    responseSunat.setStatusCode(valueFaultcode);
                    responseSunat.setMessage(valueFaultstring);
                }
                responseSunat.setSuccess(false);

            }
        } catch (IOException e) {
            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.ERROR_INTERNO_WS_API);
            responseSunat.setSuccess(false);
        } catch (Exception ex) {

            responseSunat.setMessage(ex.getMessage());
            responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.ERROR_INTERNO_WS_API);
            responseSunat.setSuccess(false);
        }
        return responseSunat;
    }

    private String obtenerFormatBuildSendSumary(String ruc, String fileName, String contentFileBase64) {
        OseModel ose = companyData.findOseByRucInter(ruc);
        String formato = "";
        if (ose != null) {
            if (ose.getId()==1){
                formato =  requestSunatTemplate.buildOseSendSummary(fileName, contentFileBase64);
            }else if (ose.getId()==2){
                formato =  requestSunatTemplate.buildOseBlizSendSummary(fileName, contentFileBase64);
            }else if (ose.getId()==12){
                formato =  requestSunatTemplate.buildOseBlizSendSummary12(fileName, contentFileBase64);
            }else if (ose.getId()==10){
                formato =  requestSunatTemplate.buildSendSummaryCerti(fileName, contentFileBase64);
            }
        } else {
            formato =  requestSunatTemplate.buildSendSummary(fileName, contentFileBase64);
        }
        return formato;
    }

    private String obtenerFormat(String ruc, String fileName, String contentFileBase64) {
        OseModel ose = companyData.findOseByRucInter(ruc);
        String formato = "";
        if (ose != null) {
            if (ose.getId()==1){
                formato =  requestSunatTemplate.buildSendOseBill(fileName, contentFileBase64);
            }else if (ose.getId()==2){
                formato =  requestSunatTemplate.buildSendOseBlizBill(fileName, contentFileBase64);
            }else if (ose.getId()==12){
                formato =  requestSunatTemplate.buildSendOseBlizBill12(fileName, contentFileBase64);
            }else if (ose.getId()==10 ){
                formato =  requestSunatTemplate.buildsendCertiBill(fileName, contentFileBase64);
            }
        } else {
            formato =  requestSunatTemplate.buildSendBill(fileName, contentFileBase64);
        }
        return formato ;
    }

    private ResponseServer send(String xml, String endpoint, String tagOperacionOK)
            throws IOException {
        ResponseServer responseServer = new ResponseServer();
        CloseableHttpResponse responsePost;
        String formattedSOAPResponse;
        StringEntity entity = null;
        HttpPost httpPost = null;
        String inputLine;
        int responseCode = 0;
        System.out.println(endpoint);
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            httpPost = new HttpPost(endpoint);
            httpPost.setHeader("Prama", "no-cache");
            httpPost.setHeader("Cache-Control", "no-cache");

            entity = new StringEntity(xml, "UTF-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-type", "text/xml");

            System.out.println("XML: " + xml);
            System.out.println("Endpoint: " + endpoint);
            System.out.println("Httppost: " + httpPost);
            System.out.println("Entity:" + entity);
            responsePost = client.execute(httpPost);
            System.out.println("HttpResponse: " + responsePost);
            responseCode = responsePost.getStatusLine().getStatusCode();
            responseServer.setServerCode(responseCode);

            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(responsePost.getEntity().getContent()));
            while ((inputLine = in.readLine()) != null) {
                String inp = inputLine.replace("S:", "soap-env:");
                inp = inp.replace(":S=", ":soap-env=");
                inp = inp.replace("SOAP-ENV:", "soap-env:");
                inp = inp.replace("ns2:", "br:");
                inp = inp.replace(":ns2", ":br");
                response.append(inp);
            }
            System.out.println("response");
            System.out.println(response);
            formattedSOAPResponse = UtilXml.formatXML(response.toString());
            responseServer.setContent(formattedSOAPResponse);
        }
        if (formattedSOAPResponse.contains("<" + tagOperacionOK + ">")) {
            responseServer.setSuccess(true);
        } else {
            responseServer.setSuccess(false);
        }

        return responseServer;
    }

    private String obtenerEndPointSunat(String ruc) {
        OseModel ose = companyData.findOseByRucInter(ruc);
        if (ose != null && ose.getId()!=10) {
            return ose.getUrlFacturas();
        } else {
            return endPointSunat;
        }
    }

    private void buildResponseSendBillStatus(ResponseSunat responseSunat, ResponseServer responseServer,
                                             String nameTagContent) throws DOMException, IOException {
        Document document;
        NodeList nodeFaultcode;
        NodeList nodeFaultstring;
        NodeList nodeContentBase64;
        Node node;
        Node nodeStatusCode;
        Map<String, String> datosCDR;
        List<String> codigosResponse;
        List<String> mensajesResponse;
        String nameDocumentResponse;
        String tipoDocumento;
        String rucEmisor;
        boolean isWarning = true;
        StringBuilder messageResponse = null;

        document = UtilXml.parseXmlFile(responseServer.getContent());

        if (responseServer.isSuccess()) {

            nodeContentBase64 = document.getElementsByTagName(nameTagContent);
            node = nodeContentBase64.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                if (nameTagContent.equals(ConstantesParameter.TAG_GET_STATUS_CONTENT)) {
                    nodeStatusCode = document.getElementsByTagName(ConstantesParameter.TAG_STATUS_CODE).item(0);

                    if (!nodeStatusCode.getTextContent().equals(ConstantesParameter.CODE_RESPONSE_OK)) {

                        responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                        responseSunat.setStatusCode(RebuildFile.getDataResponseFromCDR(node.getTextContent()).get(ConstantesParameter.PARAM_RESPONSE_CODE));
                        responseSunat.setMessage(RebuildFile.getDataResponseFromCDR(node.getTextContent()).get(ConstantesParameter.PARAM_DESCRIPTION));
                        return;
                    }
                }

                datosCDR = RebuildFile.getDataResponseFromCDR(node.getTextContent());
                nameDocumentResponse = datosCDR.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
                tipoDocumento = datosCDR.get(ConstantesParameter.PARAM_TIPO_ARCHIVO);
                rucEmisor = datosCDR.get(ConstantesParameter.PARAM_RUC_EMISOR);

                if (((String) datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).contains("|")) {
                    codigosResponse = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE).split("|"));
                    mensajesResponse = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION).split("|"));
                    for (int i = 0; i < codigosResponse.size(); i++) {
                        validateCodeReponseFromCDR(
                                responseSunat,
                                codigosResponse.get(i),
                                tipoDocumento,
                                mensajesResponse.get(i));

                        if (responseSunat.getEstadoComunicacionSunat().equals(ComunicationSunatEnum.SUCCESS)) {

                            responseSunat.setContentBase64(node.getTextContent());
                            isWarning = false;
                            break;
                        }
                        if (responseSunat.getEstadoComunicacionSunat().equals(ComunicationSunatEnum.SUCCESS_WITH_ERROR_CONTENT)) {

                            isWarning = false;
                            break;
                        }
                        if (messageResponse == null) {

                            messageResponse = new StringBuilder();
                        }
                        messageResponse.append("[").append(codigosResponse.get(i)).append("] ").append(mensajesResponse.get(i));
                        if ((i + 1) < codigosResponse.size()) {
                            messageResponse.append("|");
                        }
                    }
                    if (isWarning) {
                        responseSunat.setMessage(messageResponse.toString());
                    }
                } else {
                    validateCodeReponseFromCDR(
                            responseSunat,
                            datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE),
                            tipoDocumento,
                            datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION)
                    );

                    if (responseSunat.getEstadoComunicacionSunat().equals(ComunicationSunatEnum.SUCCESS)) {

                        responseSunat.setContentBase64(node.getTextContent());
                    }
                }
                responseSunat.setNameDocument(nameDocumentResponse);
                responseSunat.setRucEmisor(rucEmisor);
                responseSunat.setStatusCode(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE));
            } else {
                responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITHOUT_CONTENT_CDR);
                responseSunat.setMessage(ConstantesParameter.MENSAJE_NO_FOUND_CDR);
                responseSunat.setSuccess(true);
            }

        } else {

            String valueFaultcode = null;
            String valueFaultstring = null;

            nodeFaultcode = document.getElementsByTagName("faultcode");
            nodeFaultstring = document.getElementsByTagName("faultstring");
            node = nodeFaultcode.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                valueFaultcode = (node.getTextContent()).replaceAll("[^0-9]", "");
                valueFaultstring = nodeFaultstring.item(0).getTextContent();
                if (valueFaultcode.equals("")) {
                    responseSunat.setMessage("Error al comunicarse con la Sunat." + valueFaultstring);
                    responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.WITHOUT_CONNECTION);
                } else {
                    responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                    responseSunat.setStatusCode(valueFaultcode);
                    responseSunat.setMessage(valueFaultstring);
                }
            } else {
                nodeFaultcode = document.getElementsByTagName(ConstantesParameter.TAG_STATUS_CODE);
                node = nodeFaultcode.item(0);
                String code = node.getTextContent();
                if (node != null && StringUtils.isNotBlank(code)) {
                    log.info("STATUS CODE SUNAT: {}", code);
                    if (code.equals("98") || code.equals("0098")) {
                        responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.PENDING);
                        responseSunat.setStatusCode(code);
                        responseSunat.setMessage("Sunat: Ticket se encuentra en proceso");
                    }
                }
            }
            responseSunat.setSuccess(false);
        }
    }

    private void validateCodeReponseFromCDR(ResponseSunat responseSunat, String codigoRespuesta, String tipoDocumento, String mensajeRespuesta) {

        ErrorEntity errorRespuesta;

        if (codigoRespuesta.equals(ConstantesParameter.CODIGO_ACEPTADO_FROM_CDR)) {

            responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS);
            responseSunat.setMessage(mensajeRespuesta);
            responseSunat.setStatusCode(codigoRespuesta);
            responseSunat.setSuccess(true);

            return;
        }

        errorRespuesta = errorCatalogData.findFirst1ByCodeAndDocument(codigoRespuesta, tipoDocumento);

        if (errorRespuesta != null) {
            if (errorRespuesta.getType().equals(TyperErrorEnum.ERROR.getType())) {

                responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                responseSunat.setSuccess(false);
            } else {
                responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITH_WARNING);
                responseSunat.setSuccess(true);
            }
            responseSunat.setStatusCode(codigoRespuesta);
            responseSunat.setMessage(mensajeRespuesta);
        } else {

            responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
            responseSunat.setStatusCode(ConstantesParameter.CODIGO_NO_FOUND_CODE_ERROR_FROM_CDR);
            responseSunat.setMessage(ConstantesParameter.MENSAJE_NO_FOUND_CODE_FROM_CDR + ". [ResponseCode:" + codigoRespuesta + "][Tipo documento:" + tipoDocumento + "][Description:" + mensajeRespuesta + "]");
            responseSunat.setSuccess(false);
        }

    }

    private String obtenerStatusCdr(GetStatusCdrDto statusDto, String ruc) {
        OseModel ose = companyData.findOseByRucInter(ruc);
        String statusstring = "";
        if (ose != null) {
            if (ose.getId()==1){
                statusstring =   requestSunatTemplate.buildOseGetStatusCDR(statusDto);
            }else if (ose.getId()==2){
                statusstring =   requestSunatTemplate.buildOseBlizGetStatusCDR(statusDto);
            }else if (ose.getId()==12){
                statusstring =   requestSunatTemplate.buildOseBlizGetStatusCDR12(statusDto);
            }else if (ose.getId()==10 ){
                statusstring =   requestSunatTemplate.buildGetStatusCDRCerti(statusDto);
            }
        } else {
            statusstring =   requestSunatTemplate.buildGetStatusCDR(statusDto);
        }
        return  statusstring ;

    }

    private String obtenerEndPointConsultaCdr(String ruc) {
        return endPointConsultaCDR;
        /*OsesEntity ose = companyRepository.findOseByRuc(ruc);
        if (ose != null) {
            return ose.getUrlconsultacdr();
        } else {
            return endPointConsultaCDR;
        }*/
    }

    private void buildResponseGetStatusCDR(ResponseSunat responseSunat, ResponseServer responseServer) {

        Document document;
        NodeList nodeCode;
        NodeList nodeMessage;
        NodeList nodeContentResponse;
        Node node;
        String message;

        document = UtilXml.parseXmlFile(responseServer.getContent());

        if (responseServer.isSuccess()) {

            nodeContentResponse = document.getElementsByTagName("content");
            node = nodeContentResponse.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {
                responseSunat.setContentBase64(node.getTextContent());
                responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS);
            } else {
                responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITHOUT_CONTENT_CDR);
            }

            nodeCode = document.getElementsByTagName("statusCode");
            nodeMessage = document.getElementsByTagName("statusMessage");
            node = nodeCode.item(0);
            message = "[" + node.getTextContent() + "] ";
            node = nodeMessage.item(0);
            message = message + node.getTextContent();

            responseSunat.setSuccess(true);
            responseSunat.setMessage(message);

            System.out.println("Response sunat cdr 1: "+responseSunat.toString());

        } else {

            String valueFaultcode = null;
            String valueFaultstring = null;

            nodeCode = document.getElementsByTagName("faultcode");
            nodeMessage = document.getElementsByTagName("faultstring");
            node = nodeCode.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                valueFaultcode = (node.getTextContent()).replaceAll("[^0-9]", "");
                valueFaultstring = nodeMessage.item(0).getTextContent();
                if (valueFaultcode.equals("")) {
                    responseSunat.setMessage("Error al comunicarse con la Sunat." + valueFaultstring);
                    responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.WITHOUT_CONNECTION);
                } else {
                    responseSunat.setEstadoComunicacionSunat(ComunicationSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                    responseSunat.setStatusCode(valueFaultcode);
                    responseSunat.setMessage(valueFaultstring);
                }
            }
            responseSunat.setSuccess(false);
            System.out.println("Response sunat cdr 2: "+responseSunat.toString());
        }
    }

}
