package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.entity.ErrorEntity;

public interface ErrorCatalogData {

    public ErrorEntity findFirst1ByCodeAndDocument(String codigoRespuesta, String tipoDocumento);

}
