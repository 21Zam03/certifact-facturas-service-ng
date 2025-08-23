package com.certicom.certifact_facturas_service_ng.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class UtilXml {

    public static Element appendChild(org.w3c.dom.Document doc, Element element, String key, Object value) {
        Element elementChild = doc.createElement(key);
        elementChild.appendChild(doc.createTextNode(value.toString()));
        element.appendChild(elementChild);
        return elementChild;
    }

    public static Element appendChild(org.w3c.dom.Document doc, Element element, String key) {
        Element elementChild = doc.createElement(key);
        elementChild.appendChild(doc.createTextNode(" "));
        element.appendChild(elementChild);
        return elementChild;
    }

    public static Element appendChild(org.w3c.dom.Document doc, Element element, String key, Object value, Map<String, String> attributes) {
        Element elementChild = doc.createElement(key);
        elementChild.appendChild(doc.createTextNode(value.toString()));
        attributes.forEach((k,v)-> elementChild.setAttribute(k, v));
        element.appendChild(elementChild);
        return elementChild;
    }

    public static String formatXML(String xml) {
        String result = "";
        try {
            Document doc = DocumentHelper.parseText(xml);
            StringWriter sw = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter xw = new XMLWriter(sw, format);
            xw.write(doc);
            result = sw.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String formatoXML = result.replace("\n\n", "\n").replace("&lt;", "<").replace("&gt;", ">");
        return formatoXML;
    }

    public static org.w3c.dom.Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // 🔒 Proteger contra XXE
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
