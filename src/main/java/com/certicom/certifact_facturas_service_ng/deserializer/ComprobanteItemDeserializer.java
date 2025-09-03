package com.certicom.certifact_facturas_service_ng.deserializer;

import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteItem;
import com.certicom.certifact_facturas_service_ng.exceptions.DeserializerException;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class ComprobanteItemDeserializer extends CamposEntrada<ComprobanteItem> {


    @Override
    public ComprobanteItem deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ComprobanteItem objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        String codigoTipoAfectacionIGV;
        String codigoTipoCalculoISC;
        String codigoProductoSunat = null;
        String hidroCantidad;
        String hidroDescripcionTipo;
        String hidroEmbarcacion;
        String hidroFechaDescarga;
        String hidroLugarDescarga;
        String hidroMatricula;
        String codigoProductoGS1;
        String codigoDescuento;
        String codigoUnidadMedida;
        String codigoProducto;
        String descripcion;
        BigDecimal valorReferencialUnitario = null;
        BigDecimal precioVentaUnitario = null;
        BigDecimal valorUnitario = null;
        BigDecimal valorVenta = null;
        BigDecimal descuento = null;
        BigDecimal cantidad = null;
        BigDecimal igv = null;
        BigDecimal isc = null;
        BigDecimal ivap = null;
        BigDecimal impuestoVentaGratuita = null;
        BigDecimal otrosTributos = null;

        BigDecimal porcentajeIgv = null;
        BigDecimal porcentajeIvap = null;
        BigDecimal porcentajeIsc = null;
        BigDecimal porcentajeOtrosTributos = null;
        BigDecimal porcentajeTributoVentaGratuita = null;

        BigDecimal montoBaseIgv = null;
        BigDecimal montoBaseIvap = null;
        BigDecimal montoBaseExportacion = null;
        BigDecimal montoBaseExonerado = null;
        BigDecimal montoBaseInafecto = null;
        BigDecimal montoBaseGratuito = null;
        BigDecimal montoBaseIsc = null;
        BigDecimal montoIcbper = null;
        BigDecimal montoBaseIcbper = null;

        BigDecimal montoBaseOtrosTributos = null;

        //DETRACCION 027
        String detalleViajeDetraccion;
        String ubigeoOrigenDetraccion;
        String direccionOrigenDetraccion;
        String ubigeoDestinoDetraccion;
        String direccionDestinoDetraccion;
        BigDecimal valorServicioTransporte = null;
        BigDecimal valorCargaEfectiva = null;
        BigDecimal valorCargaUtil = null;

        String unidadManejo = null;
        String instruccionesEspeciales = null;
        String marca = null;
        String adicional = null;
        String mensajeError;
        Long productId;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(codigoUnidadMedidaLabel);
        codigoUnidadMedida = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(cantidadLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                cantidad = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + cantidadLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(descripcionLabel);
        descripcion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(codigoProductoLabel);
        codigoProducto = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(productoIdLabel);
        productId = campoTrama != null && !campoTrama.isNull() ? campoTrama.longValue() : null;

        campoTrama = trama.get(codigoProductoSunatLabel);
        codigoProductoSunat = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(hidroCantidadLabel);
        hidroCantidad = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(hidroDescripcionTipoLabel);
        hidroDescripcionTipo = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(hidroEmbarcacionLabel);
        hidroEmbarcacion = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(hidroFechaDescargaLabel);
        hidroFechaDescarga = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(hidroLugarDescargaLabel);
        hidroLugarDescarga = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(hidroMatriculaLabel);
        hidroMatricula = (campoTrama != null) ? campoTrama.textValue() : null;


        campoTrama = trama.get(codigoProductoGS1Label);
        codigoProductoGS1 = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(valorUnitarioLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                valorUnitario = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + valorUnitarioLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(valorVentaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                valorVenta = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + valorVentaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(descuentoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                descuento = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + descuentoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(codigoDescuentoLabel);
        codigoDescuento = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(precioVentaUnitarioLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                precioVentaUnitario = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + precioVentaUnitarioLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(valorReferencialUnitarioLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                valorReferencialUnitario = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + valorReferencialUnitarioLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(montoBaseIgvLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseIgv = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseIgvLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseIvapLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseIvap = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseIvapLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseExportacionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseExportacion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseExportacionLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseExoneradoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseExonerado = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseExoneradoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseInafectoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseInafecto = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseInafectoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseGratuitoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseGratuito = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseGratuitoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseIscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseIsc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseIcbperLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseIcbper = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoIcbperLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoIcbper = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseOtrosTributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseOtrosTributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseOtrosTributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(igvLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                igv = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + igvLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(ivapLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                ivap = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + ivapLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(impuestoVentaGratuitaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                impuestoVentaGratuita = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + impuestoVentaGratuitaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(otrosTributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                otrosTributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + otrosTributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(porcentajeIgvLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeIgv = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeIgvLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeIvapLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeIvap = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeIvapLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeIscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeIsc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeOtrosTributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeOtrosTributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeOtrosTributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeTributoVentaGratuitaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeTributoVentaGratuita = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeTributoVentaGratuitaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(iscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                isc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + iscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(tipoCalculoISCLabel);
        codigoTipoCalculoISC = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(tipoAfectacionIGVLabel);
        codigoTipoAfectacionIGV = (campoTrama != null) ? campoTrama.textValue() : null;


        campoTrama = trama.get(detalleViajeDetraccionLabel);
        detalleViajeDetraccion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(ubigeoOrigenDetraccionLabel);
        ubigeoOrigenDetraccion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(direccionOrigenDetraccionLabel);
        direccionOrigenDetraccion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(ubigeoDestinoDetraccionLabel);
        ubigeoDestinoDetraccion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(direccionDestinoDetraccionLabel);
        direccionDestinoDetraccion = (campoTrama != null) ? campoTrama.textValue() : null;


        campoTrama = trama.get(valorServicioTransporteLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                valorServicioTransporte = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + valorServicioTransporteLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(valorCargaEfectivaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                valorCargaEfectiva = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + valorCargaEfectivaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(valorCargaUtilLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                valorCargaUtil = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + valorCargaUtilLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(unidadManejoLabel);
        unidadManejo = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(instruccionesEspecialesLabel);
        instruccionesEspeciales = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(marcaLabel);
        marca = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(adicionalLabel);
        adicional = (campoTrama != null) ? campoTrama.textValue() : null;

        objectResult = new ComprobanteItem();
        objectResult.setCodigoUnidadMedida(codigoUnidadMedida);
        objectResult.setCantidad(cantidad);
        objectResult.setProductoId(productId);
        objectResult.setDescripcion(descripcion);
        objectResult.setCodigoProducto(codigoProducto);
        objectResult.setCodigoProductoSunat(codigoProductoSunat);
        objectResult.setHidroCantidad(hidroCantidad);
        objectResult.setHidroDescripcionTipo(hidroDescripcionTipo);
        objectResult.setHidroEmbarcacion(hidroEmbarcacion);
        objectResult.setHidroFechaDescarga(hidroFechaDescarga);
        objectResult.setHidroLugarDescarga(hidroLugarDescarga);
        objectResult.setHidroMatricula(hidroMatricula);
        objectResult.setCodigoProductoGS1(codigoProductoGS1);
        objectResult.setValorUnitario(valorUnitario);
        objectResult.setValorVenta(valorVenta);
        objectResult.setDescuento(descuento);
        objectResult.setCodigoDescuento(codigoDescuento);
        objectResult.setPrecioVentaUnitario(precioVentaUnitario);
        objectResult.setValorReferencialUnitario(valorReferencialUnitario);
        objectResult.setMontoBaseExonerado(montoBaseExonerado);
        objectResult.setMontoBaseExportacion(montoBaseExportacion);
        objectResult.setMontoBaseGratuito(montoBaseGratuito);
        objectResult.setMontoBaseIgv(montoBaseIgv);
        objectResult.setMontoBaseInafecto(montoBaseInafecto);
        objectResult.setMontoBaseIsc(montoBaseIsc);
        objectResult.setMontoBaseIcbper(montoBaseIcbper);
        objectResult.setMontoIcbper(montoIcbper);
        objectResult.setMontoBaseIvap(montoBaseIvap);
        objectResult.setMontoBaseOtrosTributos(montoBaseOtrosTributos);
        objectResult.setIgv(igv);
        objectResult.setIsc(isc);
        objectResult.setIvap(ivap);
        objectResult.setImpuestoVentaGratuita(impuestoVentaGratuita);
        objectResult.setOtrosTributos(otrosTributos);
        objectResult.setPorcentajeIgv(porcentajeIgv);
        objectResult.setPorcentajeIsc(porcentajeIsc);
        objectResult.setPorcentajeIvap(porcentajeIvap);
        objectResult.setPorcentajeOtrosTributos(porcentajeOtrosTributos);
        objectResult.setPorcentajeTributoVentaGratuita(porcentajeTributoVentaGratuita);
        objectResult.setCodigoTipoAfectacionIGV(codigoTipoAfectacionIGV);
        objectResult.setCodigoTipoCalculoISC(codigoTipoCalculoISC);

        //detraccion 027
        objectResult.setDetalleViajeDetraccion(detalleViajeDetraccion);
        objectResult.setUbigeoOrigenDetraccion(ubigeoOrigenDetraccion);
        objectResult.setDireccionOrigenDetraccion(direccionOrigenDetraccion);
        objectResult.setUbigeoDestinoDetraccion(ubigeoDestinoDetraccion);
        objectResult.setDireccionDestinoDetraccion(direccionDestinoDetraccion);
        objectResult.setValorServicioTransporte(valorServicioTransporte);
        objectResult.setValorCargaEfectiva(valorCargaEfectiva);
        objectResult.setValorCargaUtil(valorCargaUtil);

        objectResult.setUnidadManejo(unidadManejo);
        objectResult.setInstruccionesEspeciales(instruccionesEspeciales);
        objectResult.setMarca(marca);
        objectResult.setAdicional(adicional);
        return objectResult;
    }

}
