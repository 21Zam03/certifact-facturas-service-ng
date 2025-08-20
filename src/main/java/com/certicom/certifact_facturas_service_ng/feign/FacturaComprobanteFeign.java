package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.entity.PaymentVoucherEntity;
import com.certicom.certifact_facturas_service_ng.entity.SubidaRegistroArchivoEntity;
import com.certicom.certifact_facturas_service_ng.entity.TmpVoucherSendBillEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090")
public interface FacturaComprobanteFeign {

    @GetMapping("/api/invoice-sp")
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

    @GetMapping("/api/invoice-sp/user/{idUsuario}")
    UserInterDto obtenerUsuario(@PathVariable Long idUsuario);

    @GetMapping("/api/invoice-sp/count-total")
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

    @GetMapping("/api/invoice-sp/cash-total")
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

    @GetMapping("/api/invoice-sp/company/state")
    public String obtenerEstadoEmpresaPorRuc(@RequestParam String rucEmisor);

    @GetMapping("/api/invoice-sp/company/{ruc}")
    public EmpresaDto obtenerEmpresaPorRuc(@PathVariable String ruc);

    @GetMapping("/api/invoice-sp/number")
    public Integer obtenerSiguienteNumeracionPorTipoComprobanteYSerieYRucEmisor(
            @RequestParam String tipoComprobante, @RequestParam String serie, @RequestParam String ruc
    );

    @GetMapping("/api/invoice-sp/office")
    public OficinaDto obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
            @RequestParam Integer empresaId, @RequestParam String serie, @RequestParam String tipoComprobante
    );

    @PostMapping("/api/invoice-sp/file")
    public SubidaRegistroArchivoEntity regitrarSubidaArchivo(@RequestBody SubidaRegistroArchivoDto subidaRegistroArchivoDto);

    @PostMapping("/api/invoice-sp")
    public PaymentVoucherEntity registrarComprobante(@RequestBody PaymentVoucherEntity entity);

    @GetMapping("/api/invoice-sp/additional-field")
    public Integer obtenerCampoAdicionalIdPorNombre(@RequestParam String nombreCampo);

    @GetMapping("/api/invoice-sp/payment-voucher")
    public PaymentVoucherEntity findPaymentVoucherByRucAndTipoComprobanteAndSerieAndNumero(
            @RequestParam String rucEmisor, @RequestParam String tipoComprobante,
            @RequestParam String serie, @RequestParam Integer numero);

    @GetMapping("/api/invoice-sp/tmpVoucher/{id}")
    public TmpVoucherSendBillEntity findTmpVoucherByIdPaymentVoucher(@PathVariable Long id);

}
