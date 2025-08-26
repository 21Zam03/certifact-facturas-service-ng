package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.*;
import com.certicom.certifact_facturas_service_ng.dto.others.*;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.entity.*;
import com.certicom.certifact_facturas_service_ng.enums.*;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.feign.*;
import com.certicom.certifact_facturas_service_ng.formatter.PaymentVoucherFormatter;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.PlantillaService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.util.UUIDGen;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.google.common.collect.ImmutableMap;
import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
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

    private final PaymentVoucherFormatter paymentVoucherFormatter;

    @Value("${urlspublicas.descargaComprobante}")
    private String urlServiceDownload;

    private final PaymentVoucherFeign comprobanteFeign;
    private final TmpVoucherFeign tmpVoucherFeign;
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
            UserInterDto usuarioLogueado = userFeign.obtenerUsuario(idUsuario);
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
        return generateDocument(paymentVoucherDto, isEdit, idUsuario);
    }

    @Override
    public PaymentVoucherDto prepareComprobanteForEnvioSunatInter(String finalRucEmisor, String tipoComprobante, String serieDocumento, Integer numeroDocumento) throws ServiceException {
        PaymentVoucherDto paymentVoucherDto = comprobanteFeign.
                findPaymentVoucherByRucAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento(finalRucEmisor, tipoComprobante, serieDocumento, numeroDocumento);

        if (paymentVoucherDto == null)
            throw new ServiceException(String.format("%s [%s-%s-%s-%s]", "El comprobante que desea enviar a la Sunat, no existe: ", finalRucEmisor, tipoComprobante, serieDocumento, numeroDocumento != null ? numeroDocumento.toString() : ""));
        System.out.println("RESULTA: "+paymentVoucherDto.toString());
        if (paymentVoucherDto.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado()))
            throw new ServiceException("Este comprobante ya se encuentra aceptado en Sunat.");

        if (paymentVoucherDto.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado()))
            throw new ServiceException("Este comprobante se encuentra anulado en Sunat.");

        if (paymentVoucherDto.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA))
            throw new ServiceException("Por este metodo solo se permite enviar Facturas, Notas de crédito y Débito.");


        if (paymentVoucherDto.getTipoComprobanteAfectado() != null) {
            if (
                    (paymentVoucherDto.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO))
            ) {
                throw new ServiceException("Por este metodo solo se permite enviar Notas de crédito y Débito asociadas a Facturas, las notas asociadas a boletas se deben enviar por resumen diario.");
            }
        }
        return paymentVoucherDto;
    }

    Map<String, Object> generateDocument(PaymentVoucherDto comprobante, Boolean isEdit, Long idUsuario) {
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
            CompanyDto companyDto = completarDatosEmisor(comprobante, isEdit);
            setCodigoTipoOperacionCatalog(comprobante);
            setOficinaId(comprobante, companyDto);

            /*VALIDAMOS SI EL COMPROBANTE ES UNA FACTURA O NOTA ASOCIADA*/
            boolean isFacturaOrNoteAssociated = isfacturaOrNoteAsociated(comprobante.getTipoComprobante());

            /*VALIDAMOS SI ESTA EDITANDO*/
            Pair<String, PaymentVoucherEntity> messageAndPayment = getMessageResponseAndPaymentVoucherOld(isEdit, comprobante);
            messageResponse = messageAndPayment.getLeft();
            PaymentVoucherEntity paymentVoucherOld = messageAndPayment.getRight();

            /*SETEAMOS EL ESTADO ITEM*/
            Integer estadoItem = getEstadoItem(isFacturaOrNoteAssociated, isEdit, paymentVoucherOld);

            /*GENERAMOS PLANTILLA XML DE ACUERDO A SU OSE*/
            Map<String, String> plantillaGenerado = generarPlantillaXml(companyDto, comprobante);

            /*SETEAMOS DATOS DESDE LA PLANTILLA GENERADA*/
            nombreDocumento = plantillaGenerado.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            String fileXMLZipBase64 = plantillaGenerado.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
            String fileXMLBase64 = plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64);

            /*SE SUBE FORMATO XML DEL COMPROBANTE A AMAZON Y SE GUARDA REGISTRO EN BASE DE DATOS*/
            RegisterFileUploadEntity archivoSubido = subirXmlComprobante(companyDto, nombreDocumento, comprobante.getTipoComprobante(), ConstantesParameter.REGISTRO_STATUS_NUEVO, fileXMLZipBase64);

            /*SETEAMOS DATOS*/
            String estadoRegistro = EstadoComprobanteEnum.REGISTRADO.getCodigo();
            String estadoEnSunat = EstadoSunatEnum.NO_ENVIADO.getAbreviado();
            comprobante.setCodigoHash(plantillaGenerado.get(ConstantesParameter.CODIGO_HASH));

            /*REGISTRAMOS EL COMPROBANTE A BASE DE DATOS*/
            comprobanteCreado = registrarComprobante(comprobante, archivoSubido.getIdRegisterFileSend(), isEdit, paymentVoucherOld, estadoRegistro, estadoRegistro, estadoEnSunat, estadoItem, messageResponse, "", null,
                    new Timestamp(Calendar.getInstance().getTime().getTime()), null, OperacionLogEnum.REGISTER_PAYMENT_VOUCHER
            );

            /*REGISTRAMOS EL VOUCHER TEMPORAL*/
            if(isFacturaOrNoteAssociated) {
                registerVoucherTemporal(comprobanteCreado.getIdPaymentVoucher(), nombreDocumento, UUIDGen.generate(), comprobante.getTipoComprobante(), isEdit);
                SendBillDto dataSendBill = SendBillDto.builder().ruc(comprobante.getRucEmisor()).idPaymentVoucher(comprobante.getIdPaymentVoucher())
                        .nameDocument(nombreDocumento).envioAutomaticoSunat(companyDto.getEnvioAutomaticoSunat() == null || companyDto.getEnvioAutomaticoSunat()).build();
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

    private Map<String, String> generarPlantillaXml(CompanyDto companyDto, PaymentVoucherDto comprobante) throws IOException, NoSuchAlgorithmException {
        Map<String, String> plantillaGenerado = new HashMap<>();
        /*GENERAMOS PLANTILLA XML DE ACUERDO A SU OSE*/
        if (companyDto.getOseId() != null && companyDto.getOseId() == 1) {
            plantillaGenerado = plantillaService.buildPaymentVoucherSignOse(comprobante);
        } else if (companyDto.getOseId() != null && companyDto.getOseId() == 2) {
            plantillaGenerado = plantillaService.buildPaymentVoucherSignOseBliz(comprobante);
        } else if (companyDto.getOseId() != null && (companyDto.getOseId() == 10 || companyDto.getOseId() == 12)) {
            plantillaGenerado = plantillaService.buildPaymentVoucherSignCerti(comprobante);
        } else {
            plantillaGenerado = plantillaService.buildPaymentVoucherSign(comprobante);
        }
        log.info("PLANTILLA GENERADA: {}", plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64));
        return plantillaGenerado;
    }

    private Integer getEstadoItem(boolean isFacturaOrNoteAssociated, boolean isEdit, PaymentVoucherEntity paymentVoucherOld) {
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

    private Pair<String, PaymentVoucherEntity> getMessageResponseAndPaymentVoucherOld(boolean isEdit, PaymentVoucherDto comprobante) {
        String messageResponse = "";
        PaymentVoucherEntity paymentVoucherOld = null;
        if(isEdit) {
            messageResponse = ConstantesParameter.MSG_EDICION_DOCUMENTO_OK;
            paymentVoucherOld = comprobanteFeign.findPaymentVoucherByRucAndTipoComprobanteAndSerieAndNumero(
                    comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());

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

    private void setOficinaId(PaymentVoucherDto comprobante, CompanyDto companyDto) {
        if (Boolean.TRUE.equals(companyDto.getAllowSaveOficina()) && comprobante.getOficinaId() == null) {
            OficinaDto oficinaDto = branchOfficeFeign.obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
                    companyDto.getId(), comprobante.getSerie(), comprobante.getTipoComprobante());
            if(oficinaDto!=null) {
                if (oficinaDto.getId() != null) {
                    comprobante.setOficinaId(oficinaDto.getId());
                }
            }
        }
    }

    private void setCodigoTipoOperacionCatalog(PaymentVoucherDto paymentVoucher) {
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

    private CompanyDto completarDatosEmisor(PaymentVoucherDto paymentVoucherDto, boolean isEdit) {
        CompanyDto companyDto = companyFeign.findCompanyByRuc(paymentVoucherDto.getRucEmisor());
        paymentVoucherDto.setRucEmisor(companyDto.getRuc());
        paymentVoucherDto.setDenominacionEmisor(companyDto.getRazon());
        paymentVoucherDto.setTipoDocumentoEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        paymentVoucherDto.setNombreComercialEmisor(companyDto.getNombreComer());
        paymentVoucherDto.setUblVersion(companyDto.getUblVersion() != null ? companyDto.getUblVersion() : ConstantesSunat.UBL_VERSION_2_0);

        if (!isEdit && (companyDto.getSimultaneo() != null && companyDto.getSimultaneo())) {
            Integer proximoNumero;
            proximoNumero = getProximoNumero(paymentVoucherDto.getTipoComprobante(), paymentVoucherDto.getSerie(), paymentVoucherDto.getRucEmisor());
            if (proximoNumero > paymentVoucherDto.getNumero()) {
                paymentVoucherDto.setNumero(proximoNumero);
            }
        }
        return companyDto;
    }

    private Integer getProximoNumero(String tipoDocumento, String serie, String ruc) {
        Integer ultimoComprobante = comprobanteFeign.obtenerSiguienteNumeracionPorTipoComprobanteYSerieYRucEmisor(tipoDocumento, serie, ruc);
        if (ultimoComprobante != null) {
            return ultimoComprobante + 1;
        } else {
            return 1;
        }
    }

    private RegisterFileUploadEntity subirXmlComprobante(
            CompanyDto companyDto, String nombreDocumento, String tipoDocumento,
            String estadoRegistro, String archivoXMLZipBase64) {
        RegisterFileUploadEntity archivo = amazonS3ClientService.subirArchivoAlStorage(UtilArchivo.b64ToByteArrayInputStream(archivoXMLZipBase64),
                nombreDocumento, "invoice", companyDto);
        log.info("ARVHIVO SUBIDO: {}", archivo.toString());
        return archivo;
    }

    private PaymentVoucherEntity registrarComprobante(
            PaymentVoucherDto comprobante, Long idArchivoRegistro, Boolean isEdit,
            PaymentVoucherEntity antiguoComprobante, String estado, String estadoAnterior, String estadoEnSunat,
            Integer estadoItem, String mensajeRespuesta, String registroUsuario, String usuarioModificacion,
            Timestamp fechaRegistro, Timestamp fechaModificacion, OperacionLogEnum operacionLogEnum) {

        PaymentVoucherEntity entity = new PaymentVoucherEntity();

        log.info("SEGUIMIENTO COMPROBANTE - FACTURA - ND - NC - [OPERACION: {}]", operacionLogEnum);
        log.info("VOUCHER: {}",comprobante);

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
            entity.setPaymentVoucherFileEntityList(antiguoComprobante.getPaymentVoucherFileEntityList());
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
            List<PaymentVoucherFileEntity> comprobantesArchivosList = new ArrayList<>();
            PaymentVoucherFileEntity comprobanteArchivo = PaymentVoucherFileEntity.builder()
                    .orden(1)
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                    .idRegisterFileSend(idArchivoRegistro)
                    .tipoArchivo(TipoArchivoEnum.XML.name())
                    .build();
            comprobantesArchivosList.add(comprobanteArchivo);
            entity.setPaymentVoucherFileEntityList(comprobantesArchivosList);
            System.out.println("ARCHIVOS: "+entity.getPaymentVoucherFileEntityList());
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
                Integer campoAdicionalId = additionalFieldFeign.obtenerCampoAdicionalIdPorNombre(campoAdicional.getNombreCampo());
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
            comprobanteDetalleEntityList.add(detailEntity);
        }
        entity.setComprobanteDetalleEntityList(comprobanteDetalleEntityList);
        entity.setOficinaId(comprobante.getOficinaId());

        if (registroUsuario != null && entity.getOficinaId() == null) {
            if (!registroUsuario.equals(ConstantesSunat.SUPERADMIN)) {
                UserEntity user = userFeign.findByUserByUsername(registroUsuario);
                entity.setOficinaId(user.getOficinaId());
            }
        }
        entity.setUuid(UUIDGen.generate());
        System.out.println("ARCHIVOS: "+entity.getPaymentVoucherFileEntityList());
        PaymentVoucherEntity comprobanteCreado = paymentVoucherFeign.savePaymentVoucher(entity);
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
