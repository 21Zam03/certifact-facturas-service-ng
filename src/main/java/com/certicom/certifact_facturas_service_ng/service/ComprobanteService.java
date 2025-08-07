package com.certicom.certifact_facturas_service_ng.service;

import java.util.Map;

public interface ComprobanteService {

    Map<String, Object> obtenerComprobantesEstadoPorFiltro(
            String filtroDesde, String filtroHasta,
            String filtroTipoComprobante, String filtroRuc, String filtroSerie, Integer filtroNumero,
            Integer pageNumber, Integer perPage, Integer estadoSunat, Long idUsuario);

}
