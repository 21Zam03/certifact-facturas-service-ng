package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090")
public interface InvoicePaymentVoucherFeign {

    @GetMapping("/api/invoice-sp/payment-voucher/")
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

    @GetMapping("/api/invoice-sp/payment-voucher/count-total")
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

    @GetMapping("/api/invoice-sp/payment-voucher/cash-total")
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

    @PutMapping("/api/invoice-sp/payment-voucher/state-1")
    public int updateStatePaymentVoucher(
            @RequestParam Long idPaymentVoucher, @RequestParam String codigo, @RequestParam String messageResponse,
            @RequestParam String codesResponse
    );

    @PutMapping("/api/invoice-sp/payment-voucher/state-2")
    public int updateStatePaymentVoucher(
            @RequestParam Long idPaymentVoucher, @RequestParam String codigo, @RequestParam String estadoEnSunat,
            @RequestParam String messageResponse, @RequestParam String codesResponse
    );

    @GetMapping("/api/invoice-sp/payment-voucher/document")
    public PaymentVoucherEntity getPaymentVoucherByIdentificadorDocumento(@RequestParam String identificadorDocumento);

    @GetMapping("/api/invoice-sp/payment-voucher/number")
    public Integer obtenerSiguienteNumeracionPorTipoComprobanteYSerieYRucEmisor(
            @RequestParam String tipoComprobante, @RequestParam String serie, @RequestParam String ruc
    );

    @PostMapping("/api/invoice-sp/payment-voucher")
    public PaymentVoucherEntity savePaymentVoucher(@RequestBody PaymentVoucherEntity entity);

    @GetMapping("/api/invoice-sp/payment-voucher/basic")
    public PaymentVoucherEntity findPaymentVoucherByRucAndTipoComprobanteAndSerieAndNumero(
            @RequestParam String rucEmisor, @RequestParam String tipoComprobante,
            @RequestParam String serie, @RequestParam Integer numero);

    @GetMapping("/api/invoice-sp/payment-voucher/{id}")
    public PaymentVoucherEntity findPaymentVoucherById(@PathVariable Long id);

    @GetMapping("/api/invoice-sp/user/{idUsuario}")
    UserInterDto obtenerUsuario(@PathVariable Long idUsuario);

    @GetMapping("/api/invoice-sp/user/{username}")
    public UserEntity findByUserByUsername(@PathVariable String username);

    @GetMapping("/api/invoice-sp/company/state")
    public String getStateFromCompanyByRuc(@RequestParam String rucEmisor);

    @GetMapping("/api/invoice-sp/company/{ruc}")
    public CompanyDto findCompanyByRuc(@PathVariable String ruc);

    @GetMapping("/api/invoice-sp/office")
    public OficinaDto obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
            @RequestParam Integer empresaId, @RequestParam String serie, @RequestParam String tipoComprobante
    );

    @PostMapping("/api/invoice-sp/file")
    public RegisterFileUploadEntity saveRegisterFileUpload(@RequestBody RegisterFileUploadDto registerFileUploadDto);

    @GetMapping("/api/invoice-sp/additional-field")
    public Integer obtenerCampoAdicionalIdPorNombre(@RequestParam String nombreCampo);

    @GetMapping("/api/invoice-sp/tmpVoucher/{id}")
    public TmpVoucherSendBillEntity findTmpVoucherByIdPaymentVoucher(@PathVariable Long id);

    @PostMapping("/api/invoice-sp/tmpVoucher")
    public int saveTmpVoucher(@RequestBody TmpVoucherSendBillEntity tmpVoucherSendBillEntity);

    @GetMapping("/api/invoice-sp/payment-voucher/extended")
    public PaymentVoucherDto findPaymentVoucherByRucAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento
            (@RequestParam String finalRucEmisor, @RequestParam String tipoComprobante,
             @RequestParam String serieDocumento, @RequestParam Integer numeroDocumento);

    @PutMapping("/api/invoice-sp/tmpVoucher/status")
    public int updateStatusVoucherTmp(@RequestParam Long identificador, @RequestParam String estado);

    @GetMapping("/api/invoice-sp/register-file-upload/")
    public RegisterFileUploadDto findFirst1ByPaymentVoucherIdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc
            (@RequestParam Long idPayment, @RequestParam String tipoArchivo, @RequestParam String estadoArchivo);

    @GetMapping("/api/invoice-sp/ose")
    public OseDto findOseByRucInter(@RequestParam String ruc);

    @GetMapping("/api/invoice-sp/error-catalog")
    public ErrorEntity findFirst1ByCodeAndDocument(@RequestParam String codigoRespuesta, @RequestParam String tipoDocumento);

    @DeleteMapping("/api/invoice-sp/tmpVoucher")
    public int deleteTmpVoucherById(@RequestParam Long tmpVoucherId);

}
