package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.others.EmailSendDto;

public interface EmailService {

    Boolean sendEmailOnConfirmSunat(EmailSendDto emailSendDTO);

}
