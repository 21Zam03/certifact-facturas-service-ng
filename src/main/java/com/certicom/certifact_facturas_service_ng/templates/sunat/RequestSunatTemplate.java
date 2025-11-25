package com.certicom.certifact_facturas_service_ng.templates.sunat;

import com.certicom.certifact_facturas_service_ng.dto.others.GetStatusCdrDto;
import com.certicom.certifact_facturas_service_ng.util.UtilXml;

import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;;

@Component
public class RequestSunatTemplate {

    @Value("${sunat.rucPse}")
    private String rucPseValue;

    @Value("${sunat.usuarioPse}")
    private String usuarioPseValue;

    @Value("${sunat.rucPseose}")
    private String rucPseOseValue;

    @Value("${sunat.clavePse}")
    private String clavePseValue;

    @Value("${sunat.clavePseose}")
    private String clavePseOseValue;

    @Value("${sunat.usuarioSol}")
    private String usuarioSol;

    @Value("${sunat.claveSol}")
    private String claveSol;

    @Value("${sunat.clavePseStatus}")
    private String clavePseStatus;

    @Value("${apifact.isProduction}")
    private Boolean isProduction;

    public String buildSendOseBill(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String password = clavePseOseValue;
        String rucbliz = "20293093297BIZLINKS";
        String passbliz = "UF1TiWZB";
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");

        /*xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
        xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                .append("TESTBIZLINKS").append("</wsse:Password>\n");*/

        xml.append("<wsse:Username>").append("20293093297").append("</wsse:Username>\n");
        xml.append("<wsse:Password>").append(password).append("</wsse:Password>\n");
        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:sendBill>\n");
        xml.append("<fileName>").append(fileName).append("</fileName>\n");
        xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
        xml.append("</ser:sendBill>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildSendOseBlizBill(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String password = clavePseOseValue;
        String rucbliz = "20293093297BIZLINKS";
        String passbliz = "UF1TiWZB";
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");

        if (isProduction) {
            xml.append("<wsse:Username>").append("20478005017CERTICOM").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("VpkjBf1ciqzd5gf1").append("</wsse:Password>\n");
        }else {
            xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("TESTBIZLINKS").append("</wsse:Password>\n");
        }


        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:sendBill>\n");
        xml.append("<fileName>").append(fileName).append("</fileName>\n");
        xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
        xml.append("</ser:sendBill>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildSendOseBlizBill12(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String password = clavePseOseValue;
        String rucbliz = "20293093297BIZLINKS";
        String passbliz = "UF1TiWZB";
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");

        if (isProduction) {
            xml.append("<wsse:Username>").append("20293093297CERTICOM").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("xAXkZHroJwFifgN9").append("</wsse:Password>\n");
        }else {
            xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("TESTBIZLINKS").append("</wsse:Password>\n");
        }


        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:sendBill>\n");
        xml.append("<fileName>").append(fileName).append("</fileName>\n");
        xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
        xml.append("</ser:sendBill>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildsendCertiBill(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String username = usuarioPseValue;
        String password = clavePseValue;
        String rubbliz = "";
        String userbliz = "";
        String passbliz = "";
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");


        if (isProduction) {
            xml.append("<wsse:Username>").append("20293093297").append("JRUIZPIN").append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("44Minutos*").append("</wsse:Password>\n");
        }else{
            xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("44129327").append("</wsse:Password>\n");
        }


        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:sendBill>\n");
        xml.append("<fileName>").append(fileName).append("</fileName>\n");
        xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
        xml.append("</ser:sendBill>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildSendBill(String fileName, String contentFileBase64) {

        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String username = usuarioPseValue;
        String password = clavePseValue;
        String rubbliz = "";
        String userbliz = "";
        String passbliz = "";
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");


        if (isProduction) {
            xml.append("<wsse:Username>").append("20478005017").append("YESSICA1").append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("bizlinks2").append("</wsse:Password>\n");
        }else{
            xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("44129327").append("</wsse:Password>\n");
        }


        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:sendBill>\n");
        xml.append("<fileName>").append(fileName).append("</fileName>\n");
        xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
        xml.append("</ser:sendBill>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildOseGetStatusCDR(GetStatusCdrDto dataGetStatus) {
        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String password = clavePseOseValue;
// eZhPGmlyZb
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");
        xml.append("<wsse:Username>").append("20293093297").append("</wsse:Username>\n");
        xml.append("<wsse:Password>").append(password).append("</wsse:Password>\n");
        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:getStatusCdr>\n");
        xml.append("<rucComprobante>").append(dataGetStatus.getRuc()).append("</rucComprobante>\n");
        xml.append("<tipoComprobante>").append(dataGetStatus.getTipoComprobante()).append("</tipoComprobante>\n");
        xml.append("<serieComprobante>").append(dataGetStatus.getSerie()).append("</serieComprobante>\n");
        xml.append("<numeroComprobante>").append(dataGetStatus.getNumero()).append("</numeroComprobante>\n");
        xml.append("</ser:getStatusCdr>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildOseBlizGetStatusCDR(GetStatusCdrDto dataGetStatus) {
        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String password = clavePseOseValue;
// eZhPGmlyZb
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");


        if (isProduction) {
            xml.append("<wsse:Username>").append("20478005017CERTICOM").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("VpkjBf1ciqzd5gf1").append("</wsse:Password>\n");
        }else{
            xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("TESTBIZLINKS").append("</wsse:Password>\n");
        }


        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:getStatusCdr>\n");
        xml.append("<rucComprobante>").append(dataGetStatus.getRuc()).append("</rucComprobante>\n");
        xml.append("<tipoComprobante>").append(dataGetStatus.getTipoComprobante()).append("</tipoComprobante>\n");
        xml.append("<serieComprobante>").append(dataGetStatus.getSerie()).append("</serieComprobante>\n");
        xml.append("<numeroComprobante>").append(dataGetStatus.getNumero()).append("</numeroComprobante>\n");
        xml.append("</ser:getStatusCdr>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildOseBlizGetStatusCDR12(GetStatusCdrDto dataGetStatus) {
        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String password = clavePseOseValue;
// eZhPGmlyZb
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");


        if (isProduction) {
            xml.append("<wsse:Username>").append("20293093297CERTICOM").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("xAXkZHroJwFifgN9").append("</wsse:Password>\n");
        }else{
            xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("TESTBIZLINKS").append("</wsse:Password>\n");
        }


        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:getStatusCdr>\n");
        xml.append("<rucComprobante>").append(dataGetStatus.getRuc()).append("</rucComprobante>\n");
        xml.append("<tipoComprobante>").append(dataGetStatus.getTipoComprobante()).append("</tipoComprobante>\n");
        xml.append("<serieComprobante>").append(dataGetStatus.getSerie()).append("</serieComprobante>\n");
        xml.append("<numeroComprobante>").append(dataGetStatus.getNumero()).append("</numeroComprobante>\n");
        xml.append("</ser:getStatusCdr>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildGetStatusCDRCerti(GetStatusCdrDto dataGetStatus) {

        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String username = usuarioSol;
        String password = claveSol;

        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");


        if (isProduction) {
            xml.append("<wsse:Username>").append("20293093297").append("BGCL5LQ4").append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("Ct20180101").append("</wsse:Password>\n");
        }else {
            xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("44129327").append("</wsse:Password>\n");
        }


        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:getStatusCdr>\n");
        xml.append("<rucComprobante>").append(dataGetStatus.getRuc()).append("</rucComprobante>\n");
        xml.append("<tipoComprobante>").append(dataGetStatus.getTipoComprobante()).append("</tipoComprobante>\n");
        xml.append("<serieComprobante>").append(dataGetStatus.getSerie()).append("</serieComprobante>\n");
        xml.append("<numeroComprobante>").append(dataGetStatus.getNumero()).append("</numeroComprobante>\n");
        xml.append("</ser:getStatusCdr>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }
    public String buildGetStatusCDR(GetStatusCdrDto dataGetStatus) {

        StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String username = usuarioSol;
        String password = claveSol;

        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");


        if (isProduction) {
            xml.append("<wsse:Username>").append("20478005017").append("YESSICA1").append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("bizlinks2").append("</wsse:Password>\n");
        }else {
            xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("44129327").append("</wsse:Password>\n");
        }


        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:getStatusCdr>\n");
        xml.append("<rucComprobante>").append(dataGetStatus.getRuc()).append("</rucComprobante>\n");
        xml.append("<tipoComprobante>").append(dataGetStatus.getTipoComprobante()).append("</tipoComprobante>\n");
        xml.append("<serieComprobante>").append(dataGetStatus.getSerie()).append("</serieComprobante>\n");
        xml.append("<numeroComprobante>").append(dataGetStatus.getNumero()).append("</numeroComprobante>\n");
        xml.append("</ser:getStatusCdr>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");

        return UtilXml.formatXML(xml.toString());
    }

    public String buildOseSendSummary(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioPseValue;
            String password = clavePseOseValue;
            //String rucbliz = "20293093297BIZLINKS";
            //String passbliz = "UF1TiWZB";
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");

            /*xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("TESTBIZLINKS").append("</wsse:Password>\n");*/

            xml.append("<wsse:Username>").append("20293093297").append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append("VKU9kR8md6").append("</wsse:Password>\n");
            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:sendSummary>\n");
            xml.append("<fileName>").append(fileName).append("</fileName>\n");
            xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
            xml.append("</ser:sendSummary>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildSendSummary " : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }
    public String buildOseBlizSendSummary(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioPseValue;
            String password = clavePseOseValue;
            //String rucbliz = "20293093297BIZLINKS";
            //String passbliz = "UF1TiWZB";
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");


            if (isProduction) {
                xml.append("<wsse:Username>").append("20478005017CERTICOM").append("</wsse:Username>\n");
                xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                        .append("VpkjBf1ciqzd5gf1").append("</wsse:Password>\n");
            }else {
                xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
                xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                        .append("TESTBIZLINKS").append("</wsse:Password>\n");
            }


            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:sendSummary>\n");
            xml.append("<fileName>").append(fileName).append("</fileName>\n");
            xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
            xml.append("</ser:sendSummary>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildSendSummary " : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }
    public String buildOseBlizSendSummary12(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioPseValue;
            String password = clavePseOseValue;
            //String rucbliz = "20293093297BIZLINKS";
            //String passbliz = "UF1TiWZB";
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");


            if (isProduction) {
                xml.append("<wsse:Username>").append("20293093297CERTICOM").append("</wsse:Username>\n");
                xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                        .append("xAXkZHroJwFifgN9").append("</wsse:Password>\n");
            }else {
                xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
                xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                        .append("TESTBIZLINKS").append("</wsse:Password>\n");
            }


            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:sendSummary>\n");
            xml.append("<fileName>").append(fileName).append("</fileName>\n");
            xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
            xml.append("</ser:sendSummary>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildSendSummary " : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }
    public String buildSendSummaryCerti(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioPseValue;
            String password = clavePseValue;
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");

            if (isProduction) {
                xml.append("<wsse:Username>").append("20293093297").append("JRUIZPIN").append("</wsse:Username>\n");
                xml.append("<wsse:Password>").append("44Minutos*").append("</wsse:Password>\n");
            }else{
                xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
                xml.append("<wsse:Password>").append("44129327").append("</wsse:Password>\n");
            }

            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:sendSummary>\n");
            xml.append("<fileName>").append(fileName).append("</fileName>\n");
            xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
            xml.append("</ser:sendSummary>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildSendSummary " : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }
    public String buildSendSummary(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioPseValue;
            String password = clavePseValue;
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");


            if (isProduction) {
                xml.append("<wsse:Username>").append("20478005017").append("YESSICA1").append("</wsse:Username>\n");
                xml.append("<wsse:Password>").append("bizlinks2").append("</wsse:Password>\n");
            }else{
                xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
                xml.append("<wsse:Password>").append("44129327").append("</wsse:Password>\n");
            }


            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:sendSummary>\n");
            xml.append("<fileName>").append(fileName).append("</fileName>\n");
            xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
            xml.append("</ser:sendSummary>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildSendSummary " : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }

    public String buildGetStatusOse(String nroTicket) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String rucbliz = "20293093297BIZLINKS";
            String passbliz = "UF1TiWZB";
            String password = clavePseOseValue;

            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");

            /*xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
            xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                    .append("TESTBIZLINKS").append("</wsse:Password>\n");*/

            xml.append("<wsse:Username>").append(ruc).append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append(password).append("</wsse:Password>\n");
            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:getStatus>\n");
            xml.append("<ticket>").append(nroTicket).append("</ticket>\n");
            xml.append("</ser:getStatus>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildGetStatus" : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }
    public String buildGetStatusOseBliz(String nroTicket) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String rucbliz = "20293093297BIZLINKS";
            String passbliz = "UF1TiWZB";
            String password = clavePseOseValue;

            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");


            if (isProduction) {
                xml.append("<wsse:Username>").append("20478005017CERTICOM").append("</wsse:Username>\n");
                xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                        .append("VpkjBf1ciqzd5gf1").append("</wsse:Password>\n");
            }else {
                xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
                xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                        .append("TESTBIZLINKS").append("</wsse:Password>\n");
            }


            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:getStatus>\n");
            xml.append("<ticket>").append(nroTicket).append("</ticket>\n");
            xml.append("</ser:getStatus>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildGetStatus" : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }
    public String buildGetStatusOseBliz12(String nroTicket) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String rucbliz = "20293093297BIZLINKS";
            String passbliz = "UF1TiWZB";
            String password = clavePseOseValue;

            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");


            if (isProduction) {
                xml.append("<wsse:Username>").append("20293093297CERTICOM").append("</wsse:Username>\n");
                xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                        .append("xAXkZHroJwFifgN9").append("</wsse:Password>\n");
            }else {
                xml.append("<wsse:Username>").append("20293093297BIZLINKS").append("</wsse:Username>\n");
                xml.append("<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">")
                        .append("TESTBIZLINKS").append("</wsse:Password>\n");
            }


            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:getStatus>\n");
            xml.append("<ticket>").append(nroTicket).append("</ticket>\n");
            xml.append("</ser:getStatus>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildGetStatus" : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }
    public String buildGetStatusCerti(String nroTicket) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioSol;
            String password = claveSol;
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");

            if (isProduction) {
                xml.append("<wsse:Username>").append("20293093297").append("JRUIZPIN").append("</wsse:Username>\n");
                xml.append("<wsse:Password>").append("44Minutos*").append("</wsse:Password>\n");
            }else{
                xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
                xml.append("<wsse:Password>").append("44129327").append("</wsse:Password>\n");
            }


            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:getStatus>\n");
            xml.append("<ticket>").append(nroTicket).append("</ticket>\n");
            xml.append("</ser:getStatus>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildGetStatus" : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }
    public String buildGetStatus(String nroTicket) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioSol;
            String password = claveSol;
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");

            if (isProduction) {
                xml.append("<wsse:Username>").append("20478005017").append("YESSICA1").append("</wsse:Username>\n");
                xml.append("<wsse:Password>").append("bizlinks2").append("</wsse:Password>\n");
            }else{
                xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
                xml.append("<wsse:Password>").append("44129327").append("</wsse:Password>\n");
            }


            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:getStatus>\n");
            xml.append("<ticket>").append(nroTicket).append("</ticket>\n");
            xml.append("</ser:getStatus>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildGetStatus" : ex.getMessage()) + " en el método buildGetStatus");
        }
        return UtilXml.formatXML(xml.toString());
    }

}
