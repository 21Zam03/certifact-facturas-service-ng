package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.dto.others.EmailCompanyNotifyDto;

import java.util.List;

public interface EmailCompanyNotifyData {

    List<EmailCompanyNotifyDto> findAllByCompanyRucAndEstadoIsTrue(String rucEmisor);

}
