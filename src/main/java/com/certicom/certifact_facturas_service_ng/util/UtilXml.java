package com.certicom.certifact_facturas_service_ng.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Element;

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

}
