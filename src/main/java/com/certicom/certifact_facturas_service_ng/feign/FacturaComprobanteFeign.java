package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.entity.SubidaRegistroArchivoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "comprobante-service", url = "http://localhost:8090")
public interface FacturaComprobanteFeign {

    @GetMapping("/api/data/invoice")
    List<ComprobanteInterDto> listarComprobantesConFiltros(
            @RequestParam(name = "rucEmisor", required = true) String rucEmisor,
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "idOficina", required = false) Integer idOficina,
            @RequestParam(name = "estadoSunat", required = false) String estadoSunat,
            @RequestParam(name = "pageNumber", required = true) Integer pageNumber,
            @RequestParam(name = "perPage", required = true) Integer perPage
    );

    @GetMapping("/api/data/user/{idUsuario}")
    UserInterDto obtenerUsuario(@PathVariable Long idUsuario);

    @GetMapping("/api/data/invoice/count-total")
    Integer contarComprobantes(
            @RequestParam(name = "rucEmisor", required = true) String rucEmisor,
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "idOficina", required = false) Integer idOficina,
            @RequestParam(name = "estadoSunat", required = false) String estadoSunat,
            @RequestParam(name = "pageNumber", required = true) Integer pageNumber,
            @RequestParam(name = "perPage", required = true) Integer perPage
    );

    @GetMapping("/api/data/invoice/cash-total")
    List<ComprobanteInterDto> obtenerTotalSolesGeneral(
            @RequestParam(name = "rucEmisor", required = true) String rucEmisor,
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "idOficina", required = false) Integer idOficina,
            @RequestParam(name = "estadoSunat", required = false) String estadoSunat,
            @RequestParam(name = "pageNumber", required = true) Integer pageNumber,
            @RequestParam(name = "perPage", required = true) Integer perPage
    );

    @GetMapping("/api/data/company/estado")
    public String obtenerEstadoEmpresaPorRuc(@RequestParam String rucEmisor);

    @GetMapping("/api/data/company/{ruc}")
    public EmpresaDto obtenerEmpresaPorRuc(@PathVariable String ruc);

    @GetMapping("/api/data/invoice")
    public Integer obtenerSiguienteNumeracionPorTipoComprobanteYSerieYRucEmisor(
            @RequestParam String tipoComprobante, @RequestParam String serie, @RequestParam String ruc
    );

    @GetMapping("/api/data/office")
    public OficinaDto obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
            @RequestParam Integer empresaId, @RequestParam String serie, @RequestParam String tipoComprobante
    );

    @PostMapping("/api/data/file")
    public SubidaRegistroArchivoEntity regitrarSubidaArchivo(@RequestBody SubidaRegistroArchivoDto subidaRegistroArchivoDto);

}
