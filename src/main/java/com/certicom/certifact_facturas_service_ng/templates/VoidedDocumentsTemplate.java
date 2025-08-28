package com.certicom.certifact_facturas_service_ng.templates;

import com.certicom.certifact_facturas_service_ng.dto.model.Voided;
import com.certicom.certifact_facturas_service_ng.dto.model.VoidedLine;
import com.certicom.certifact_facturas_service_ng.dto.others.Signature;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.templates.sunat.TemplateSunat;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.certicom.certifact_facturas_service_ng.util.UtilXml.appendChild;
import static com.certicom.certifact_facturas_service_ng.util.UtilXml.formatXML;

@Component
public class VoidedDocumentsTemplate extends TemplateSunat {

    public String buildVoidedDocuments(Voided voided) {

        Signature signature = buildSignature(voided);
        String templateBuilt = buildTemplateInXML(voided, signature);

        return templateBuilt;
    }

    protected Signature buildSignature(Voided voided) {

        Signature signature = new Signature();
        signature.setId("S" + voided.getId());
        signature.setUri("#SignatureKG");
        signature.setDenominacionEmisor(voided.getDenominacionEmisor());
        signature.setRucEmisor(voided.getRucEmisor());

        return signature;
    }

    private String buildTemplateInXML(Voided voided, Signature signature) {

        String stringXMLGenerate = null;
        Document document;
        Element elementRoot;

        try {
            document = createDocument();
            elementRoot = addElementRoot(document, ROOT_VOIDED_DOCUMENTS, getAttributesElementRoot());

            appendChildUBLExtensions(document, elementRoot);
            appendChild(document, elementRoot, "cbc:UBLVersionID", ConstantesSunat.UBL_VERSION_ID_VOIDED_DOCUMENTS);
            appendChild(document, elementRoot, "cbc:CustomizationID", ConstantesSunat.CUSTOMIZATION_ID_VOIDED_DOCUMENTS);
            appendChild(document, elementRoot, "cbc:ID", voided.getId());
            appendChild(document, elementRoot, "cbc:ReferenceDate", voided.getFechaBaja());
            appendChild(document, elementRoot, "cbc:IssueDate", voided.getFechaGeneracion());
            appendChildSignature(document, elementRoot, signature);
            appendChildAccountingSupplierParty(document, elementRoot, voided.getRucEmisor(),
                    voided.getTipoDocumentoEmisor(), voided.getDenominacionEmisor());
            appendChildVoidedDocumentsLine(document, elementRoot, voided.getLines());

            String stringXML = buildStringFromDOM(document);
            stringXMLGenerate = formatXML(stringXML);

        } catch (Exception e) {

            e.printStackTrace();
            throw new TemplateException(e.getMessage());
        }

        return stringXMLGenerate;
    }

    private void appendChildUBLExtensions(Document document, Element rootElement) {

        appendChild(document, rootElement, "ext:UBLExtensions");
    }

    private void appendChildVoidedDocumentsLine(Document document, Element rootElement,
                                                List<VoidedLine> items) {

        Integer correlativo = 1;
        for (VoidedLine item : items) {

            Element voidedDocumentsLine = appendChild(document, rootElement, "sac:VoidedDocumentsLine");
            appendChild(document, voidedDocumentsLine, "cbc:LineID", correlativo);
            appendChild(document, voidedDocumentsLine, "cbc:DocumentTypeCode", item.getTipoComprobante());
            appendChild(document, voidedDocumentsLine, "sac:DocumentSerialID", item.getSerieDocumento());
            appendChild(document, voidedDocumentsLine, "sac:DocumentNumberID", item.getNumeroDocumento());
            appendChild(document, voidedDocumentsLine, "sac:VoidReasonDescription", item.getRazon());

            item.setNumeroItem(correlativo);
            correlativo++;
        }
    }

    private void appendChildAccountingSupplierParty(Document document, Element rootElement,
                                                    String numeroDocumentoEmisor, String tipoDocumentoEmisor, String denominacionEmisor) {

        Element accountingSupplierParty = appendChild(document, rootElement, "cac:AccountingSupplierParty");
        appendChild(document, accountingSupplierParty, "cbc:CustomerAssignedAccountID", numeroDocumentoEmisor);
        appendChild(document, accountingSupplierParty, "cbc:AdditionalAccountID", tipoDocumentoEmisor);

        Element partyElement = appendChild(document, accountingSupplierParty, "cac:Party");
        Element cacPartyLegalEntity = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, cacPartyLegalEntity, "cbc:RegistrationName", denominacionEmisor);

    }

    private Map<String, String> getAttributesElementRoot() {

        Map<String, String> atributos = new HashMap<String, String>();

        atributos.put("xmlns", "urn:sunat:names:specification:ubl:peru:schema:xsd:VoidedDocuments-1");
        atributos.put("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        atributos.put("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
        atributos.put("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        atributos.put("xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        atributos.put("xmlns:sac", "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1");
        atributos.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        return atributos;
    }

}
