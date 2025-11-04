package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.Voided;
import com.certicom.certifact_facturas_service_ng.dto.others.SignatureResp;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.service.TemplateService;
import com.certicom.certifact_facturas_service_ng.signed.Firmado;
import com.certicom.certifact_facturas_service_ng.templates.VoidedDocumentsTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template.FacturaTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template.NotaCreditoTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template.NotaDebitoTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template21.FacturaTemplate21;
import com.certicom.certifact_facturas_service_ng.templates.template21.NotaCreditoTemplateSunat21;
import com.certicom.certifact_facturas_service_ng.templates.template21.NotaDebitoTemplateSunat21;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import com.certicom.certifact_facturas_service_ng.util.UtilConversion;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    private final FacturaTemplate facturaTemplate;
    private final FacturaTemplate21 facturaTemplate21;
    private final NotaCreditoTemplate notaCreditoTemplate;
    private final NotaCreditoTemplateSunat21 notaCreditoTemplate21;
    private final NotaDebitoTemplate notaDebitoTemplate;
    private final NotaDebitoTemplateSunat21 notaDebitoTemplate21;
    private final VoidedDocumentsTemplate voidedDocumentsTemplate;

    private final Firmado firma;

    @Override
    public Map<String, String> buildPaymentVoucherSignOse(PaymentVoucherDto paymentVoucherDto) {

        String xmlGenerado = null;
        String idFirma;
        String nombreDocumento;
        Map<String, String> resp;
/*
        switch (comprobanteDto.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if(comprobanteDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = invoiceTemplateOse.buildInvoice(voucher);
                }else if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = invoiceTemplateOse.buildInvoice(voucher);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = creditNoteTemplateOse.buildCreditNote(voucher);

                }else if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = creditNoteTemplateOse.buildCreditNote(voucher);
                }
                break;
            default:
                if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = debitNoteTemplateOse.buildDebitNote(voucher);
                }else if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = debitNoteTemplateOse.buildDebitNote(voucher);
                }
                break;
        }
*/
        return null;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSignOseBliz(PaymentVoucherDto paymentVoucherDto) {
        return null;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSignCerti(PaymentVoucherDto paymentVoucherDto) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException {
        /*FACTURA, NOTA DE CREDITO Y DEBITO*/
        String xmlGenerado = null;
        String idFirma;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResp signatureResp;

        switch (paymentVoucherDto.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = facturaTemplate.construirFactura(paymentVoucherDto);
                }else if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = facturaTemplate21.construirFactura(paymentVoucherDto);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = notaCreditoTemplate.construirNotaCredito(paymentVoucherDto);

                }else if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = notaCreditoTemplate21.construirNotaCredito(paymentVoucherDto);
                }
                break;
            default:
                if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = notaDebitoTemplate.construirNotaDebito(paymentVoucherDto);
                }else if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = notaDebitoTemplate21.buildDebitNote(paymentVoucherDto);
                }
                break;
        }

        idFirma = "S" + paymentVoucherDto.getTipoComprobante() + paymentVoucherDto.getSerie() + "-" + paymentVoucherDto.getNumero();
        signatureResp = firma.signCerticom(xmlGenerado, idFirma);
        nombreDocumento = paymentVoucherDto.getRucEmisor() + "-" + paymentVoucherDto.getTipoComprobante() + "-" +
                paymentVoucherDto.getSerie() + "-" + paymentVoucherDto.getNumero();

        resp = buildDataTemplate(signatureResp, nombreDocumento);
        resp.put(ConstantesParameter.CODIGO_HASH, UtilArchivo.generarCodigoHash(signatureResp.toString()));

        return resp;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSign(PaymentVoucherDto paymentVoucherDto) {
        return null;
    }

    @Override
    public Map<String, String> buildVoidedDocumentsSign(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException {

        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> buildVoidedDocumentsSignCerti(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException {
        String xmlGenerado;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResp signatureResp;

        xmlGenerado = voidedDocumentsTemplate.buildVoidedDocuments(voided);
        idSignature = "S" + voided.getId();
        signatureResp = firma.signCerticom(xmlGenerado, idSignature);

        nombreDocumento = voided.getRucEmisor() + "-" + voided.getId();
        resp = buildDataTemplate(signatureResp, nombreDocumento);

        return resp;
    }



    private Map<String, String> buildDataTemplate(SignatureResp signatureResp, String nombreDocumento) throws SignedException, IOException, NoSuchAlgorithmException {

        Map<String, String> resp;
        File zipeado;

        zipeado = UtilArchivo.comprimir(signatureResp.getSignatureFile(),
                ConstantesParameter.TYPE_FILE_XML, nombreDocumento);
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
        String shaChecksum = getFileChecksum(shaDigest, zipeado);
        resp = new HashMap<>();
        resp.put(ConstantesParameter.PARAM_NAME_DOCUMENT, nombreDocumento);
        try {

            byte encoded[] = Base64.getEncoder().encode(signatureResp.getSignatureFile().toByteArray());
            String xmlBase64 = new String(encoded);

            resp.put(ConstantesParameter.PARAM_FILE_ZIP_BASE64, UtilConversion.encodeFileToBase64(zipeado));
            resp.put(ConstantesParameter.PARAM_FILE_XML_BASE64, xmlBase64);
            resp.put(ConstantesParameter.PARAM_STRING_HASH, shaChecksum);
        } catch (IOException e) {
            throw new SignedException(e.getMessage());
        }

        return resp;
    }

    private String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content


        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        try(FileInputStream fis = new FileInputStream(file)) {
            //Read file data and update in message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

}
