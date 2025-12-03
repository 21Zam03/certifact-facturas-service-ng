package com.certicom.certifact_facturas_service_ng.templates.templateose;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.*;
import com.certicom.certifact_facturas_service_ng.enums.AfectacionIgvEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.templates.sunat.TemplateSunat;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.util.UtilGenerateLetraNumber;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.certicom.certifact_facturas_service_ng.util.UtilXml.appendChild;
import static com.certicom.certifact_facturas_service_ng.util.UtilXml.formatXML;

@Component
public class NotaDebitoTemplateOse extends TemplateSunat {

    public String buildDebitNote(PaymentVoucherDto debitNote) throws TemplateException {

        Signature signature = buildSignature(debitNote);
        String templateBuilt = buildTemplateInXML(debitNote, signature);

        return templateBuilt;
    }

    protected Signature buildSignature(PaymentVoucherDto debitNote) {

        Signature signature = new Signature();

        signature.setId("IDSignST");
        signature.setUri("#SignST");
        signature.setDenominacionEmisor(debitNote.getDenominacionEmisor());
        signature.setRucEmisor(debitNote.getRucEmisor());

        return signature;
    }

    private String buildTemplateInXML(PaymentVoucherDto debitNote, Signature signature) throws TemplateException {

        String stringXMLGenerate = null;
        Document document;
        Element elementRoot;
        Map<String, String> attributes = new HashMap<>();
        BigDecimal montoZero = BigDecimal.ZERO;
        BigDecimal montoTotalImpuestos;

        try {

            document = createDocument();
            elementRoot = addElementRoot(document, ROOT_DEBIT_NOTE, getAttributesElementRoot());

            Element extUBLExtensions = appendChild(document, elementRoot, "ext:UBLExtensions");
            //appendChild(document, elementRoot, "ext:UBLExtensions");
            appendChild(document, elementRoot, "cbc:UBLVersionID", ConstantesSunat.UBL_VERSION_2_1);
            appendChild(document, elementRoot, "cbc:CustomizationID", ConstantesSunat.CUSTOMIZATION_VERSION_2_0);
            appendChild(document, elementRoot, "cbc:ID", debitNote.getSerie()+"-"+debitNote.getNumero());
            appendChild(document, elementRoot, "cbc:IssueDate", debitNote.getFechaEmision());
            appendChild(document, elementRoot, "cbc:IssueTime",	debitNote.getHoraEmision());

            if (debitNote.getLeyendas() != null && debitNote.getLeyendas().isEmpty()) {
                for (Leyenda note : debitNote.getLeyendas()) {
                    if (StringUtils.isNotBlank(note.getCodigo())) {
                        appendChild(document, elementRoot, "cbc:Note", note.getDescripcion())
                                .setAttribute(ConstantesSunat.ATTRIBUTE_LANGUAGE_LOCALE_ID, note.getCodigo());
                    } else {
                        appendChild(document, elementRoot, "cbc:Note", note.getDescripcion());
                    }
                }
            }
           /* attributes.clear();
            String stringMoneda = debitNote.getCodigoMoneda()!=null?debitNote.getCodigoMoneda().equalsIgnoreCase("USD")?"Dólares Americanos":"Soles":"Soles";
            attributes.put(ConstantesSunat.ATTRIBUTE_LANGUAGE_LOCALE_ID, "1000");
            appendChild(document, elementRoot, "cbc:Note", GenerateLetraNumber.Convertir(debitNote.getImporteTotalVenta().setScale(2,BigDecimal.ROUND_HALF_UP).toString(),stringMoneda,true) , attributes);
*/

            /*if(debitNote.getLeyendas()!=null && debitNote.getLeyendas().isEmpty()) {
                for (Leyenda note: debitNote.getLeyendas()) {
                    if (StringUtils.isNotBlank(note.getCodigo())) {
                        appendChild(document, elementRoot, "cbc:Note", note.getDescripcion())
                            .setAttribute(ConstantesSunat.ATTRIBUTE_LANGUAGE_LOCALE_ID, note.getCodigo());
                    } else {
                        appendChild(document, elementRoot, "cbc:Note", note.getDescripcion());
                    }
                }
            }*/
            appendChild(document, elementRoot, "cbc:DocumentCurrencyCode", debitNote.getCodigoMoneda());
            //appendChild(document, elementRoot, "cbc:LineCountNumeric", debitNote.getItems().size());
            appendChildDiscrepancyResponse(document, elementRoot,
                    //debitNote.getSerieAfectado()+"-"+debitNote.getNumeroAfectado(),
                    debitNote.getCodigoTipoNotaDebito(),
                    debitNote.getMotivoNota());

            appendChildBillingReference(document, elementRoot,
                    debitNote.getSerieAfectado()+"-"+debitNote.getNumeroAfectado(),
                    debitNote.getTipoComprobanteAfectado(), attributes);
            /*
            attributes.clear();
            attributes.put("xmlns", "http://www.w3.org/2000/09/xmldsig#");

            Element cacSignature = appendChild(document, elementRoot, "cac:Signature");
            appendChild(document, cacSignature, "cbc:ID", "IDSignKG");

            Element cacSignatoryParty = appendChild(document, cacSignature, "cac:SignatoryParty");

            Element cacPartyIdentification = appendChild(document, cacSignatoryParty, "cac:PartyIdentification");
            appendChild(document, cacPartyIdentification, "cbc:ID", debitNote.getRucEmisor());

            Element cacPartyName = appendChild(document, cacSignatoryParty, "cac:PartyName");
            appendChild(document, cacPartyName, "cbc:Name", debitNote.getDenominacionEmisor());

            Element cacDigitalSignatureAttachment = appendChild(document, cacSignature, "cac:DigitalSignatureAttachment");
            Element cacExternalReference = appendChild(document, cacDigitalSignatureAttachment, "cac:ExternalReference");
            appendChild(document, cacExternalReference, "cbc:URI", "#SignST");*/



            if(debitNote.getGuiasRelacionadas()!=null && !debitNote.getGuiasRelacionadas().isEmpty()){

                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Tipo de Documento");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
                for (GuiaRelacionada guiaRelacionada : debitNote.getGuiasRelacionadas()){
                    /*appendChilDespatchDocumentReference(document, elementRoot,
                            guiaRelacionada.getSerieNumeroGuia(),
                            guiaRelacionada.getCodigoTipoGuia(),
                            attributes);*/
                }
            }

            if (debitNote.getDocumentosRelacionados() != null && !debitNote.getDocumentosRelacionados().isEmpty()) {

                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Documento Relacionado");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo12");
                for (DocumentoRelacionado documentoRelacionado : debitNote.getDocumentosRelacionados()) {
                    appendChilAdditionalDocumentReference(document, elementRoot, documentoRelacionado.getNumero(),
                            documentoRelacionado.getTipoDocumento(), attributes);
                }
            }

            appendChildSignature(document, elementRoot, signature);

            appendChildAccountingSupplierParty(document, elementRoot,
                    debitNote.getRucEmisor(), debitNote.getTipoDocumentoEmisor(),
                    debitNote.getDenominacionEmisor(), debitNote.getNombreComercialEmisor(),
                    debitNote.getCodigoLocalAnexoEmisor(), debitNote.getDireccionOficinaEmisor(),
                    attributes);

            String nombrecomercial = "";
            if (StringUtils.isNotBlank(debitNote.getNombreComercialEmisor())) {
                nombrecomercial = debitNote.getNombreComercialEmisor();
            }else {
                nombrecomercial = debitNote.getDenominacionReceptor();
            }
            String  emailreceptor = "false";
            if(debitNote.getEmailReceptor()!=null){
                emailreceptor=debitNote.getEmailReceptor();
            }
            appendChildAccountingCustomerParty(document, elementRoot,
                    debitNote.getNumeroDocumentoReceptor(),  debitNote.getTipoDocumentoReceptor(),
                    debitNote.getDenominacionReceptor(),  attributes);

            montoTotalImpuestos = importeTotalImpuestos(debitNote.getTotalIgv(),
                    debitNote.getTotalIvap(),
                    debitNote.getTotalIsc(),
                    debitNote.getTotalOtrostributos(),
                    null);

            Element cactaxTotal = appendChild(document, elementRoot, "cac:TaxTotal");
            appendChild(document, cactaxTotal, "cbc:TaxAmount", UtilFormat.format(montoTotalImpuestos))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, debitNote.getCodigoMoneda());

            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
            //attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, "UN/ECE 5153");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Codigo de tributos");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");

            appendChildSubTotalHeader(document, cactaxTotal, debitNote.getTotalValorVentaExportacion(),
                    montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_EXPORTACION, debitNote.getCodigoMoneda());
            appendChildSubTotalHeader(document, cactaxTotal, debitNote.getTotalValorVentaInafecta(),
                    montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_INAFECTO, debitNote.getCodigoMoneda());
            appendChildSubTotalHeader(document, cactaxTotal, debitNote.getTotalValorVentaExonerada(),
                    montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_EXONERADO, debitNote.getCodigoMoneda());

            appendChildSubTotalHeader(document, cactaxTotal, debitNote.getTotalValorVentaGratuita(),
                    debitNote.getTotalImpOperGratuita(), attributes, ConstantesSunat.CODIGO_TRIBUTO_GRATUITO, debitNote.getCodigoMoneda());
            appendChildSubTotalHeader(document, cactaxTotal, debitNote.getTotalValorVentaGravada(),
                    debitNote.getTotalIgv(), attributes, ConstantesSunat.CODIGO_TRIBUTO_IGV, debitNote.getCodigoMoneda());
            appendChildSubTotalHeader(document, cactaxTotal, debitNote.getTotalValorVentaGravadaIVAP(),
                    debitNote.getTotalIvap(), attributes, ConstantesSunat.CODIGO_TRIBUTO_IVAP, debitNote.getCodigoMoneda());
            appendChildSubTotalHeader(document, cactaxTotal, debitNote.getTotalValorBaseIsc(),
                    debitNote.getTotalIsc(), attributes, ConstantesSunat.CODIGO_TRIBUTO_ISC, debitNote.getCodigoMoneda());
            appendChildSubTotalHeader(document, cactaxTotal, debitNote.getTotalValorBaseOtrosTributos(),
                    debitNote.getTotalOtrostributos(), attributes, ConstantesSunat.CODIGO_TRIBUTO_OTROS, debitNote.getCodigoMoneda());

            Element legalMonetaryTotalElement = appendChild(document, elementRoot, "cac:RequestedMonetaryTotal");
            appendChild(document, legalMonetaryTotalElement, "cbc:PayableAmount", UtilFormat.format(debitNote.getImporteTotalVenta())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, debitNote.getCodigoMoneda());

            appendDebitNoteLine(document, elementRoot,debitNote.getItems(),debitNote.getCodigoMoneda());

            String stringXML = buildStringFromDOM(document);
            stringXMLGenerate = formatXML(stringXML);

        } catch (Exception e) {
            e.printStackTrace();
            throw new TemplateException("Error al generar plantilla para Nota de Credito:" +e.getMessage());
        }

        return stringXMLGenerate;
    }

    private void appendDebitNoteLine(Document document, Element elementRoot,
                                     List<ComprobanteItem> debitNotelines, String codigoMoneda) throws TemplateException {

        int correlativoItem = 1;
        Map<String, String> attributes = new HashMap<>();
        BigDecimal montoZero = BigDecimal.ZERO;

        for(ComprobanteItem item: debitNotelines) {

            BigDecimal montoTotalImpuestosLinea = importeTotalImpuestos(item.getIgv(),
                    item.getIvap(), item.getIsc(), item.getOtrosTributos(), null);

            Element debitNoteLineElement = appendChild(document, elementRoot, "cac:DebitNoteLine");
            appendChild(document, debitNoteLineElement, "cbc:ID", correlativoItem);

            if(item.getCantidad() != null && item.getCodigoUnidadMedida() != null) {

                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_UNIT_CODE, item.getCodigoUnidadMedida());
                attributes.put(ConstantesSunat.ATTRIBUTE_UNIT_CODE_LIST_ID, "UN/ECE rec 20");
                attributes.put(ConstantesSunat.ATTRIBUTE_UNIT_CODE_LIST_AGENCY_NAME, "United Nations Economic Commission for Europe");
                appendChild(document, debitNoteLineElement, "cbc:DebitedQuantity", item.getCantidad(),attributes);
            }

            if(item.getValorVenta() != null) {
                appendChild(document, debitNoteLineElement, "cbc:LineExtensionAmount", UtilFormat.format(item.getValorVenta())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            }

            Element pricingReferenceElement = appendChild(document, debitNoteLineElement, "cac:PricingReference");
            appendChildPricingReference(document, pricingReferenceElement, item.getPrecioVentaUnitario(),
                    ConstantesSunat.CODIGO_TIPO_PRECIO_PRECIO_UNITARIO, codigoMoneda, attributes);
            appendChildPricingReference(document, pricingReferenceElement, item.getValorReferencialUnitario(),
                    ConstantesSunat.CODIGO_TIPO_PRECIO_VALOR_REFERENCIAL, codigoMoneda, attributes);

            Element taxTotalElement = appendChild(document, debitNoteLineElement, "cac:TaxTotal");
            appendChild(document, taxTotalElement, "cbc:TaxAmount", UtilFormat.format(montoTotalImpuestosLinea))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            addElementsChildSubTaxTotal(document, taxTotalElement, item.getMontoBaseIgv(),
                    item.getIgv(), item.getCodigoTipoAfectacionIGV(), null, codigoMoneda,
                    item.getPorcentajeIgv(), false, attributes);
            addElementsChildSubTaxTotal(document, taxTotalElement, item.getMontoBaseIvap(),
                    item.getIvap(), item.getCodigoTipoAfectacionIGV(), null, codigoMoneda,
                    item.getPorcentajeIvap(), false, attributes);
            addElementsChildSubTaxTotal(document, taxTotalElement, item.getMontoBaseExportacion(),
                    montoZero, item.getCodigoTipoAfectacionIGV(), null, codigoMoneda,
                    montoZero, false, attributes);
            addElementsChildSubTaxTotal(document, taxTotalElement, item.getMontoBaseExonerado(),
                    montoZero, item.getCodigoTipoAfectacionIGV(), null, codigoMoneda,
                    montoZero, false, attributes);
            addElementsChildSubTaxTotal(document, taxTotalElement, item.getMontoBaseInafecto(),
                    montoZero, item.getCodigoTipoAfectacionIGV(), null, codigoMoneda,
                    montoZero, false, attributes);
            addElementsChildSubTaxTotal(document, taxTotalElement, item.getMontoBaseGratuito(),
                    item.getImpuestoVentaGratuita(), item.getCodigoTipoAfectacionIGV(), null,
                    codigoMoneda, item.getPorcentajeTributoVentaGratuita(), false, attributes);

            addElementsChildSubTaxTotal(document, taxTotalElement, item.getMontoBaseIsc(),
                    item.getIsc(), null, item.getCodigoTipoCalculoISC(), codigoMoneda,
                    item.getPorcentajeIsc(), false, attributes);
            addElementsChildSubTaxTotal(document, taxTotalElement, item.getMontoBaseOtrosTributos(),
                    item.getOtrosTributos(), null, null, codigoMoneda,
                    item.getPorcentajeOtrosTributos(), false, attributes);

            appendChildItem(document, debitNoteLineElement, item.getDescripcion(), item.getCodigoProducto(),
                    item.getCodigoProductoSunat(), item.getCodigoProductoGS1(), attributes);

            Element cbcPrice = appendChild(document, debitNoteLineElement, "cac:Price");
            appendChild(document, cbcPrice, "cbc:PriceAmount", UtilFormat.format7(item.getValorUnitario())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            item.setNumeroItem(correlativoItem);
            correlativoItem++;
        }
    }





    private void appendChildAccountingCustomerParty(Document document, Element elementRoot,
                                                    String numeroDocumentoReceptor, String tipoDocumentoReceptor, String denominacionReceptor,
                                                    Map<String, String> attributes) {

        Element accountingCustomerPartyElement = appendChild(document, elementRoot,"cac:AccountingCustomerParty");

        Element partyElement = appendChild(document, accountingCustomerPartyElement, "cac:Party");
        Element partyIdentificationElement = appendChild(document, partyElement, "cac:PartyIdentification");

        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, tipoDocumentoReceptor);
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Documento de Identidad");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
        appendChild(document, partyIdentificationElement, "cbc:ID", numeroDocumentoReceptor, attributes);

        Element partyLegalElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, partyLegalElement, "cbc:RegistrationName", denominacionReceptor);

    }

    private BigDecimal importeTotalImpuestos(BigDecimal totalIGV,BigDecimal totalIVAP,
                                             BigDecimal totalISC, BigDecimal totalOtrosTributos, BigDecimal impuestoVentaGratuita) {

        BigDecimal montoTotalImpuestos = BigDecimal.ZERO;
        if(totalIGV != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(totalIGV);
        }
        if(totalIVAP != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(totalIVAP);
        }
        if(totalISC != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(totalISC);
        }
        if(totalOtrosTributos != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(totalOtrosTributos);
        }
        if(impuestoVentaGratuita != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(impuestoVentaGratuita);
        }

        return montoTotalImpuestos;
    }

    private static void appendChildAccountingSupplierParty(Document document, Element elementRoot,
                                                           String numeroDocumentoEmisor, String tipoDocumentoEmisor, String denominacionEmisor,
                                                           String nombreComercialEmisor, String codigoLocalAnexo,
                                                           String direccionOficinaEmisor, Map<String, String> attributes) {


        Element accountingSupplierPartyElement = appendChild(document, elementRoot, "cac:AccountingSupplierParty");

        Element partyElement = appendChild(document, accountingSupplierPartyElement, "cac:Party");

        Element partyIdentificationElement = appendChild(document, partyElement, "cac:PartyIdentification");

        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, tipoDocumentoEmisor);
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Documento de Identidad");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
        appendChild(document, partyIdentificationElement, "cbc:ID", numeroDocumentoEmisor, attributes);

        if (StringUtils.isNotBlank(nombreComercialEmisor)) {

            Element partyNameElement = appendChild(document, partyElement, "cac:PartyName");
            appendChild(document, partyNameElement, "cbc:Name", nombreComercialEmisor);
        }

        Element partyLegalElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, partyLegalElement, "cbc:RegistrationName", denominacionEmisor);

        //if (StringUtils.isNotBlank(codigoLocalAnexo)) {

        Element registrationAddressElement = appendChild(document, partyLegalElement, "cac:RegistrationAddress");
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Establecimientos anexos");
        appendChild(document, registrationAddressElement, "cbc:AddressTypeCode", "0000", attributes);

        //}
        //Element registrationAddressElement = appendChild(document, partyLegalElement, "cac:RegistrationAddress");
        /*if (StringUtils.isNotBlank(direccionOficinaEmisor)) {

            Element cacAddressLine = appendChild(document, registrationAddressElement, "cac:AddressLine");
            appendChild(document, cacAddressLine, "cbc:Line", direccionOficinaEmisor);
        }*/
        /*attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "United Nations Economic Commission for Europe");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_ID, "ISO 3166-1");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Country");
        Element cacAddressCountry = appendChild(document, registrationAddressElement, "cac:Country");
        appendChild(document, cacAddressCountry, "cbc:IdentificationCode", "PE",attributes);*/
    }

    private void appendChildBillingReference(Document document, Element elementRoot,
                                             String identificadorDocumentoAfectado, String tipoComprobanteAfectado, Map<String, String> attributes) {

        Element billingReferenceElement = appendChild(document, elementRoot, "cac:BillingReference");
        Element invoiceDocumentReferenceElement = appendChild(document, billingReferenceElement, "cac:InvoiceDocumentReference");

        appendChild(document, invoiceDocumentReferenceElement, "cbc:ID", identificadorDocumentoAfectado);
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Tipo de Documento");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
        appendChild(document, invoiceDocumentReferenceElement, "cbc:DocumentTypeCode", tipoComprobanteAfectado, attributes);

    }

    private void appendChildDiscrepancyResponse(Document document, Element elementRoot,
                                                String tipoNotaDebito, String motivo) {

        Element discrepancyResponseElement = appendChild(document, elementRoot, "cac:DiscrepancyResponse");

        appendChild(document, discrepancyResponseElement, "cbc:ResponseCode", tipoNotaDebito);
        appendChild(document, discrepancyResponseElement, "cbc:Description", motivo);

    }

    private void appendChilDespatchDocumentReference(Document document, Element elementRoot,
                                                     String numeroGuiaRemision, String tipoGuiaRemision, Map<String, String> attributes) {

        if(StringUtils.isNotBlank(tipoGuiaRemision) && StringUtils.isNotBlank(numeroGuiaRemision)) {

            Element despatchDocumentReferenceElement = appendChild(document, elementRoot, "cac:DespatchDocumentReference");
            appendChildDocumentReference(document, despatchDocumentReferenceElement,
                    numeroGuiaRemision, tipoGuiaRemision, attributes);
        }
    }

    private void appendChilAdditionalDocumentReference(Document document, Element elementRoot,
                                                       String numeroDocumentoAdicional, String tipoDocumentoAdicional, Map<String, String> attributes) {

        if(StringUtils.isNotBlank(tipoDocumentoAdicional) && StringUtils.isNotBlank(numeroDocumentoAdicional)) {

            Element additionalDocumentReferenceElement = appendChild(document, elementRoot, "cac:AdditionalDocumentReference");
            appendChildDocumentReference(document, additionalDocumentReferenceElement,numeroDocumentoAdicional,
                    tipoDocumentoAdicional, attributes);
        }
    }

    private void appendChildDocumentReference(Document document, Element despatchDocumentReferenceElement,
                                              String numeroDocumentoReferencia, String tipoDocumentoReferencia, Map<String, String> attributes) {

        appendChild(document, despatchDocumentReferenceElement, "cbc:ID", numeroDocumentoReferencia);
        appendChild(document, despatchDocumentReferenceElement, "cbc:DocumentTypeCode", tipoDocumentoReferencia, attributes);
    }

    private void appendChildItem(Document document, Element invoiceLineElement, String descripcion,
                                 String codigoProducto, String codigoProductoSunat, String codigoProductoGS1, Map<String, String> attributes) {

        if(StringUtils.isNotBlank(descripcion) || StringUtils.isNotBlank(codigoProducto) ||
                StringUtils.isNotBlank(codigoProductoSunat) || StringUtils.isNotBlank(codigoProductoGS1)) {

            Element itemElement = appendChild(document, invoiceLineElement, "cac:Item");

            if(StringUtils.isNotBlank(descripcion)) {
                appendChild(document, itemElement, "cbc:Description", descripcion);
            }

            if(StringUtils.isNotBlank(codigoProducto)) {
                Element sellersItemIdentificationElement = appendChild(document, itemElement,
                        "cac:SellersItemIdentification");
                appendChild(document, sellersItemIdentificationElement, "cbc:ID", codigoProducto);
            }

            if(StringUtils.isNotBlank(codigoProductoGS1)) {
                Element standarItemIdentificationElement = appendChild(document, itemElement,
                        "cac:StandardItemIdentification");
                appendChild(document, standarItemIdentificationElement, "cbc:ID", codigoProductoGS1);
            }

            if(StringUtils.isNotBlank(codigoProductoSunat)) {

                Element commodityClassificationElement = appendChild(document, itemElement, "cac:CommodityClassification");
                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_ID, "Tipo de Precio");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "Tipo de Precio");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo16");
                appendChild(document, commodityClassificationElement, "cbc:ItemClassificationCode", codigoProductoSunat, attributes);
            }
        }
    }

    private void appendChildPricingReference(Document document, Element pricingReferenceElement,
                                             BigDecimal monto, String tipoPrecio, String codigoMoneda, Map<String, String> attributes) {

        if(monto != null) {
            Element alternativeConditionElement = appendChild(document, pricingReferenceElement,
                    "cac:AlternativeConditionPrice");
            Element priceAmountElement = appendChild(document, alternativeConditionElement,
                    "cbc:PriceAmount", UtilFormat.format(monto));
            priceAmountElement.setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Tipo de Precio");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "Tipo de Precio");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo16");

            appendChild(document, alternativeConditionElement, "cbc:PriceTypeCode", tipoPrecio, attributes);
        }
    }

    private void addElementsChildSubTaxTotal(Document document, Element taxTotalElement, BigDecimal montoBase,
                                             BigDecimal montoImpuesto, String codigoTipoAfectacionIgv, String codigoIsc, String codigoMoneda,
                                             BigDecimal porcentaje, Boolean isGratuito, Map<String, String> attributes) {

        if (montoBase != null && montoImpuesto != null) {

            Tipo tipoTributo = null;
            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxableAmount", UtilFormat.format(montoBase))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(montoImpuesto))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            appendChild(document, taxCategoryElement, "cbc:Percent", UtilFormat.format(porcentaje));

            if(StringUtils.isNotBlank(codigoTipoAfectacionIgv)) {
                /*attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Afectacion del IGV");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo07");*/
                appendChild(document, taxCategoryElement, "cbc:TaxExemptionReasonCode", codigoTipoAfectacionIgv);
                AfectacionIgvEnum afectacionEnum = AfectacionIgvEnum.getEstadoComprobante(codigoTipoAfectacionIgv, isGratuito);

                switch (afectacionEnum.getCodigoTributo()) {

                    case ConstantesSunat.CODIGO_TRIBUTO_IGV:
                        tipoTributo = ConstantesSunat.TRIBUTO_IGV;
                        break;
                    case ConstantesSunat.CODIGO_TRIBUTO_IVAP:
                        tipoTributo = ConstantesSunat.TRIBUTO_IVAP;
                        break;
                    case ConstantesSunat.CODIGO_TRIBUTO_EXONERADO:
                        tipoTributo = ConstantesSunat.TRIBUTO_EXONERADO;
                        break;
                    case ConstantesSunat.CODIGO_TRIBUTO_EXPORTACION:
                        tipoTributo = ConstantesSunat.TRIBUTO_EXPORTACION;
                        break;
                    case ConstantesSunat.CODIGO_TRIBUTO_GRATUITO:
                        tipoTributo = ConstantesSunat.TRIBUTO_GRATUITO;
                        break;
                    case ConstantesSunat.CODIGO_TRIBUTO_INAFECTO:
                        tipoTributo = ConstantesSunat.TRIBUTO_INAFECTO;
                        break;
                }
            }
            if(StringUtils.isNotBlank(codigoIsc)) {
                tipoTributo = ConstantesSunat.TRIBUTO_ISC;
                appendChild(document, taxCategoryElement, "cbc:TierRange", codigoIsc);
            }
            if(StringUtils.isBlank(codigoIsc) && StringUtils.isBlank(codigoTipoAfectacionIgv)) {
                tipoTributo = ConstantesSunat.TRIBUTO_OTROS;
            }

            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Codigo de tributos");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");

            Element taxSchemeElement = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            appendChild(document, taxSchemeElement, "cbc:ID", tipoTributo.getId(), attributes);
            appendChild(document, taxSchemeElement, "cbc:Name", tipoTributo.getName());
            appendChild(document, taxSchemeElement, "cbc:TaxTypeCode", tipoTributo.getTypeCode());
        }
    }

    private void appendChildSubTotalHeader(Document document, Element elementTaxTotal, BigDecimal montoBase,
                                           BigDecimal montoImpuesto, Map<String, String> attributes, String codigoTipoTributo, String codigoMoneda) {

        if (montoBase != null && montoImpuesto != null) {

            Element taxSubTotalElement = appendChild(document, elementTaxTotal, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxableAmount", UtilFormat.format(montoBase))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(montoImpuesto))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            Element taxSchemeElement = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            Tipo tipoTributo = null;

            switch (codigoTipoTributo) {

                case ConstantesSunat.CODIGO_TRIBUTO_IGV:
                    tipoTributo = ConstantesSunat.TRIBUTO_IGV;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_IVAP:
                    tipoTributo = ConstantesSunat.TRIBUTO_IVAP;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_ISC:
                    tipoTributo = ConstantesSunat.TRIBUTO_ISC;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_OTROS:
                    tipoTributo = ConstantesSunat.TRIBUTO_OTROS;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_EXPORTACION:
                    tipoTributo = ConstantesSunat.TRIBUTO_EXPORTACION;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_EXONERADO:
                    tipoTributo = ConstantesSunat.TRIBUTO_EXONERADO;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_GRATUITO:
                    tipoTributo = ConstantesSunat.TRIBUTO_GRATUITO;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_INAFECTO:
                    tipoTributo = ConstantesSunat.TRIBUTO_INAFECTO;
                    break;
            }

            appendChild(document, taxSchemeElement, "cbc:ID", tipoTributo.getId(), attributes);
            appendChild(document, taxSchemeElement, "cbc:Name", tipoTributo.getName());
            appendChild(document, taxSchemeElement, "cbc:TaxTypeCode", tipoTributo.getTypeCode());
        }
    }

    private Map<String, String> getAttributesElementRoot(){

        Map<String, String> atributos = new HashMap<String, String>();

        atributos.put("xmlns", 	   "urn:oasis:names:specification:ubl:schema:xsd:DebitNote-2");
        atributos.put("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        atributos.put("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
        atributos.put("xmlns:ccts","urn:un:unece:uncefact:documentation:2");
        atributos.put("xmlns:ds",  "http://www.w3.org/2000/09/xmldsig#");
        atributos.put("xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        atributos.put("xmlns:qdt", "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2");
        atributos.put("xmlns:sac", "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1");
        atributos.put("xmlns:udt", "urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2");
        atributos.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        return atributos;
    }

    private void appendChildAccountingCustomerParty(Document document, Element elementRoot,
                                                    String numeroDocumentoReceptor, String nombrecomercial, String tipoDocumentoReceptor, String denominacionReceptor,
                                                    Map<String, String> attributes, String direccionReceptor, String emailreceptor) {

        Element accountingCustomerPartyElement = appendChild(document, elementRoot,"cac:AccountingCustomerParty");

        Element partyElement = appendChild(document, accountingCustomerPartyElement, "cac:Party");
        Element partyIdentificationElement = appendChild(document, partyElement, "cac:PartyIdentification");
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, tipoDocumentoReceptor);
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Documento de Identidad");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
        appendChild(document, partyIdentificationElement, "cbc:ID", numeroDocumentoReceptor, attributes);

        Element partyNameElement = appendChild(document, partyElement, "cac:PartyName");
        appendChild(document, partyNameElement, "cbc:Name", nombrecomercial);
        Element partyLegalElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, partyLegalElement, "cbc:RegistrationName", denominacionReceptor);
        Element partyRegistrationAddress = appendChild(document, partyLegalElement, "cac:RegistrationAddress");
        Element partyAddressLine = appendChild(document, partyRegistrationAddress, "cac:AddressLine");
        appendChild(document, partyAddressLine, "cbc:Line", direccionReceptor);
        Element partyCountry = appendChild(document, partyRegistrationAddress, "cac:Country");
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "United Nations Economic Commission for Europe");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_ID, "ISO 3166-1");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "ISO 3166-1");
        appendChild(document, partyCountry, "cbc:IdentificationCode", "PE",attributes);
        if(emailreceptor!="false"){
            Element partyContact = appendChild(document, partyElement, "cac:Contact");
            appendChild(document, partyContact, "cbc:ElectronicMail", emailreceptor);
        }

    }

    private void appendChildDiscrepancyResponse(Document document, Element elementRoot, String serienumber,
                                                String tipoNotaDebito, String motivo) {

        Element discrepancyResponseElement = appendChild(document, elementRoot, "cac:DiscrepancyResponse");
        appendChild(document, discrepancyResponseElement, "cbc:ReferenceID", serienumber);
        appendChild(document, discrepancyResponseElement, "cbc:ResponseCode", tipoNotaDebito);
        appendChild(document, discrepancyResponseElement, "cbc:Description", motivo);

    }

}
