package com.certicom.certifact_facturas_service_ng.templates.template21;

import com.certicom.certifact_facturas_service_ng.dto.model.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.*;
import com.certicom.certifact_facturas_service_ng.enums.AfectacionIgvEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.util.UtilGenerateLetraNumber;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.certicom.certifact_facturas_service_ng.util.UtilXml.*;

@Component
@Slf4j
public class FacturaTemplate21 {

    private static final String CUOTA0 = "Cuota000";
    HashMap<String,String> mapMoneda = new HashMap<String, String>();
    HashMap<Integer,String> mapTransaccion = new HashMap<Integer, String>();
    String ICBPER_ID = "7152";
    String ICBPER_NAME = "ICBPER";
    String ICBPER_TYPE = "OTH";
    DecimalFormat decimalFormat = new DecimalFormat("#");

    public FacturaTemplate21() {
        this.mapMoneda.put("PEN","Soles");
        this.mapMoneda.put("USD","Dólares Americanos");
        this.mapMoneda.put("EUR","Euros");
        this.mapMoneda.put("CLP","Peso chileno");
        this.mapMoneda.put("BRL","Real brasileño");
        this.mapMoneda.put("MXN","Peso mexicano");
        this.mapMoneda.put("COP","Peso colombiano");
        this.mapMoneda.put("BOB","Boliviano");
        this.mapMoneda.put("ARS","Peso argentino");
        this.mapMoneda.put("CAD","Dólar canadiense");
        this.mapMoneda.put("GBP","Libra esterlina (de Gran Bretaña)");
        this.mapMoneda.put("JPY","Yen japonés");
        this.mapMoneda.put("CHF","Franco suizo");
        this.mapMoneda.put("AUD","Dólar australiano");
        this.mapMoneda.put("CLF","Unidad de Fomento Chilena");
        this.mapTransaccion.put(1,"Contado");
        this.mapTransaccion.put(2,"Credito");
    }

    public String construirFactura(PaymentVoucherDto factura) throws TemplateException {
        String xml = "";
        DOMSource source;
        StringWriter writer;
        int correlativoItem = 1;
        BigDecimal montoAnticiposTotalValorVenta = BigDecimal.ZERO;
        BigDecimal montoAnticiposTotalSinIgv = BigDecimal.ZERO;
        BigDecimal montoAnticiposTotalValorVentaIgv = BigDecimal.ZERO;
        BigDecimal montoZero = BigDecimal.ZERO;
        BigDecimal montoTotalImpuestos;
        Map<String, String> attributes = new HashMap<>();
        String codigoEstablecimientoEmisor;
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

            Attr xsi = doc.createAttribute("xsi:schemaLocation");
            xsi.setValue("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2 2.1maindocUBL-Invoice-2.1.xsd");
            invoiceRootElement.setAttributeNode(xsi);
            appendChild(doc, invoiceRootElement, "ext:UBLExtensions");
            appendChild(doc, invoiceRootElement, "cbc:UBLVersionID", ConstantesSunat.UBL_VERSION_2_1);
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
            appendChild(doc, invoiceRootElement, "cbc:CustomizationID", ConstantesSunat.CUSTOMIZATION_VERSION_2_0,
                    attributes);
/*
			if (StringUtils.isNotBlank(invoice.getCodigoTipoOperacion())) {

				attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "SUNAT:Identificador de Tipo de Operación");
				attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
				attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI,
						"urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo17");
				appendChild(doc, invoiceRootElement, "cbc:ProfileID", invoice.getCodigoTipoOperacion(), attributes);
			}
*/

            appendChild(doc, invoiceRootElement, "cbc:ID", factura.getSerie() + "-" + factura.getNumero());
            appendChild(doc, invoiceRootElement, "cbc:IssueDate", factura.getFechaEmision());
            if (StringUtils.isNotBlank(factura.getHoraEmision())) {
                appendChild(doc, invoiceRootElement, "cbc:IssueTime", factura.getHoraEmision());
            }
            if (StringUtils.isNotBlank(factura.getFechaVencimiento())) {
                appendChild(doc, invoiceRootElement, "cbc:DueDate", factura.getFechaVencimiento());
            }

            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Tipo de Documento");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_ID, factura.getCodigoTipoOperacionCatalogo51());
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_SCHEMA_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo51");
            attributes.put(ConstantesSunat.ATTRIBUTE_NAME, "Tipo de Operacion");
            appendChild(doc, invoiceRootElement, "cbc:InvoiceTypeCode", factura.getTipoComprobante(), attributes);
            attributes.clear();
            String stringMoneda = mapMoneda.get(factura.getCodigoMoneda().toUpperCase())!=null?
                    mapMoneda.get(factura.getCodigoMoneda().toUpperCase()):"Soles";
            //invoice.getCodigoMoneda()!=null?invoice.getCodigoMoneda()
            //.equalsIgnoreCase("USD")?"Dólares Americanos":"Soles":"Soles";
            if (factura.getLeyendas() != null && !factura.getLeyendas().isEmpty()) {
                for (Leyenda note : factura.getLeyendas()) {
                    if (StringUtils.isNotBlank(note.getCodigo())) {
                        appendChild(doc, invoiceRootElement, "cbc:Note", note.getDescripcion())
                                .setAttribute(ConstantesSunat.ATTRIBUTE_LANGUAGE_LOCALE_ID, note.getCodigo());
                    } else {
                        appendChild(doc, invoiceRootElement, "cbc:Note", note.getDescripcion());
                    }
                }
            }else{

                attributes.put(ConstantesSunat.ATTRIBUTE_LANGUAGE_LOCALE_ID, "1000");
                appendChild(doc, invoiceRootElement, "cbc:Note", UtilGenerateLetraNumber.Convertir(
                        factura.getImporteTotalVenta()
                                .setScale(2,BigDecimal.ROUND_HALF_UP).toString(),stringMoneda,true) , attributes);
            }

            if(factura.getCamposAdicionales()!=null && !factura.getCamposAdicionales().isEmpty()){
                String valor = "";
                for (CampoAdicional adicional : factura.getCamposAdicionales()){
                    valor = (adicional.getValorCampo()).length()>70?
                            (adicional.getValorCampo()).substring(0,69):adicional.getValorCampo();
                    appendChild(doc, invoiceRootElement, "cbc:Note","<![CDATA["+
                            adicional.getNombreCampo()+":"+valor+
                            "]]>");
                }

            }
            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "United Nations Economic Commission for Europe");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_ID, "ISO 4217 Alpha");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Currency");
            appendChild(doc, invoiceRootElement, "cbc:DocumentCurrencyCode", factura.getCodigoMoneda(), attributes);

            if (StringUtils.isNotBlank(factura.getOrdenCompra())) {
                Element cacOrderReference = appendChild(doc, invoiceRootElement, "cac:OrderReference");
                appendChild(doc, cacOrderReference, "cbc:ID", factura.getOrdenCompra());
            }
            if (factura.getGuiasRelacionadas() != null && !factura.getGuiasRelacionadas().isEmpty()) {
                for (GuiaRelacionada guiaRelacionada : factura.getGuiasRelacionadas()) {
                    appendChilDespatchDocumentReference(doc, invoiceRootElement, guiaRelacionada.getSerieNumeroGuia(),
                            guiaRelacionada.getCodigoTipoGuia(), attributes);
                }
            }

            if (StringUtils.isNotBlank(factura.getCodigoTipoOperacion())
                    && (factura.getCodigoTipoOperacion().equals(ConstantesSunat.CODIGO_TIPO_OPERACION_VENTA_INTERNA_ANTICIPOS) ||
                    factura.getCodigoTipoOperacion().equals("1001"))
                    && factura.getAnticipos() != null && !factura.getAnticipos().isEmpty()) {

                for (Anticipo anticipo : factura.getAnticipos()) {

                    Element additionalDocument = appendChild(doc, invoiceRootElement, "cac:AdditionalDocumentReference");
                    appendChild(doc, additionalDocument, "cbc:ID", String.format("%s-%s", anticipo.getSerieAnticipo(), anticipo.getNumeroAnticipo()));
                    appendChild(doc, additionalDocument, "cbc:DocumentTypeCode", anticipo.getTipoDocumentoAnticipo().equals("01")?"02":anticipo.getTipoDocumentoAnticipo());
                    appendChild(doc, additionalDocument, "cbc:DocumentStatusCode", anticipo.getIdentificadorPago());
                    Element issuerParty = appendChild(doc, additionalDocument, "cac:IssuerParty");
                    Element partyIdentification = appendChild(doc, issuerParty, "cac:PartyIdentification");
                    attributes.clear();
                    attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
                    attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Documento de Identidad");
                    attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
                    attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
                    appendChild(doc, partyIdentification, "cbc:ID", factura.getRucEmisor(), attributes);

                }
            }

            if (factura.getDocumentosRelacionados() != null && !factura.getDocumentosRelacionados().isEmpty()) {
                for (DocumentoRelacionado documentoRelacionado : factura.getDocumentosRelacionados()) {
                    appendChilAdditionalDocumentReference(doc, invoiceRootElement, documentoRelacionado.getNumero(),
                            documentoRelacionado.getTipoDocumento(), attributes);
                }
            }

            Element cacSignature = appendChild(doc, invoiceRootElement, "cac:Signature");
            appendChild(doc, cacSignature, "cbc:ID", "IDSignKG");

            Element cacSignatoryParty = appendChild(doc, cacSignature, "cac:SignatoryParty");

            attributes.clear();
            attributes.put("schemeAgencyName", "PE:SUNAT");
            attributes.put("schemeID", factura.getTipoDocumentoEmisor());
            attributes.put("schemeName", "Documento de Identidad");
            attributes.put("schemeURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");

            Element cacPartyIdentification = appendChild(doc, cacSignatoryParty, "cac:PartyIdentification");
            appendChild(doc, cacPartyIdentification, "cbc:ID", factura.getRucEmisor(), attributes);

            Element cacPartyName = appendChild(doc, cacSignatoryParty, "cac:PartyName");
            appendChild(doc, cacPartyName, "cbc:Name", factura.getDenominacionEmisor());

            Element cacDigitalSignatureAttachment = appendChild(doc, cacSignature, "cac:DigitalSignatureAttachment");
            Element cacExternalReference = appendChild(doc, cacDigitalSignatureAttachment, "cac:ExternalReference");
            appendChild(doc, cacExternalReference, "cbc:URI", "#SignatureKG");

            Element cacAccountingSupplierParty = appendChild(doc, invoiceRootElement, "cac:AccountingSupplierParty");
            Element cacPartySupplier = appendChild(doc, cacAccountingSupplierParty, "cac:Party");

            Element partyIdentification = appendChild(doc, cacPartySupplier, "cac:PartyIdentification");
            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, factura.getTipoDocumentoEmisor());
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Documento de Identidad");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");
            appendChild(doc, partyIdentification, "cbc:ID", factura.getRucEmisor(), attributes);

            if (StringUtils.isNotBlank(factura.getNombreComercialEmisor())) {
                Element cacPartyNameElement = appendChild(doc, cacPartySupplier, "cac:PartyName");
                appendChild(doc, cacPartyNameElement, "cbc:Name", factura.getNombreComercialEmisor());
            }
            Element cacPartyLegalEntity = appendChild(doc, cacPartySupplier, "cac:PartyLegalEntity");
            appendChild(doc, cacPartyLegalEntity, "cbc:RegistrationName", factura.getDenominacionEmisor());

            Element cacRegistrationAddress = appendChild(doc, cacPartyLegalEntity, "cac:RegistrationAddress");
            codigoEstablecimientoEmisor = (factura.getCodigoLocalAnexoEmisor() != null) ? factura.getCodigoLocalAnexoEmisor() : "0000";
            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Establecimientos anexos");
            appendChild(doc, cacRegistrationAddress, "cbc:AddressTypeCode", codigoEstablecimientoEmisor, attributes);
            if (StringUtils.isNotBlank(factura.getDireccionOficinaEmisor())) {
                Element cacAddressLine = appendChild(doc, cacRegistrationAddress, "cac:AddressLine");
                appendChild(doc, cacAddressLine, "cbc:Line", factura.getDireccionOficinaEmisor());
            }

            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, factura.getTipoDocumentoReceptor());
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Documento de Identidad");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo06");

            Element cacAccountingCustomerParty = appendChild(doc, invoiceRootElement, "cac:AccountingCustomerParty");
            Element cacPartyCustomer = appendChild(doc, cacAccountingCustomerParty, "cac:Party");
            Element cacPartyIdentificationCustomer = appendChild(doc, cacPartyCustomer, "cac:PartyIdentification");
            appendChild(doc, cacPartyIdentificationCustomer, "cbc:ID", factura.getNumeroDocumentoReceptor(), attributes);
            cacPartyLegalEntity = appendChild(doc, cacPartyCustomer, "cac:PartyLegalEntity");
            appendChild(doc, cacPartyLegalEntity, "cbc:RegistrationName", factura.getDenominacionReceptor());

            if (StringUtils.isNotBlank(factura.getDireccionReceptor())) {
                cacRegistrationAddress = appendChild(doc, cacPartyLegalEntity, "cac:RegistrationAddress");
                Element cacAddressLine = appendChild(doc, cacRegistrationAddress, "cac:AddressLine");
                appendChild(doc, cacAddressLine, "cbc:Line", factura.getDireccionReceptor());
            }

            if (StringUtils.isNotBlank(factura.getCodigoBienDetraccion())) {

                Element paymentMeansElement = appendChild(doc, invoiceRootElement, "cac:PaymentMeans");
                appendChild(doc, paymentMeansElement, "cbc:ID", "Detraccion");
                appendChild(doc, paymentMeansElement, "cbc:PaymentMeansCode", factura.getCodigoMedioPago());
                Element payeeFinancialAccountElement = appendChild(doc, paymentMeansElement, "cac:PayeeFinancialAccount");
                appendChild(doc, payeeFinancialAccountElement, "cbc:ID", factura.getCuentaFinancieraBeneficiario());

                Element paymentTermsElement = appendChild(doc, invoiceRootElement, "cac:PaymentTerms");
                appendChild(doc, paymentTermsElement, "cbc:ID", "Detraccion");
                appendChild(doc, paymentTermsElement, "cbc:PaymentMeansID", factura.getCodigoBienDetraccion());
                appendChild(doc, paymentTermsElement, "cbc:PaymentPercent", factura.getPorcentajeDetraccion());
                appendChild(doc, paymentTermsElement, "cbc:Amount", UtilFormat.format(factura.getMontoDetraccion().setScale(0,BigDecimal.ROUND_HALF_UP)))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, "PEN");
            }

            if (factura.getTipoTransaccion()!=null&& factura.getTipoTransaccion().intValue()> 0) {
                Element paymentTermsElementTransaction = appendChild(doc, invoiceRootElement, "cac:PaymentTerms");
                appendChild(doc, paymentTermsElementTransaction, "cbc:ID", "FormaPago");
                if (factura.getTipoTransaccion().intValue()==1){
                    appendChild(doc, paymentTermsElementTransaction, "cbc:PaymentMeansID", mapTransaccion.get(factura.getTipoTransaccion().intValue()));
                }

                if (factura.getTipoTransaccion().intValue()==2){
                    appendChild(doc, paymentTermsElementTransaction, "cbc:PaymentMeansID", mapTransaccion.get(factura.getTipoTransaccion().intValue()));
                    appendChild(doc, paymentTermsElementTransaction, "cbc:Amount", UtilFormat.format(factura.getMontoPendiente()))
                            .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());

                    for (int i = 0; i < factura.getCuotas().size();i++){
                        Element paymentTermsElementTransactionCuota = appendChild(doc, invoiceRootElement, "cac:PaymentTerms");
                        appendChild(doc, paymentTermsElementTransactionCuota, "cbc:ID", "FormaPago");
                        appendChild(doc, paymentTermsElementTransactionCuota, "cbc:PaymentMeansID", getStringCuota(factura.getCuotas().get(i).getNumero()));
                        appendChild(doc, paymentTermsElementTransactionCuota, "cbc:Amount", UtilFormat.format(factura.getCuotas().get(i).getMonto()))
                                .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());;
                        appendChild(doc, paymentTermsElementTransactionCuota, "cbc:PaymentDueDate", factura.getCuotas().get(i).getFecha());
                    }

                }
            }

            // [Anticipos con deduccion]
            if (StringUtils.isNotBlank(factura.getCodigoTipoOperacion())
                    && (factura.getCodigoTipoOperacion().equals(ConstantesSunat.CODIGO_TIPO_OPERACION_VENTA_INTERNA_ANTICIPOS) ||
                    factura.getCodigoTipoOperacion().equals("1001"))
                    && factura.getAnticipos() != null && !factura.getAnticipos().isEmpty()) {

                for (Anticipo anticipo : factura.getAnticipos()) {

                    Element prepaidPayment = appendChild(doc, invoiceRootElement, "cac:PrepaidPayment");
                    attributes.clear();
                    attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Anticipo");
                    attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");

                    appendChild(doc, prepaidPayment, "cbc:ID", anticipo.getIdentificadorPago(), attributes);
                    appendChild(doc, prepaidPayment, "cbc:PaidAmount", UtilFormat.format(anticipo.getMontoAnticipado()))
                            .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());

                    montoAnticiposTotalValorVenta = montoAnticiposTotalValorVenta.add(anticipo.getMontoAnticipado());
                    factura.setTotalAnticipos(montoAnticiposTotalValorVenta);
                }

                montoAnticiposTotalValorVentaIgv = montoAnticiposTotalValorVenta;
                BigDecimal totalValorVentaInafectaYExonerada = BigDecimal.ZERO;

                boolean allowanceChargeProcessed = false;

                for (ComprobanteItem item : factura.getItems()) {
                    if ("30".equals(item.getCodigoTipoAfectacionIGV())) {
                        System.out.println("Generando AllowanceCharge con código: 06");


                        if(factura.getImporteTotalVenta() != null) {
                            totalValorVentaInafectaYExonerada = totalValorVentaInafectaYExonerada.add(factura.getImporteTotalVenta());
                        }

                        if(factura.getTotalAnticipos() != null) {
                            totalValorVentaInafectaYExonerada = totalValorVentaInafectaYExonerada.add(factura.getTotalAnticipos()
                            );
                        }

                        appendChildAllowanceAnti(
                                doc, invoiceRootElement, montoAnticiposTotalValorVentaIgv,
                                factura.getCodigoMoneda(), "06",
                                totalValorVentaInafectaYExonerada == null
                                        ? factura.getTotalValorVentaExonerada()
                                        : factura.getTotalValorVentaInafecta()
                        );
                        allowanceChargeProcessed = true;
                        break;
                    }
                }

                if (!allowanceChargeProcessed) {
                    if ((factura.getTotalValorVentaGravada() == null) &&
                            (factura.getTotalValorVentaInafecta() != null || factura.getTotalValorVentaExonerada() != null)) {
                        System.out.println("Generando AllowanceCharge con código: 05");
                        appendChildAllowanceAnti(
                                doc, invoiceRootElement, montoAnticiposTotalValorVentaIgv,
                                factura.getCodigoMoneda(), "05",
                                factura.getTotalValorVentaInafecta() == null
                                        ? factura.getTotalValorVentaExonerada()
                                        : factura.getTotalValorVentaInafecta()
                        );
                    } else {
                        montoAnticiposTotalValorVentaIgv = montoAnticiposTotalValorVentaIgv.divide(
                                new BigDecimal(1.18), 2, RoundingMode.HALF_UP
                        );
                        System.out.println("Generando AllowanceCharge con código: 04");
                        appendChildAllowanceAnti(
                                doc, invoiceRootElement, montoAnticiposTotalValorVenta,
                                factura.getCodigoMoneda(), "04", montoAnticiposTotalValorVentaIgv
                        );
                    }
                }


            }
            if (factura.getMontoRetencion()==null?false:factura.getMontoRetencion().compareTo(BigDecimal.ZERO) > 0){
                appendChildAllowanceDetra(doc,invoiceRootElement, factura.getMontoRetencion(),factura.getCodigoMoneda(),
                        "62",factura.getImporteTotalVenta(),(factura.getPorcentajeRetencion()
                                .divide(new BigDecimal(100),4, RoundingMode.HALF_UP)));
            }

            // [Anticipos]
            if (factura.getDescuentoGlobales() != null) {
                appendChildAllowance(doc, invoiceRootElement, factura.getDescuentoGlobales(), factura.getCodigoMoneda(),
                        "02");
            }
            if (factura.getSumatoriaOtrosCargos() != null) {
                appendChildAllowanceCargos(doc, invoiceRootElement, factura.getSumatoriaOtrosCargos(), factura.getCodigoMoneda(),
                        "50");
            }
            montoTotalImpuestos = importeTotalImpuestos(factura.getTotalIgv(),
                    factura.getTotalIvap(),
                    factura.getTotalIsc(),
                    factura.getTotalOtrostributos(),
                    null);
            Element cactaxTotal = appendChild(doc, invoiceRootElement, "cac:TaxTotal");

            appendChild(doc, cactaxTotal, "cbc:TaxAmount", UtilFormat.format(montoTotalImpuestos))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());


            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Codigo de tributos");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");

            appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorVentaExportacion(),
                    montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_EXPORTACION, factura.getCodigoMoneda());

            appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorVentaInafecta(),
                    montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_INAFECTO, factura.getCodigoMoneda());

            appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorVentaExonerada(),
                    montoZero, attributes, ConstantesSunat.CODIGO_TRIBUTO_EXONERADO, factura.getCodigoMoneda());

            appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorVentaGratuita(),
                    factura.getTotalImpOperGratuita(), attributes, ConstantesSunat.CODIGO_TRIBUTO_GRATUITO, factura.getCodigoMoneda());
            if (factura.getCodigoTipoOperacion().equals("04")){
                appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorVentaGravada()==null?BigDecimal.ZERO:factura.getTotalValorVentaGravada(),
                        factura.getTotalIgv()==null?BigDecimal.ZERO:factura.getTotalIgv(), attributes, ConstantesSunat.CODIGO_TRIBUTO_IGV, factura.getCodigoMoneda());
            }else{
                appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorVentaGravada(),
                        factura.getTotalIgv(), attributes, ConstantesSunat.CODIGO_TRIBUTO_IGV, factura.getCodigoMoneda());
            }


            appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorVentaGravadaIVAP(),
                    factura.getTotalIvap(), attributes, ConstantesSunat.CODIGO_TRIBUTO_IVAP, factura.getCodigoMoneda());

            appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorBaseIsc(),
                    factura.getTotalIsc(), attributes, ConstantesSunat.CODIGO_TRIBUTO_ISC, factura.getCodigoMoneda());

            appendChildSubTotalHeader(doc, cactaxTotal, factura.getTotalValorBaseOtrosTributos(),
                    factura.getTotalOtrostributos(), attributes, ConstantesSunat.CODIGO_TRIBUTO_OTROS, factura.getCodigoMoneda());

            Element legalMonetaryTotalElement = appendChild(doc, invoiceRootElement, "cac:LegalMonetaryTotal");

            appendChild(doc, legalMonetaryTotalElement, "cbc:LineExtensionAmount",
                    UtilFormat.format((factura.getTotalValorVentaGravada()==null?BigDecimal.ZERO:factura.getTotalValorVentaGravada())
                                    .add(factura.getTotalValorVentaExonerada()==null?BigDecimal.ZERO:factura.getTotalValorVentaExonerada())
                                    .add(factura.getTotalValorVentaInafecta()==null?BigDecimal.ZERO:factura.getTotalValorVentaInafecta())
                                    .add(factura.getTotalValorVentaExportacion()==null?BigDecimal.ZERO:factura.getTotalValorVentaExportacion())
                                    .add(factura.getTotalAnticipos()==null?BigDecimal.ZERO:factura.getTotalAnticipos()
                                            .divide(BigDecimal.valueOf(1.18),2, RoundingMode.HALF_UP))
                            //.add(invoice.getTotalValorVentaGratuita()==null?BigDecimal.ZERO:invoice.getTotalValorVentaGratuita())
                    ))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());

            if (montoAnticiposTotalValorVenta.compareTo(BigDecimal.ZERO) > 0) {
                appendChild(doc, legalMonetaryTotalElement, "cbc:TaxInclusiveAmount",
                        UtilFormat.format(factura.getImporteTotalVenta().add(montoAnticiposTotalValorVenta)))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
            }else{
                appendChild(doc, legalMonetaryTotalElement, "cbc:TaxInclusiveAmount",
                        UtilFormat.format(factura.getImporteTotalVenta()))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
            }
            BigDecimal otros = BigDecimal.ZERO;
            if (factura.getSumatoriaOtrosCargos() != null) {

                appendChild(doc, legalMonetaryTotalElement, "cbc:ChargeTotalAmount",
                        UtilFormat.format(factura.getSumatoriaOtrosCargos()))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
                otros = factura.getSumatoriaOtrosCargos();
            }
            // si existen anticipos deducidos
            if (montoAnticiposTotalValorVenta.compareTo(BigDecimal.ZERO) > 0) {
                appendChild(doc, legalMonetaryTotalElement, "cbc:PrepaidAmount",
                        UtilFormat.format(montoAnticiposTotalValorVenta))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
                appendChild(doc, legalMonetaryTotalElement, "cbc:PayableAmount",
                        UtilFormat.format(factura.getImporteTotalVenta()))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
            }else{
                appendChild(doc, legalMonetaryTotalElement, "cbc:PayableAmount",
                        UtilFormat.format(factura.getImporteTotalVenta().add(otros)))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
            }

            for (ComprobanteItem item : factura.getItems()) {

                BigDecimal montoTotalImpuestosLinea = importeTotalImpuestos(item.getIgv(),
                        item.getIvap(), item.getIsc(), item.getOtrosTributos(), item.getImpuestoVentaGratuita());

                Element cacInvoiceLine = appendChild(doc, invoiceRootElement, "cac:InvoiceLine");
                appendChild(doc, cacInvoiceLine, "cbc:ID", correlativoItem);
                attributes.clear();
                attributes.put("unitCodeListID", "UN/ECE rec 20");
                attributes.put("unitCodeListAgencyName", "United Nations Economic Commission for Europe");

                appendChild(doc, cacInvoiceLine, "cbc:InvoicedQuantity", UtilFormat.format3(item.getCantidad()), attributes)
                        .setAttribute("unitCode", item.getCodigoUnidadMedida());

                if (item.getCodigoTipoAfectacionIGV().equals("21")){
                    appendChild(doc, cacInvoiceLine, "cbc:LineExtensionAmount", UtilFormat.format4(item.getValorReferencialUnitario().multiply(item.getCantidad())))
                            .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
                    Element pricingReferenceElement = appendChild(doc, cacInvoiceLine, "cac:PricingReference");
                    appendChildPricingReference(doc, pricingReferenceElement, item.getValorReferencialUnitario(),
                            ConstantesSunat.CODIGO_TIPO_PRECIO_VALOR_REFERENCIAL, factura.getCodigoMoneda());
                }
                else{
                    String constAttribute = ConstantesSunat.CODIGO_TIPO_PRECIO_PRECIO_UNITARIO;
                    if (item.getCodigoTipoAfectacionIGV().equals("15")){
                        constAttribute = ConstantesSunat.CODIGO_TIPO_PRECIO_VALOR_REFERENCIAL;
                    }

                    BigDecimal vv = (item.getValorVenta()).setScale(6,RoundingMode.HALF_UP);
                    appendChild(doc, cacInvoiceLine, "cbc:LineExtensionAmount", UtilFormat.format(vv))
                            .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
                    Element pricingReferenceElement = appendChild(doc, cacInvoiceLine, "cac:PricingReference");
                    appendChildPricingReference(doc, pricingReferenceElement, item.getPrecioVentaUnitario(),
                            constAttribute, factura.getCodigoMoneda());
                }


                appendChildAllowance(doc, cacInvoiceLine, item.getDescuento(), factura.getCodigoMoneda(),
                        item.getCodigoDescuento());


                //DETRACCION CODIGO BIEN DETRACION 027
                if ((StringUtils.isNotBlank(factura.getCodigoBienDetraccion()) && factura.getCodigoBienDetraccion().equals("027")) &&
                        ((StringUtils.isNotBlank(factura.getCodigoTipoOperacion()) && factura.getCodigoTipoOperacion().equals("1004")))) {
                    appendChildDeliveryDetraccion(doc, cacInvoiceLine, item, attributes);
                }
                Element taxTotalElement = appendChild(doc, cacInvoiceLine, "cac:TaxTotal");
                appendChild(doc, taxTotalElement, "cbc:TaxAmount", UtilFormat.format(montoTotalImpuestosLinea))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
                addElementsChildSubTaxTotal(doc, taxTotalElement, item.getMontoBaseIgv(),
                        item.getIgv(), item.getCodigoTipoAfectacionIGV(), null, factura.getCodigoMoneda(),
                        item.getPorcentajeIgv(), false, attributes);
                addElementsChildSubTaxTotal(doc, taxTotalElement, item.getMontoBaseIvap(),
                        item.getIvap(), item.getCodigoTipoAfectacionIGV(), null, factura.getCodigoMoneda(),
                        item.getPorcentajeIvap(), false, attributes);
                addElementsChildSubTaxTotal(doc, taxTotalElement, item.getMontoBaseExportacion(),
                        montoZero, item.getCodigoTipoAfectacionIGV(), null, factura.getCodigoMoneda(),
                        montoZero, false, attributes);
                addElementsChildSubTaxTotal(doc, taxTotalElement, item.getMontoBaseExonerado(),
                        montoZero, item.getCodigoTipoAfectacionIGV(), null, factura.getCodigoMoneda(),
                        montoZero, false, attributes);
                addElementsChildSubTaxTotal(doc, taxTotalElement, item.getMontoBaseInafecto(),
                        montoZero, item.getCodigoTipoAfectacionIGV(), null, factura.getCodigoMoneda(),
                        montoZero, false, attributes);
                addElementsChildSubTaxTotal(doc, taxTotalElement, item.getMontoBaseGratuito(),
                        item.getImpuestoVentaGratuita(), item.getCodigoTipoAfectacionIGV(), null,
                        factura.getCodigoMoneda(), item.getPorcentajeTributoVentaGratuita(), true, attributes);
                addElementsChildSubTaxTotal(doc, taxTotalElement, item.getMontoBaseIsc(),
                        item.getIsc(), null, item.getCodigoTipoCalculoISC(), factura.getCodigoMoneda(),
                        item.getPorcentajeIsc(), false, attributes);
                addElementsChildSubTaxBolsaTotal(doc, taxTotalElement, item.getMontoBaseIcbper(),
                        item.getMontoIcbper(),  UtilFormat.format(item.getCantidad()), factura.getCodigoMoneda(),attributes);
                addElementsChildSubTaxTotal(doc, taxTotalElement, item.getMontoBaseOtrosTributos(),
                        item.getOtrosTributos(), null, null, factura.getCodigoMoneda(),
                        item.getPorcentajeOtrosTributos(), false, attributes);

                int isHidro = 0;
                if ((StringUtils.isNotBlank(factura.getCodigoBienDetraccion()) && factura.getCodigoBienDetraccion().equals("004")) &&
                        ((StringUtils.isNotBlank(factura.getCodigoTipoOperacion()) && factura.getCodigoTipoOperacion().equals("1001")))) {
                    isHidro =1;
                }
                appendChildItem(doc, cacInvoiceLine, item.getDescripcion(), item.getCodigoProducto(),
                        item.getCodigoProductoSunat(),item,isHidro);

                Element cbcPrice = appendChild(doc, cacInvoiceLine, "cac:Price");
                appendChild(doc, cbcPrice, "cbc:PriceAmount", UtilFormat.format7(item.getValorUnitario()))
                        .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, factura.getCodigoMoneda());
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

    private void appendChilDespatchDocumentReference(Document document, Element elementRoot, String numeroGuiaRemision,
                                                     String tipoGuiaRemision, Map<String, String> attributes) {

        if (StringUtils.isNotBlank(tipoGuiaRemision) && StringUtils.isNotBlank(numeroGuiaRemision)) {

            Element despatchDocumentReferenceElement = appendChild(document, elementRoot,
                    "cac:DespatchDocumentReference");
            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Tipo de Documento");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo01");
            appendChildDocumentReference(document, despatchDocumentReferenceElement, numeroGuiaRemision,
                    tipoGuiaRemision, attributes);
        }
    }

    private void appendChildDocumentReference(Document document, Element despatchDocumentReferenceElement,
                                              String numeroDocumentoReferencia, String tipoDocumentoReferencia, Map<String, String> attributes) {

        appendChild(document, despatchDocumentReferenceElement, "cbc:ID", numeroDocumentoReferencia);
        appendChild(document, despatchDocumentReferenceElement, "cbc:DocumentTypeCode", tipoDocumentoReferencia,
                attributes);
    }

    private void appendChilAdditionalDocumentReference(Document document, Element elementRoot,
                                                       String numeroDocumentoAdicional, String tipoDocumentoAdicional, Map<String, String> attributes) {

        if (StringUtils.isNotBlank(tipoDocumentoAdicional) && StringUtils.isNotBlank(numeroDocumentoAdicional)) {

            Element additionalDocumentReferenceElement = appendChild(document, elementRoot,
                    "cac:AdditionalDocumentReference");
            attributes.clear();
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Documento Relacionado");
            attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo12");
            appendChildDocumentReference(document, additionalDocumentReferenceElement, numeroDocumentoAdicional,
                    tipoDocumentoAdicional, attributes);
        }
    }

    private String getStringCuota(Integer numero) {
        String cadena = CUOTA0;
        String res = cadena.substring(0,cadena.length()-((numero.toString()).length())) + numero;
        return res;
    }

    private void appendChildAllowanceAnti(Document doc, Element cacInvoiceLine, BigDecimal descuento,
                                          String moneda, String codigoDescuento, BigDecimal gravado) {

        if (descuento != null) {
            Element allowanceElement = appendChild(doc, cacInvoiceLine, "cac:AllowanceCharge");
            appendChild(doc, allowanceElement, "cbc:ChargeIndicator", "false");
            appendChild(doc, allowanceElement, "cbc:AllowanceChargeReasonCode", codigoDescuento);
            appendChild(doc, allowanceElement, "cbc:Amount", UtilFormat.format(gravado))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, moneda);
            /*appendChild(doc, allowanceElement, "cbc:BaseAmount", UtilFormat.format(gravado))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, moneda);*/
        }
    }

    private void appendChildAllowanceDetra(Document doc, Element cacInvoiceLine, BigDecimal descuento,
                                           String moneda, String codigoDescuento, BigDecimal gravado, BigDecimal porcentaje) {

        if (descuento != null) {
            Element allowanceElement = appendChild(doc, cacInvoiceLine, "cac:AllowanceCharge");
            appendChild(doc, allowanceElement, "cbc:ChargeIndicator", "false");
            appendChild(doc, allowanceElement, "cbc:AllowanceChargeReasonCode", codigoDescuento);
            appendChild(doc, allowanceElement, "cbc:MultiplierFactorNumeric", UtilFormat.format(porcentaje));
            appendChild(doc, allowanceElement, "cbc:Amount", UtilFormat.format(descuento))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, moneda);
            appendChild(doc, allowanceElement, "cbc:BaseAmount", UtilFormat.format(gravado))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, moneda);
        }
    }

    private void appendChildAllowance(Document doc, Element cacInvoiceLine, BigDecimal descuento,
                                      String moneda, String codigoDescuento) {

        if (descuento != null) {
            System.out.println("LLENAR DESCUENTO ITEM");
            Element allowanceElement = appendChild(doc, cacInvoiceLine, "cac:AllowanceCharge");
            appendChild(doc, allowanceElement, "cbc:ChargeIndicator", "false");
            appendChild(doc, allowanceElement, "cbc:AllowanceChargeReasonCode", codigoDescuento);
            appendChild(doc, allowanceElement, "cbc:Amount", UtilFormat.format(descuento))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, moneda);
        }
    }

    private void appendChildAllowanceCargos(Document doc, Element cacInvoiceLine, BigDecimal cargo,
                                            String moneda, String codigoDescuento) {
        if (cargo != null) {
            System.out.println("LLENAR CARGOS");
            Element allowanceElement = appendChild(doc, cacInvoiceLine, "cac:AllowanceCharge");
            appendChild(doc, allowanceElement, "cbc:ChargeIndicator", "true");
            appendChild(doc, allowanceElement, "cbc:AllowanceChargeReasonCode", codigoDescuento);
            appendChild(doc, allowanceElement, "cbc:Amount", UtilFormat.format(cargo))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, moneda);
        }
    }

    private BigDecimal importeTotalImpuestos(BigDecimal totalIGV, BigDecimal totalIVAP,
                                             BigDecimal totalISC, BigDecimal totalOtrosTributos, BigDecimal impuestoVentaGratuita) {

        BigDecimal montoTotalImpuestos = BigDecimal.ZERO;
        if (totalIGV != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(totalIGV);
        }
        if (totalIVAP != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(totalIVAP);
        }
        if (totalISC != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(totalISC);
        }
        if (totalOtrosTributos != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(totalOtrosTributos);
        }
        if (impuestoVentaGratuita != null) {
            montoTotalImpuestos = montoTotalImpuestos.add(impuestoVentaGratuita);
        }

        return montoTotalImpuestos;
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

    private void appendChildPricingReference(Document document, Element pricingReferenceElement, BigDecimal monto,
                                             String tipoPrecio, String codigoMoneda) {

        if (monto != null) {
            Element alternativeConditionElement = appendChild(document, pricingReferenceElement,
                    "cac:AlternativeConditionPrice");
            Element priceAmountElement = appendChild(document, alternativeConditionElement, "cbc:PriceAmount", UtilFormat.format(monto));
            priceAmountElement.setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Map<String, String> attributes = new HashMap<>();
            attributes.put("listName", "Tipo de Precio");
            attributes.put("listAgencyName", "PE:SUNAT");
            attributes.put("listURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo16");
            appendChild(document, alternativeConditionElement, "cbc:PriceTypeCode", tipoPrecio);
        }
    }

    private void appendChildDeliveryDetraccion(Document doc, Element cacInvoiceLine, ComprobanteItem item, Map<String, String> attributes) {
        //DETRACCIONES SERVICIOS DE TRANSPORTE DE CARGA
        Element deliveryElement = appendChild(doc, cacInvoiceLine, "cac:Delivery");

        //DESTINO
        Element deliveryLocationElement = appendChild(doc, deliveryElement, "cac:DeliveryLocation");
        Element deliveryLocationAddressElement = appendChild(doc, deliveryLocationElement, "cac:Address");
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Ubigeos");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:INEI");
        appendChild(doc, deliveryLocationAddressElement, "cbc:ID", item.getUbigeoDestinoDetraccion(), attributes);
        Element deliveryLocationDespatchElement = appendChild(doc, deliveryLocationAddressElement, "cac:AddressLine");
        appendChild(doc, deliveryLocationDespatchElement, "cbc:Line", item.getDireccionDestinoDetraccion());

        //ORIGEN
        Element despatchElement = appendChild(doc, deliveryElement, "cac:Despatch");
        appendChild(doc, despatchElement, "cbc:Instructions", item.getDetalleViajeDetraccion());
        Element despatchAddressElement = appendChild(doc, despatchElement, "cac:DespatchAddress");
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Ubigeos");
        attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:INEI");
        appendChild(doc, despatchAddressElement, "cbc:ID", item.getUbigeoOrigenDetraccion(), attributes);
        Element addressDespatchElement = appendChild(doc, despatchAddressElement, "cac:AddressLine");
        appendChild(doc, addressDespatchElement, "cbc:Line", item.getDireccionOrigenDetraccion());

        //DELIVERY TERMS
        Element deliveryTermsElementSt = appendChild(doc, deliveryElement, "cac:DeliveryTerms");
        Element deliveryTermsElementCf = appendChild(doc, deliveryElement, "cac:DeliveryTerms");
        Element deliveryTermsElementCu = appendChild(doc, deliveryElement, "cac:DeliveryTerms");

        appendChild(doc, deliveryTermsElementSt, "cbc:ID", ConstantesSunat.CODIGO_VALOR_REFERENCIAL_DETRACCION_SERVICIO_TRANSPORTE);
        appendChild(doc, deliveryTermsElementSt, "cbc:Amount", UtilFormat.format(item.getValorServicioTransporte()))
                .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, "PEN");

        appendChild(doc, deliveryTermsElementCf, "cbc:ID", ConstantesSunat.CODIGO_VALOR_REFERENCIAL_DETRACCION_CARGA_EFECTIVA);
        appendChild(doc, deliveryTermsElementCf, "cbc:Amount", UtilFormat.format(item.getValorCargaEfectiva()))
                .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, "PEN");

        appendChild(doc, deliveryTermsElementCu, "cbc:ID", ConstantesSunat.CODIGO_VALOR_REFERENCIAL_DETRACCION_CARGA_UTIL);
        appendChild(doc, deliveryTermsElementCu, "cbc:Amount", UtilFormat.format(item.getValorCargaUtil()))
                .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, "PEN");

    }

    private void addElementsChildSubTaxTotal(Document document, Element taxTotalElement, BigDecimal montoBase,
                                             BigDecimal montoImpuesto, String codigoTipoAfectacionIgv, String codigoIsc, String codigoMoneda,
                                             BigDecimal porcentaje, Boolean isGratuito, Map<String, String> attributes) {

        if (montoBase != null && montoImpuesto != null) {

            Tipo tipoTributo = null;
            montoBase = montoBase.setScale(6,RoundingMode.HALF_UP);
            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxableAmount", UtilFormat.format(montoBase))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(montoImpuesto))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            if(isGratuito){
                attributes.clear();
                attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, "UN/ECE 5305");
                attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Tax Category Identifier");
                attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "United Nations Economic Commission for Europe");
                //appendChild(document, taxCategoryElement, "cbc:ID", "Z",attributes);
            }
            appendChild(document, taxCategoryElement, "cbc:Percent", UtilFormat.format(porcentaje));

            if (StringUtils.isNotBlank(codigoTipoAfectacionIgv)) {

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
            if (StringUtils.isNotBlank(codigoIsc)) {
                tipoTributo = ConstantesSunat.TRIBUTO_ISC;
                appendChild(document, taxCategoryElement, "cbc:TierRange", codigoIsc);
            }
            if (StringUtils.isBlank(codigoIsc) && StringUtils.isBlank(codigoTipoAfectacionIgv)) {
                tipoTributo = ConstantesSunat.TRIBUTO_OTROS;
            }

            attributes.clear();
            if(isGratuito){
                //attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_ID, "6");
                attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_ID, "UN/ECE 5305");


            }
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_NAME, "Codigo de tributos");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_AGENCY_NAME, "PE:SUNAT");
            attributes.put(ConstantesSunat.ATTRIBUTE_SCHEME_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo05");



            Element taxSchemeElement = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            appendChild(document, taxSchemeElement, "cbc:ID", tipoTributo.getId());
            appendChild(document, taxSchemeElement, "cbc:Name", tipoTributo.getName());
            appendChild(document, taxSchemeElement, "cbc:TaxTypeCode", tipoTributo.getTypeCode());
        }
    }

    private void addElementsChildSubTaxBolsaTotal(Document document, Element taxTotalElement, BigDecimal montoBaseIcbper,
                                                  BigDecimal icbper, String formatCantidad, String codigoMoneda,
                                                  Map<String,String> attributes) {

        if (montoBaseIcbper != null && icbper != null) {
            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(icbper))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            appendChild(document, taxSubTotalElement, "cbc:BaseUnitMeasure",
                    decimalFormat.format(Double.parseDouble(formatCantidad)))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_UNIT_CODE, "NIU");


            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            appendChild(document, taxCategoryElement, "cbc:PerUnitAmount", UtilFormat.format(montoBaseIcbper))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            Element taxSchemeElement   = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            appendChild(document, taxSchemeElement, "cbc:ID", ICBPER_ID);
            appendChild(document, taxSchemeElement, "cbc:Name", ICBPER_NAME);
            appendChild(document, taxSchemeElement, "cbc:TaxTypeCode", ICBPER_TYPE);
        }
    }

    private void appendChildItem(Document document, Element invoiceLineElement, String descripcion,
                                 String codigoProducto, String codigoProductoSunat, ComprobanteItem item, int isHidro) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Propiedad de item");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_URI, "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo55");
        Element itemElement = appendChild(document, invoiceLineElement, "cac:Item");
        //DETRACCION CODIGO BIEN DETRACION 004
        System.out.println("INGRESANDO A TEMPLATE HIDRO");
        // if ((StringUtils.isNotBlank(invoice.getCodigoBienDetraccion()) && invoice.getCodigoBienDetraccion().equals("004")) &&
        //        ((StringUtils.isNotBlank(invoice.getCodigoTipoOperacion()) && invoice.getCodigoTipoOperacion().equals("1001")))) {
        appendChild(document, itemElement, "cbc:Description", descripcion);

        if (StringUtils.isNotBlank(codigoProducto)) {
            Element sellersItemIdentificationElement = appendChild(document, itemElement,"cac:SellersItemIdentification");
            appendChild(document, sellersItemIdentificationElement, "cbc:ID", codigoProducto);
        }
        if (StringUtils.isNotBlank(codigoProductoSunat)) {
            Element commodityClassificationElement = appendChild(document, itemElement,
                    "cac:CommodityClassification");
            appendChild(document, commodityClassificationElement, "cbc:ItemClassificationCode", codigoProductoSunat);
        }

        if(isHidro == 1){
            System.out.println("INGRESO METODO TEMPLATE HIDRO");
            appendChildItemHidro(document, itemElement, item);

        }

    }

    private void appendChildItemHidro(Document document, Element itemElement,ComprobanteItem item){

        // 3001 MATRICULA
        System.out.println("METODO CHILHIDRO");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("listName", "Propiedad del item");
        attributes.put("listAgencyName", "PE:SUNAT");
        attributes.put("listURI", "urn:pe:gob:sunat:cpe:see:gem:catalogos:catalogo55");

        Element addictionalMatri = appendChild(document, itemElement, "cac:AdditionalItemProperty");
        appendChild(document,addictionalMatri,"cbc:Name","Detracciones: Recursos Hidrobiológicos-Matrícula de la embarcación");
        appendChild(document,addictionalMatri,"cbc:NameCode","3001",attributes);
        appendChild(document,addictionalMatri,"cbc:Value",item.getHidroMatricula());
        System.out.println("METODO CHILHIDRO2");
        Element addictionalEmba = appendChild(document, itemElement, "cac:AdditionalItemProperty");
        appendChild(document,addictionalEmba,"cbc:Name","Detracciones: Recursos Hidrobiológicos-Nombre de la embarcación");
        appendChild(document,addictionalEmba,"cbc:NameCode","3002",attributes);
        appendChild(document,addictionalEmba,"cbc:Value",item.getHidroEmbarcacion());
        System.out.println("METODO CHILHIDRO3");
        Element addictionalTipo = appendChild(document, itemElement, "cac:AdditionalItemProperty");
        appendChild(document,addictionalTipo,"cbc:Name","Detracciones: Recursos Hidrobiológicos-Tipo de especie vendida");
        appendChild(document,addictionalTipo,"cbc:NameCode","3003",attributes);
        appendChild(document,addictionalTipo,"cbc:Value",item.getHidroDescripcionTipo());
        System.out.println("METODO CHILHIDRO4");
        Element addictionalLuga = appendChild(document, itemElement, "cac:AdditionalItemProperty");
        appendChild(document,addictionalLuga,"cbc:Name","Detracciones: Recursos Hidrobiológicos-Lugar de descarga");
        appendChild(document,addictionalLuga,"cbc:NameCode","3004",attributes);
        appendChild(document,addictionalLuga,"cbc:Value",item.getHidroLugarDescarga());
        System.out.println("METODO CHILHIDRO5");
        Element addictionalItemDes = appendChild(document, itemElement, "cac:AdditionalItemProperty");
        appendChild(document,addictionalItemDes,"cbc:Name","Detracciones: Recursos Hidrobiológicos-Fecha de descarga");
        appendChild(document,addictionalItemDes,"cbc:NameCode","3005",attributes);
        Element childUsability = appendChild(document,addictionalItemDes,"cac:UsabilityPeriod");
        appendChild(document,childUsability,"cbc:StartDate",item.getHidroFechaDescarga());
        System.out.println("METODO CHILHIDRO6");
        Map<String, String> attributesFC = new HashMap<>();
        Element addictionalItemQua = appendChild(document, itemElement, "cac:AdditionalItemProperty");
        appendChild(document,addictionalItemQua,"cbc:Name","Detracciones: Recursos Hidrobiológicos-Cantidad de especie vendida");
        appendChild(document,addictionalItemQua,"cbc:NameCode","3006",attributes);
        attributesFC.clear();
        attributesFC.put("unitCode", "TNE");
        appendChild(document,addictionalItemQua,"cbc:ValueQuantity",item.getHidroCantidad(),attributesFC);
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
