package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.ParameterModel;

public interface ParameterData {

    ParameterModel findByName(String name);

}
