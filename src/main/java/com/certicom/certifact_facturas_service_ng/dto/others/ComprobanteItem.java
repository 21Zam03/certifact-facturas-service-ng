package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComprobanteItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer numeroItem;
    private String codigoUnidadMedida;

    private BigDecimal cantidad;
    private String descripcion;
    private String codigoProducto;
    private String codigoProductoSunat;
    private String codigoProductoGS1;
    private BigDecimal valorUnitario;
    private BigDecimal valorVenta;
    private BigDecimal descuento;
    private String codigoDescuento;
    private BigDecimal precioVentaUnitario;

    private BigDecimal valorReferencialUnitario;
    private BigDecimal montoBaseIgv;
    private BigDecimal montoBaseIvap;
    private BigDecimal montoBaseExportacion;
    private BigDecimal montoBaseExonerado;
    private BigDecimal montoBaseInafecto;
    private BigDecimal montoBaseGratuito;
    private BigDecimal montoBaseIsc;
    private BigDecimal montoBaseOtrosTributos;
    private BigDecimal igv;
    private BigDecimal isc;
    private BigDecimal montoIcbper;
    private BigDecimal montoBaseIcbper;
    private BigDecimal ivap;
    private BigDecimal impuestoVentaGratuita;
    private BigDecimal otrosTributos;
    private BigDecimal porcentajeIgv;
    private BigDecimal porcentajeIvap;
    private BigDecimal porcentajeIsc;
    private BigDecimal porcentajeOtrosTributos;
    private BigDecimal porcentajeTributoVentaGratuita;
    private String codigoTipoAfectacionIGV;
    private String codigoTipoCalculoISC;
    private Long productoId;

    //DETRACCION 027

    private String detalleViajeDetraccion;
    private String ubigeoOrigenDetraccion;
    private String direccionOrigenDetraccion;
    private String ubigeoDestinoDetraccion;
    private String direccionDestinoDetraccion;
    private BigDecimal valorServicioTransporte;
    private BigDecimal valorCargaEfectiva;
    private BigDecimal valorCargaUtil;

    private String hidroMatricula;
    private String hidroEmbarcacion;
    private String hidroDescripcionTipo;
    private String hidroLugarDescarga;
    private String hidroFechaDescarga;
    private String hidroCantidad;

    private String unidadManejo;
    private String instruccionesEspeciales;
    private String marca;
    private String adicional;

/*
    public static List<ComprobanteItem> transformarAListaBean(List<ComprobanteDetalleEntity> lines) {
        List<ComprobanteItem> items = new ArrayList<>();
        lines.forEach(line -> {
            items.add(ComprobanteItem.builder()
                    .numeroItem(line.getNumeroItem())
                    .codigoUnidadMedida(line.getCodigoUnidadMedida())
                    .cantidad(line.getCantidad())
                    .descripcion(line.getDescripcion())
                    .codigoProducto(line.getCodigoProducto())
                    .codigoProductoSunat(line.getCodigoProductoSunat())
                    .codigoProductoGS1(line.getCodigoProductoGS1())
                    .valorUnitario(line.getValorUnitario())
                    .valorVenta(line.getValorVenta())
                    .descuento(line.getDescuento())
                    .codigoDescuento(line.getCodigoDescuento())
                    .precioVentaUnitario(line.getPrecioVentaUnitario())
                    .valorReferencialUnitario(line.getValorReferencialUnitario())
                    .codigoTipoAfectacionIGV(line.getCodigoTipoAfectacionIGV())
                    .codigoTipoCalculoISC(line.getCodigoTipoSistemaISC())
                    .igv(line.getAfectacionIGV())
                    .isc(line.getSistemaISC())
                    .otrosTributos(line.getOtrosTributos())
                    .ivap(line.getIvap())
                    .impuestoVentaGratuita(line.getTributoVentaGratuita())
                    .montoBaseIgv(line.getMontoBaseIgv())
                    .montoBaseExonerado(line.getMontoBaseExonerado())
                    .montoBaseExportacion(line.getMontoBaseExportacion())
                    .montoBaseGratuito(line.getMontoBaseGratuito())
                    .montoBaseInafecto(line.getMontoBaseInafecto())
                    .montoBaseIsc(line.getMontoBaseIsc())
                    .montoBaseIvap(line.getMontoBaseIvap())
                    .montoBaseOtrosTributos(line.getMontoBaseOtrosTributos())
                    .porcentajeIgv(line.getPorcentajeIgv())
                    .porcentajeIsc(line.getPorcentajeIsc())
                    .porcentajeIvap(line.getPorcentajeIvap())
                    .porcentajeOtrosTributos(line.getPorcentajeOtrosTributos())
                    .porcentajeTributoVentaGratuita(line.getPorcentajeTributoVentaGratuita())

                    .detalleViajeDetraccion(line.getDetalleViajeDetraccion())
                    .ubigeoOrigenDetraccion(line.getUbigeoOrigenDetraccion())
                    .direccionOrigenDetraccion(line.getDireccionOrigenDetraccion())
                    .ubigeoDestinoDetraccion(line.getUbigeoDestinoDetraccion())
                    .direccionDestinoDetraccion(line.getDireccionDestinoDetraccion())
                    .valorServicioTransporte(line.getValorServicioTransporte())
                    .valorCargaEfectiva(line.getValorCargaEfectiva())
                    .valorCargaUtil(line.getValorCargaUtil())

                    .hidroMatricula(line.getHidroMatricula())
                    .hidroCantidad(line.getHidroCantidad())
                    .hidroDescripcionTipo(line.getHidroDescripcionTipo())
                    .hidroEmbarcacion(line.getHidroEmbarcacion())
                    .hidroFechaDescarga(line.getHidroFechaDescarga())
                    .hidroLugarDescarga(line.getHidroLugarDescarga())
                    .montoIcbper(line.getMontoIcbper())
                    .montoBaseIcbper(line.getMontoBaseIcbper())
                    .unidadManejo(line.getUnidadManejo())
                    .instruccionesEspeciales(line.getInstruccionesEspeciales())
                    .marca(line.getMarca())
                    .build());
        });
        return items;
    }
*/
}
