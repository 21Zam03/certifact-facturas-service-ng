package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.BranchOfficesModel;

public interface BranchOfficeData {

    public BranchOfficesModel obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
            Integer empresaId, String serie, String tipoComprobante
    );

}
