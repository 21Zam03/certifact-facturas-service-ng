package com.certicom.certifact_facturas_service_ng.dto.request;

import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteItem;
import com.certicom.certifact_facturas_service_ng.validation.anottations.NumeroComprobante;
import com.certicom.certifact_facturas_service_ng.validation.anottations.RucActivo;
import com.certicom.certifact_facturas_service_ng.validation.anottations.SerieFactura;
import com.certicom.certifact_facturas_service_ng.validation.anottations.TipoComprobanteFactura;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ComprobanteRequest {

    //EMISOR
    @RucActivo
    @JsonProperty("emisor_ruc")
    private String rucEmisor;

    //DOCUMENTO
    @TipoComprobanteFactura
    @JsonProperty("documento_tipo_comprobante")
    private String tipoComprobante;
    @SerieFactura
    @JsonProperty("documento_serie")
    private String serie;
    @NumeroComprobante
    @JsonProperty("documento_numero")
    private Integer numero;
    @JsonProperty("documento_fecha_emision")
    private String fechaEmision;
    @JsonProperty("documento_hora_emision")
    private String horaEmision;
    @JsonProperty("documento_tipo_moneda")
    private String codigoMoneda;
    @JsonProperty("documento_fecha_vencimiento")
    private String fechaVencimiento;
    @JsonProperty("tipo_operacion")
    private String codigoTipoOperacion;

    //@JsonProperty("documento_numero")
    //private String codigoTipoOperacionCatalogo51;

    //RECEPTOR
    @JsonProperty("cliente_tipo_documento")
    private String tipoDocumentoReceptor;

    @JsonProperty("cliente_numero_documento")
    private String numeroDocumentoReceptor;
    @JsonProperty("cliente_denominacion")
    private String denominacionReceptor;
    @JsonProperty("cliente_direccion")
    private String direccionReceptor;
    @JsonProperty("cliente_email")
    private String emailReceptor;

    //MONTOS
    @JsonProperty("total_venta_gravadas")
    private BigDecimal totalValorVentaGravada;
    @JsonProperty("total_igv")
    private BigDecimal totalIgv;
    @JsonProperty("importe_total")
    private BigDecimal importeTotalVenta;

    //PRODUCTOS
    private List<ComprobanteItem> items;

}
