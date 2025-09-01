package com.certicom.certifact_facturas_service_ng.templates.template;

import com.certicom.certifact_facturas_service_ng.model.PaymentVoucher;
import com.certicom.certifact_facturas_service_ng.dto.others.Anticipo;
import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteItem;
import com.certicom.certifact_facturas_service_ng.dto.others.GuiaRelacionada;
import com.certicom.certifact_facturas_service_ng.dto.others.Tipo;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;

import static com.certicom.certifact_facturas_service_ng.util.UtilXml.appendChild;
import static com.certicom.certifact_facturas_service_ng.util.UtilXml.formatXML;

@Component
@Slf4j
public class FacturaTemplate {

    public String construirFactura(PaymentVoucher factura) throws TemplateException {
        String xml;
        DOMSource source;
        StringWriter writer;
        int correlativoItem = 1;

        BigDecimal montoAnticiposTotalValorVenta = BigDecimal.ZERO;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();

            Element invoiceRootElement = doc.createElement("Invoice");
            doc.appendChild(invoiceRootElement);

            Attr xmlns = doc.createAttribute("xmlns");
            xmlns.setValue("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
            invoiceRootElement.setAttributeNode(xmlns);

            Attr xmlnscac = doc.createAttribute("xmlns:cac");
            xmlnscac.setValue("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
            invoiceRootElement.setAttributeNode(xmlnscac);

            Attr xmlnscbc = doc.createAttribute("xmlns:cbc");
            xmlnscbc.setValue("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
            invoiceRootElement.setAttributeNode(xmlnscbc);

            Attr xmlnsccts = doc.createAttribute("xmlns:ccts");
            xmlnsccts.setValue("urn:un:unece:uncefact:documentation:2");
            invoiceRootElement.setAttributeNode(xmlnsccts);

            Attr xmlnsds = doc.createAttribute("xmlns:ds");
            xmlnsds.setValue("http://www.w3.org/2000/09/xmldsig#");
            invoiceRootElement.setAttributeNode(xmlnsds);

            Attr xmlnsext = doc.createAttribute("xmlns:ext");
            xmlnsext.setValue("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
            invoiceRootElement.setAttributeNode(xmlnsext);

            Attr xmlnsqdt = doc.createAttribute("xmlns:qdt");
            xmlnsqdt.setValue("urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2");
            invoiceRootElement.setAttributeNode(xmlnsqdt);

            Attr xmlnssac = doc.createAttribute("xmlns:sac");
            xmlnssac.setValue("urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1");
            invoiceRootElement.setAttributeNode(xmlnssac);

            Attr xmlnsudt = doc.createAttribute("xmlns:udt");
            xmlnsudt.setValue("urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2");
            invoiceRootElement.setAttributeNode(xmlnsudt);

            Attr xmlnsxsi = doc.createAttribute("xmlns:xsi");
            xmlnsxsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
            invoiceRootElement.setAttributeNode(xmlnsxsi);

            Element extUBLExtensions = appendChild(doc, invoiceRootElement, "ext:UBLExtensions");
            Element extUBLExtension = appendChild(doc, extUBLExtensions, "ext:UBLExtension");
            Element extExtensionContent = appendChild(doc, extUBLExtension, "ext:ExtensionContent");
            Element sacAdditionalInformation = appendChild(doc, extExtensionContent,
                    "sac:AdditionalInformation");

            if (factura.getTotalValorVentaExportacion() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_EXPORTADA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(factura.getTotalValorVentaExportacion())).
                        setAttribute("currencyID", factura.getCodigoMoneda());
            }
            if (factura.getTotalValorVentaGravada() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_GRAVADA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(factura.getTotalValorVentaGravada())).
                        setAttribute("currencyID", factura.getCodigoMoneda());
            }
            if (factura.getTotalValorVentaInafecta() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_INAFECTA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(factura.getTotalValorVentaInafecta())).
                        setAttribute("currencyID", factura.getCodigoMoneda());
            }
            if (factura.getTotalValorVentaExonerada() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_EXONERADA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(factura.getTotalValorVentaExonerada())).
                        setAttribute("currencyID", factura.getCodigoMoneda());
            }
            if (factura.getTotalValorVentaGratuita() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_GRATUITA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(factura.getTotalValorVentaGratuita())).
                        setAttribute("currencyID", factura.getCodigoMoneda());
            }
            if (factura.getTotalDescuento() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID", ConstantesSunat.TOTAL_DESCUENTO);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(factura.getTotalDescuento())).
                        setAttribute("currencyID", factura.getCodigoMoneda());
            }

            if (StringUtils.isNotBlank(factura.getCodigoTipoOperacion())) {
                Element sunatTransactionElement = appendChild(doc, sacAdditionalInformation,
                        "sac:SUNATTransaction");
                appendChild(doc, sunatTransactionElement, "cbc:ID", factura.getCodigoTipoOperacion());
            }

            appendChild(doc, invoiceRootElement, "cbc:UBLVersionID", "2.0");
            appendChild(doc, invoiceRootElement, "cbc:CustomizationID", "1.0");
            appendChild(doc, invoiceRootElement, "cbc:ID", factura.getSerie() + "-" + factura.getNumero());
            appendChild(doc, invoiceRootElement, "cbc:IssueDate", factura.getFechaEmision());
            if (StringUtils.isNotBlank(factura.getHoraEmision())) {
                appendChild(doc, invoiceRootElement, "cbc:IssueTime", factura.getHoraEmision());
            }
            appendChild(doc, invoiceRootElement, "cbc:InvoiceTypeCode", factura.getTipoComprobante());
            appendChild(doc, invoiceRootElement, "cbc:DocumentCurrencyCode", factura.getCodigoMoneda());

            if (StringUtils.isNotBlank(factura.getFechaVencimiento())) {
                appendChild(doc, invoiceRootElement, "cbc:ExpiryDate", factura.getFechaVencimiento());
            }


            //ORDEN DE COMPRA
            if (StringUtils.isNotBlank(factura.getOrdenCompra())) {
                Element cacOrderReference = appendChild(doc, invoiceRootElement, "cac:OrderReference");
                appendChild(doc, cacOrderReference, "cbc:ID",
                        factura.getOrdenCompra());
            }

            if (factura.getGuiasRelacionadas() != null && !factura.getGuiasRelacionadas().isEmpty()) {
                for (GuiaRelacionada guiaRelacionada : factura.getGuiasRelacionadas()) {
                    appendChilDespatchDocumentReference(doc, invoiceRootElement,
                            guiaRelacionada.getSerieNumeroGuia(),
                            guiaRelacionada.getCodigoTipoGuia());
                }
            }

            appendChilAdditionalDocumentReference(doc, invoiceRootElement,
                    factura.getSerieNumeroOtroDocumentoRelacionado(),
                    factura.getCodigoTipoOtroDocumentoRelacionado());

            Element cacSignature = appendChild(doc, invoiceRootElement, "cac:Signature");
            appendChild(doc, cacSignature, "cbc:ID", "IDSignKG");

            Element cacSignatoryParty = appendChild(doc, cacSignature, "cac:SignatoryParty");

            Element cacPartyIdentification = appendChild(doc, cacSignatoryParty, "cac:PartyIdentification");
            appendChild(doc, cacPartyIdentification, "cbc:ID", factura.getRucEmisor());

            Element cacPartyName = appendChild(doc, cacSignatoryParty, "cac:PartyName");
            appendChild(doc, cacPartyName, "cbc:Name", factura.getDenominacionEmisor());

            Element cacDigitalSignatureAttachment = appendChild(doc, cacSignature,
                    "cac:DigitalSignatureAttachment");
            Element cacExternalReference = appendChild(doc, cacDigitalSignatureAttachment,
                    "cac:ExternalReference");
            appendChild(doc, cacExternalReference, "cbc:URI", "#SignatureKG");

            Element cacAccountingSupplierParty = appendChild(doc, invoiceRootElement,
                    "cac:AccountingSupplierParty");
            appendChild(doc, cacAccountingSupplierParty, "cbc:CustomerAssignedAccountID",
                    factura.getRucEmisor());
            appendChild(doc, cacAccountingSupplierParty, "cbc:AdditionalAccountID",
                    factura.getTipoDocumentoEmisor());

            Element cacPartySupplier = appendChild(doc, cacAccountingSupplierParty, "cac:Party");
            if (StringUtils.isNotBlank(factura.getNombreComercialEmisor())) {
                Element cacPartyNameElement = appendChild(doc, cacPartySupplier, "cac:PartyName");
                appendChild(doc, cacPartyNameElement, "cbc:Name", factura.getNombreComercialEmisor());
            }

            Element cacPartyLegalEntity = appendChild(doc, cacPartySupplier, "cac:PartyLegalEntity");

            if (StringUtils.isNotBlank(factura.getCodigoLocalAnexoEmisor())) {
                Element cacRegistrationAddress = appendChild(doc, cacPartyLegalEntity, "cac:RegistrationAddress");
                appendChild(doc, cacRegistrationAddress, "cbc:AddressTypeCode", factura.getCodigoLocalAnexoEmisor());
            }
            appendChild(doc, cacPartyLegalEntity, "cbc:RegistrationName", factura.getDenominacionEmisor());

            Element cacAccountingCustomerParty = appendChild(doc, invoiceRootElement, "cac:AccountingCustomerParty");
            appendChild(doc, cacAccountingCustomerParty, "cbc:CustomerAssignedAccountID", factura.getNumeroDocumentoReceptor());
            appendChild(doc, cacAccountingCustomerParty, "cbc:AdditionalAccountID", factura.getTipoDocumentoReceptor());

            if (StringUtils.isNotBlank(factura.getDenominacionReceptor())) {
                Element cacPartyCustomer = appendChild(doc, cacAccountingCustomerParty, "cac:Party");
                cacPartyLegalEntity = appendChild(doc, cacPartyCustomer, "cac:PartyLegalEntity");
                appendChild(doc, cacPartyLegalEntity, "cbc:RegistrationName", factura.getDenominacionReceptor());
            }


            //[Anticipos con deduccion]
            if (StringUtils.isNotBlank(factura.getCodigoTipoOperacion()) &&
                    factura.getCodigoTipoOperacion().equals(ConstantesSunat.CODIGO_TIPO_OPERACION_VENTA_INTERNA_ANTICIPOS) &&
                    factura.getAnticipos() != null && !factura.getAnticipos().isEmpty()) {

                for (Anticipo anticipo : factura.getAnticipos()) {

                    Element prepaidPayment = appendChild(doc, invoiceRootElement, "cac:PrepaidPayment");
                    appendChild(doc, prepaidPayment, "cbc:ID",
                            String.format("%s-%s", anticipo.getSerieAnticipo(), anticipo.getNumeroAnticipo())).
                            setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID, anticipo.getTipoDocumentoAnticipo());
                    appendChild(doc, prepaidPayment, "cbc:PaidAmount", UtilFormat.format(anticipo.getMontoAnticipado())).
                            setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
                    appendChild(doc, prepaidPayment, "cbc:InstructionID", factura.getRucEmisor()).
                            setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID, factura.getTipoDocumentoEmisor());
                    ;

                    montoAnticiposTotalValorVenta = montoAnticiposTotalValorVenta.add(anticipo.getMontoAnticipado());
                    factura.setTotalAnticipos(montoAnticiposTotalValorVenta);
                }
            }
            //[Anticipos]

            appendChildSumatoria(doc, invoiceRootElement, factura.getTotalIgv(),
                    ConstantesSunat.CODIGO_TRIBUTO_IGV, factura.getCodigoMoneda());
            appendChildSumatoria(doc, invoiceRootElement, factura.getTotalIsc(),
                    ConstantesSunat.CODIGO_TRIBUTO_ISC, factura.getCodigoMoneda());
            appendChildSumatoria(doc, invoiceRootElement, factura.getTotalOtrostributos(),
                    ConstantesSunat.CODIGO_TRIBUTO_OTROS, factura.getCodigoMoneda());

            Element legalMonetaryTotalElement = appendChild(doc, invoiceRootElement, "cac:LegalMonetaryTotal");
            if (factura.getDescuentoGlobales() != null) {
                appendChild(doc, legalMonetaryTotalElement, "cbc:AllowanceTotalAmount",
                        UtilFormat.format(factura.getDescuentoGlobales())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
            }
            if (factura.getSumatoriaOtrosCargos() != null) {
                appendChild(doc, legalMonetaryTotalElement, "cbc:ChargeTotalAmount",
                        UtilFormat.format(factura.getSumatoriaOtrosCargos())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
            }
            //si existen anticipos deducidos
            if (montoAnticiposTotalValorVenta.compareTo(BigDecimal.ZERO) > 0) {
                appendChild(doc, legalMonetaryTotalElement, "cbc:PrepaidAmount",
                        UtilFormat.format(montoAnticiposTotalValorVenta)).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
            }

            appendChild(doc, legalMonetaryTotalElement, "cbc:PayableAmount",
                    UtilFormat.format(factura.getImporteTotalVenta())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());

            for (ComprobanteItem item : factura.getItems()) {

                Element cacInvoiceLine = appendChild(doc, invoiceRootElement, "cac:InvoiceLine");
                appendChild(doc, cacInvoiceLine, "cbc:ID", correlativoItem);
                appendChild(doc, cacInvoiceLine, "cbc:InvoicedQuantity", UtilFormat.format(item.getCantidad())).
                        setAttribute("unitCode", item.getCodigoUnidadMedida());
                appendChild(doc, cacInvoiceLine, "cbc:LineExtensionAmount", UtilFormat.format(item.getValorVenta())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());

                Element pricingReferenceElement = appendChild(doc, cacInvoiceLine, "cac:PricingReference");
                appendChildPricingReference(doc, pricingReferenceElement, item.getPrecioVentaUnitario(),
                        ConstantesSunat.CODIGO_TIPO_PRECIO_PRECIO_UNITARIO, factura.getCodigoMoneda());
                appendChildPricingReference(doc, pricingReferenceElement, item.getValorReferencialUnitario(),
                        ConstantesSunat.CODIGO_TIPO_PRECIO_VALOR_REFERENCIAL, factura.getCodigoMoneda());

                appendChildAllowance(doc, cacInvoiceLine, item.getDescuento(), factura.getCodigoMoneda());

                addElementsChildTaxTotal(doc, cacInvoiceLine, item.getIgv(),
                        ConstantesSunat.CODIGO_TRIBUTO_IGV, item.getCodigoTipoAfectacionIGV(),
                        factura.getCodigoMoneda());
                addElementsChildTaxTotal(doc, cacInvoiceLine, item.getIsc(),
                        ConstantesSunat.CODIGO_TRIBUTO_ISC, item.getCodigoTipoCalculoISC(),
                        factura.getCodigoMoneda());

                appendChildItem(doc, cacInvoiceLine, item.getDescripcion(), item.getCodigoProducto(),
                        item.getCodigoProductoSunat());
                Element cbcPrice = appendChild(doc, cacInvoiceLine, "cac:Price");
                appendChild(doc, cbcPrice, "cbc:PriceAmount", UtilFormat.format(item.getValorUnitario())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());

                item.setNumeroItem(correlativoItem);
                correlativoItem++;
            }

            source = new DOMSource(doc);

            Transformer transformer = getTransformer();

            writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);

            xml = formatXML(writer.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TemplateException(ex.getMessage());
        }
        return xml;
    }

    private void appendChilDespatchDocumentReference(Document document, Element elementRoot,
                                                     String numeroGuiaRemision, String tipoGuiaRemision) {

        if (StringUtils.isNotBlank(tipoGuiaRemision) && StringUtils.isNotBlank(numeroGuiaRemision)) {

            Element despatchDocumentReferenceElement = appendChild(document, elementRoot, "cac:DespatchDocumentReference");
            appendChildDocumentReference(document, despatchDocumentReferenceElement,
                    numeroGuiaRemision, tipoGuiaRemision);
        }
    }

    private void appendChildDocumentReference(Document document, Element despatchDocumentReferenceElement,
                                              String numeroDocumentoReferencia, String tipoDocumentoReferencia) {

        appendChild(document, despatchDocumentReferenceElement, "cbc:ID", numeroDocumentoReferencia);
        appendChild(document, despatchDocumentReferenceElement, "cbc:DocumentTypeCode", tipoDocumentoReferencia);
    }

    private void appendChilAdditionalDocumentReference(Document document, Element elementRoot,
                                                       String numeroDocumentoAdicional, String tipoDocumentoAdicional) {

        if (StringUtils.isNotBlank(tipoDocumentoAdicional) && StringUtils.isNotBlank(numeroDocumentoAdicional)) {

            Element additionalDocumentReferenceElement = appendChild(document, elementRoot, "cac:AdditionalDocumentReference");
            appendChildDocumentReference(document, additionalDocumentReferenceElement,
                    numeroDocumentoAdicional, tipoDocumentoAdicional);
        }
    }

    private void appendChildSumatoria(Document document, Element elementRoot,
                                      BigDecimal sumatoriaTributo, String codigoTipoTributo, String codigoMoneda) {

        if (sumatoriaTributo != null) {

            Element taxTotalElement = appendChild(document, elementRoot, "cac:TaxTotal");
            appendChild(document, taxTotalElement, "cbc:TaxAmount", UtilFormat.format(sumatoriaTributo))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(sumatoriaTributo))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            Element taxSchemeElement = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            Tipo tipoTributo = null;

            switch (codigoTipoTributo) {

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

    private void appendChildPricingReference(Document document, Element pricingReferenceElement,
                                             BigDecimal monto, String tipoPrecio, String codigoMoneda) {

        if (monto != null) {
            Element alternativeConditionElement = appendChild(document, pricingReferenceElement, "cac:AlternativeConditionPrice");
            Element priceAmountElement = appendChild(document, alternativeConditionElement, "cbc:PriceAmount", UtilFormat.format(monto));
            priceAmountElement.setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            appendChild(document, alternativeConditionElement, "cbc:PriceTypeCode", tipoPrecio);
        }
    }

    private void appendChildAllowance(Document doc, Element cacInvoiceLine, BigDecimal descuento,
                                      String moneda) {

        if (descuento != null) {
            Element allowanceElement = appendChild(doc, cacInvoiceLine, "cac:AllowanceCharge");
            appendChild(doc, allowanceElement, "cbc:ChargeIndicator", "false");
            appendChild(doc, allowanceElement, "cbc:Amount", UtilFormat.format(descuento)).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, moneda);
        }
    }

    private void addElementsChildTaxTotal(Document document, Element invoiceLineElement,
                                          BigDecimal monto, String codigoTributo, String codigoTipoIgvIsc, String codigoMoneda) {

        if (monto != null && monto.compareTo(BigDecimal.ZERO) > 0) {
            Element taxTotalElement = appendChild(document, invoiceLineElement, "cac:TaxTotal");
            Element taxAmountElement = appendChild(document, taxTotalElement, "cbc:TaxAmount", UtilFormat.format(monto));
            taxAmountElement.setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(monto))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            Tipo tipoTributo = null;

            switch (codigoTributo) {

                case ConstantesSunat.CODIGO_TRIBUTO_IGV:
                    appendChild(document, taxCategoryElement, "cbc:TaxExemptionReasonCode", codigoTipoIgvIsc);
                    tipoTributo = ConstantesSunat.TRIBUTO_IGV;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_ISC:
                    appendChild(document, taxCategoryElement, "cbc:TierRange", codigoTipoIgvIsc);
                    tipoTributo = ConstantesSunat.TRIBUTO_ISC;
                    break;

            }

            Element taxSchemeElement = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            appendChild(document, taxSchemeElement, "cbc:ID", tipoTributo.getId());
            appendChild(document, taxSchemeElement, "cbc:Name", tipoTributo.getName());
            appendChild(document, taxSchemeElement, "cbc:TaxTypeCode", tipoTributo.getTypeCode());
        }
    }

    private void appendChildItem(Document document, Element invoiceLineElement, String descripcion,
                                 String codigoProducto, String codigoProductoSunat) {

        Element itemElement = appendChild(document, invoiceLineElement, "cac:Item");

        appendChild(document, itemElement, "cbc:Description", descripcion);
        if (StringUtils.isNotBlank(codigoProducto)) {
            Element sellersItemIdentificationElement = appendChild(document, itemElement,
                    "cac:SellersItemIdentification");
            appendChild(document, sellersItemIdentificationElement, "cbc:ID", codigoProducto);
        }
        if (StringUtils.isNotBlank(codigoProductoSunat)) {
            Element standarItemIdentificationElement = appendChild(document, itemElement,
                    "cac:StandardItemIdentification");
            appendChild(document, standarItemIdentificationElement, "cbc:ID", codigoProductoSunat);
        }
    }

    private Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // Bloquear el acceso a DTDs y entidades externas
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        } catch (IllegalArgumentException | TransformerConfigurationException e) {
            log.error("Error al activar la seguridad en el transformador", e);
            throw new TransformerConfigurationException("No se pudo configurar la seguridad XML.", e);
        }
        return transformerFactory.newTransformer();
    }

}
