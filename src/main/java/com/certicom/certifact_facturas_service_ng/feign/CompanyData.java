package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.CompanyModel;
import com.certicom.certifact_facturas_service_ng.model.OseModel;

public interface CompanyData {

    public String getStateFromCompanyByRuc(String ruc);
    public CompanyModel findCompanyByRuc(String ruc);
    public OseModel findOseByRucInter(String ruc);

}
