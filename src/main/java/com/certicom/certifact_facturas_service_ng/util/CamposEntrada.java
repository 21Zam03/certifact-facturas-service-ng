package com.certicom.certifact_facturas_service_ng.util;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Value;

public abstract class CamposEntrada<T> extends JsonDeserializer<T> {

    @Value("${json.payment_voucher.input.tipoComprobante}")
    protected String tipoComprobanteLabel;
    @Value("${json.payment_voucher.input.serie}")
    protected String serieLabel;
    @Value("${json.payment_voucher.input.numero}")
    protected String numeroLabel;
    @Value("${json.payment_voucher.input.fechaEmision}")
    protected String fechaEmisionLabel;
    @Value("${json.payment_voucher.input.horaEmision}")
    protected String horaEmisionLabel;
    @Value("${json.payment_voucher.input.fechaVencimiento}")
    protected String fechaVencimientoLabel;
    @Value("${json.payment_voucher.input.codigoMoneda}")
    protected String codigoMonedaLabel;


    @Value("${json.payment_voucher.input.tipoTransaccion}")
    protected String tipoTransaccionLabel;
    @Value("${json.payment_voucher.input.montoPendiente}")
    protected String montoPendienteLabel;
    @Value("${json.payment_voucher.input.cantidadCuotas}")
    protected String cantidadCuotasLabel;
    @Value("${json.payment_voucher.input.pagoCuenta}")
    protected String pagoCuentaLabel;

    protected String idPaymentVoucherReferenceLabel;
    @Value("${json.payment_voucher.input.rucEmisor}")
    protected String rucEmisorLabel;
    @Value("${json.payment_voucher.input.codigoLocalAnexoEmisor}")
    protected String codigoLocalAnexoEmisorLabel;
    @Value("${json.payment_voucher.input.tipoDocumentoReceptor}")
    protected String tipoDocumentoReceptorLabel;
    @Value("${json.payment_voucher.input.numeroDocumentoReceptor}")
    protected String numeroDocumentoReceptorLabel;
    @Value("${json.payment_voucher.input.denominacionReceptor}")
    protected String denominacionReceptorLabel;

    @Value("${json.payment_voucher.input.direccionReceptor}")
    protected String direccionReceptorLabel;
    @Value("${json.payment_voucher.input.emailReceptor}")
    protected String emailReceptorLabel;
    @Value("${json.payment_voucher.input.idpay}")
    protected String idpayLabel;

    @Value("${json.payment_voucher.input.tipoDocumentoRelacionado}")
    protected String tipoDocumentoRelacionadoLabel;
    @Value("${json.payment_voucher.input.numeroDocumentoRelacionado}")
    protected String numeroDocumentoRelacionadoLabel;

    @Value("${json.payment_voucher.input.totalValorVentaExportacion}")
    protected String totalValorVentaExportacionlabel;
    @Value("${json.payment_voucher.input.totalValorVentaGravada}")
    protected String totalValorVentaGravadaLabel;
    @Value("${json.payment_voucher.input.totalValorVentaGravadaIVAP}")
    protected String totalValorVentaGravadaIVAPLabel;
    @Value("${json.payment_voucher.input.totalValorVentaInafecta}")
    protected String totalValorVentaInafectaLabel;
    @Value("${json.payment_voucher.input.totalValorVentaExonerada}")
    protected String totalValorVentaExoneradaLabel;
    @Value("${json.payment_voucher.input.totalValorVentaGratuita}")
    protected String totalValorVentaGratuitaLabel;
    @Value("${json.payment_voucher.input.totalValorBaseIsc}")
    protected String totalValorBaseIscLabel;
    @Value("${json.payment_voucher.input.totalValorBaseOtrosTributos}")
    protected String totalValorBaseOtrosTributosLabel;

    @Value("${json.payment_voucher.input.redondeoImporteTotal}")
    protected String montoRedondeoImporteTotalLabel;
    @Value("${json.payment_voucher.input.totalDescuento}")
    protected String totalDescuentoLabel;
    @Value("${json.payment_voucher.input.totalIgv}")
    protected String totalIgvLabel;
    @Value("${json.payment_voucher.input.totalIsc}")
    protected String totalIscLabel;
    @Value("${json.payment_voucher.input.totalImpOperGratuita}")
    protected String totalImpOperGratuitaLabel;
    @Value("${json.payment_voucher.input.totalOtrostributos}")
    protected String totalOtrostributosLabel;
    @Value("${json.payment_voucher.input.descuentoGlobales}")
    protected String descuentoGlobalesLabel;
    @Value("${json.payment_voucher.input.totalOtrosCargos}")
    protected String totalOtrosCargosLabel;
    @Value("${json.payment_voucher.input.totalAnticipos}")
    protected String totalAnticiposLabel;
    @Value("${json.payment_voucher.input.importeTotal}")
    protected String importeTotalLabel;
    @Value("${json.payment_voucher.input.tipoOperacion}")
    protected String tipoOperacionLabel;
    @Value("${json.payment_voucher.input.tipoNotaCredito}")
    protected String tipoNotaCreditoLabel;
    @Value("${json.payment_voucher.input.tipoNotaDebito}")
    protected String tipoNotaDebitoLabel;
    @Value("${json.payment_voucher.input.serieAfectado}")
    protected String serieAfectadoLabel;
    @Value("${json.payment_voucher.input.numeroAfectado}")
    protected String numeroAfectadoLabel;
    @Value("${json.payment_voucher.input.tipoComprobanteAfectado}")
    protected String tipoComprobanteAfectadoLabel;
    @Value("${json.payment_voucher.input.motivoNota}")
    protected String motivoNotaLabel;
    @Value("${json.payment_voucher.input.items}")
    protected String itemsLabel;

    @Value("${json.payment_voucher.input.anticipos}")
    protected String anticiposLabel;

    @Value("${json.payment_voucher.input.anticipo.serie}")
    protected String serieAnticipoLabel;
    @Value("${json.payment_voucher.input.anticipo.numero}")
    protected String numeroAnticipoLabel;
    @Value("${json.payment_voucher.input.anticipo.tipoDocumento}")
    protected String tipoDocumentoAnticipoLabel;
    @Value("${json.payment_voucher.input.anticipo.monto}")
    protected String montoAnticipadoLabel;

    @Value("${json.payment_voucher.input.guiasRelacionadas}")
    protected String guiasRelacionadasLabel;

    @Value("${json.payment_voucher.input.guiaRelacionada.codigoTipoGuia}")
    protected String codigoTipoGuiaLabel;
    @Value("${json.payment_voucher.input.guiaRelacionada.serieNumeroGuia}")
    protected String serieNumeroGuiaLabel;
    @Value("${json.payment_voucher.input.guiaRelacionada.idguiaremision}")
    protected String idguiaremisionLabel;


    @Value("${json.payment_voucher.input.ordenCompra}")
    protected String ordenCompraLabel;

    @Value("${json.payment_voucher.input.camposAdicionales}")
    protected String camposAdicionalesLabel;


    @Value("${json.payment_voucher.input.cuotas}")
    protected String cuotasLabel;


    @Value("${json.payment_voucher.input.campoAdicional.nombreCampo}")
    protected String nombreCampoAdicionalLabel;
    @Value("${json.payment_voucher.input.campoAdicional.valorCampo}")
    protected String valorCampoAdicionalLabel;

    @Value("${json.payment_voucher.input.cuota.numero}")
    protected String numeroCuotaLabel;
    @Value("${json.payment_voucher.input.cuota.monto}")
    protected String montoCuotaLabel;
    @Value("${json.payment_voucher.input.cuota.fecha}")
    protected String fechaCuotaLabel;

    @Value("${json.payment_voucher.item.input.codigoUnidadMedida}")
    protected String codigoUnidadMedidaLabel;
    @Value("${json.payment_voucher.item.input.cantidad}")
    protected String cantidadLabel;

    @Value("${json.payment_voucher.item.input.productoId}")
    protected String productoIdLabel;

    @Value("${json.payment_voucher.item.input.descripcion}")
    protected String descripcionLabel;


    @Value("${json.payment_voucher.item.input.codigoProducto}")
    protected String codigoProductoLabel;
    @Value("${json.payment_voucher.item.input.codigoProductoSunat}")
    protected String codigoProductoSunatLabel;
    @Value("${json.payment_voucher.item.input.hidroCantidad}")
    protected String hidroCantidadLabel;
    @Value("${json.payment_voucher.item.input.hidroDescripcionTipo}")
    protected String hidroDescripcionTipoLabel;
    @Value("${json.payment_voucher.item.input.hidroEmbarcacion}")
    protected String hidroEmbarcacionLabel;
    @Value("${json.payment_voucher.item.input.hidroFechaDescarga}")
    protected String hidroFechaDescargaLabel;
    @Value("${json.payment_voucher.item.input.hidroLugarDescarga}")
    protected String hidroLugarDescargaLabel;
    @Value("${json.payment_voucher.item.input.hidroMatricula}")
    protected String hidroMatriculaLabel;
    @Value("${json.payment_voucher.item.input.codigoProductoGS1}")
    protected String codigoProductoGS1Label;
    @Value("${json.payment_voucher.item.input.valorUnitario}")
    protected String valorUnitarioLabel;
    @Value("${json.payment_voucher.item.input.valorVenta}")
    protected String valorVentaLabel;
    @Value("${json.payment_voucher.item.input.descuento}")
    protected String descuentoLabel;
    @Value("${json.payment_voucher.item.input.codigoDescuento}")
    protected String codigoDescuentoLabel;
    @Value("${json.payment_voucher.item.input.precioVentaUnitario}")
    protected String precioVentaUnitarioLabel;
    @Value("${json.payment_voucher.item.input.valorReferencialUnitario}")
    protected String valorReferencialUnitarioLabel;

    @Value("${json.payment_voucher.item.input.montoBaseIgv}")
    protected String montoBaseIgvLabel;
    @Value("${json.payment_voucher.item.input.montoBaseIvap}")
    protected String montoBaseIvapLabel;
    @Value("${json.payment_voucher.item.input.montoBaseExportacion}")
    protected String montoBaseExportacionLabel;
    @Value("${json.payment_voucher.item.input.montoBaseExonerado}")
    protected String montoBaseExoneradoLabel;
    @Value("${json.payment_voucher.item.input.montoBaseInafecto}")
    protected String montoBaseInafectoLabel;
    @Value("${json.payment_voucher.item.input.montoBaseGratuito}")
    protected String montoBaseGratuitoLabel;
    @Value("${json.payment_voucher.item.input.montoBaseIsc}")
    protected String montoBaseIscLabel;
    @Value("${json.payment_voucher.item.input.montoBaseIcbper}")
    protected String montoBaseIcbperLabel;
    @Value("${json.payment_voucher.item.input.montoIcbper}")
    protected String montoIcbperLabel;
    @Value("${json.payment_voucher.item.input.montoBaseOtrosTributos}")
    protected String montoBaseOtrosTributosLabel;

    @Value("${json.payment_voucher.item.input.impuestoVentaGratuita}")
    protected String impuestoVentaGratuitaLabel;
    @Value("${json.payment_voucher.item.input.otrosTributos}")
    protected String otrosTributosLabel;
    @Value("${json.payment_voucher.item.input.ivap}")
    protected String ivapLabel;
    @Value("${json.payment_voucher.item.input.igv}")
    protected String igvLabel;

    @Value("${json.payment_voucher.item.input.porcentajeIgv}")
    protected String porcentajeIgvLabel;
    @Value("${json.payment_voucher.item.input.porcentajeIvap}")
    protected String porcentajeIvapLabel;
    @Value("${json.payment_voucher.item.input.porcentajeIsc}")
    protected String porcentajeIscLabel;
    @Value("${json.payment_voucher.item.input.porcentajeOtrosTributos}")
    protected String porcentajeOtrosTributosLabel;
    @Value("${json.payment_voucher.item.input.porcentajeTributoVentaGratuita}")
    protected String porcentajeTributoVentaGratuitaLabel;

    @Value("${json.payment_voucher.item.input.tipoAfectacionIGV}")
    protected String tipoAfectacionIGVLabel;
    @Value("${json.payment_voucher.item.input.isc}")
    protected String iscLabel;
    @Value("${json.payment_voucher.item.input.tipoCalculoISC}")
    protected String tipoCalculoISCLabel;

    //Detracciones
    @Value("${json.payment_voucher.input.codigoMedioPago}")
    protected String codigoMedioPagoLabel;

    @Value("${json.payment_voucher.input.cuentaFinancieraBeneficiario}")
    protected String cuentaFinancieraBeneficiarioLabel;

    @Value("${json.payment_voucher.input.codigoBienDetraccion}")
    protected String codigoBienDetraccionLabel;

    @Value("${json.payment_voucher.input.porcentajeDetraccion}")
    protected String porcentajeDetraccionLabel;

    @Value("${json.payment_voucher.input.porcentajeRetencion}")
    protected String porcentajeRetencionLabel;

    @Value("${json.payment_voucher.input.montoRetencion}")
    protected String montoRetencionLabel;

    @Value("${json.payment_voucher.input.montoDetraccion}")
    protected String montoDetraccionLabel;

    @Value("${json.payment_voucher.input.detraccion}")
    protected String detraccionLabel;

    @Value("${json.payment_voucher.input.retencion}")
    protected String retencionLabel;

    // retention 027
    @Value("${json.payment_voucher.item.input.detalleViajeDetraccion}")
    protected String detalleViajeDetraccionLabel;

    @Value("${json.payment_voucher.item.input.ubigeoOrigenDetraccion}")
    protected String ubigeoOrigenDetraccionLabel;

    @Value("${json.payment_voucher.item.input.direccionOrigenDetraccion}")
    protected String direccionOrigenDetraccionLabel;

    @Value("${json.payment_voucher.item.input.ubigeoDestinoDetraccion}")
    protected String ubigeoDestinoDetraccionLabel;

    @Value("${json.payment_voucher.item.input.direccionDestinoDetraccion}")
    protected String direccionDestinoDetraccionLabel;

    @Value("${json.payment_voucher.item.input.valorServicioTransporte}")
    protected String valorServicioTransporteLabel;

    @Value("${json.payment_voucher.item.input.valorCargaEfectiva}")
    protected String valorCargaEfectivaLabel;

    @Value("${json.payment_voucher.item.input.valorCargaUtil}")
    protected String valorCargaUtilLabel;

    @Value("${json.payment_voucher.item.input.unidadManejo}")
    protected String unidadManejoLabel;

    @Value("${json.payment_voucher.item.input.instruccionesEspeciales}")
    protected String instruccionesEspecialesLabel;

    @Value("${json.payment_voucher.item.input.marca}")
    protected String marcaLabel;

    @Value("${json.payment_voucher.item.input.adicional}")
    protected String adicionalLabel;

}
