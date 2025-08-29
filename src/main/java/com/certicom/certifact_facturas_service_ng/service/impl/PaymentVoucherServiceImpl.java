package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.dto.others.*;
import com.certicom.certifact_facturas_service_ng.dto.others.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.entity.*;
import com.certicom.certifact_facturas_service_ng.enums.*;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.feign.*;
import com.certicom.certifact_facturas_service_ng.formatter.PaymentVoucherFormatter;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.TemplateService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.util.UUIDGen;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.google.common.collect.ImmutableMap;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentVoucherServiceImpl implements PaymentVoucherService {

    private static final String CODSOLES = "PEN";
    private static final String CODDOLAR = "USD";
    private static final String CODEURO = "EUR";

    private final PaymentVoucherFeign paymentVoucherFeign;
    private final UserFeign userFeign;
    private final CompanyFeign companyFeign;
    private final BranchOfficeFeign branchOfficeFeign;
    private final AdditionalFieldFeign additionalFieldFeign;
    private final TmpVoucherFeign tmpVoucherFeign;
    private final DetailPaymentVoucherFeign detailPaymentVoucherFeign;
    private final AnticipoFeign anticipoFeign;
    private final AdditionalFieldFeign aditionalFieldFeign;
    private final CuotaPaymentVoucherFeign cuotaPaymentVoucherFeign;
    private final GuiaPaymentFeign guiaPaymentFeign;

    private final PaymentVoucherFormatter paymentVoucherFormatter;

    @Value("${urlspublicas.descargaComprobante}")
    private String urlServiceDownload;

    private final TemplateService templateService;
    private final AmazonS3ClientService amazonS3ClientService;

    @Override
    public Map<String, Object> findPaymentVoucherWithFilter(
            String fechaEmisionDesde, String fechaEmisionHasta, String filtroTipoComprobante, String filtroRuc, String filtroSerie,
            Integer filtroNumero, Integer pageNumber, Integer perPage, Integer estadoSunats, Long idUsuario) {
        Integer idOficina = null;
        Integer numPagina = null;
        String estadoSunat = null;

        List<PaymentVoucherDto> result = null;
        Integer cantidad = null;
        List<PaymentVoucherDto> tsolespayment = null;
        BigDecimal tsolesnew = BigDecimal.ZERO;
        BigDecimal tdolaresnew = BigDecimal.ZERO;
        BigDecimal teurosnew = BigDecimal.ZERO;
        PaymentVoucherDto comprobanteMonedaSol = null;
        PaymentVoucherDto comprobanteMonedaDolar = null;
        PaymentVoucherDto comprobanteMonedaEur = null;

        try {
            UserDto usuarioLogueado = userFeign.obtenerUsuario(idUsuario);
            if(usuarioLogueado == null) {
                throw new ServiceException("Usuario no encontrado");
            }
            if(usuarioLogueado.getIdUsuario()!=null){
                idOficina = usuarioLogueado.getIdOficina();
            }
            numPagina = (pageNumber-1) * perPage;
            if(filtroNumero == null) filtroNumero = 0;
            if(idOficina == null) idOficina = 0;
            estadoSunat = estadoSunats.toString();

            result = paymentVoucherFeign.listarComprobantesConFiltros(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, numPagina, perPage);

            cantidad = paymentVoucherFeign.contarComprobantes(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, numPagina, perPage);

            tsolespayment = paymentVoucherFeign.obtenerTotalSolesGeneral(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, pageNumber, perPage);

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
    public Map<String, Object> generatePaymentVoucher(PaymentVoucher paymentVoucher, boolean isEdit, Long idUsuario) {
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
        return generateDocument(paymentVoucher, isEdit, idUsuario);
    }

    Map<String, Object> generateDocument(PaymentVoucher comprobante, Boolean isEdit, Long idUsuario) {
        Map<String, Object> resultado = new HashMap<>();
        ResponsePSE response;
        boolean status = false;
        String messageResponse;
        String nombreDocumento = "";
        PaymentVoucherEntity comprobanteCreado = null;

        try {
            log.info("GENERANDO COMPROBANTE - {} - {}", comprobante.getSerie(), comprobante.getNumero());
            /*NORMALIZACION TECNICA DE DATOS DEL DTO*/
            paymentVoucherFormatter.formatPaymentVoucher(comprobante);

            /*SETEAMOS DATOS DE NEGOCIO NECESARIOS DEL DTO*/
            Company company = completarDatosEmisor(comprobante, isEdit);
            setCodigoTipoOperacionCatalog(comprobante);
            setOficinaId(comprobante, company);

            /*VALIDAMOS SI EL COMPROBANTE ES UNA FACTURA O NOTA ASOCIADA*/
            boolean isFacturaOrNoteAssociated = isfacturaOrNoteAsociated(comprobante.getTipoComprobante());

            /*VALIDAMOS SI ESTA EDITANDO*/
            Pair<String, PaymentVoucher> messageAndPayment = getMessageResponseAndPaymentVoucherOld(isEdit, comprobante);
            messageResponse = messageAndPayment.getLeft();
            PaymentVoucher paymentVoucherOld = messageAndPayment.getRight();

            /*SETEAMOS EL ESTADO ITEM*/
            Integer estadoItem = getEstadoItem(isFacturaOrNoteAssociated, isEdit, paymentVoucherOld);

            /*GENERAMOS PLANTILLA XML DE ACUERDO A SU OSE*/
            Map<String, String> plantillaGenerado = generarPlantillaXml(company, comprobante);

            /*SETEAMOS DATOS DESDE LA PLANTILLA GENERADA*/
            nombreDocumento = plantillaGenerado.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            String fileXMLZipBase64 = plantillaGenerado.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
            String fileXMLBase64 = plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64);

            /*SE SUBE FORMATO XML DEL COMPROBANTE A AMAZON Y SE GUARDA REGISTRO EN BASE DE DATOS*/
            RegisterFileUpload archivoSubido = subirXmlComprobante(company, nombreDocumento, comprobante.getTipoComprobante(), ConstantesParameter.REGISTRO_STATUS_NUEVO, fileXMLZipBase64);

            /*SETEAMOS DATOS*/
            String estadoRegistro = EstadoComprobanteEnum.REGISTRADO.getCodigo();
            String estadoEnSunat = EstadoSunatEnum.NO_ENVIADO.getAbreviado();
            comprobante.setCodigoHash(plantillaGenerado.get(ConstantesParameter.CODIGO_HASH));

            /*REGISTRAMOS EL COMPROBANTE A BASE DE DATOS*/
            comprobanteCreado = registrarComprobante(comprobante, archivoSubido.getIdRegisterFileSend(), isEdit, paymentVoucherOld, estadoRegistro, estadoRegistro, estadoEnSunat, estadoItem, messageResponse, "s-admin", null,
                    new Timestamp(Calendar.getInstance().getTime().getTime()), null, OperacionLogEnum.REGISTER_PAYMENT_VOUCHER
            );

            /*REGISTRAMOS EL VOUCHER TEMPORAL*/
            if(isFacturaOrNoteAssociated) {
                registerVoucherTemporal(comprobanteCreado.getIdPaymentVoucher(), nombreDocumento, UUIDGen.generate(), comprobante.getTipoComprobante(), isEdit);
                SendBillDto dataSendBill = SendBillDto.builder().ruc(comprobante.getRucEmisor()).idPaymentVoucher(comprobante.getIdPaymentVoucher())
                        .nameDocument(nombreDocumento).envioAutomaticoSunat(company.getEnvioAutomaticoSunat() == null || company.getEnvioAutomaticoSunat()).build();
                resultado.put(ConstantesParameter.PARAM_BEAN_SEND_BILL, dataSendBill);
            }
            status = true;
        } catch (TemplateException | SignedException e) {
            messageResponse = "Error al generar plantilla del documento[" + comprobante.getIdentificadorDocumento() + "] " + e.getMessage();
        }  catch (Exception e) {
            messageResponse = e.getMessage();
        }
        if(!status) {
            throw new ServiceException(messageResponse);
        }
        response = ResponsePSE.builder().mensaje(messageResponse).estado(status).nombre(nombreDocumento).build();
        transformarUrlsAResponse(response, comprobanteCreado);

        resultado.put("idPaymentVoucher", comprobanteCreado.getIdPaymentVoucher());
        resultado.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, response);

        validateAutomaticDelivery(resultado);
        return resultado;
    }

    private Map<String, String> generarPlantillaXml(Company company, PaymentVoucher comprobante) throws IOException, NoSuchAlgorithmException {
        Map<String, String> plantillaGenerado = new HashMap<>();
        /*GENERAMOS PLANTILLA XML DE ACUERDO A SU OSE*/
        if (company.getOseId() != null && company.getOseId() == 1) {
            plantillaGenerado = templateService.buildPaymentVoucherSignOse(comprobante);
        } else if (company.getOseId() != null && company.getOseId() == 2) {
            plantillaGenerado = templateService.buildPaymentVoucherSignOseBliz(comprobante);
        } else if (company.getOseId() != null && (company.getOseId() == 10 || company.getOseId() == 12)) {
            plantillaGenerado = templateService.buildPaymentVoucherSignCerti(comprobante);
        } else {
            plantillaGenerado = templateService.buildPaymentVoucherSign(comprobante);
        }
        log.info("PLANTILLA GENERADA: {}", plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64));
        return plantillaGenerado;
    }

    private Integer getEstadoItem(boolean isFacturaOrNoteAssociated, boolean isEdit, PaymentVoucher paymentVoucherOld) {
        Integer estadoItem = null;
        if (!isFacturaOrNoteAssociated) {
            //SI ESTA EDITANDO UNA BOLETA O NOTAS ASOCIADAS A BOLETAS
            if (isEdit) {
                //SI EL COMPROBANTE YA ESTA REGISTRADO EN SUNAT, EL ESTADO DE RESUMEN ES 2 MODIFICACION
                if (paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())) {
                    estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION;
                    //SI NO EL ESTADO ES 1 ADICIONAR
                } else {
                    estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION;
                }
            }
            //SI NO ESTA EDITANDO EL ESTADO PARA RESUMEN ES 1 ADICIONAR
            else {
                estadoItem = ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION;
            }
        }
        return estadoItem;
    }

    private Pair<String, PaymentVoucher> getMessageResponseAndPaymentVoucherOld(boolean isEdit, PaymentVoucher comprobante) {
        String messageResponse = "";
        PaymentVoucher paymentVoucherOld = null;
        if(isEdit) {
            messageResponse = ConstantesParameter.MSG_EDICION_DOCUMENTO_OK;
            paymentVoucherOld = paymentVoucherFeign.findPaymentVoucherByRucAndTipoComprobanteAndSerieAndNumero(
                    comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
            System.out.println("PAYMENTVOUCHER OLD: "+paymentVoucherOld);
            if (paymentVoucherOld == null)
                throw new ServiceException("Este comprobante que desea editar, no existe en la base de datos del PSE");

            if ((!paymentVoucherOld.getEstado().equals(EstadoComprobanteEnum.REGISTRADO.getCodigo()) &&
                    !paymentVoucherOld.getEstado().equals(EstadoComprobanteEnum.ERROR.getCodigo()))
                    || paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())
                    || paymentVoucherOld.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())
            ) {
                throw new ServiceException("Este comprobante no se puede editar, ya fue declarado a Sunat.");
            }
        } else {
            messageResponse = ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK;
        }
        return Pair.of(messageResponse, paymentVoucherOld);
    }

    private boolean isfacturaOrNoteAsociated(String tipoComprobante) {
        return !tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                && !tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO);
    }

    private void setOficinaId(PaymentVoucher comprobante, Company company) {
        if (Boolean.TRUE.equals(company.getAllowSaveOficina()) && comprobante.getOficinaId() == null) {
            OficinaDto oficinaDto = branchOfficeFeign.obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
                    company.getId(), comprobante.getSerie(), comprobante.getTipoComprobante());
            if(oficinaDto!=null) {
                if (oficinaDto.getId() != null) {
                    comprobante.setOficinaId(oficinaDto.getId());
                }
            }
        }
    }

    private void setCodigoTipoOperacionCatalog(PaymentVoucher paymentVoucher) {
        if (paymentVoucher.getCodigoTipoOperacion() != null) {
            if (paymentVoucher.getCodigoTipoOperacion().trim().length() == 4) {
                paymentVoucher.setCodigoTipoOperacionCatalogo51(paymentVoucher.getCodigoTipoOperacion());
            } else {
                switch (paymentVoucher.getCodigoTipoOperacion()) {
                    case "01":
                    case "04":
                        paymentVoucher.setCodigoTipoOperacionCatalogo51("0101");
                        break;
                    case "02":
                        paymentVoucher.setCodigoTipoOperacionCatalogo51("0200");
                        break;
                    default:
                        paymentVoucher.setCodigoTipoOperacionCatalogo51("0101");
                        break;
                }
            }
        } else {
            paymentVoucher.setCodigoTipoOperacionCatalogo51("0101");
        }
    }

    private void validateAutomaticDelivery(Map<String, Object> result) {
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_BILL) != null) {
            SendBillDto dataSendBill = (SendBillDto) result.get(ConstantesParameter.PARAM_BEAN_SEND_BILL);
            if (dataSendBill.getEnvioAutomaticoSunat()) {
                System.out.println("Enviar mensaje");
                //messageProducer.produceSendBill(dataSendBill);
            }
        }
    }

    private Company completarDatosEmisor(PaymentVoucher paymentVoucher, boolean isEdit) {
        Company company = companyFeign.findCompanyByRuc(paymentVoucher.getRucEmisor());
        paymentVoucher.setRucEmisor(company.getRuc());
        paymentVoucher.setDenominacionEmisor(company.getRazon());
        paymentVoucher.setTipoDocumentoEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        paymentVoucher.setNombreComercialEmisor(company.getNombreComer());
        paymentVoucher.setUblVersion(company.getUblVersion() != null ? company.getUblVersion() : ConstantesSunat.UBL_VERSION_2_0);

        /*OJO - VALIDAR SI ESTO SE TIENE QUE ELIMINAR*/
        if (!isEdit && (company.getSimultaneo() != null && company.getSimultaneo())) {
            Integer proximoNumero;
            proximoNumero = getProximoNumero(paymentVoucher.getTipoComprobante(), paymentVoucher.getSerie(), paymentVoucher.getRucEmisor());
            if (proximoNumero > paymentVoucher.getNumero()) {
                paymentVoucher.setNumero(proximoNumero);
            }
        }
        return company;
    }

    private Integer getProximoNumero(String tipoDocumento, String serie, String ruc) {
        Integer ultimoComprobante = paymentVoucherFeign.obtenerSiguienteNumeracionPorTipoComprobanteYSerieYRucEmisor(tipoDocumento, serie, ruc);
        if (ultimoComprobante != null) {
            return ultimoComprobante + 1;
        } else {
            return 1;
        }
    }

    private RegisterFileUpload subirXmlComprobante(
            Company company, String nombreDocumento, String tipoDocumento,
            String estadoRegistro, String archivoXMLZipBase64) {
        RegisterFileUpload archivo = amazonS3ClientService.subirArchivoAlStorage(UtilArchivo.b64ToByteArrayInputStream(archivoXMLZipBase64),
                nombreDocumento, "invoice", company);
        log.info("ARVHIVO SUBIDO: {}", archivo.toString());
        return archivo;
    }

    private PaymentVoucherEntity registrarComprobante(
            PaymentVoucher comprobante, Long idArchivoRegistro, Boolean isEdit,
            PaymentVoucher antiguoComprobante, String estado, String estadoAnterior, String estadoEnSunat,
            Integer estadoItem, String mensajeRespuesta, String registroUsuario, String usuarioModificacion,
            Timestamp fechaRegistro, Timestamp fechaModificacion, OperacionLogEnum operacionLogEnum) {

        PaymentVoucherEntity entity = new PaymentVoucherEntity();

        log.info("SEGUIMIENTO COMPROBANTE - FACTURA - ND - NC - [OPERACION: {}]", operacionLogEnum);
        log.info("VOUCHER: {}",comprobante);

        if(isEdit) {
            List<ComprobanteItem> items = antiguoComprobante.getItems();
            List<Anticipo> anticipos = antiguoComprobante.getAnticipos();
            List<CampoAdicional> adicionales = antiguoComprobante.getCamposAdicionales();
            List<ComprobanteCuota> cuotas = antiguoComprobante.getCuotas();
            List<GuiaRelacionada> guias = antiguoComprobante.getGuiasRelacionadas();

            if (items != null && !items.isEmpty()) {
                for (ComprobanteItem item : items) {
                    //historialStockService.eliminarHistorialStockByDetail(item);
                    System.out.println("Eliminar items - stock del comprobante");
                }
                for (ComprobanteItem item : items) {
                    detailPaymentVoucherFeign.deleteDetailPaymentVoucherById(item.getIdComprobanteDetalle());
                    System.out.println("Eliminar items del comprobante");
                }
            }
            if (anticipos != null && !anticipos.isEmpty()) {
                for (Anticipo anticipo : anticipos) {
                    anticipoFeign.deleteAnticipoById(anticipo.getIdAnticipoPayment());
                    System.out.println("Eliminar anticipos del comprobante");
                }
            }
            if (adicionales != null && !adicionales.isEmpty()) {
                for (CampoAdicional adicional : adicionales) {
                    aditionalFieldFeign.deleteAditionalFieldPaymentById(Long.valueOf(adicional.getId()));
                    System.out.println("Eliminar campos adicionales del comprobante");
                }
            }
            if (cuotas != null && !cuotas.isEmpty()) {
                for (ComprobanteCuota cuota : cuotas) {
                    cuotaPaymentVoucherFeign.deletePaymentCuotaById(cuota.getIdCuotas());
                    System.out.println("Eliminar cuotas del comprobante");
                }
            }
            if (guias != null && !guias.isEmpty()) {
                for (GuiaRelacionada guia : guias) {
                    guiaPaymentFeign.deleteGuiaPaymentById(guia.getIdPaymentVoucher());
                    System.out.println("Eliminar guias relacionadas del comprobante");
                }
            }
            comprobante.setIdPaymentVoucher(antiguoComprobante.getIdPaymentVoucher());
            comprobante.setUuid(antiguoComprobante.getUuid());
            comprobante.setPaymentVoucherFileList(antiguoComprobante.getPaymentVoucherFileList());
            comprobante.setFechaRegistro(fechaRegistro);

            //entity.setBranchOfficeEntity(antiguoComprobante.getBranchOfficeEntity());
            //entity.setIdPaymentVoucher(antiguoComprobante.getIdPaymentVoucher());
            //entity.setUuid(antiguoComprobante.getUuid());
            //entity.setPaymentVoucherFileEntityList(antiguoComprobante.getPaymentVoucherFileEntityList());
            //entity.setFechaRegistro(antiguoComprobante.getFechaRegistro());
        }
/*
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
*/
        comprobante.setIdentificadorDocumento(comprobante.getRucEmisor()+ "-" +comprobante.getTipoComprobante()+ "-" +
                comprobante.getSerie()+ "-" +comprobante.getNumero());
        /*
        entity.setIdentificadorDocumento(comprobante.getRucEmisor()+ "-" +comprobante.getTipoComprobante()+ "-" +
                comprobante.getSerie()+ "-" +comprobante.getNumero());
        */
        comprobante.setEstado(estado);
        comprobante.setEstadoAnterior(estadoAnterior);
        comprobante.setEstadoItem(estadoItem);
        comprobante.setEstadoSunat(estadoEnSunat);
        comprobante.setMensajeRespuesta(mensajeRespuesta);
        /*
        entity.setEstado(estado);
        entity.setEstadoAnterior(estadoAnterior);
        entity.setEstadoItem(estadoItem);
        entity.setEstadoSunat(estadoEnSunat);
        entity.setMensajeRespuesta(mensajeRespuesta);
*/
        if(!isEdit) {
            comprobante.setFechaRegistro(fechaRegistro);
            comprobante.setUserName(registroUsuario);
            /*
            entity.setFechaRegistro(fechaRegistro);
            entity.setUserName(registroUsuario);*/
        } else {
            comprobante.setFechaModificacion(antiguoComprobante.getFechaModificacion());
            comprobante.setUserName(antiguoComprobante.getUserName());
            /*
            entity.setFechaModificacion(antiguoComprobante.getFechaModificacion());
            entity.setUserName(antiguoComprobante.getUserName());*/
        }

        comprobante.setFechaModificacion(fechaModificacion);
        comprobante.setUserNameModificacion(usuarioModificacion);
        /*
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
*/

        /*INFORMACION DE ARCHIVOS*/
        if (idArchivoRegistro != null) {
            List<PaymentVoucherFile> comprobantesArchivosList = new ArrayList<>();
            PaymentVoucherFile comprobanteArchivo = PaymentVoucherFile.builder()
                    .orden(1)
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                    .idRegisterFileSend(idArchivoRegistro)
                    .tipoArchivo(TipoArchivoEnum.XML.name())
                    .build();
            comprobantesArchivosList.add(comprobanteArchivo);
            //entity.setPaymentVoucherFileEntityList(comprobantesArchivosList);
            comprobante.setPaymentVoucherFileList(comprobantesArchivosList);
        }
        System.out.println("LISTA: "+comprobante.getPaymentVoucherFileList());

        /*INFORMACION DE ANTICIPOS*/
        /*if (comprobante.getAnticipos() != null && !comprobante.getAnticipos().isEmpty()) {
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
        }*/

        /*INFORMACION DE CAMPOS ADICIONALES*/
        if (comprobante.getCamposAdicionales() != null && !comprobante.getCamposAdicionales().isEmpty()) {
            for (CampoAdicional campoAdicional : comprobante.getCamposAdicionales()) {
                Integer campoAdicionalId = additionalFieldFeign.findAditionalFieldIdByValorCampo(campoAdicional.getNombreCampo());
                campoAdicional.setCampoAdicionalEntityId(campoAdicionalId);
            }
        }

        /*INFORMACION DE CUOTAS*/
    /*    if (comprobante.getCuotas() != null && !comprobante.getCuotas().isEmpty()) {
            List<CuotasPaymentVoucherEntity> cuotasPaymentVoucherEntityList = new ArrayList<>();
            for (ComprobanteCuota cuota : comprobante.getCuotas()) {
                CuotasPaymentVoucherEntity centity = CuotasPaymentVoucherEntity.builder()
                        .numero(cuota.getNumero())
                        .monto(cuota.getMonto())
                        .fecha(cuota.getFecha())
                        .build();
                cuotasPaymentVoucherEntityList.add(centity);
            }
            entity.setCuotasEntityList(cuotasPaymentVoucherEntityList);
        }
*/
        boolean existGuiaRelacionada = false;
        if (comprobante.getGuiasRelacionadas() != null && !comprobante.getGuiasRelacionadas().isEmpty()) {
            for (GuiaRelacionada guiaRelacionada : comprobante.getGuiasRelacionadas()) {
                if (guiaRelacionada.getIdguiaremision() != null) {
                    existGuiaRelacionada = true;
                }
            }
        }

        //List<DetailsPaymentVoucherEntity> detailsPaymentVoucherEntityList = new ArrayList<>();
        for (ComprobanteItem item : comprobante.getItems()) {
            item.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
            /*
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
            detailsPaymentVoucherEntityList.add(detailEntity);

            * */
        }
        //entity.setDetailsPaymentVoucherEntityList(detailsPaymentVoucherEntityList);
        //entity.setOficinaId(comprobante.getOficinaId());

        if (registroUsuario != null && entity.getOficinaId() == null) {
            if (!registroUsuario.equals(ConstantesSunat.SUPERADMIN)) {
                UserEntity user = userFeign.findByUserByUsername(registroUsuario);
                comprobante.setOficinaId(user.getOficinaId());
                //entity.setOficinaId(user.getOficinaId());
            }
        }
        //entity.setUuid(UUIDGen.generate());
        comprobante.setUuid(UUIDGen.generate());
        comprobante.setFechaEmisionDate(new Date());
        PaymentVoucherEntity comprobanteCreado = paymentVoucherFeign.savePaymentVoucher(comprobante);

        log.info("COMPROBANTE CREADO: {}", comprobanteCreado.toString());
        return comprobanteCreado;
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
            tmpEntity = tmpVoucherFeign.findTmpVoucherByIdPaymentVoucher(idPaymentVoucher);
        }
        if (tmpEntity == null) {
            tmpEntity = new TmpVoucherSendBillEntity();
        }
        tmpEntity.setEstado(EstadoVoucherTmpEnum.PENDIENTE.getEstado());
        tmpEntity.setIdPaymentVoucher(idPaymentVoucher);
        tmpEntity.setNombreDocumento(nombreCompletoDocumento);
        tmpEntity.setUuidSaved(uuidSaved);
        tmpEntity.setTipoComprobante(tipoComprobante);

        tmpVoucherFeign.saveTmpVoucher(tmpEntity);
    }
}
