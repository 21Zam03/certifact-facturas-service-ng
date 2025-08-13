package com.certicom.certifact_facturas_service_ng.util;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class UtilFirma {

    public static Document buildDocument(InputStream inDocument) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        // 🔒 Desactivar entidades externas para prevenir XXE
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);

        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStreamReader reader = new InputStreamReader(inDocument, StandardCharsets.UTF_8);//ISO8859_1
        return db.parse(new InputSource(reader));
    }

    public static Node addExtensionContent(Document doc) {
        NodeList nodeList = doc.getDocumentElement().getElementsByTagNameNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "UBLExtensions");
        Node extensions = nodeList.item(0);
        extensions.appendChild(doc.createTextNode("\t"));
        Element extension = doc.createElement("ext:UBLExtension");
        extension.appendChild(doc.createTextNode("\n\t"));
        Element content = doc.createElement("ext:ExtensionContent");
        extension.appendChild(content);
        extension.appendChild(doc.createTextNode("\n\t"));
        extensions.appendChild(extension);
        extensions.appendChild(doc.createTextNode("\n"));
        return content;
    }

    public static Element appendChild(org.w3c.dom.Document doc, Element element, String key, Object value) {
        Element elementChild = doc.createElement(key);
        elementChild.appendChild(doc.createTextNode(value.toString()));
        element.appendChild(elementChild);
        return elementChild;
    }

    public static String getNode(Node node) throws Exception {
        StringBuilder valorClave = new StringBuilder();
        valorClave.setLength(0);
        Integer tamano = node.getChildNodes().getLength();
        int i = 0;
        while (i < tamano) {
            Node c = node.getChildNodes().item(i);
            if (c.getNodeType() == 3) {
                valorClave.append(c.getNodeValue());
            }
            ++i;
        }
        String nodo = valorClave.toString().trim();
        return nodo;
    }

    public static void outputDocToOutputStream(Document doc, ByteArrayOutputStream signatureFile) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        } catch (IllegalArgumentException | TransformerConfigurationException e) {
            log.error("Error setting secure processing feature", e);
            throw new TransformerConfigurationException("No se pudo configurar la seguridad XML.", e);
        }

        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "no");
        transformer.setOutputProperty("encoding", "UTF-8");
        transformer.transform(new DOMSource(doc), new StreamResult(signatureFile));
    }

}
