package com.certicom.certifact_facturas_service_ng.util;

import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class RebuildFile {

    public static Map<String, String> getDataResponseFromCDR(String base64) throws IOException {

        Map<String, String> resultDatos;
        NodeList nodeResponseCode;
        NodeList nodeDescription;
        NodeList nodeLinkcdr;
        String responseCode;
        String description;
        String linkcdr;
        String nameFile;
        String tipoDocumento;
        String rucEmisor;
        String formatXML;
        Document document;
        String[] datosFileXml;
        File fileZip;

        fileZip = UtilConversion.decodeBase64ToFile(base64, ConstantesParameter.TYPE_FILE_ZIP);
        datosFileXml = getDataFileXmlAsStringFromFileZip(fileZip);
        formatXML = datosFileXml[0];
        nameFile  = datosFileXml[1];

        document = UtilXml.parseXmlFile(formatXML);
        nodeDescription = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_DESCRIPTION);
        nodeResponseCode = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_RESPONSE_CODE);
        nodeLinkcdr = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_RESPONSE_DOCUMENTDESC);
        if (nodeDescription.getLength() == 0 && nodeResponseCode.getLength() == 0){
            nodeDescription = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_DESCRIPTION_OSE);
            nodeResponseCode = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_RESPONSE_CODE_OSE);
        }
        if (nodeLinkcdr.getLength()==0){
            nodeLinkcdr = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_RESPONSE_DOCUMENTDESC_OSE);
        }
        List<String> codigosRpta = new ArrayList<String>();
        List<String> descripcionesRpta = new ArrayList<String>();
        List<String> linkcdrRpta = new ArrayList<String>();
        for(int i = 0; i < nodeResponseCode.getLength(); i++ ) {
            codigosRpta.add(nodeResponseCode.item(i).getTextContent());
            descripcionesRpta.add(nodeDescription.item(i).getTextContent());
            if (nodeLinkcdr.getLength()>0){
                linkcdrRpta.add(nodeLinkcdr.item(i).getTextContent()==null?"":nodeLinkcdr.item(i).getTextContent());
            }

        }
        nameFile = nameFile.substring(0, (nameFile.length()-4));
        tipoDocumento = nameFile.substring(14, 16);
        rucEmisor = nameFile.substring(2, 13);

        responseCode = String.join("|", codigosRpta);
        description = String.join("|", descripcionesRpta);
        linkcdr = String.join("|",linkcdrRpta);
        resultDatos = new HashMap<>();
        resultDatos.put(ConstantesParameter.PARAM_NAME_DOCUMENT, nameFile);
        resultDatos.put(ConstantesParameter.PARAM_DESCRIPTION, description);
        resultDatos.put(ConstantesParameter.PARAM_RESPONSE_CODE, responseCode);
        resultDatos.put(ConstantesParameter.PARAM_TIPO_ARCHIVO, tipoDocumento);
        resultDatos.put(ConstantesParameter.PARAM_RUC_EMISOR, rucEmisor);
        resultDatos.put(ConstantesParameter.PARAM_DOCUMENT_DESCRIPTION, linkcdr);
        return resultDatos;
    }

    private static String[] getDataFileXmlAsStringFromFileZip(File fileZip)
            throws ZipException, IOException {

        InputStream inputStream = null;
        String[] resp = new String[2];
        StringBuilder stringBuilder;
        String nameFile = null;
        ZipEntry zipEntry;
        String inputLine;
        String formatXML;
        try(ZipFile zip = new ZipFile(fileZip)) {

            Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zip.entries();
            while (enumeration.hasMoreElements()) {

                zipEntry = enumeration.nextElement();
                nameFile = zipEntry.getName();

                if (nameFile.toLowerCase().endsWith(ConstantesParameter.TYPE_FILE_XML)) {

                    inputStream = zip.getInputStream(zipEntry);
                    break;
                }
            }

            stringBuilder = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            inputStream.close();
        }
        formatXML = UtilXml.formatXML(stringBuilder.toString());

        resp[0] = formatXML;
        resp[1] = nameFile;

        return resp;
    }

}
