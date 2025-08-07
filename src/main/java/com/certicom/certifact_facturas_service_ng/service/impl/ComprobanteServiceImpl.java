package com.certicom.certifact_facturas_service_ng.service.impl;

import com.google.common.collect.ImmutableMap;
import com.certicom.certifact_facturas_service_ng.dto.model.ComprobanteInterDto;
import com.certicom.certifact_facturas_service_ng.dto.model.UserInterDto;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
import com.certicom.certifact_facturas_service_ng.feign.ComprobanteFeign;
import com.certicom.certifact_facturas_service_ng.service.ComprobanteService;
import com.certicom.certifact_facturas_service_ng.util.UtilDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprobanteServiceImpl implements ComprobanteService {

    private final ComprobanteFeign comprobanteFeign;

    @Override
    public Map<String, Object> obtenerComprobantesEstadoPorFiltro(
            String fechaEmisionDesde, String fechaEmisionHasta, String filtroTipoComprobante, String filtroRuc, String filtroSerie,
            Integer filtroNumero, Integer pageNumber, Integer perPage, Integer estadoSunats, Long idUsuario) {
        Integer idOficina = null;
        Integer numPagina = null;
        Date filtroDesde = null;
        Date filtroHasta = null;
        String estadoSunat = null;

        List<ComprobanteInterDto> result = null;
        Integer cantidad = null;
        List<ComprobanteInterDto> tsolespayment = null;
        BigDecimal tsolesnew = BigDecimal.ZERO;
        BigDecimal tdolaresnew = BigDecimal.ZERO;
        BigDecimal teurosnew = BigDecimal.ZERO;
        ComprobanteInterDto comprobanteMonedaSol = null;
        ComprobanteInterDto comprobanteMonedaDolar = null;
        ComprobanteInterDto comprobanteMonedaEur = null;


        try {
            UserInterDto usuarioLogueado = comprobanteFeign.obtenerUsuario(idUsuario);
            if(usuarioLogueado == null) {
                throw new ExcepcionNegocio("Usuario no encontrado");
            }
            if(usuarioLogueado.getIdUsuario()!=null){
                idOficina = usuarioLogueado.getIdOficina();
            }
            numPagina = (pageNumber-1) * perPage;
            filtroDesde = UtilDate.stringToDate(fechaEmisionDesde, "dd-MM-yyyy");
            filtroHasta = UtilDate.stringToDate(fechaEmisionHasta, "dd-MM-yyyy");
            if(filtroNumero == null) filtroNumero = 0;
            if(idOficina == null) idOficina = 0;
            estadoSunat = estadoSunats.toString();

            result = comprobanteFeign.listarComprobantesConFiltros(usuarioLogueado.getRuc(), filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat);
            cantidad = comprobanteFeign.contarComprobantes(usuarioLogueado.getRuc(), filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat);
            tsolespayment = comprobanteFeign.obtenerTotalSolesGeneral(usuarioLogueado.getRuc(), filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat);

            comprobanteMonedaSol = tsolespayment.stream().filter(f -> f.getCodigoMoneda().equals("")).findFirst().orElse(null);
            if(comprobanteMonedaSol!=null) {
                tsolesnew = comprobanteMonedaSol.getMontoImporteTotalVenta()!=null?comprobanteMonedaSol.getMontoImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP):BigDecimal.ZERO;
            }
            comprobanteMonedaDolar = tsolespayment.stream().filter(f -> f.getCodigoMoneda().equals("")).findFirst().orElse(null);
            if (comprobanteMonedaDolar!=null) {
                tdolaresnew = comprobanteMonedaDolar.getMontoImporteTotalVenta()!=null?comprobanteMonedaDolar.getMontoImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP):BigDecimal.ZERO;
            }
            comprobanteMonedaEur = tsolespayment.stream().filter(f -> f.getCodigoMoneda().equals("")).findFirst().orElse(null);
            if (comprobanteMonedaEur!=null) {
                teurosnew = comprobanteMonedaEur.getMontoImporteTotalVenta()!=null?comprobanteMonedaEur.getMontoImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP):BigDecimal.ZERO;
            }
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage());
        }
        return ImmutableMap.of("comprobantesList", result, "total", cantidad, "totalsoles", tsolesnew,
                "totaldolares", tdolaresnew, "totaleuros", teurosnew);
    }

}
