package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.ComprobanteDto;
import com.certicom.certifact_facturas_service_ng.dto.others.FirmaResp;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.service.PlantillaService;
import com.certicom.certifact_facturas_service_ng.signed.Firmado;
import com.certicom.certifact_facturas_service_ng.templates.template.FacturaTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template.NotaCreditoTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template.NotaDebitoTemplate;
import com.certicom.certifact_facturas_service_ng.templates.template21.FacturaTemplate21;
import com.certicom.certifact_facturas_service_ng.templates.template21.NotaCreditoTemplate21;
import com.certicom.certifact_facturas_service_ng.templates.template21.NotaDebitoTemplate21;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParametro;
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
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlantillaServiceImpl implements PlantillaService {

    private final FacturaTemplate facturaTemplate;
    private final FacturaTemplate21 facturaTemplate21;
    private final NotaCreditoTemplate notaCreditoTemplate;
    private final NotaCreditoTemplate21 notaCreditoTemplate21;
    private final NotaDebitoTemplate notaDebitoTemplate;
    private final NotaDebitoTemplate21 notaDebitoTemplate21;

    private final Firmado firma;

    @Override
    public Map<String, String> buildPaymentVoucherSignOse(ComprobanteDto comprobanteDto) {

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
    public Map<String, String> buildPaymentVoucherSignOseBliz(ComprobanteDto comprobanteDto) {
        return null;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSignCerti(ComprobanteDto comprobanteDto) throws TemplateException, SignedException, IOException, NoSuchAlgorithmException {
        /*FACTURA, NOTA DE CREDITO Y DEBITO*/
        String xmlGenerado = null;
        String idFirma;
        String nombreDocumento;
        Map<String, String> resp;
        FirmaResp firmaResp;

        switch (comprobanteDto.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if(comprobanteDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = facturaTemplate.construirFactura(comprobanteDto);
                }else if(comprobanteDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = facturaTemplate21.construirFactura(comprobanteDto);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                if(comprobanteDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = notaCreditoTemplate.construirNotaCredito(comprobanteDto);

                }else if(comprobanteDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = notaCreditoTemplate21.construirNotaCredito(comprobanteDto);
                }
                break;
            default:
                if(comprobanteDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = notaDebitoTemplate.construirNotaDebito(comprobanteDto);
                }else if(comprobanteDto.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = notaDebitoTemplate21.buildDebitNote(comprobanteDto);
                }
                break;
        }

        idFirma = "S" + comprobanteDto.getTipoComprobante() + comprobanteDto.getSerie() + "-" + comprobanteDto.getNumero();
        firmaResp = firma.signCerticom(xmlGenerado, idFirma);
        nombreDocumento = comprobanteDto.getRucEmisor() + "-" + comprobanteDto.getTipoComprobante() + "-" +
                comprobanteDto.getSerie() + "-" + comprobanteDto.getNumero();

        resp = buildDataTemplate(firmaResp, nombreDocumento);
        resp.put(ConstantesParametro.CODIGO_HASH, UtilArchivo.generarCodigoHash(firmaResp.toString()));

        return resp;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSign(ComprobanteDto comprobanteDto) {
        return null;
    }

    private Map<String, String> buildDataTemplate(FirmaResp firmaResp, String nombreDocumento) throws SignedException, IOException, NoSuchAlgorithmException {

        Map<String, String> resp;
        File zipeado;

        zipeado = UtilArchivo.comprimir(firmaResp.getSignatureFile(),
                ConstantesParametro.TYPE_FILE_XML, nombreDocumento);
        MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
        String shaChecksum = getFileChecksum(shaDigest, zipeado);
        resp = new HashMap<>();
        resp.put(ConstantesParametro.PARAM_NAME_DOCUMENT, nombreDocumento);
        try {

            byte encoded[] = Base64.getEncoder().encode(firmaResp.getSignatureFile().toByteArray());
            String xmlBase64 = new String(encoded);

            resp.put(ConstantesParametro.PARAM_FILE_ZIP_BASE64, UtilConversion.encodeFileToBase64(zipeado));
            resp.put(ConstantesParametro.PARAM_FILE_XML_BASE64, xmlBase64);
            resp.put(ConstantesParametro.PARAM_STRING_HASH, shaChecksum);
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
