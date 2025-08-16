package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.dto.others.Anticipo;
import com.certicom.certifact_facturas_service_ng.dto.others.Leyenda;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.entity.ComprobanteEntity;
import com.certicom.certifact_facturas_service_ng.entity.SubidaRegistroArchivoEntity;
import com.certicom.certifact_facturas_service_ng.enums.EstadoComprobanteEnum;
import com.certicom.certifact_facturas_service_ng.enums.EstadoSunatEnum;
import com.certicom.certifact_facturas_service_ng.enums.OperacionLogEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.ServicioException;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.PlantillaService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParametro;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.google.common.collect.ImmutableMap;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
import com.certicom.certifact_facturas_service_ng.feign.FacturaComprobanteFeign;
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

    private final FacturaComprobanteFeign comprobanteFeign;
    private final PlantillaService plantillaService;
    private final AmazonS3ClientService amazonS3ClientService;

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
    public Map<String, Object> generarComprobante(ComprobanteDto comprobante, Boolean isEdit, Long idUsuario) {
        if(comprobante.getCodigoTipoOperacion() != null) {
            if(comprobante.getCodigoTipoOperacion().equals("1001") ||  comprobante.getCodigoTipoOperacion().equals("1002") ||
            comprobante.getCodigoTipoOperacion().equals("1003") ||  comprobante.getCodigoTipoOperacion().equals("1004")) {
                Leyenda leyendaDto = Leyenda.builder()
                        .descripcion("Operación sujeta al Sistema de Pago de Obligaciones Tributarias con el Gobierno Central")
                        .codigo("2006")
                        .build();
                comprobante.setLeyendas(new ArrayList<>());
                comprobante.getLeyendas().add(leyendaDto);
            }
        }
        return generarDocumento(comprobante, isEdit, idUsuario);
    }

    Map<String, Object> generarDocumento(ComprobanteDto comprobante, Boolean isEdit, Long idUsuario) {

        /*resultado=[resultado]*/
        Map<String, Object> resultado = new HashMap<>();
        ResponsePSE response = new ResponsePSE();
        String messageResponse = null;
        Boolean status = false;
        String nombreDocumento = null;

        ComprobanteEntity comprobanteCreado = null;

        Map<String, String> plantillaGenerado;
        String fileXMLZipBase64 = null;
        String fileXMLBase64 = null;
        String estadoRegistro;
        String estadoEnSunat;
        Integer estadoItem = null;

        Long idPaymentVoucher;
        Date fechaActual;

        ComprobanteEntity antiguoComprobante = null;

        try {
            /*PREPARAMOS LOS DATOS NECESARIOS DEL COMPROBANTE*/
            log.info("GENERANDO COMPROBANTE - {} - {}", comprobante.getSerie(), comprobante.getNumero());

            formatearComprobante(comprobante);
            EmpresaDto empresaDto = completarDatosEmisor(comprobante, isEdit);

            if (comprobante.getTipoTransaccion() == null) {
                comprobante.setTipoTransaccion(BigDecimal.ONE);
            }
            log.info("COMPROBANTE: {}", comprobante);
            log.info("EMPRESA: {}", empresaDto);
            if (Boolean.TRUE.equals(empresaDto.getAllowSaveOficina()) && comprobante.getOficinaId() == null) {
                OficinaDto oficinaDto = comprobanteFeign.obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
                        empresaDto.getId(), comprobante.getSerie(), comprobante.getTipoComprobante());
                if(oficinaDto!=null) {
                    if (oficinaDto.getId() != null) {
                        comprobante.setOficinaId(oficinaDto.getId());
                    }
                }
                log.info("OFICINA: {}", oficinaDto);
            }

            /*GENERAMOS PLANTILLA XML DE ACUERDO A SU OSE*/
            if (empresaDto.getOseId() != null && empresaDto.getOseId() == 1) {
                plantillaGenerado = plantillaService.buildPaymentVoucherSignOse(comprobante);
            } else if (empresaDto.getOseId() != null && empresaDto.getOseId() == 2) {
                plantillaGenerado = plantillaService.buildPaymentVoucherSignOseBliz(comprobante);
            } else if (empresaDto.getOseId() != null && (empresaDto.getOseId() == 10 || empresaDto.getOseId() == 12)) {
                plantillaGenerado = plantillaService.buildPaymentVoucherSignCerti(comprobante);
            } else {
                plantillaGenerado = plantillaService.buildPaymentVoucherSign(comprobante);
            }

            log.info("PLANTILLA GENERADA: {}", plantillaGenerado.get(ConstantesParametro.PARAM_FILE_XML_BASE64));

            nombreDocumento = plantillaGenerado.get(ConstantesParametro.PARAM_NAME_DOCUMENT);
            fileXMLZipBase64 = plantillaGenerado.get(ConstantesParametro.PARAM_FILE_ZIP_BASE64);
            fileXMLBase64 = plantillaGenerado.get(ConstantesParametro.PARAM_FILE_XML_BASE64);

            /*SE SUBE FORMATO XML DEL COMPROBANTE A AMAZON Y SE GUARDA REGISTRO EN BASE DE DATOS*/
            SubidaRegistroArchivoEntity archivoSubido = subirXmlComprobante(empresaDto, nombreDocumento, comprobante.getTipoComprobante(),
                    ConstantesParametro.REGISTRO_STATUS_NUEVO, fileXMLZipBase64);
            log.info("ARVHIVO SUBIDO: {}", archivoSubido.toString());


            fechaActual = Calendar.getInstance().getTime();
            estadoRegistro = EstadoComprobanteEnum.REGISTRADO.getCodigo();
            estadoEnSunat = EstadoSunatEnum.ACEPTADO.getAbreviado();
            comprobante.setCodigoHash(plantillaGenerado.get(ConstantesParametro.CODIGO_HASH));

            comprobanteCreado = registrarComprobante(
                    comprobante, archivoSubido.getIdRegisterFileSend(), isEdit, antiguoComprobante, estadoRegistro,
                    estadoRegistro, estadoEnSunat, estadoItem, messageResponse, "", null,
                    new Timestamp(fechaActual.getTime()), null, OperacionLogEnum.REGISTER_PAYMENT_VOUCHER
            );
/*
            idPaymentVoucher = comprobanteCreado.getId();

            status = true;
            resultado.put("idPaymentVoucher", idPaymentVoucher);*/
            status = true;
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
        response.setNombre(nombreDocumento);
        transformarUrlsAResponse(response, comprobanteCreado);
        resultado.put(ConstantesParametro.PARAM_BEAN_RESPONSE_PSE, response);

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

    private EmpresaDto completarDatosEmisor(ComprobanteDto comprobanteDto, boolean isEdit) {
        EmpresaDto empresaDto = comprobanteFeign.obtenerEmpresaPorRuc(comprobanteDto.getRucEmisor());
        comprobanteDto.setDenominacionEmisor(empresaDto.getRazon());
        comprobanteDto.setTipoDocumentoEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        comprobanteDto.setNombreComercialEmisor(empresaDto.getNombreComer());
        comprobanteDto.setUblVersion(empresaDto.getUblVersion() != null ? empresaDto.getUblVersion() : ConstantesSunat.UBL_VERSION_2_0);

        if (!isEdit && (empresaDto.getSimultaneo() != null && empresaDto.getSimultaneo())) {
            Integer proximoNumero;
            proximoNumero = getProximoNumero(comprobanteDto.getTipoComprobante(), comprobanteDto.getSerie(), comprobanteDto.getRucEmisor());
            if (proximoNumero > comprobanteDto.getNumero()) {
                comprobanteDto.setNumero(proximoNumero);
            }
        }
        return empresaDto;
    }

    private Integer getProximoNumero(String tipoDocumento, String serie, String ruc) {
        Integer ultimoComprobante = comprobanteFeign.obtenerSiguienteNumeracionPorTipoComprobanteYSerieYRucEmisor(tipoDocumento, serie, ruc);
        if (ultimoComprobante != null) {
            return ultimoComprobante + 1;
        } else {
            return 1;
        }
    }

    private SubidaRegistroArchivoEntity subirXmlComprobante(
            EmpresaDto empresaDto, String nombreDocumento, String tipoDocumento,
            String estadoRegistro, String archivoXMLZipBase64) {
        SubidaRegistroArchivoEntity archivo = amazonS3ClientService.subirArchivoAlStorage(UtilArchivo.b64ToByteArrayInputStream(archivoXMLZipBase64),
                nombreDocumento, "invoice", empresaDto);
        return archivo;
    }

    private ComprobanteEntity registrarComprobante(
            ComprobanteDto comprobante, Long idArchivoRegistro, Boolean isEdit,
            ComprobanteEntity antiguoComprobante, String estado, String estadoAnterior, String estadoEnSunat,
            Integer estadoItem, String mensajeRespuesta, String registroUsuario, String usuarioModificacion,
            Timestamp fechaRegistro, Timestamp fechaModificacion, OperacionLogEnum operacionLogEnum) {

        ComprobanteEntity comprobanteEntity = new ComprobanteEntity();
        EmpresaDto empresa = comprobanteFeign.obtenerEmpresaPorRuc(comprobante.getRucEmisor());
        if(empresa == null) {
            throw new ServicioException("Error al momento de obtener la empresa");
        }

        /**/
        /*
        if (idRegisterFile != null) {
            entity.addFile(PaymentVoucherFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                    .tipoArchivo(TipoArchivoEnum.XML)
                    .build());
        }

        if (comprobante.getAnticipos() != null && !comprobante.getAnticipos().isEmpty()) {
            for (Anticipo anticipo : comprobante.getAnticipos()) {
                AnticipoEntity anticipoEntity = new AnticipoEntity();
                anticipoEntity.setMontoAnticipo(anticipo.getMontoAnticipado());
                anticipoEntity.setNumeroAnticipo(anticipo.getNumeroAnticipo());
                anticipoEntity.setSerieAnticipo(anticipo.getSerieAnticipo());
                anticipoEntity.setTipoDocumentoAnticipo(anticipo.getTipoDocumentoAnticipo());

                entity.addAnticipo(anticipoEntity);
            }
        }
        if (voucher.getCamposAdicionales() != null && !voucher.getCamposAdicionales().isEmpty()) {

            for (CampoAdicional campoAdicional : voucher.getCamposAdicionales()) {
                AditionalFieldEntity aditionalFieldEntity = new AditionalFieldEntity();
                TypeFieldEntity typeField = typeFieldRepository.findByName(campoAdicional.getNombreCampo());
                if (typeField != null)
                    aditionalFieldEntity.setTypeField(typeField);
                else {

                    typeField = new TypeFieldEntity();
                    typeField.setName(campoAdicional.getNombreCampo());
                    typeField.setCompanys(new ArrayList<>());
                    typeField.getCompanys().add(CompanyEntity.builder().id(company.getId()).build());
                    typeField = typeFieldRepository.save(typeField);
                    aditionalFieldEntity.setTypeField(typeField);
                }
                aditionalFieldEntity.setValorCampo(campoAdicional.getValorCampo());
                entity.addAditionalField(aditionalFieldEntity);
            }
        }

        if (voucher.getCuotas() != null && !voucher.getCuotas().isEmpty()) {
            for (PaymentVoucherCuota cuota : voucher.getCuotas()) {
                CuotasPaymentVoucherEntity centity = new CuotasPaymentVoucherEntity();
                centity.setNumero(cuota.getNumero());
                centity.setMonto(cuota.getMonto());
                centity.setFecha(cuota.getFecha());
                entity.addCuotas(centity);
            }
        }

        boolean existGuiaRelacionada = false;
        if (voucher.getGuiasRelacionadas() != null && !voucher.getGuiasRelacionadas().isEmpty()) {

            for (GuiaRelacionada guiaRelacionada : voucher.getGuiasRelacionadas()) {
                GuiaRelacionadaEntity guiaRelacionadaEntity = new GuiaRelacionadaEntity();
                guiaRelacionadaEntity.setCodigoTipoGuia(guiaRelacionada.getCodigoTipoGuia());
                guiaRelacionadaEntity.setSerieNumeroGuia(guiaRelacionada.getSerieNumeroGuia());
                guiaRelacionadaEntity.setIdguiaremision(guiaRelacionada.getIdguiaremision());
                entity.addGuiaRelacionada(guiaRelacionadaEntity);
                if (guiaRelacionadaEntity.getIdguiaremision()!= null) {
                    existGuiaRelacionada = true;
                }
            }
        }

        for (PaymentVoucherLine item : voucher.getItems()) {

            DetailsPaymentVoucherEntity detailEntity = new DetailsPaymentVoucherEntity();

            detailEntity.setNumeroItem(item.getNumeroItem());
            detailEntity.setCantidad(item.getCantidad());
            detailEntity.setCodigoUnidadMedida(item.getCodigoUnidadMedida());

            detailEntity.setDescripcion(item.getDescripcion());
            detailEntity.setCodigoProducto(item.getCodigoProducto());
            detailEntity.setCodigoProductoSunat(item.getCodigoProductoSunat());
            detailEntity.setCodigoProductoGS1(item.getCodigoProductoGS1());

            detailEntity.setValorUnitario(item.getValorUnitario());
            detailEntity.setPrecioVentaUnitario(item.getPrecioVentaUnitario());
            detailEntity.setValorReferencialUnitario(item.getValorReferencialUnitario());

            detailEntity.setMontoBaseExonerado(item.getMontoBaseExonerado());
            detailEntity.setMontoBaseExportacion(item.getMontoBaseExportacion());
            detailEntity.setMontoBaseGratuito(item.getMontoBaseGratuito());
            detailEntity.setMontoBaseIgv(item.getMontoBaseIgv());
            detailEntity.setMontoBaseInafecto(item.getMontoBaseInafecto());
            detailEntity.setMontoBaseIsc(item.getMontoBaseIsc());
            detailEntity.setMontoBaseIvap(item.getIvap());
            detailEntity.setMontoBaseOtrosTributos(item.getMontoBaseOtrosTributos());

            detailEntity.setAfectacionIGV(item.getIgv());
            detailEntity.setSistemaISC(item.getIsc());
            detailEntity.setIvap(item.getIvap());
            detailEntity.setTributoVentaGratuita(item.getImpuestoVentaGratuita());
            detailEntity.setOtrosTributos(item.getOtrosTributos());

            detailEntity.setCodigoTipoAfectacionIGV(item.getCodigoTipoAfectacionIGV());
            detailEntity.setCodigoTipoSistemaISC(item.getCodigoTipoCalculoISC());

            detailEntity.setPorcentajeIgv(item.getPorcentajeIgv());
            detailEntity.setPorcentajeIsc(item.getPorcentajeIsc());
            detailEntity.setPorcentajeIvap(item.getPorcentajeIvap());
            detailEntity.setPorcentajeOtrosTributos(item.getPorcentajeOtrosTributos());
            detailEntity.setPorcentajeTributoVentaGratuita(item.getPorcentajeTributoVentaGratuita());

            detailEntity.setDescuento(item.getDescuento());
            detailEntity.setCodigoDescuento(item.getCodigoDescuento());
            detailEntity.setValorVenta(item.getValorVenta());

            detailEntity.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
            detailEntity.setDetalleViajeDetraccion(item.getDetalleViajeDetraccion());
            detailEntity.setUbigeoOrigenDetraccion(item.getUbigeoOrigenDetraccion());
            detailEntity.setDireccionOrigenDetraccion(item.getDireccionOrigenDetraccion());
            detailEntity.setUbigeoDestinoDetraccion(item.getUbigeoDestinoDetraccion());
            detailEntity.setDireccionDestinoDetraccion(item.getDireccionDestinoDetraccion());
            detailEntity.setValorServicioTransporte(item.getValorServicioTransporte());
            detailEntity.setValorCargaEfectiva(item.getValorCargaEfectiva());
            detailEntity.setValorCargaUtil(item.getValorCargaUtil());

            detailEntity.setHidroMatricula(item.getHidroMatricula());
            detailEntity.setHidroCantidad(item.getHidroCantidad());
            detailEntity.setHidroDescripcionTipo(item.getHidroDescripcionTipo());
            detailEntity.setHidroEmbarcacion(item.getHidroEmbarcacion());
            detailEntity.setHidroFechaDescarga(item.getHidroFechaDescarga());
            detailEntity.setHidroLugarDescarga(item.getHidroLugarDescarga());

            detailEntity.setMontoIcbper(item.getMontoIcbper());
            detailEntity.setMontoBaseIcbper(item.getMontoBaseIcbper());

            detailEntity.setUnidadManejo(item.getUnidadManejo());
            detailEntity.setInstruccionesEspeciales(item.getInstruccionesEspeciales());
            detailEntity.setMarca(item.getMarca());
            detailEntity.setAdicional(item.getAdicional());
            entity.addDetailsPaymentVoucher(detailEntity);


        }

        * */

        return null;
    }

    private void transformarUrlsAResponse(ResponsePSE response, ComprobanteEntity comprobanteEntity) {
    }
}
