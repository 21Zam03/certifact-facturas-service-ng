package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.entity.ComprobanteEntity;
import com.certicom.certifact_facturas_service_ng.entity.SubidaRegistroArchivoEntity;
import com.certicom.certifact_facturas_service_ng.enums.OperacionLogEnum;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParametro;
import com.google.common.collect.ImmutableMap;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
import com.certicom.certifact_facturas_service_ng.feign.ComprobanteFeign;
import com.certicom.certifact_facturas_service_ng.service.ComprobanteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprobanteServiceImpl implements ComprobanteService {

    private static final String CODSOLES = "PEN";
    private static final String CODDOLAR = "USD";
    private static final String CODEURO = "EUR";

    @Value("${urlspublicas.descargaComprobante}")
    private String urlServiceDownload;

    private final ComprobanteFeign comprobanteFeign;

    @Override
    public Map<String, Object> obtenerComprobantesEstadoPorFiltro(
            String fechaEmisionDesde, String fechaEmisionHasta, String filtroTipoComprobante, String filtroRuc, String filtroSerie,
            Integer filtroNumero, Integer pageNumber, Integer perPage, Integer estadoSunats, Long idUsuario) {
        Integer idOficina = null;
        Integer numPagina = null;
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
            if(filtroNumero == null) filtroNumero = 0;
            if(idOficina == null) idOficina = 0;
            estadoSunat = estadoSunats.toString();

            result = comprobanteFeign.listarComprobantesConFiltros(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, numPagina, perPage);

            cantidad = comprobanteFeign.contarComprobantes(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, numPagina, perPage);

            tsolespayment = comprobanteFeign.obtenerTotalSolesGeneral(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, pageNumber, perPage);

            //log.info("RESULTADO: {}", tsolespayment);

            comprobanteMonedaSol = tsolespayment.stream().filter(f -> f.getCodigoMoneda().equals(CODSOLES)).findFirst().orElse(null);
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
        return ImmutableMap.of("comprobantesList", result, "cantidad", cantidad, "totalsoles", tsolesnew, "totaldolares", tdolaresnew, "totaleuros", teurosnew);
    }

    @Override
    public Map<String, Object> generarComprobante(ComprobanteDto comprobanteDto, Boolean isEdit, Long idUsuario) {
        if(comprobanteDto.getCodigoTipoOperacion() != null) {
            if(comprobanteDto.getCodigoTipoOperacion().equals("1001") ||  comprobanteDto.getCodigoTipoOperacion().equals("1002") ||
            comprobanteDto.getCodigoTipoOperacion().equals("1003") ||  comprobanteDto.getCodigoTipoOperacion().equals("1004")) {
                LeyendaDto leyendaDto = LeyendaDto.builder()
                        .descripcion("Operación sujeta al Sistema de Pago de Obligaciones Tributarias con el Gobierno Central")
                        .codigo("2006")
                        .build();
                comprobanteDto.setLeyendas(new ArrayList<>());
                comprobanteDto.getLeyendas().add(leyendaDto);
            }
        }
        return null;
    }

    Map<String, Object> generarDocumento(ComprobanteDto comprobanteDto, Boolean isEdit, Long idUsuario) {
        String usuarioBoleta = "";
        Map<String, Object> resultado = new HashMap<>();
        Map<String, Object> plantillaGenerado;
        ResponsePSE response = new ResponsePSE();
        boolean isFacturaOrNoteAssociated = true;
        String fileXMLZipBase64 = null;
        String fileXMLBase64 = null;
        String messageResponse = null;
        String nombreDocumento = null;
        String estadoRegistro;
        String estadoEnSunat;
        Integer estadoItem = null;
        //SendBillDTO dataSendBill;
        //SendBoletaDTO sendBoletaDTO;
        //PaymentVoucherEntity paymentVoucher = null;
        Long idPaymentVoucher;
        Date fechaActual;
        Boolean status;
        StringBuilder msgLog = new StringBuilder();
        //PaymentVoucherEntity paymentVoucherOld = null;
        //PaymentVoucherInterDto paymentVoucherOldDto = null;
        try {
            log.info("GENERANDO FACTURA - {} - {}", comprobanteDto.getSerie(), comprobanteDto.getNumero());


            formatearComprobante(comprobanteDto);

            status = true;
            resultado.put("idPaymentVoucher", "");
        } catch (Exception e) {
            status = false;
            messageResponse = e.getMessage();
            log.info("ERROR: {}", messageResponse);
        }

        if(!status) {
            throw new ExcepcionNegocio(messageResponse);
        }

        response.setMensaje(messageResponse);
        response.setEstado(status);
        //response.setNombre(nombreDocumento);

        resultado.put(ConstantesParametro.PARAM_BEAN_RESPONSE_PSE, response);

        /*LOGGER*/
        return resultado;
    }

    private void formatearComprobante(ComprobanteDto comprobanteDto) {
        if (comprobanteDto.getTotalValorVentaGravada() != null && comprobanteDto.getTotalValorVentaGravada().compareTo(new BigDecimal(0)) == 0) {
            comprobanteDto.setTotalValorVentaGravada(null);
        }
        if (comprobanteDto.getTotalValorVentaGratuita() != null && comprobanteDto.getTotalValorVentaGratuita().compareTo(new BigDecimal(0)) == 0) {
            comprobanteDto.setTotalValorVentaGratuita(null);
        }
        if (comprobanteDto.getTotalValorVentaExonerada() != null && comprobanteDto.getTotalValorVentaExonerada().compareTo(new BigDecimal(0)) == 0) {
            comprobanteDto.setTotalValorVentaExonerada(null);
        }
        if (comprobanteDto.getTotalValorVentaExportacion() != null && comprobanteDto.getTotalValorVentaExportacion().compareTo(new BigDecimal(0)) == 0) {
            comprobanteDto.setTotalValorVentaExportacion(null);
        }
        if (comprobanteDto.getTotalValorVentaInafecta() != null && comprobanteDto.getTotalValorVentaInafecta().compareTo(new BigDecimal(0)) == 0) {
            comprobanteDto.setTotalValorVentaInafecta(null);
        }
        if (comprobanteDto.getTotalIgv() != null && comprobanteDto.getTotalIgv().compareTo(new BigDecimal(0)) == 0) {
            comprobanteDto.setTotalIgv(null);
        }
        if (comprobanteDto.getMontoDetraccion() != null) {
            comprobanteDto.setMontoDetraccion(comprobanteDto.getMontoDetraccion().setScale(2, RoundingMode.CEILING));
        }
    }

    private SubidaRegistroArchivoEntity subirXmlComprobante(
            EmpresaInterDto empresaInterDto, String nombreDocumento, String tipoDocumento,
            String estadoRegistro, String archivoXMLZipBase64) {
        SubidaRegistroArchivoEntity archivo = new SubidaRegistroArchivoEntity();
        return archivo;
    }

    private ComprobanteEntity registrarComprobante(
            ComprobanteDto comprobanteDto, Long idArchivoRegistro, Boolean isEdit,
            ComprobanteEntity antiguoComprobante, String estado, String estadoAnterior, String estadoEnSunat,
            Integer estadoItem, String mensajeRespuesta, String registroUsuario, String usuarioModificacion,
            Timestamp fechaRegistro, Timestamp fechaModificacion, OperacionLogEnum operacionLogEnum) {

        return null;
    }

    private void transformarUrlsAResponse(ResponsePSE response, ComprobanteEntity comprobanteEntity) {
    }
}
