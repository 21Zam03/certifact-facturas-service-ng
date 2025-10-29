package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.ErrorCatalogModel;
import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.PaymentVoucherDtoFilter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "paymentVoucher")
public interface PaymentVoucherFeign {

    /**
     * Obtiene la lista de comprobantes electrónicos emitidos por un emisor,
     * permitiendo aplicar filtros de búsqueda y paginación.
     *
     * <p>Los filtros disponibles permiten consultar por rango de fechas de emisión,
     * tipo de comprobante, datos del receptor, serie, número, oficina y estado en SUNAT.</p>
     *
     * @param rucEmisor              RUC del emisor (obligatorio).
     * @param filtroDesde            Fecha de inicio del rango de búsqueda en formato {@code yyyy-MM-dd} (obligatorio).
     * @param filtroHasta            Fecha de fin del rango de búsqueda en formato {@code yyyy-MM-dd} (obligatorio).
     * @param filtroTipoComprobante  Código del tipo de comprobante (opcional).
     * @param filtroRuc              RUC del receptor (opcional).
     * @param filtroSerie            Serie del comprobante (opcional).
     * @param filtroNumero           Número del comprobante (opcional).
     * @param idOficina              Identificador de la oficina asociada al comprobante (opcional).
     * @param estadoSunat            Estado en SUNAT del comprobante (ej. ACEPTADO, RECHAZADO, PENDIENTE) (opcional).
     * @param pageNumber             Número de página para la paginación (obligatorio).
     * @param perPage                Cantidad de registros por página (obligatorio).
     *
     * @return Lista paginada de comprobantes que cumplen con los filtros aplicados.
     */
    @GetMapping("/api/payment-voucher")
    List<PaymentVoucherDtoFilter> listPaymentVoucherWithFilter(
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

    /**
     * Obtiene el número total de comprobantes electrónicos registrados
     * por un emisor, aplicando filtros de búsqueda específicos.
     *
     * <p>Este método consume el servicio remoto de facturación a través de Feign
     * y retorna únicamente la cantidad de comprobantes que cumplen las condiciones.</p>
     *
     * @param rucEmisor              RUC del emisor (obligatorio).
     * @param filtroDesde            Fecha de inicio del rango de búsqueda en formato {@code yyyy-MM-dd} (obligatorio).
     * @param filtroHasta            Fecha de fin del rango de búsqueda en formato {@code yyyy-MM-dd} (obligatorio).
     * @param filtroTipoComprobante  Código del tipo de comprobante (opcional).
     * @param filtroRuc              RUC del receptor (opcional).
     * @param filtroSerie            Serie del comprobante (opcional).
     * @param filtroNumero           Número del comprobante (opcional).
     * @param idOficina              Identificador de la oficina asociada al comprobante (opcional).
     * @param estadoSunat            Estado en SUNAT del comprobante (ej. ACEPTADO, RECHAZADO, PENDIENTE) (opcional).
     * @param pageNumber             Número de página para la paginación (obligatorio).
     * @param perPage                Cantidad de registros por página (obligatorio).
     *
     * @return Cantidad total de comprobantes que cumplen con los filtros aplicados.
     */
    @GetMapping("/api/payment-voucher/count-total")
    Integer countPaymentVoucher(
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


    @GetMapping("/api/payment-voucher/cash-total")
    List<PaymentVoucherDtoFilter> getTotalSoles(
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

    @PutMapping("/api/payment-voucher/state-1")
    public int updateStatePaymentVoucher(
            @RequestParam Long idPaymentVoucher, @RequestParam String codigo, @RequestParam String messageResponse,
            @RequestParam String codesResponse
    );

    @PutMapping("/api/payment-voucher/state-2")
    public int updateStatePaymentVoucher(
            @RequestParam Long idPaymentVoucher, @RequestParam String codigo, @RequestParam String estadoEnSunat,
            @RequestParam String messageResponse, @RequestParam String codesResponse
    );

    @GetMapping("/api/payment-voucher/id-document")
    public PaymentVoucherDto getPaymentVoucherByIdentificadorDocumento(@RequestParam String idDocumento);

    @GetMapping("/api/payment-voucher/number")
    public Integer obtenerSiguienteNumeracionPorTipoComprobanteYSerieYRucEmisor(
            @RequestParam String tipoComprobante, @RequestParam String serie, @RequestParam String ruc
    );

    @PostMapping("/api/payment-voucher")
    public PaymentVoucherDto savePaymentVoucher(@RequestBody PaymentVoucherDto entity);

    @GetMapping("/api/payment-voucher/parameters")
    public PaymentVoucherDto findPaymentVoucherByRucAndTipoComprobanteAndSerieAndNumero(
            @RequestParam String rucEmisor, @RequestParam String tipoComprobante,
            @RequestParam String serie, @RequestParam Integer numero);

    @GetMapping("/api/payment-voucher/{id}")
    public PaymentVoucherDto findPaymentVoucherById(@PathVariable Long id);

    @GetMapping("/api/payment-voucher/parameters-dto")
    public PaymentVoucherDto findPaymentVoucherByRucAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento
            (@RequestParam String finalRucEmisor, @RequestParam String tipoComprobante,
             @RequestParam String serieDocumento, @RequestParam Integer numeroDocumento);

    @GetMapping("/api/invoice-sp/error-catalog")
    public ErrorCatalogModel findFirst1ByCodeAndDocument(@RequestParam String codigoRespuesta, @RequestParam String tipoDocumento);

    @GetMapping("/api/payment-voucher/id-document")
    public PaymentVoucherDto getIdentificadorDocument(@RequestParam String idDocumento);

    @PutMapping("/api/payment-voucher/state-3")
    public int updateStateToSendSunatForVoidedDocuments(
            @RequestParam List<String> identificadorComprobantes,
            @RequestParam String estadoPendienteAnulacion,
            @RequestParam String usuario,
            @RequestParam Timestamp fechaModificacion);

    @GetMapping("/api/payment-voucher/idpaymentvoucheranduuid")
    public PaymentVoucherDto findByIdPaymentVoucherAndUuid(@RequestParam Long id, @RequestParam String uuid);

    @GetMapping("/api/payment-voucher/lastnumber")
    Integer getUltimoNumeroForNumeracion(
            @RequestParam String tipoDocumento,
            @RequestParam String serie,
            @RequestParam String ruc);

}
