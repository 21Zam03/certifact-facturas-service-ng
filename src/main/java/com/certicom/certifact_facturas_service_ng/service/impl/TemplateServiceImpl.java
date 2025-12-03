package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.Voided;
import com.certicom.certifact_facturas_service_ng.dto.others.SignatureResponse;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.service.TemplateService;
import com.certicom.certifact_facturas_service_ng.signed.Signed;
import com.certicom.certifact_facturas_service_ng.templates.VoidedDocumentsTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template.FacturaTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template.NotaCreditoTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template.NotaDebitoTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template21.FacturaTemplate21;
import com.certicom.certifact_facturas_service_ng.templates.template21.NotaCreditoTemplateSunat21;
import com.certicom.certifact_facturas_service_ng.templates.template21.NotaDebitoTemplateSunat21;
import com.certicom.certifact_facturas_service_ng.templates.templateose.FacturaTemplateOse;
import com.certicom.certifact_facturas_service_ng.templates.templateose.NotaCreditoTemplateOse;
import com.certicom.certifact_facturas_service_ng.templates.templateose.NotaDebitoTemplateOse;
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

    private final FacturaTemplate invoiceTemplate;
    private final FacturaTemplate21 invoiceTemplate21;
    private final FacturaTemplateOse invoiceTemplateOse;
    private final NotaCreditoTemplate creditNoteTemplate;
    private final NotaCreditoTemplateSunat21 creditNoteTemplate21;
    private final NotaCreditoTemplateOse creditNoteTemplateOse;
    private final NotaDebitoTemplate debitNoteTemplate;
    private final NotaDebitoTemplateSunat21 debitNoteTemplate21;
    private final NotaDebitoTemplateOse debitNoteTemplateOse;
    private final VoidedDocumentsTemplate voidedDocumentsTemplate;

    private final Signed signed;

    @Override
    public Map<String, String> buildPaymentVoucherSignOse(PaymentVoucherDto voucher) throws IOException, NoSuchAlgorithmException {
        String xmlGenerado = null;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;
        switch (voucher.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
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

        idSignature = "S"+voucher.getTipoComprobante()+voucher.getSerie()+"-"+voucher.getNumero();
        signatureResp = signed.sign(xmlGenerado, idSignature);
        nombreDocumento = voucher.getRucEmisor()+"-"+voucher.getTipoComprobante()+"-"+
                voucher.getSerie()+"-"+voucher.getNumero();

        resp = buildDataTemplate(signatureResp, nombreDocumento);
        resp.put(ConstantesParameter.CODIGO_HASH, UtilArchivo.generarCodigoHash(signatureResp.toString()));
        return resp;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSignOseBliz(PaymentVoucherDto voucher) throws IOException, NoSuchAlgorithmException {
        String xmlGenerado = null;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        switch (voucher.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = invoiceTemplate.construirFactura(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = invoiceTemplate21.construirFactura(voucher);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = creditNoteTemplate.construirNotaCredito(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = creditNoteTemplate21.construirNotaCredito(voucher);
                }
                break;
            default:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = debitNoteTemplate.construirNotaDebito(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = debitNoteTemplate21.buildDebitNote(voucher);
                }
                break;
        }

        idSignature = "S" + voucher.getTipoComprobante() + voucher.getSerie() + "-" + voucher.getNumero();
        signatureResp = signed.signBliz(xmlGenerado, idSignature);
        nombreDocumento = voucher.getRucEmisor() + "-" + voucher.getTipoComprobante() + "-" +
                voucher.getSerie() + "-" + voucher.getNumero();

        resp = buildDataTemplate(signatureResp, nombreDocumento);
        resp.put(ConstantesParameter.CODIGO_HASH, UtilArchivo.generarCodigoHash(signatureResp.toString()));
        return resp;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSignCerti(PaymentVoucherDto paymentVoucherDto) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException {
        /*FACTURA, NOTA DE CREDITO Y DEBITO*/
        String xmlGenerado = null;
        String idFirma;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResponse;

        switch (paymentVoucherDto.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = invoiceTemplate.construirFactura(paymentVoucherDto);
                }else if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = invoiceTemplate21.construirFactura(paymentVoucherDto);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = creditNoteTemplate.construirNotaCredito(paymentVoucherDto);

                }else if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = creditNoteTemplate21.construirNotaCredito(paymentVoucherDto);
                }
                break;
            default:
                if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = debitNoteTemplate.construirNotaDebito(paymentVoucherDto);
                }else if(paymentVoucherDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = debitNoteTemplate21.buildDebitNote(paymentVoucherDto);
                }
                break;
        }

        idFirma = "S" + paymentVoucherDto.getTipoComprobante() + paymentVoucherDto.getSerie() + "-" + paymentVoucherDto.getNumero();
        signatureResponse = signed.signCerticom(xmlGenerado, idFirma);
        nombreDocumento = paymentVoucherDto.getRucEmisor() + "-" + paymentVoucherDto.getTipoComprobante() + "-" +
                paymentVoucherDto.getSerie() + "-" + paymentVoucherDto.getNumero();

        resp = buildDataTemplate(signatureResponse, nombreDocumento);
        resp.put(ConstantesParameter.CODIGO_HASH, UtilArchivo.generarCodigoHash(signatureResponse.toString()));

        return resp;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSign(PaymentVoucherDto voucher) throws IOException, NoSuchAlgorithmException {
        String xmlGenerado = null;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        switch (voucher.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = invoiceTemplate.construirFactura(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = invoiceTemplate21.construirFactura(voucher);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = creditNoteTemplate.construirNotaCredito(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = creditNoteTemplate21.construirNotaCredito(voucher);
                }
                break;
            default:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = debitNoteTemplate.construirNotaDebito(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = debitNoteTemplate21.buildDebitNote(voucher);
                }
                break;
        }

        idSignature = "S" + voucher.getTipoComprobante() + voucher.getSerie() + "-" + voucher.getNumero();
        signatureResp = signed.signBliz(xmlGenerado, idSignature);
        nombreDocumento = voucher.getRucEmisor() + "-" + voucher.getTipoComprobante() + "-" +
                voucher.getSerie() + "-" + voucher.getNumero();

        resp = buildDataTemplate(signatureResp, nombreDocumento);
        resp.put(ConstantesParameter.CODIGO_HASH, UtilArchivo.generarCodigoHash(signatureResp.toString()));
        return resp;
    }

    @Override
    public Map<String, String> buildVoidedDocumentsSign(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException {
        String xmlGenerado;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        xmlGenerado = voidedDocumentsTemplate.buildVoidedDocuments(voided);
        idSignature = "S" + voided.getId();
        signatureResp = signed.signBliz(xmlGenerado, idSignature);

        nombreDocumento = voided.getRucEmisor() + "-" + voided.getId();
        resp = buildDataTemplate(signatureResp, nombreDocumento);

        return resp;
    }

    @Override
    public Map<String, String> buildVoidedDocumentsSignCerti(Voided voided) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException {
        String xmlGenerado;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResponse;

        xmlGenerado = voidedDocumentsTemplate.buildVoidedDocuments(voided);
        idSignature = "S" + voided.getId();
        signatureResponse = signed.signCerticom(xmlGenerado, idSignature);

        nombreDocumento = voided.getRucEmisor() + "-" + voided.getId();
        resp = buildDataTemplate(signatureResponse, nombreDocumento);

        return resp;
    }


    private Map<String, String> buildDataTemplate(SignatureResponse signatureResponse, String nombreDocumento) throws SignedException, IOException, NoSuchAlgorithmException {

        Map<String, String> resp;
        File zipeado;

        zipeado = UtilArchivo.comprimir(signatureResponse.getSignatureFile(),
                ConstantesParameter.TYPE_FILE_XML, nombreDocumento);
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
        String shaChecksum = getFileChecksum(shaDigest, zipeado);
        resp = new HashMap<>();
        resp.put(ConstantesParameter.PARAM_NAME_DOCUMENT, nombreDocumento);
        try {

            byte encoded[] = Base64.getEncoder().encode(signatureResponse.getSignatureFile().toByteArray());
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
