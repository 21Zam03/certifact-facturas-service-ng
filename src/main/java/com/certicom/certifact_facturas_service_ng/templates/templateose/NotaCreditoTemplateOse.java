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
public class NotaCreditoTemplateOse extends TemplateSunat {

    HashMap<Integer,String> mapTransaccion = new HashMap<>();
    private static final String CUOTA0 = "Cuota000";
    public NotaCreditoTemplateOse() {
        this.mapTransaccion.put(1,"Contado");
        this.mapTransaccion.put(2,"Credito");
    }

    public String buildCreditNote(PaymentVoucherDto creditNote) throws TemplateException {

        Signature signature = buildSignature(creditNote);
        String templateBuilt = buildTemplateInXML(creditNote, signature);

        return templateBuilt;
    }

    protected Signature buildSignature(PaymentVoucherDto creditNote) {

        Signature signature = new Signature();

        signature.setId("IDSignST");
        signature.setUri("#SignST");
        signature.setDenominacionEmisor(creditNote.getDenominacionEmisor());
        signature.setRucEmisor(creditNote.getRucEmisor());

        return signature;
    }

    private void appendCreditNoteLine(Document document, Element elementRoot,
                                      List<ComprobanteItem> creditNotelines, String codigoMoneda, PaymentVoucherDto notaCredito) throws TemplateException {

        int correlativoItem = 1;
        Map<String, String> attributes = new HashMap<>();
        BigDecimal montoZero = BigDecimal.ZERO;

        for(ComprobanteItem item: creditNotelines) {

            BigDecimal montoTotalImpuestosLinea = importeTotalImpuestos(item.getIgv(),
                    item.getIvap(), item.getIsc(), item.getOtrosTributos(), null);

            Element creditNoteLineElement = appendChild(document, elementRoot, "cac:CreditNoteLine");
            appendChild(document, creditNoteLineElement, "cbc:ID", correlativoItem);

            if(item.getCantidad() != null && item.getCodigoUnidadMedida() != null) {

                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_UNIT_CODE, item.getCodigoUnidadMedida());
                attributes.put(ConstantesSunat.ATTRIBUTE_UNIT_CODE_LIST_ID, "UN/ECE rec 20");
                attributes.put(ConstantesSunat.ATTRIBUTE_UNIT_CODE_LIST_AGENCY_NAME, "United Nations Economic Commission for Europe");
                appendChild(document, creditNoteLineElement, "cbc:CreditedQuantity", item.getCantidad(),attributes);
            }

            if(item.getValorVenta() != null) {
                appendChild(document, creditNoteLineElement, "cbc:LineExtensionAmount", UtilFormat.format(item.getValorVenta())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            }

            Element pricingReferenceElement = appendChild(document, creditNoteLineElement, "cac:PricingReference");
            if ((item.getCodigoTipoAfectacionIGV().equals("21"))) {
                appendChildPricingReference(document, pricingReferenceElement, item.getPrecioVentaUnitario(),
                        ConstantesSunat.CODIGO_TIPO_PRECIO_VALOR_REFERENCIAL, codigoMoneda, attributes);
            }else {
                appendChildPricingReference(document, pricingReferenceElement, item.getValorReferencialUnitario(),
                        ConstantesSunat.CODIGO_TIPO_PRECIO_PRECIO_UNITARIO, codigoMoneda, attributes);
            }
            Element taxTotalElement = appendChild(document, creditNoteLineElement, "cac:TaxTotal");
            appendChild(document, taxTotalElement, "cbc:TaxAmount", UtilFormat.format(montoTotalImpuestosLinea))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);



            if (notaCredito.getCodigoTipoNotaCredito().equals("13")){

                appendChildSubTotalHeaderNota(document, taxTotalElement, item.getMontoBaseIgv(),
                        montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_IGV, codigoMoneda,true);

            }else{
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
                        montoZero, true, attributes);
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
            }


            appendChildItem(document, creditNoteLineElement, item.getDescripcion(), item.getCodigoProducto(),
                    item.getCodigoProductoSunat());

            Element cbcPrice = appendChild(document, creditNoteLineElement, "cac:Price");
            appendChild(document, cbcPrice, "cbc:PriceAmount", UtilFormat.format7(item.getValorUnitario())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            item.setNumeroItem(correlativoItem);
            correlativoItem++;
        }
    }



    private void appendChildPricingReference(Document document, Element pricingReferenceElement,
                                             BigDecimal monto, String tipoPrecio, String codigoMoneda, Map<String, String> attributes) {

        if(monto != null) {
            Element alternativeConditionElement = appendChild(document, pricingReferenceElement,
                    "cac:AlternativeConditionPrice");
            Element priceAmountElement = appendChild(document, alternativeConditionElement,
                    "cbc:PriceAmount", UtilFormat.formatNota(monto));
            priceAmountElement.setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Tipo de Precio");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo16");

            appendChild(document, alternativeConditionElement, "cbc:PriceTypeCode", tipoPrecio, attributes);
        }
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

        /*if (StringUtils.isNotBlank(direccionOficinaEmisor)) {
            Element registrationAddressElement = appendChild(document, partyLegalElement, "cac:RegistrationAddress");
            Element cacAddressLine = appendChild(document, registrationAddressElement, "cac:AddressLine");
            appendChild(document, cacAddressLine, "cbc:Line", direccionOficinaEmisor);
        }*/
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "United Nations Economic Commission for Europe");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_ID, "ISO 3166-1");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Country");
        Element cacAddressCountry = appendChild(document, registrationAddressElement, "cac:Country");
        appendChild(document, cacAddressCountry, "cbc:IdentificationCode", "PE",attributes);
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

    private void appendChildDiscrepancyResponse(Document document, Element elementRoot,String afectado,
                                                String tipoNotaCredito, String motivo) {

        Element discrepancyResponseElement = appendChild(document, elementRoot, "cac:DiscrepancyResponse");
        appendChild(document, discrepancyResponseElement, "cbc:ReferenceID", afectado);
        appendChild(document, discrepancyResponseElement, "cbc:ResponseCode", tipoNotaCredito);
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




    private void appendChildSumatoria(Document document, Element elementRoot,
                                      BigDecimal sumatoriaTributo, String codigoTipoTributo, String codigoMoneda) {

        if(sumatoriaTributo != null) {
            Element taxTotalElement = appendChild(document, elementRoot, "cac:TaxTotal");
            appendChild(document, taxTotalElement, "cbc:TaxAmount", UtilFormat.format(sumatoriaTributo))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(sumatoriaTributo))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            Element taxSchemeElement = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            Tipo tipoTributo = null;

            switch(codigoTipoTributo) {
                case ConstantesSunat.CODIGO_TRIBUTO_IGV:
                    tipoTributo = ConstantesSunat.TRIBUTO_IGV;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_ISC:
                    tipoTributo = ConstantesSunat.TRIBUTO_ISC;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_OTROS:
                    tipoTributo = ConstantesSunat.TRIBUTO_OTROS;
                    break;

            }
            appendChild(document, taxSchemeElement, "cbc:ID", tipoTributo.getId());
            appendChild(document, taxSchemeElement, "cbc:Name", tipoTributo.getName());
            appendChild(document, taxSchemeElement, "cbc:TaxTypeCode", tipoTributo.getTypeCode());
        }
    }
    private void appendChildDocumentReference(Document document, Element despatchDocumentReferenceElement,
                                              String numeroDocumentoReferencia, String tipoDocumentoReferencia, Map<String, String> attributes) {

        appendChild(document, despatchDocumentReferenceElement, "cbc:ID", numeroDocumentoReferencia);
        appendChild(document, despatchDocumentReferenceElement, "cbc:DocumentTypeCode", tipoDocumentoReferencia, attributes);
    }
    private void appendChildItem(Document document, Element invoiceLineElement, String descripcion,
                                 String codigoProducto, String codigoProductoSunat) {

        if(descripcion != null || codigoProducto != null || codigoProductoSunat != null) {
            Element itemElement = appendChild(document, invoiceLineElement, "cac:Item");

            if(StringUtils.isNotBlank(descripcion)) {
                appendChild(document, itemElement, "cbc:Description", descripcion);
            }
            if(StringUtils.isNotBlank(codigoProducto)) {
                Element sellersItemIdentificationElement = appendChild(document, itemElement,
                        "cac:SellersItemIdentification");
                appendChild(document, sellersItemIdentificationElement, "cbc:ID", codigoProducto);
            }
            if(StringUtils.isNotBlank(codigoProductoSunat)) {
                Element commodityClassificationElement = appendChild(document, itemElement,
                        "cac:CommodityClassification");
                Element itemClassificationCodeElement = appendChild(document, commodityClassificationElement, "cbc:ItemClassificationCode", codigoProductoSunat);
                itemClassificationCodeElement.setAttribute("listID", "UNSPSC");
                itemClassificationCodeElement.setAttribute("listAgencyName", "GS1 US");
                itemClassificationCodeElement.setAttribute("listName", "Item Classification");
            }
        }
    }
    private Map<String, String> getAttributesElementRoot(){

        Map<String, String> atributos = new HashMap<String, String>();

        atributos.put("xmlns", 	   "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2");
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
    private void appendChildSubTotalHeaderNota(Document document, Element cactaxTotal, BigDecimal totalValorVentaExportacion,
                                               BigDecimal montoZero, Map<String, String> attributes, String codigoTipoTributo, String codigoMoneda, boolean line) {
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Codigo de tributos");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");

        Element taxSubTotalElement = appendChild(document, cactaxTotal, "cac:TaxSubtotal");
        appendChild(document, taxSubTotalElement, "cbc:TaxableAmount", UtilFormat.format(BigDecimal.ZERO))
                .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
        appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(BigDecimal.ZERO))
                .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

        Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");

        if (line)
            appendChild(document, taxCategoryElement, "cbc:Percent", UtilFormat.format(new BigDecimal(18)));

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

    private void addElementsChildSubTaxTotal(Document document, Element taxTotalElement, BigDecimal montoBase,
                                             BigDecimal montoImpuesto, String codigoTipoAfectacionIgv, String codigoIsc, String codigoMoneda,
                                             BigDecimal porcentaje, Boolean isGratuito, Map<String, String> attributes) {

        if ((montoBase != null && montoImpuesto != null)||(isGratuito&&montoBase != null)) {

            Tipo tipoTributo = null;
            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxableAmount", UtilFormat.format(montoBase))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(montoImpuesto))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            appendChild(document, taxCategoryElement, "cbc:Percent", UtilFormat.format(porcentaje));

            if(StringUtils.isNotBlank(codigoTipoAfectacionIgv)) {
                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Afectacion del IGV");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo07");
                appendChild(document, taxCategoryElement, "cbc:TaxExemptionReasonCode", codigoTipoAfectacionIgv,attributes);
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

    private String buildTemplateInXML(PaymentVoucherDto notaCredito, Signature signature) throws TemplateException {

        String stringXMLGenerate = null;
        Document document;
        Element elementRoot;
        Map<String, String> attributes = new HashMap<>();
        BigDecimal montoZero = BigDecimal.ZERO;
        BigDecimal montoTotalImpuestos;

        try {

            document = createDocument();
            elementRoot = addElementRoot(document, ROOT_CREDIT_NOTE, getAttributesElementRoot());

            appendChild(document, elementRoot, "ext:UBLExtensions");





            appendChild(document, elementRoot, "cbc:UBLVersionID", ConstantesSunat.UBL_VERSION_2_1);
            appendChild(document, elementRoot, "cbc:CustomizationID", ConstantesSunat.CUSTOMIZATION_VERSION_2_0);
            appendChild(document, elementRoot, "cbc:ID", notaCredito.getSerie()+"-"+notaCredito.getNumero());
            appendChild(document, elementRoot, "cbc:IssueDate", notaCredito.getFechaEmision());
            appendChild(document, elementRoot, "cbc:IssueTime",	notaCredito.getHoraEmision());

            attributes.clear();
            String stringMoneda = notaCredito.getCodigoMoneda()!=null?notaCredito.getCodigoMoneda().equalsIgnoreCase("USD")?"Dólares Americanos":"Soles":"Soles";
            attributes.put(ConstantesSunat.ATTRIBUTE_LANGUAGE_LOCALE_ID, "1000");
            appendChild(document, elementRoot, "cbc:Note", UtilGenerateLetraNumber.Convertir(notaCredito.getImporteTotalVenta().setScale(2,BigDecimal.ROUND_HALF_UP).toString(),stringMoneda,true) , attributes);
/*
            if(notaCredito.getLeyendas()!=null && notaCredito.getLeyendas().isEmpty()) {
                for (Leyenda note: notaCredito.getLeyendas()) {
                    if (StringUtils.isNotBlank(note.getCodigo())) {
                        appendChild(document, elementRoot, "cbc:Note", note.getDescripcion())
                            .setAttribute(ConstantesSunat.ATTRIBUTE_LANGUAGE_LOCALE_ID, note.getCodigo());
                    } else {
                        appendChild(document, elementRoot, "cbc:Note", note.getDescripcion());
                    }
                }
            }*/

            appendChild(document, elementRoot, "cbc:DocumentCurrencyCode", notaCredito.getCodigoMoneda());
            appendChild(document, elementRoot, "cbc:LineCountNumeric", notaCredito.getItems().size());
            appendChildDiscrepancyResponse(document, elementRoot,notaCredito.getSerieAfectado()+"-"+notaCredito.getNumeroAfectado(),
                    notaCredito.getCodigoTipoNotaCredito(),
                    notaCredito.getMotivoNota());

            appendChildBillingReference(document, elementRoot,
                    notaCredito.getSerieAfectado()+"-"+notaCredito.getNumeroAfectado(),
                    notaCredito.getTipoComprobanteAfectado(), attributes);
            attributes.clear();
            attributes.put("xmlns", "http://www.w3.org/2000/09/xmldsig#");

            if(notaCredito.getGuiasRelacionadas()!=null && !notaCredito.getGuiasRelacionadas().isEmpty()){

                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Tipo de Documento");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
                for (GuiaRelacionada guiaRelacionada : notaCredito.getGuiasRelacionadas()){
                    appendChilDespatchDocumentReference(document, elementRoot,
                            guiaRelacionada.getSerieNumeroGuia(),
                            guiaRelacionada.getCodigoTipoGuia(),
                            attributes);
                }
            }




            if (notaCredito.getDocumentosRelacionados() != null && !notaCredito.getDocumentosRelacionados().isEmpty()) {

                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Documento Relacionado");
                attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo12");
                for (DocumentoRelacionado documentoRelacionado : notaCredito.getDocumentosRelacionados()) {
                    appendChilAdditionalDocumentReference(document, elementRoot, documentoRelacionado.getNumero(),
                            documentoRelacionado.getTipoDocumento(), attributes);
                }
            }

            Element cacSignature = appendChild(document, elementRoot, "cac:Signature");
            appendChild(document, cacSignature, "cbc:ID", "IDSignKG");
            Element cacSignatoryParty = appendChild(document, cacSignature, "cac:SignatoryParty");
            Element cacPartyIdentification = appendChild(document, cacSignatoryParty, "cac:PartyIdentification");
            appendChild(document, cacPartyIdentification, "cbc:ID", notaCredito.getRucEmisor());
            Element cacPartyName = appendChild(document, cacSignatoryParty, "cac:PartyName");
            appendChild(document, cacPartyName, "cbc:Name", notaCredito.getDenominacionEmisor());
            Element cacDigitalSignatureAttachment = appendChild(document, cacSignature, "cac:DigitalSignatureAttachment");
            Element cacExternalReference = appendChild(document, cacDigitalSignatureAttachment, "cac:ExternalReference");
            appendChild(document, cacExternalReference, "cbc:URI", "#SignatureKG");

            appendChildAccountingSupplierParty(document, elementRoot,
                    notaCredito.getRucEmisor(), notaCredito.getTipoDocumentoEmisor(),
                    notaCredito.getDenominacionEmisor(), notaCredito.getNombreComercialEmisor(),
                    notaCredito.getCodigoLocalAnexoEmisor(), notaCredito.getDireccionOficinaEmisor(),
                    attributes);
            String nombrecomercial = "";
            if (StringUtils.isNotBlank(notaCredito.getNombreComercialEmisor())) {
                nombrecomercial = notaCredito.getNombreComercialEmisor();
            }else {
                nombrecomercial = notaCredito.getDenominacionReceptor();
            }
            String  emailreceptor = "false";
            if(notaCredito.getEmailReceptor()!=null){
                emailreceptor=notaCredito.getEmailReceptor();
            }
            appendChildAccountingCustomerParty(document, elementRoot,
                    notaCredito.getNumeroDocumentoReceptor(), nombrecomercial ,notaCredito.getTipoDocumentoReceptor(),
                    notaCredito.getDenominacionReceptor(), attributes,notaCredito.getDireccionReceptor(),emailreceptor);

            montoTotalImpuestos = importeTotalImpuestos(notaCredito.getTotalIgv(),
                    notaCredito.getTotalIvap(),
                    notaCredito.getTotalIsc(),
                    notaCredito.getTotalOtrostributos(),
                    null);



            if (notaCredito.getTipoTransaccion()!=null&& notaCredito.getTipoTransaccion().intValue()> 0) {

                /*if (notaCredito.getTipoTransaccion().intValue()==1){
                    System.out.println(mapTransaccion.get(notaCredito.getTipoTransaccion().intValue()));
                    appendChild(document, paymentTermsElementTransaction, "cbc:PaymentMeansID", mapTransaccion.get(notaCredito.getTipoTransaccion().intValue()));
                }*/
                if (notaCredito.getTipoTransaccion().intValue()==2){
                    Element paymentTermsElementTransaction = appendChild(document, elementRoot, "cac:PaymentTerms");
                    appendChild(document, paymentTermsElementTransaction, "cbc:ID", "FormaPago");

                    appendChild(document, paymentTermsElementTransaction, "cbc:PaymentMeansID", mapTransaccion.get(notaCredito.getTipoTransaccion().intValue()));
                    appendChild(document, paymentTermsElementTransaction, "cbc:Amount", UtilFormat.format(notaCredito.getMontoPendiente()))
                            .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, notaCredito.getCodigoMoneda());

                    for (int i = 0; i < notaCredito.getCuotas().size();i++){
                        Element paymentTermsElementTransactionCuota = appendChild(document, elementRoot, "cac:PaymentTerms");
                        appendChild(document, paymentTermsElementTransactionCuota, "cbc:ID", "FormaPago");
                        appendChild(document, paymentTermsElementTransactionCuota, "cbc:PaymentMeansID", getStringCuota(notaCredito.getCuotas().get(i).getNumero()));
                        appendChild(document, paymentTermsElementTransactionCuota, "cbc:Amount", UtilFormat.format(notaCredito.getCuotas().get(i).getMonto()))
                                .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, notaCredito.getCodigoMoneda());;
                        appendChild(document, paymentTermsElementTransactionCuota, "cbc:PaymentDueDate", notaCredito.getCuotas().get(i).getFecha());
                    }

                }
            }







            Element cactaxTotal = appendChild(document, elementRoot, "cac:TaxTotal");
            appendChild(document, cactaxTotal, "cbc:TaxAmount", UtilFormat.format(montoTotalImpuestos))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, notaCredito.getCodigoMoneda());

            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, "UN/ECE 5153");
            if (notaCredito.getCodigoTipoNotaCredito().equals("13")){
                appendChildSubTotalHeaderNota(document, cactaxTotal, notaCredito.getTotalValorVentaExportacion(),
                        montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_IGV, notaCredito.getCodigoMoneda(),false);
            }else{
                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
                attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, "UN/ECE 5153");
                appendChildSubTotalHeader(document, cactaxTotal, notaCredito.getTotalValorVentaExportacion(),
                        montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_EXPORTACION, notaCredito.getCodigoMoneda());
                appendChildSubTotalHeader(document, cactaxTotal, notaCredito.getTotalValorVentaInafecta(),
                        montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_INAFECTO, notaCredito.getCodigoMoneda());
                appendChildSubTotalHeader(document, cactaxTotal, notaCredito.getTotalValorVentaExonerada(),
                        montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_EXONERADO, notaCredito.getCodigoMoneda());
                appendChildSubTotalHeader(document, cactaxTotal, notaCredito.getTotalValorVentaGratuita(),
                        montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_GRATUITO, notaCredito.getCodigoMoneda());

                appendChildSubTotalHeader(document, cactaxTotal, notaCredito.getTotalValorVentaGravada(),
                        notaCredito.getTotalIgv(), attributes, ConstantesSunat.CODIGO_TRIBUTO_IGV, notaCredito.getCodigoMoneda());
                appendChildSubTotalHeader(document, cactaxTotal, notaCredito.getTotalValorVentaGravadaIVAP(),
                        notaCredito.getTotalIvap(), attributes, ConstantesSunat.CODIGO_TRIBUTO_IVAP, notaCredito.getCodigoMoneda());
                appendChildSubTotalHeader(document, cactaxTotal, notaCredito.getTotalValorBaseIsc(),
                        notaCredito.getTotalIsc(), attributes, ConstantesSunat.CODIGO_TRIBUTO_ISC, notaCredito.getCodigoMoneda());
                appendChildSubTotalHeader(document, cactaxTotal, notaCredito.getTotalValorBaseOtrosTributos(),
                        notaCredito.getTotalOtrostributos(), attributes, ConstantesSunat.CODIGO_TRIBUTO_OTROS, notaCredito.getCodigoMoneda());
            }


            Element legalMonetaryTotalElement = appendChild(document, elementRoot, "cac:LegalMonetaryTotal");
            if(notaCredito.getSumatoriaOtrosCargos() != null) {
                appendChild(document, legalMonetaryTotalElement, "cbc:ChargeTotalAmount",
                        UtilFormat.format(notaCredito.getSumatoriaOtrosCargos())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, notaCredito.getCodigoMoneda());
            }
            appendChild(document, legalMonetaryTotalElement, "cbc:PayableAmount",
                    UtilFormat.format(notaCredito.getImporteTotalVenta())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, notaCredito.getCodigoMoneda());

            appendCreditNoteLine(document, elementRoot, notaCredito.getItems(),
                    notaCredito.getCodigoMoneda(), notaCredito);

            String stringXML = buildStringFromDOM(document);
            stringXMLGenerate = formatXML(stringXML);

        } catch (Exception e) {
            e.printStackTrace();
            throw new TemplateException("Error al generar plantilla para Nota de Credito:" +e.getMessage());
        }

        return stringXMLGenerate;
    }
    private String getStringCuota(Integer numero) {
        String cadena = CUOTA0;
        String res = cadena.substring(0,cadena.length()-((numero.toString()).length())) + numero;
        return res;
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

}
