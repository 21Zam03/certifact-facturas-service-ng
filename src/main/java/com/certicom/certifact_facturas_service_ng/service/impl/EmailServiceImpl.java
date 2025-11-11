package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.EmailSendDto;
import com.certicom.certifact_facturas_service_ng.entity.PaymentVoucherEntity;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherData;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherFileData;
import com.certicom.certifact_facturas_service_ng.feign.RegisterFileUploadData;
import com.certicom.certifact_facturas_service_ng.model.CompanyModel;
import com.certicom.certifact_facturas_service_ng.model.RegisterFileUploadModel;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.EmailService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class EmailServiceImpl implements EmailService {

    private final PaymentVoucherData paymentVoucherData;
    private final PaymentVoucherFileData paymentVoucherFileData;
    private final RegisterFileUploadData registerFileUploadData;

    private final AmazonS3ClientService amazonS3ClientService;

    @Autowired
    public EmailServiceImpl(
            PaymentVoucherData paymentVoucherData,
            RegisterFileUploadData registerFileUploadData,
            PaymentVoucherFileData paymentVoucherFileData,
            AmazonS3ClientService amazonS3ClientService) {
        this.paymentVoucherData = paymentVoucherData;
        this.registerFileUploadData = registerFileUploadData;
        this.paymentVoucherFileData = paymentVoucherFileData;
        this.amazonS3ClientService = amazonS3ClientService;
    }

    @Override
    public Boolean sendEmailOnConfirmSunat(EmailSendDto emailSendDTO) {
        String rucEmisor = "";
        String receptor = "";
        String serie = "";
        String numDocum = "";
        String tipo = "";
        String estado = "";
        String afectado = "";
        String uuid = "";
        Long idp = 0l;
        RegisterFileUploadModel uploadEntity = null;
        InputStream isXmlprevio = null;
        String[] arrayEmail = null;
        System.out.println("ENVIO EMAIL CONFIRM "+emailSendDTO.getTipo());
        try {
            PaymentVoucherDto comprobante = paymentVoucherData.findPaymentVoucherById(emailSendDTO.getId());
            rucEmisor = comprobante.getRucEmisor();
            arrayEmail = (comprobante.getEmailReceptor()==null?"":comprobante.getEmailReceptor()).split(",");

            receptor = comprobante.getDenominacionReceptor();
            serie = comprobante.getSerie();
            numDocum = String.valueOf(comprobante.getNumero());
            tipo = comprobante.getTipoComprobante();
            estado = comprobante.getEstado();
            afectado = comprobante.getTipoComprobanteAfectado();
            idp = comprobante.getIdPaymentVoucher();
            uuid = comprobante.getUuid();

            paymentVoucherFileData.

            uploadEntity = comprobante.getXmlActivo();
            String nombreComprobanteprev = String.format("%s-%s-%s-%s", rucEmisor, tipo, serie, numDocum);
            isXmlprevio = extractXmlFromZip(amazonS3ClientService.downloadFileStorageDto(uploadEntity), nombreComprobanteprev + ".xml");

            CompanyModel companyEntity = companyRepository.findByRuc(rucEmisor);
            List<String> emailsVoucher = new ArrayList<>();
            System.out.println("GET EMAIL "+(emailSendDTO.getEmail()));
            if (emailSendDTO.getEmail() != null) {
                String[] emails = (emailSendDTO.getEmail()).split(",");
                //emailToSend = emailSendDTO.getEmail();
                for (int i=0;i<emails.length;i++){
                    emailsVoucher.add(emails[i].trim());
                }
                //emailsVoucher.add(emailSendDTO.getEmail());
            }else {
                String[] emails = arrayEmail;
                for (int i=0;i<emails.length;i++){
                    emailsVoucher.add(emails[i].trim());
                }
            } //emailToSend = comprobante.getEmailReceptor();

            //EMAIL ADICIONALES
            List<EmailCompanyNotifyEntity> emailsAdicionalesNotificar = emailCompanyNotifyRepository.findAllByCompany_RucAndEstadoIsTrue(rucEmisor);
            List<String> emailsList = new ArrayList<>();

            if (!emailsAdicionalesNotificar.isEmpty() && (emailSendDTO.getEmail() == null || ((emailSendDTO.getEmail().trim()).length()==0) )){

                emailsList = emailsAdicionalesNotificar.stream().filter(e -> (e.getEmail().trim()).length()>0)
                        .map(e -> e.getEmail()).collect(Collectors.toList());
            }
            System.out.println(emailsList);
            /*if (emailToSend != null && !emailToSend.isEmpty())
                emailsList.add(emailToSend);*/
            for (int j=0;j<emailsVoucher.size();j++){
                emailsList.add(emailsVoucher.get(j));
            }

            //for (String emailSend : emailsList) {
            if (emailsList.size()>0) {
                //log.info(emailsList);
                List<String> emailListFinal = emailsList;

                String finalReceptor = receptor;
                String finalSerie = serie;
                String finalNumDocum = numDocum;
                String finalTipo = tipo;
                String finalRucEmisor = rucEmisor;
                Long idPayment = idp;
                String finalEstado = estado;
                String finalAfectado = afectado;
                String finalUuid = uuid;
                InputStream finalIsXmlprevio = isXmlprevio;
                MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                        String textMensajeHtml = mailTemplateService.getFreeMarkerTemplateContent(
                                new HashMap<String, Object>() {{
                                    put("urlLogo", companyEntity.getArchivoLogo() == null ? "" : (urlImagenes + companyEntity.getArchivoLogo().getIdRegisterFileSend()));
                                    put("nombreReceptor", finalReceptor);
                                    put("nombreDocumento", StringsUtils.getNombreCortoTipoComprobante(finalTipo));
                                    put("serie", finalSerie);
                                    put("numero", finalNumDocum);
                                    put("urlConsultaComprobante", urlConsultaComprobante + companyEntity.getRuc());
                                    put("nombreEmpresa", companyEntity.getRazonSocial() != null ? companyEntity.getRazonSocial() : "");
                                    put("nombreComercial", companyEntity.getNombreComercial() != null ? companyEntity.getNombreComercial() : "");
                                    put("rucEmpresa", companyEntity.getRuc() != null ? companyEntity.getRuc() : "");
                                    put("direccionEmpresa", companyEntity.getDireccion() != null ? companyEntity.getDireccion() : "");
                                }}, "templateMailConfirmMessage.txt"
                        );
                        helper.setSubject(StringsUtils.getNombreCortoTipoComprobante(finalTipo) + " " + finalSerie + "-" + finalNumDocum + " " + companyEntity.getRazonSocial());

                        helper.setFrom(new InternetAddress(emailFrom, companyEntity.getRazonSocial()));
                        String[] stringArray = new String[emailListFinal.size()];
                        System.out.println(" stringArray1 "+stringArray);
                        System.out.println(emailListFinal);
                        System.out.println(emailListFinal.size());
                        for (int j = 0; j < emailListFinal.size(); j++) {
                            if((emailListFinal.get(j).trim()).length()>5){
                                stringArray[j] = emailListFinal.get(j);
                            }

                        }
                        System.out.println(" stringArray2"+stringArray);
                        helper.setTo(stringArray);
                        helper.setText(textMensajeHtml, true);
                        if (companyEntity.getEmail() != null) {
                            helper.setReplyTo(new InternetAddress(companyEntity.getEmail(), companyEntity.getRazonSocial()));
                        }


                        String nombreComprobante = String.format("%s-%s-%s-%s", finalRucEmisor, finalTipo, finalSerie, finalNumDocum);

                        InputStream isXml = finalIsXmlprevio;

                        if (emailSendDTO.getTipo()!=null){
                            if (emailSendDTO.getTipo()==2){
                                InputStream isGuia = reportService.getPdfComprobanteGuia(finalRucEmisor,  finalSerie, Integer.parseInt(finalNumDocum),"guia");
                                helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isGuia)));
                            }else{
                                try {
                                    if (finalEstado.equals("02")&&(finalTipo.equals("01")|| finalAfectado.equals("01"))){

                                        InputStream isCdr = extractXmlFromZip(amazonS3ClientService.downloadFileInvoice(idPayment, finalUuid, TipoArchivoEnum.CDR),"R-"+nombreComprobante + ".xml");
                                        helper.addAttachment(nombreComprobante + "_CDR.xml", new ByteArrayResource(IOUtils.toByteArray(isCdr)));
                                    }

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                if (companyEntity.getSendticket() == 1) {
                                    InputStream isTicket = reportService.getPdfComprobanteTicket(finalRucEmisor, finalTipo, finalSerie, Integer.parseInt(finalNumDocum));
                                    helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isTicket)));
                                } else {
                                    InputStream isPdf = reportService.getPdfComprobanteA4(finalRucEmisor, finalTipo, finalSerie, Integer.parseInt(finalNumDocum));
                                    helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isPdf)));
                                }
                            }

                        }else{
                            try {
                                if (finalEstado.equals("02")&&(finalTipo.equals("01")|| finalAfectado.equals("01"))){

                                    InputStream isCdr = extractXmlFromZip(amazonS3ClientService.downloadFileInvoice(idPayment, finalUuid, TipoArchivoEnum.CDR),"R-"+nombreComprobante + ".xml");
                                    helper.addAttachment(nombreComprobante + "_CDR.xml", new ByteArrayResource(IOUtils.toByteArray(isCdr)));
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if (companyEntity.getSendticket() == 1) {
                                InputStream isTicket = reportService.getPdfComprobanteTicket(finalRucEmisor, finalTipo, finalSerie, Integer.parseInt(finalNumDocum));
                                helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isTicket)));
                            } else {
                                InputStream isPdf = reportService.getPdfComprobanteA4(finalRucEmisor, finalTipo, finalSerie, Integer.parseInt(finalNumDocum));
                                helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isPdf)));
                            }
                        }


                        helper.addAttachment(nombreComprobante + ".xml", new ByteArrayResource(IOUtils.toByteArray(isXml)));

                    }
                };

                emailSender.send(preparator);

            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private ByteArrayInputStream extractXmlFromZip(ByteArrayInputStream zipBis, String zipEntryName) throws IOException {
        try (ZipInputStream zipin = new ZipInputStream(zipBis)) {
            ZipEntry ze;
            while ((ze = zipin.getNextEntry()) != null) {
                String zeName = ze.getName();
                if ((zipEntryName.toUpperCase()).equals(zeName.toUpperCase())) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int b = zipin.read();
                    while (b >= 0) {
                        baos.write(b);
                        b = zipin.read();
                    }
                    zipin.close();
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            }
            zipin.close();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
