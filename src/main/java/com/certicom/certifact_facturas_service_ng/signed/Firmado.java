package com.certicom.certifact_facturas_service_ng.signed;

import com.certicom.certifact_facturas_service_ng.dto.others.FirmaResp;
import com.certicom.certifact_facturas_service_ng.exceptions.FirmaException;
import com.certicom.certifact_facturas_service_ng.util.UtilConversion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.net.ssl.KeyManagerFactory;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import static com.certicom.certifact_facturas_service_ng.util.UtilFirma.*;

@Component
public class Firmado {

    @Value("${apifact.keystoreblizlink.file}")
    private String keystoreFileblizlink;

    @Value("${apifact.keystoreblizlink.password}")
    private String keystorePasswordblizlink;

    @Value("${apifact.keystoreBlizOse.file}")
    private String keystoreFileBlizOse;

    @Value("${apifact.keystoreBlizOse.password}")
    private String keystorePasswordBlizOse;

    @Value("${apifact.keystoreCerti.file}")
    private String keystoreFileCerti;

    @Value("${apifact.keystoreCerti.password}")
    private String keystorePasswordCerti;

    @Value("${apifact.keystoreTest.file}")
    private String keystoreFileTest;

    @Value("${apifact.keystoreTest.password}")
    private String keystorePasswordTest;

    @Value("${apifact.isProduction}")
    private Boolean isProduction;

    public FirmaResp signCerticom(String xml, String id) throws FirmaException {
        String keystoreFile;
        String keystorePassword;

        if (isProduction) {
            keystoreFile = keystoreFileCerti;
            keystorePassword = keystorePasswordCerti;
        } else {
            keystoreFile = keystoreFileCerti;
            keystorePassword = keystorePasswordCerti;
        }

        FirmaResp response = new FirmaResp();
        try {

            KeyStore ks = getKeyStore(UtilConversion.decodeBase64ToFile(keystoreFile, "pfx"), keystorePassword);
            String alias = ks.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, keystorePassword.toCharArray());
            X509Certificate cert = getCertificate(UtilConversion.decodeBase64ToFile(keystoreFile, "pfx"), keystorePassword);
            ByteArrayOutputStream signatureFile = new ByteArrayOutputStream();
            Document doc = buildDocument(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            Node parentNode = addExtensionContent(doc);
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance();
            Reference ref = fac.newReference("", fac.newDigestMethod("http://www.w3.org/2000/09/xmldsig#sha1", null), Collections.singletonList(fac.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature", (TransformParameterSpec) null)), null, null);
            SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", (C14NMethodParameterSpec) null), fac.newSignatureMethod("http://www.w3.org/2000/09/xmldsig#rsa-sha1", null), Collections.singletonList(ref));
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            ArrayList<X509Certificate> x509Content = new ArrayList<>();
            x509Content.add(cert);
            X509Data xd = kif.newX509Data(x509Content);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
            DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
            XMLSignature signature = fac.newXMLSignature(si, ki);
            if (parentNode != null) {
                dsc.setParent(parentNode);
            }
            dsc.setDefaultNamespacePrefix("ds");
            signature.sign(dsc);
            String digestValue = null;
            Element elementParent = (Element) dsc.getParent();
            if (elementParent.getElementsByTagName("ds:Signature") != null) {
                Element elementSignature = (Element) elementParent.getElementsByTagName("ds:Signature").item(0);
                elementSignature.setAttribute("Id", id);

                Element keyInfo = (Element) elementSignature.getElementsByTagName("ds:KeyInfo").item(0);
                Element X509Data = (Element) keyInfo.getElementsByTagName("ds:X509Data").item(0);
                appendChild(doc, X509Data, "ds:X509SubjectName", cert.getSubjectX500Principal().getName());

                NodeList nodeList = elementParent.getElementsByTagName("ds:DigestValue");
                int i = 0;
                while (i < nodeList.getLength()) {
                    digestValue = getNode(nodeList.item(i));
                    ++i;
                }
            }
            outputDocToOutputStream(doc, signatureFile);
            signatureFile.close();
            response.setSignatureFile(signatureFile);
            response.setDigestValue(digestValue);
            response.setStatus(!signatureFile.toString().trim().isEmpty() && digestValue != null && !digestValue.trim().isEmpty());

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new FirmaException("Error al firmar documento xml: " + (ex == null ? "" : ex.getMessage()));
        }
        return response;
    }

    private static KeyStore getKeyStore(File certificate, String privateKey) throws Exception {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        char[] password = privateKey.toCharArray();
        keystore.load(new FileInputStream(certificate), password);
        return keystore;
    }

    private static X509Certificate getCertificate(File certificate, String privateKey) throws Exception {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        char[] password = privateKey.toCharArray();
        keystore.load(new FileInputStream(certificate), password);
        kmf.init(keystore, privateKey.toCharArray());
        Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (keystore.getCertificate(alias).getType().equals("X.509")) {
                return (X509Certificate) keystore.getCertificate(alias);
            }
        }
        return null;
    }

}
