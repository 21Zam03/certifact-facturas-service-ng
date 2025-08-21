package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.converter.PaymentVoucherConverter;
import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.dto.others.*;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.entity.*;
import com.certicom.certifact_facturas_service_ng.enums.*;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.formatter.PaymentVoucherFormatter;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.PlantillaService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParametro;
import com.certicom.certifact_facturas_service_ng.util.UUIDGen;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.google.common.collect.ImmutableMap;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
import com.certicom.certifact_facturas_service_ng.feign.FacturaComprobanteFeign;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
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
public class PaymentVoucherServiceImpl implements PaymentVoucherService {

    private static final String CODSOLES = "PEN";
    private static final String CODDOLAR = "USD";
    private static final String CODEURO = "EUR";
    private final FacturaComprobanteFeign facturaComprobanteFeign;

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
    public Map<String, Object> generatePaymentVoucher(PaymentVoucherDto paymentVoucherDto, boolean isEdit, Long idUsuario) {
        /*if(paymentVoucherDto.getCodigoTipoOperacion() != null) {
            if(paymentVoucherDto.getCodigoTipoOperacion().equals("1001") ||  paymentVoucherDto.getCodigoTipoOperacion().equals("1002") ||
            paymentVoucherDto.getCodigoTipoOperacion().equals("1003") ||  paymentVoucherDto.getCodigoTipoOperacion().equals("1004")) {
                Leyenda leyendaDto = Leyenda.builder()
                        .descripcion("Operación sujeta al Sistema de Pago de Obligaciones Tributarias con el Gobierno Central")
                        .codigo("2006")
                        .build();
                paymentVoucherDto.setLeyendas(new ArrayList<>());
                paymentVoucherDto.getLeyendas().add(leyendaDto);
            }
        }*/
        if (paymentVoucherDto.getCodigoTipoOperacion() != null) {
            if (paymentVoucherDto.getCodigoTipoOperacion().trim().length() == 4) {
                paymentVoucherDto.setCodigoTipoOperacionCatalogo51(paymentVoucherDto.getCodigoTipoOperacion());
            } else {
                switch (paymentVoucherDto.getCodigoTipoOperacion()) {
                    case "01":
                    case "04":
                        paymentVoucherDto.setCodigoTipoOperacionCatalogo51("0101");
                        break;
                    case "02":
                        paymentVoucherDto.setCodigoTipoOperacionCatalogo51("0200");
                        break;
                    default:
                        paymentVoucherDto.setCodigoTipoOperacionCatalogo51("0101");
                        break;
                }
            }
        } else {
            paymentVoucherDto.setCodigoTipoOperacionCatalogo51("0101");
        }
        return generateDocument(paymentVoucherDto, isEdit, idUsuario);
    }

    @Override
    public PaymentVoucherDto prepareComprobanteForEnvioSunatInter(String finalRucEmisor, String tipoComprobante, String serieDocumento, Integer numeroDocumento) throws ServiceException {
        PaymentVoucherDto paymentVoucherDto = comprobanteFeign.
                findPaymentVoucherByRucAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento(finalRucEmisor, tipoComprobante, serieDocumento, numeroDocumento);

        if (paymentVoucherDto == null)
            throw new ServiceException(String.format("%s [%s-%s-%s-%s]", "El comprobante que desea enviar a la Sunat, no existe: ", finalRucEmisor, tipoComprobante, serieDocumento, numeroDocumento != null ? numeroDocumento.toString() : ""));

        if (paymentVoucherDto.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()))
            throw new ServiceException("Este comprobante ya se encuentra aceptado en Sunat.");

        if (paymentVoucherDto.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado()))
            throw new ServiceException("Este comprobante se encuentra anulado en Sunat.");

        if (paymentVoucherDto.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA))
            throw new ServiceException("Por este metodo solo se permite enviar Facturas, Notas de crédito y Débito.");


        if (paymentVoucherDto.getTipoComprobanteAfectado() != null) {
            if (
                    (paymentVoucherDto.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) || paymentVoucherDto.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO))
                            && paymentVoucherDto.getTipoComprobanteAfectado().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
            ) {
                throw new ServiceException("Por este metodo solo se permite enviar Notas de crédito y Débito asociadas a Facturas, las notas asociadas a boletas se deben enviar por resumen diario.");
            }
        }
        return paymentVoucherDto;
    }

    Map<String, Object> generateDocument(PaymentVoucherDto comprobante, Boolean isEdit, Long idUsuario) {

        Map<String, Object> resultado = new HashMap<>();
        ResponsePSE response = new ResponsePSE();
        String messageResponse = null;
        Boolean status = false;
        String nombreDocumento = null;
        boolean isFacturaOrNoteAssociated = true;

        PaymentVoucherEntity comprobanteCreado = null;
        PaymentVoucherEntity paymentVoucherOld = null;

        Map<String, String> plantillaGenerado;
        String fileXMLZipBase64 = null;
        String fileXMLBase64 = null;
        String estadoRegistro;
        String estadoEnSunat;
        Integer estadoItem = null;
        SendBillDto dataSendBill;

        Long idPaymentVoucher;
        Date fechaActual;

        try {
            /*PREPARAMOS LOS DATOS NECESARIOS DEL COMPROBANTE*/
            log.info("GENERANDO COMPROBANTE - {} - {}", comprobante.getSerie(), comprobante.getNumero());

            PaymentVoucherFormatter.formatPaymentVoucher(comprobante);
            EmpresaDto empresaDto = completarDatosEmisor(comprobante, isEdit);

            //FLAG DE ENVIO POR SENDBILL O RESUMEN
            if (comprobante.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                    || comprobante.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
                isFacturaOrNoteAssociated = false;
            }

            if(isEdit) {
                messageResponse = ConstantesParametro.MSG_EDICION_DOCUMENTO_OK;
                paymentVoucherOld = comprobanteFeign.findPaymentVoucherByRucAndTipoComprobanteAndSerieAndNumero(
                        comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
                //VALIDO QUE EXISTA EL COMPROBANTE A EDITAR
                if (paymentVoucherOld == null)
                    throw new ServiceException("Este comprobante que desea editar, no existe en la base de datos del PSE");

                //VALIDO QUE EL ESTADO DEL COMPROBANTE SEA REGISTRADO Y QUE AUN NO ESTE EN LA SUNAT PARA PODER EDITARLO
                if ((!paymentVoucherOld.getEstado().equals(EstadoComprobanteEnum.REGISTRADO.getCodigo()) &&
                        !paymentVoucherOld.getEstado().equals(EstadoComprobanteEnum.ERROR.getCodigo()))
                        || paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())
                        || paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())
                ) {
                    throw new ServiceException("Este comprobante no se puede editar, ya fue declarado a Sunat.");
                }
            } else {
                messageResponse = ConstantesParametro.MSG_REGISTRO_DOCUMENTO_OK;
            }

            //SETEANDO ESTADO DEL ITEM RESUMEN
            if (!isFacturaOrNoteAssociated) {
                //SI ESTA EDITANDO UNA BOLETA O NOTAS ASOCIADAS A BOLETAS
                if (isEdit) {
                    //SI EL COMPROBANTE YA ESTA REGISTRADO EN SUNAT, EL ESTADO DE RESUMEN ES 2 MODIFICACION
                    if (paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())) {
                        estadoItem = ConstantesParametro.STATE_ITEM_PENDIENTE_MODIFICACION;
                        //SI NO EL ESTADO ES 1 ADICIONAR
                    } else {
                        estadoItem = ConstantesParametro.STATE_ITEM_PENDIENTE_ADICION;
                    }
                }
                //SI NO ESTA EDITANDO EL ESTADO PARA RESUMEN ES 1 ADICIONAR
                else {
                    estadoItem = ConstantesParametro.STATE_ITEM_PENDIENTE_ADICION;
                }
            }

            if (Boolean.TRUE.equals(empresaDto.getAllowSaveOficina()) && comprobante.getOficinaId() == null) {
                OficinaDto oficinaDto = comprobanteFeign.obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
                        empresaDto.getId(), comprobante.getSerie(), comprobante.getTipoComprobante());
                if(oficinaDto!=null) {
                    if (oficinaDto.getId() != null) {
                        comprobante.setOficinaId(oficinaDto.getId());
                    }
                }
            }
            log.info("VOUCHER: {}",comprobante);
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
            estadoEnSunat = EstadoSunatEnum.NO_ENVIADO.getAbreviado();
            comprobante.setCodigoHash(plantillaGenerado.get(ConstantesParametro.CODIGO_HASH));

            comprobanteCreado = registrarComprobante(
                    comprobante, archivoSubido.getIdRegisterFileSend(), isEdit, paymentVoucherOld, estadoRegistro,
                    estadoRegistro, estadoEnSunat, estadoItem, messageResponse, "", null,
                    new Timestamp(fechaActual.getTime()), null, OperacionLogEnum.REGISTER_PAYMENT_VOUCHER
            );
            log.info("COMPROBANTE CREADO: {}", comprobanteCreado);

            idPaymentVoucher = comprobanteCreado.getIdPaymentVoucher();

            if(isFacturaOrNoteAssociated) {
                registerVoucherTemporal(idPaymentVoucher, nombreDocumento, UUIDGen.generate(), comprobante.getTipoComprobante(), isEdit);
                dataSendBill = new SendBillDto();
                dataSendBill.setRuc(comprobante.getRucEmisor());
                dataSendBill.setIdPaymentVoucher(idPaymentVoucher);
                dataSendBill.setNameDocument(nombreDocumento);
                dataSendBill.setEnvioAutomaticoSunat(empresaDto.getEnvioAutomaticoSunat() == null || empresaDto.getEnvioAutomaticoSunat());
                resultado.put(ConstantesParametro.PARAM_BEAN_SEND_BILL, dataSendBill);
            }

            status = true;

            resultado.put("idPaymentVoucher", idPaymentVoucher);

        } catch (TemplateException | SignedException e) {
            status = false;
            messageResponse = "Error al generar plantilla del documento[" + comprobante.getIdentificadorDocumento() + "] " + e.getMessage();
            log.info("ERROR: {}", messageResponse);
        } catch (ServiceException e) {
            status = false;
            messageResponse = e.getMessage();
            log.info("ERROR: {}", messageResponse);
        } catch (Exception e) {
            status = false;
            messageResponse = e.getMessage();
            log.info("ERROR: {}", messageResponse);
        }

        if(!status) {
            throw new ServiceException(messageResponse);
        }

        response.setMensaje(messageResponse);
        response.setEstado(status);
        response.setNombre(nombreDocumento);
        transformarUrlsAResponse(response, comprobanteCreado);
        resultado.put(ConstantesParametro.PARAM_BEAN_RESPONSE_PSE, response);

        validateAutomaticDelivery(resultado);
        //ResponsePSE resp = (ResponsePSE) resultado.get(ConstantesParametro.PARAM_BEAN_RESPONSE_PSE);
        return resultado;
    }

    private void validateAutomaticDelivery(Map<String, Object> result) {
        if (result.get(ConstantesParametro.PARAM_BEAN_SEND_BILL) != null) {
            SendBillDto dataSendBill = (SendBillDto) result.get(ConstantesParametro.PARAM_BEAN_SEND_BILL);
            if (dataSendBill.getEnvioAutomaticoSunat()) {
                System.out.println("Enviar mensaje");
                //messageProducer.produceSendBill(dataSendBill);
            }
        }
    }

    private void formatPaymentVoucher(PaymentVoucherDto paymentVoucherDto) {
        if (paymentVoucherDto.getTotalValorVentaGravada() != null && paymentVoucherDto.getTotalValorVentaGravada().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaGravada(null);
        }
        if (paymentVoucherDto.getTotalValorVentaGratuita() != null && paymentVoucherDto.getTotalValorVentaGratuita().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaGratuita(null);
        }
        if (paymentVoucherDto.getTotalValorVentaExonerada() != null && paymentVoucherDto.getTotalValorVentaExonerada().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaExonerada(null);
        }
        if (paymentVoucherDto.getTotalValorVentaExportacion() != null && paymentVoucherDto.getTotalValorVentaExportacion().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaExportacion(null);
        }
        if (paymentVoucherDto.getTotalValorVentaInafecta() != null && paymentVoucherDto.getTotalValorVentaInafecta().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalValorVentaInafecta(null);
        }
        if (paymentVoucherDto.getTotalIgv() != null && paymentVoucherDto.getTotalIgv().compareTo(new BigDecimal(0)) == 0) {
            paymentVoucherDto.setTotalIgv(null);
        }
        if (paymentVoucherDto.getMontoDetraccion() != null) {
            paymentVoucherDto.setMontoDetraccion(paymentVoucherDto.getMontoDetraccion().setScale(2, RoundingMode.CEILING));
        }
    }

    private EmpresaDto completarDatosEmisor(PaymentVoucherDto paymentVoucherDto, boolean isEdit) {
        EmpresaDto empresaDto = comprobanteFeign.obtenerEmpresaPorRuc(paymentVoucherDto.getRucEmisor());
        paymentVoucherDto.setRucEmisor(empresaDto.getRuc());
        paymentVoucherDto.setDenominacionEmisor(empresaDto.getRazon());
        paymentVoucherDto.setTipoDocumentoEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        paymentVoucherDto.setNombreComercialEmisor(empresaDto.getNombreComer());
        paymentVoucherDto.setUblVersion(empresaDto.getUblVersion() != null ? empresaDto.getUblVersion() : ConstantesSunat.UBL_VERSION_2_0);

        if (!isEdit && (empresaDto.getSimultaneo() != null && empresaDto.getSimultaneo())) {
            Integer proximoNumero;
            proximoNumero = getProximoNumero(paymentVoucherDto.getTipoComprobante(), paymentVoucherDto.getSerie(), paymentVoucherDto.getRucEmisor());
            if (proximoNumero > paymentVoucherDto.getNumero()) {
                paymentVoucherDto.setNumero(proximoNumero);
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

    private PaymentVoucherEntity registrarComprobante(
            PaymentVoucherDto comprobante, Long idArchivoRegistro, Boolean isEdit,
            PaymentVoucherEntity antiguoComprobante, String estado, String estadoAnterior, String estadoEnSunat,
            Integer estadoItem, String mensajeRespuesta, String registroUsuario, String usuarioModificacion,
            Timestamp fechaRegistro, Timestamp fechaModificacion, OperacionLogEnum operacionLogEnum) {

        PaymentVoucherEntity entity = new PaymentVoucherEntity();

        log.info("SEGUIMIENTO COMPROBANTE - FACTURA - ND - NC - [OPERACION: {}]", operacionLogEnum);

        if(isEdit) {
            List<ComprobanteDetalleEntity> items = antiguoComprobante.getComprobanteDetalleEntityList();
            List<AnticipoEntity> anticipos = antiguoComprobante.getAnticipoEntityList();
            List<ComprobanteCampoAdicionalEntity> adicionales = antiguoComprobante.getComprobanteCampoAdicionalEntityList();
            List<ComprobanteCuotaEntity> cuotas = antiguoComprobante.getCuotasEntityList();
            List<GuiaRelacionadaEntity> guias = antiguoComprobante.getGuiaRelacionadaEntityList();

            if (items != null && !items.isEmpty()) {
                for (ComprobanteDetalleEntity item : items) {
                    //historialStockService.eliminarHistorialStockByDetail(item);
                    System.out.println("Eliminar items - stock del comprobante");
                }
                for (ComprobanteDetalleEntity item : items) {
                    //detailsPaymentVoucherRepository.deleteDetailsPaymentVoucher(item.getIdDetailsPayment());
                    System.out.println("Eliminar items del comprobante");
                }
            }
            if (anticipos != null && !anticipos.isEmpty()) {
                for (AnticipoEntity anticipo : anticipos) {
                    //anticipoRepository.deleteAnticipo(anticipo.getIdAnticipoPayment());
                    System.out.println("Eliminar anticipos del comprobante");
                }
            }
            if (adicionales != null && !adicionales.isEmpty()) {
                for (ComprobanteCampoAdicionalEntity adicional : adicionales) {
                    //aditionalFieldRepository.deleteAditionakField(adicional.getId());
                    System.out.println("Eliminar campos adicionales del comprobante");
                }
            }
            if (cuotas != null && !cuotas.isEmpty()) {
                for (ComprobanteCuotaEntity cuota : cuotas) {
                    //cuotaPaymentVoucherRepository.deleteCuotaPayment(cuota.getId());
                    System.out.println("Eliminar cuotas del comprobante");
                }
            }
            if (guias != null && !guias.isEmpty()) {
                for (GuiaRelacionadaEntity guia : guias) {
                    //guiaRelacionadaRepository.deleteGuiaRelacionada(guia.getIdGuiaPayment());
                    System.out.println("Eliminar guias relacionadas del comprobante");
                }
            }
            entity.setBranchOfficeEntity(antiguoComprobante.getBranchOfficeEntity());
            entity.setIdPaymentVoucher(antiguoComprobante.getIdPaymentVoucher());
            entity.setUuid(antiguoComprobante.getUuid());
            entity.setComprobanteArchivoEntityList(antiguoComprobante.getComprobanteArchivoEntityList());
            entity.setFechaRegistro(antiguoComprobante.getFechaRegistro());
        }

        entity.setSerie(comprobante.getSerie());
        entity.setNumero(comprobante.getNumero());
        entity.setFechaEmision(comprobante.getFechaEmision());
        entity.setFechaEmisionDate(UtilFormat.fechaDate(comprobante.getFechaEmision()));
        entity.setHoraEmision(comprobante.getHoraEmision());
        entity.setTipoComprobante(comprobante.getTipoComprobante());
        entity.setCodigoMoneda(comprobante.getCodigoMoneda());
        entity.setFechaVencimiento(comprobante.getFechaVencimiento());
        entity.setTipoOperacion(comprobante.getCodigoTipoOperacion());

        entity.setRucEmisor(comprobante.getRucEmisor());
        entity.setCodigoLocalAnexo(comprobante.getCodigoLocalAnexoEmisor());

        entity.setTipoDocIdentReceptor(comprobante.getTipoDocumentoReceptor());
        entity.setNumDocIdentReceptor(comprobante.getNumeroDocumentoReceptor());
        entity.setDenominacionReceptor(comprobante.getDenominacionReceptor());

        entity.setEmailReceptor(comprobante.getEmailReceptor());
        entity.setDireccionReceptor(comprobante.getDireccionReceptor());

        entity.setCodigoTipoDocumentoRelacionado(comprobante.getCodigoTipoOtroDocumentoRelacionado());
        entity.setSerieNumeroDocumentoRelacionado(comprobante.getSerieNumeroOtroDocumentoRelacionado());

        entity.setTotalValorVentaOperacionExportada(comprobante.getTotalValorVentaExportacion());
        entity.setTotalValorVentaOperacionGravada(comprobante.getTotalValorVentaGravada());
        entity.setTotalValorVentaOperacionInafecta(comprobante.getTotalValorVentaInafecta());
        entity.setTotalValorVentaOperacionExonerada(comprobante.getTotalValorVentaExonerada());
        entity.setTotalValorVentaOperacionGratuita(comprobante.getTotalValorVentaGratuita());
        entity.setTotalValorVentaGravadaIVAP(comprobante.getTotalValorVentaGravadaIVAP());
        entity.setTotalValorBaseIsc(comprobante.getTotalValorBaseIsc());
        entity.setTotalValorBaseOtrosTributos(comprobante.getTotalValorBaseOtrosTributos());
        entity.setTotalDescuento(comprobante.getTotalDescuento());

        entity.setMontoDescuentoGlobal(comprobante.getDescuentoGlobales());
        entity.setMontoSumatorioOtrosCargos(comprobante.getSumatoriaOtrosCargos());
        entity.setMontoImporteTotalVenta(comprobante.getImporteTotalVenta());
        entity.setMontoTotalAnticipos(comprobante.getTotalAnticipos());

        entity.setSumatoriaIGV(comprobante.getTotalIgv());
        entity.setSumatoriaISC(comprobante.getTotalIsc());
        entity.setSumatoriaTributosOperacionGratuita(comprobante.getTotalImpOperGratuita());
        entity.setSumatoriaOtrosTributos(comprobante.getTotalOtrostributos());
        entity.setSumatoriaIvap(comprobante.getTotalIvap());

        entity.setSerieAfectado(comprobante.getSerieAfectado());
        entity.setNumeroAfectado(comprobante.getNumeroAfectado());
        entity.setTipoComprobanteAfectado(comprobante.getTipoComprobanteAfectado());
        entity.setMotivoNota(comprobante.getMotivoNota());
        entity.setCodigoTipoNotaCredito(comprobante.getCodigoTipoNotaCredito());
        entity.setCodigoTipoNotaDebito(comprobante.getCodigoTipoNotaDebito());

        entity.setIdentificadorDocumento(comprobante.getRucEmisor()+ "-" +comprobante.getTipoComprobante()+ "-" +
                comprobante.getSerie()+ "-" +comprobante.getNumero());

        entity.setEstado(estado);
        entity.setEstadoAnterior(estadoAnterior);
        entity.setEstadoItem(estadoItem);
        entity.setEstadoSunat(estadoEnSunat);
        entity.setMensajeRespuesta(mensajeRespuesta);

        if(!isEdit) {
            entity.setFechaRegistro(fechaRegistro);
            entity.setUserName(registroUsuario);
        } else {
            entity.setFechaModificacion(antiguoComprobante.getFechaModificacion());
            entity.setUserName(antiguoComprobante.getUserName());
        }

        entity.setFechaModificacion(fechaModificacion);
        entity.setUserNameModify(usuarioModificacion);
        entity.setOrdenCompra(comprobante.getOrdenCompra());
        entity.setUblVersion(comprobante.getUblVersion());
        entity.setCodigoHash(comprobante.getCodigoHash());

        entity.setCodigoMedioPago(comprobante.getCodigoMedioPago());
        entity.setCuentaFinancieraBeneficiario(comprobante.getCuentaFinancieraBeneficiario());
        entity.setCodigoBienDetraccion(comprobante.getCodigoBienDetraccion());
        entity.setPorcentajeDetraccion(comprobante.getPorcentajeDetraccion());
        entity.setMontoDetraccion(comprobante.getMontoDetraccion());
        entity.setDetraccion(comprobante.getDetraccion());

        entity.setPorcentajeRetencion(comprobante.getPorcentajeRetencion());
        entity.setMontoRetencion(comprobante.getMontoRetencion());
        entity.setRetencion(comprobante.getRetencion());

        entity.setTipoTransaccion(comprobante.getTipoTransaccion());
        entity.setMontoPendiente(comprobante.getMontoPendiente());
        entity.setCantidadCuotas(comprobante.getCantidadCuotas());
        entity.setPagoCuenta(comprobante.getPagoCuenta());

        /*INFORMACION DE ARCHIVOS*/
        if (idArchivoRegistro != null) {
            List<ComprobanteArchivoEntity> comprobantesArchivosList = new ArrayList<>();
            ComprobanteArchivoEntity comprobanteArchivo = ComprobanteArchivoEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                    .idRegisterFileSend(idArchivoRegistro)
                    .tipoArchivo(TipoArchivoEnum.XML.name())
                    .build();
            comprobantesArchivosList.add(comprobanteArchivo);
            entity.setComprobanteArchivoEntityList(comprobantesArchivosList);
        }

        /*INFORMACION DE ANTICIPOS*/
        if (comprobante.getAnticipos() != null && !comprobante.getAnticipos().isEmpty()) {
            List<AnticipoEntity> anticipoEntityList = new ArrayList<>();
            for (Anticipo anticipo : comprobante.getAnticipos()) {
                AnticipoEntity anticipoEntity = new AnticipoEntity();
                anticipoEntity.setMontoAnticipo(anticipo.getMontoAnticipado());
                anticipoEntity.setNumeroAnticipo(anticipo.getNumeroAnticipo());
                anticipoEntity.setSerieAnticipo(anticipo.getSerieAnticipo());
                anticipoEntity.setTipoDocumentoAnticipo(anticipo.getTipoDocumentoAnticipo());
                anticipoEntityList.add(anticipoEntity);
            }
            entity.setAnticipoEntityList(anticipoEntityList);
        }

        /*INFORMACION DE CAMPOS ADICIONALES*/
        if (comprobante.getCamposAdicionales() != null && !comprobante.getCamposAdicionales().isEmpty()) {
            List<ComprobanteCampoAdicionalEntity> comprobanteCampoAdicionalEntityList = new ArrayList<>();
            for (CampoAdicional campoAdicional : comprobante.getCamposAdicionales()) {
                Integer campoAdicionalId = comprobanteFeign.obtenerCampoAdicionalIdPorNombre(campoAdicional.getNombreCampo());
                ComprobanteCampoAdicionalEntity comprobanteCampoAdicionalEntity = ComprobanteCampoAdicionalEntity.builder()
                        .valorCampo(campoAdicional.getValorCampo())
                        .campoAdicionalEntityId(campoAdicionalId)
                        .build();
                comprobanteCampoAdicionalEntityList.add(comprobanteCampoAdicionalEntity);
            }
            entity.setComprobanteCampoAdicionalEntityList(comprobanteCampoAdicionalEntityList);
        }

        /*INFORMACION DE CUOTAS*/
        if (comprobante.getCuotas() != null && !comprobante.getCuotas().isEmpty()) {
            List<ComprobanteCuotaEntity> comprobanteCuotaEntityList = new ArrayList<>();
            for (ComprobanteCuota cuota : comprobante.getCuotas()) {
                ComprobanteCuotaEntity centity = ComprobanteCuotaEntity.builder()
                        .numero(cuota.getNumero())
                        .monto(cuota.getMonto())
                        .fecha(cuota.getFecha())
                        .build();
                comprobanteCuotaEntityList.add(centity);
            }
            entity.setCuotasEntityList(comprobanteCuotaEntityList);
        }

        boolean existGuiaRelacionada = false;
        if (comprobante.getGuiasRelacionadas() != null && !comprobante.getGuiasRelacionadas().isEmpty()) {
            for (GuiaRelacionada guiaRelacionada : comprobante.getGuiasRelacionadas()) {
                if (guiaRelacionada.getIdguiaremision() != null) {
                    existGuiaRelacionada = true;
                    break;
                }
            }
        }

        List<ComprobanteDetalleEntity> comprobanteDetalleEntityList = new ArrayList<>();
        for (ComprobanteItem item : comprobante.getItems()) {

            ComprobanteDetalleEntity detailEntity = new ComprobanteDetalleEntity();

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

            detailEntity.setEstado(ConstantesParametro.REGISTRO_ACTIVO);
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
            comprobanteDetalleEntityList.add(detailEntity);
        }
        entity.setComprobanteDetalleEntityList(comprobanteDetalleEntityList);
        entity.setOficinaId(comprobante.getOficinaId());

        if (registroUsuario != null && entity.getOficinaId() == null) {
            if (!registroUsuario.equals(ConstantesSunat.SUPERADMIN)) {
                UserEntity user = facturaComprobanteFeign.findByUserByUsername(registroUsuario);
                entity.setOficinaId(user.getOficinaId());
            }
        }
        entity.setUuid(UUIDGen.generate());
        return facturaComprobanteFeign.registrarComprobante(entity);
    }

    private void transformarUrlsAResponse(ResponsePSE response, PaymentVoucherEntity paymentVoucher) {
        if (paymentVoucher != null) {
            String urlTicket = urlServiceDownload + "descargapdfuuid/" + paymentVoucher.getIdPaymentVoucher() + "/" + paymentVoucher.getUuid() + "/ticket/" + paymentVoucher.getIdentificadorDocumento();
            String urlA4 = urlServiceDownload + "descargapdfuuid/" + paymentVoucher.getIdPaymentVoucher() + "/" + paymentVoucher.getUuid() + "/a4/" + paymentVoucher.getIdentificadorDocumento();
            String urlXml = urlServiceDownload + "descargaxmluuid/" + paymentVoucher.getIdPaymentVoucher() + "/" + paymentVoucher.getUuid() + "/" + paymentVoucher.getIdentificadorDocumento();
            response.setUrlPdfTicket(urlTicket);
            response.setUrlPdfA4(urlA4);
            response.setUrlXml(urlXml);
            response.setCodigoHash(paymentVoucher.getCodigoHash());
        }
    }

    private void registerVoucherTemporal(Long idPaymentVoucher, String nombreCompletoDocumento, String uuidSaved,
                                         String tipoComprobante, Boolean isEdit) {

        TmpVoucherSendBillEntity tmpEntity = null;

        if (!isEdit) {
            tmpEntity = new TmpVoucherSendBillEntity();
        } else {
            tmpEntity = facturaComprobanteFeign.findTmpVoucherByIdPaymentVoucher(idPaymentVoucher);
        }
        if (tmpEntity == null) {
            tmpEntity = new TmpVoucherSendBillEntity();
        }
        tmpEntity.setEstado(EstadoVoucherTmpEnum.PENDIENTE.getEstado());
        tmpEntity.setIdPaymentVoucher(idPaymentVoucher);
        tmpEntity.setNombreDocumento(nombreCompletoDocumento);
        tmpEntity.setUuidSaved(uuidSaved);
        tmpEntity.setTipoComprobante(tipoComprobante);

        facturaComprobanteFeign.saveTmpVoucher(tmpEntity);
    }
}
