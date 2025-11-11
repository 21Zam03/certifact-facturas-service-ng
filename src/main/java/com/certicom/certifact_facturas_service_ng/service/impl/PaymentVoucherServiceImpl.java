package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.UserDto;
import com.certicom.certifact_facturas_service_ng.dto.others.*;
import com.certicom.certifact_facturas_service_ng.entity.*;
import com.certicom.certifact_facturas_service_ng.enums.*;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.exceptions.SignedException;
import com.certicom.certifact_facturas_service_ng.exceptions.TemplateException;
import com.certicom.certifact_facturas_service_ng.feign.*;
import com.certicom.certifact_facturas_service_ng.formatter.PaymentVoucherFormatter;
import com.certicom.certifact_facturas_service_ng.model.*;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.service.TemplateService;
import com.certicom.certifact_facturas_service_ng.sqs.SqsProducer;
import com.certicom.certifact_facturas_service_ng.util.*;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.google.common.collect.ImmutableMap;
import com.certicom.certifact_facturas_service_ng.service.PaymentVoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class PaymentVoucherServiceImpl implements PaymentVoucherService {

    private static final String CODSOLES = "PEN";
    private static final String CODDOLAR = "USD";
    private static final String CODEURO = "EUR";

    private final UserData userData;
    private final PaymentVoucherData paymentVoucherData;
    private final CompanyData companyData;
    private final BranchOfficeData branchOfficeData;
    private final AdditionalFieldData additionalFieldData;
    private final TmpVoucherData tmpVoucherData;
    private final DetailPaymentVoucherData detailPaymentVoucherData;
    private final AnticipoData anticipoData;
    private final CuotaPaymentVoucherData cuotaPaymentVoucherData;
    private final GuiaPaymentData guiaPaymentData;

    private final PaymentVoucherFormatter paymentVoucherFormatter;

    private final TemplateService templateService;
    private final AmazonS3ClientService amazonS3ClientService;

    private final SqsProducer sqsProducer;

    @Autowired
    public PaymentVoucherServiceImpl(
            UserData userData,
            PaymentVoucherData paymentVoucherData,
            CompanyData companyData,
            BranchOfficeData branchOfficeData,
            AdditionalFieldData additionalFieldData,
            TmpVoucherData tmpVoucherData,
            DetailPaymentVoucherData detailPaymentVoucherData,
            AnticipoData anticipoData,
            CuotaPaymentVoucherData cuotaPaymentVoucherData,
            GuiaPaymentData guiaPaymentData,
            PaymentVoucherFormatter paymentVoucherFormatter,
            TemplateService templateService,
            AmazonS3ClientService amazonS3ClientService,
            SqsProducer sqsProducer) {
        this.userData = userData;
        this.paymentVoucherData = paymentVoucherData;
        this.companyData = companyData;
        this.branchOfficeData = branchOfficeData;
        this.additionalFieldData = additionalFieldData;
        this.tmpVoucherData = tmpVoucherData;
        this.detailPaymentVoucherData = detailPaymentVoucherData;
        this.anticipoData = anticipoData;
        this.cuotaPaymentVoucherData = cuotaPaymentVoucherData;
        this.guiaPaymentData = guiaPaymentData;
        this.paymentVoucherFormatter = paymentVoucherFormatter;
        this.templateService = templateService;
        this.amazonS3ClientService = amazonS3ClientService;
        this.sqsProducer = sqsProducer;
    }

    @Value("${urlspublicas.descargaComprobante}")
    private String urlServiceDownload;

    @Override
    public Map<String, Object> findPaymentVoucherWithFilter(
            String fechaEmisionDesde, String fechaEmisionHasta, String filtroTipoComprobante, String filtroRuc, String filtroSerie,
            Integer filtroNumero, Integer pageNumber, Integer perPage, Integer estadoSunats, Long idUsuario) {
        Integer idOficina = null;
        Integer numPagina = null;
        String estadoSunat = null;

        List<PaymentVoucherDtoFilter> result = null;
        Integer cantidad = null;
        List<PaymentVoucherDtoFilter> tsolespayment = null;
        BigDecimal tsolesnew = BigDecimal.ZERO;
        BigDecimal tdolaresnew = BigDecimal.ZERO;
        BigDecimal teurosnew = BigDecimal.ZERO;
        PaymentVoucherDtoFilter comprobanteMonedaSol = null;
        PaymentVoucherDtoFilter comprobanteMonedaDolar = null;
        PaymentVoucherDtoFilter comprobanteMonedaEur = null;

        try {
            UserDto usuarioLogueado = userData.findUserById(idUsuario);
            if(usuarioLogueado == null) {
                LogHelper.errorLog(LogMessages.currentMethod(), "Usuario con id: "+idUsuario+" no existe en la base de datos");
                throw new ServiceException("Usuario con id: "+idUsuario+" no existe en la base de datos");
            }
            if(usuarioLogueado.getIdUser()!=null){
                idOficina = usuarioLogueado.getIdOficina();
            }
            numPagina = (pageNumber-1) * perPage;
            if(filtroNumero == null) filtroNumero = 0;
            if(idOficina == null) idOficina = 0;
            estadoSunat = estadoSunats.toString();

            result = paymentVoucherData.listPaymentVoucherWithFilter(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, numPagina, perPage);

            cantidad = paymentVoucherData.countPaymentVoucher(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, numPagina, perPage);

            tsolespayment = paymentVoucherData.getTotalSoles(usuarioLogueado.getRuc(), fechaEmisionDesde, fechaEmisionHasta, filtroTipoComprobante, filtroRuc,
                    filtroSerie, filtroNumero, idOficina, estadoSunat, pageNumber, perPage);

            comprobanteMonedaSol = tsolespayment.stream().filter(f -> f.getCodigoMoneda().equals(CODSOLES)).findFirst().orElse(null);

            if(comprobanteMonedaSol!=null) {
                tsolesnew = comprobanteMonedaSol.getImporteTotalVenta()!=null?comprobanteMonedaSol.getImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP):BigDecimal.ZERO;
            }

            comprobanteMonedaDolar = tsolespayment.stream().filter(f -> f.getCodigoMoneda().equals(CODDOLAR)).findFirst().orElse(null);
            if (comprobanteMonedaDolar!=null) {
                tdolaresnew = comprobanteMonedaDolar.getImporteTotalVenta()!=null?comprobanteMonedaDolar.getImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP):BigDecimal.ZERO;
            }

            comprobanteMonedaEur = tsolespayment.stream().filter(f -> f.getCodigoMoneda().equals(CODEURO)).findFirst().orElse(null);
            if (comprobanteMonedaEur!=null) {
                teurosnew = comprobanteMonedaEur.getImporteTotalVenta()!=null?comprobanteMonedaEur.getImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP):BigDecimal.ZERO;
            }
        } catch (Exception e) {
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error, "+ e.getMessage());
            throw new ServiceException("Ocurrio un error, ", e);
        }
        return ImmutableMap.of("comprobantesList", result, "cantidad", cantidad, "totalsoles", tsolesnew, "totaldolares", tdolaresnew, "totaleuros", teurosnew);
    }

    @Override
    public Map<String, Object> createPaymentVoucher(PaymentVoucherDto paymentVoucherDto, Long idUsuario) {
        return generateNewDocument(paymentVoucherDto,  idUsuario);
    }

    @Override
    public Map<String, Object> updatePaymentVoucher(PaymentVoucherDto paymentVoucherDto, Long idUsuario) {
        return updateExistingDocument(paymentVoucherDto, idUsuario);
    }

    @Override
    public Integer getSiguienteNumeroComprobante(String tipoDocumento, String serie, String ruc) {
        Integer ultimoComprobante = paymentVoucherData.getUltimoNumeroForNumeracion(tipoDocumento, serie, ruc);
        if (ultimoComprobante != null) {
            return ultimoComprobante + 1;
        }
        return 1;
    }

    @Override
    public List<PaymentVoucherDto> findComprobanteByAnticipo(String numDocIdentReceptor, String rucEmisor) {
        List<String> tipoComprobante = new ArrayList<>();
        tipoComprobante.add("01");
        tipoComprobante.add("03");

        if (numDocIdentReceptor == null) {
            numDocIdentReceptor = "";
        }

        List<PaymentVoucherDto> result = new ArrayList<>();
        List<PaymentVoucherDto> list = paymentVoucherData
                .findAllByTipoComprobanteInAndNumDocIdentReceptorAndRucEmisorAndTipoOperacionAndEstadoOrderByNumDocIdentReceptor(
                        tipoComprobante, numDocIdentReceptor, rucEmisor, "04", "02");

        List<PaymentVoucherDto> listDetracciones = paymentVoucherData
                .findAllByTipoComprobanteInAndNumDocIdentReceptorAndRucEmisorAndTipoOperacionAndEstadoOrderByNumDocIdentReceptor(
                        tipoComprobante, numDocIdentReceptor, rucEmisor, "1001", "02");


        for (PaymentVoucherDto vouch : list) {
            if (vouch.getAnticipos().size() <= 0) {
                result.add(vouch);

            }
        }

        for (PaymentVoucherDto vouch : listDetracciones) {

            if (vouch.getEstadoAnticipo() != null && vouch.getEstadoAnticipo() == 1) {
                if (vouch.getAnticipos().size() <= 0) {
                    result.add(vouch);
                }
            }
        }

        return result;
    }

    @Override
    public List<PaymentVoucherDto> findComprobanteByCredito(String numDocIdentReceptor, String rucEmisor) {
        if (numDocIdentReceptor == null) {
            numDocIdentReceptor = "";
        }

        List<PaymentVoucherDto> result = new ArrayList<>();
        List<PaymentVoucherDto> list = paymentVoucherData
                .getPaymentVocuherByCredito(numDocIdentReceptor, rucEmisor);

        for (PaymentVoucherDto vouch : list) {
            if (vouch.getAnticipos().size() <= 0) {
                result.add(vouch);

            }
        }

        return list;
    }

    @Override
    public List<InfoEstadoSunat> getEstadoSunatByListaIdsInter(List<Long> ids) {
        List<InfoEstadoSunat> respuesta = new ArrayList<>();
        List<PaymentVoucherDto> comprobantes = paymentVoucherData.findByIdPaymentVoucherInterList(ids);
        comprobantes.forEach(pv -> {
            respuesta.add(InfoEstadoSunat.builder().id(pv.getIdPaymentVoucher()).estado(pv.getEstado())
                    .estadoSunat(pv.getEstadoSunat()).build());
        });
        return respuesta;
    }

    /*
    Map<String, Object> generateDocument(PaymentVoucherModel comprobante, Boolean isEdit, Long idUsuario) {
        Map<String, Object> resultado = new HashMap<>();
        ResponsePSE response;
        boolean status = false;
        String messageResponse;
        String nombreDocumento = "";
        PaymentVoucherModel comprobanteCreado = null;

        try {
            log.info("GENERANDO COMPROBANTE - {} - {}", comprobante.getSerie(), comprobante.getNumero());

            paymentVoucherFormatter.formatPaymentVoucher(comprobante);


            CompanyModel companyModel = completarDatosEmisor(comprobante);
            setCodigoTipoOperacionCatalog(comprobante);
            setOficinaId(comprobante, companyModel);

            boolean isFacturaOrNoteAssociated = !comprobante.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                    && !comprobante.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO);

            Pair<String, PaymentVoucherModel> messageAndPayment = getMessageResponseAndPaymentVoucherOld(isEdit, comprobante);
            messageResponse = messageAndPayment.getLeft();
            PaymentVoucherModel paymentVoucherModelOld = messageAndPayment.getRight();

            Integer estadoItem = getEstadoItem(isFacturaOrNoteAssociated, isEdit, paymentVoucherModelOld);

            Map<String, String> plantillaGenerado = generarPlantillaXml(companyModel, comprobante);

            nombreDocumento = plantillaGenerado.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            String fileXMLZipBase64 = plantillaGenerado.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
            String fileXMLBase64 = plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64);

            RegisterFileUploadModel archivoSubido = subirXmlComprobante(companyModel, nombreDocumento, fileXMLZipBase64);

            String estadoRegistro = EstadoComprobanteEnum.REGISTRADO.getCodigo();
            String estadoEnSunat = EstadoSunatEnum.NO_ENVIADO.getAbreviado();
            comprobante.setCodigoHash(plantillaGenerado.get(ConstantesParameter.CODIGO_HASH));

            comprobanteCreado = registrarComprobante(comprobante, archivoSubido.getIdRegisterFileSend(), isEdit, paymentVoucherModelOld, estadoRegistro, estadoRegistro, estadoEnSunat, estadoItem, messageResponse, "s-admin", null,
                    new Timestamp(Calendar.getInstance().getTime().getTime()), null, OperacionLogEnum.REGISTER_PAYMENT_VOUCHER
            );

            if(isFacturaOrNoteAssociated) {
                registerVoucherTemporal(comprobanteCreado.getIdPaymentVoucher(), nombreDocumento, UUIDGen.generate(), comprobante.getTipoComprobante(), isEdit);
                SendBillDto dataSendBill = SendBillDto.builder().ruc(comprobante.getRucEmisor()).idPaymentVoucher(comprobante.getIdPaymentVoucher())
                        .nameDocument(nombreDocumento).envioAutomaticoSunat(companyModel.getEnvioAutomaticoSunat() == null || companyModel.getEnvioAutomaticoSunat()).build();
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

    * */

    Map<String, Object> generateNewDocument(PaymentVoucherDto paymentVoucherDto, Long idUsuario) {
        Map<String, Object> resultado = new HashMap<>();
        String messageResponse = ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK;
        PaymentVoucherDto comprobanteCreado = null;
        ResponsePSE response;
        boolean status = true;
        try {
            paymentVoucherFormatter.formatPaymentVoucher(paymentVoucherDto);
            CompanyModel companyModel = completarDatosEmisor(paymentVoucherDto);
            setCodigoTipoOperacionCatalog(paymentVoucherDto);
            setOficinaId(paymentVoucherDto, companyModel);
            setLeyenda(paymentVoucherDto);

            if ((companyModel.getSimultaneo() != null && companyModel.getSimultaneo())) {
                Integer proximoNumero;
                proximoNumero = getProximoNumero(paymentVoucherDto.getTipoComprobante(), paymentVoucherDto.getSerie(), paymentVoucherDto.getRucEmisor());
                if (proximoNumero > paymentVoucherDto.getNumero()) {
                    paymentVoucherDto.setNumero(proximoNumero);
                }
            }

            Map<String, String> plantillaGenerado = generarPlantillaXml(companyModel, paymentVoucherDto);
            paymentVoucherDto.setCodigoHash(plantillaGenerado.get(ConstantesParameter.CODIGO_HASH));

            RegisterFileUploadModel archivoSubido = subirXmlComprobante(companyModel, plantillaGenerado);

            String username = userData.getUsernameById(idUsuario);
            comprobanteCreado = saveVoucher(paymentVoucherDto, archivoSubido.getIdRegisterFileSend(), username);

            createTmpVoucher(comprobanteCreado);
            SendBillDto dataSendBill = SendBillDto.builder()
                    .ruc(comprobanteCreado.getRucEmisor())
                    .idPaymentVoucher(comprobanteCreado.getIdPaymentVoucher())
                    .nameDocument(comprobanteCreado.getIdentificadorDocumento())
                    .envioAutomaticoSunat(companyModel.getEnvioAutomaticoSunat() == null || companyModel.getEnvioAutomaticoSunat())
                    .build();
            resultado.put(ConstantesParameter.PARAM_BEAN_SEND_BILL, dataSendBill);
        } catch (TemplateException | SignedException e) {
            status = false;
            messageResponse = "Error al generar plantilla del documento[" + paymentVoucherDto.getIdentificadorDocumento() + "] " + e.getMessage();
        }  catch (Exception e) {
            status = false;
            messageResponse = e.getMessage();
        }
        if(!status) {
            throw new ServiceException(messageResponse);
        }
        response = createResponsePse(messageResponse, status, comprobanteCreado);

        resultado.put("idPaymentVoucher", comprobanteCreado.getIdPaymentVoucher());
        resultado.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, response);

        validateAutomaticDelivery((SendBillDto) resultado.get(ConstantesParameter.PARAM_BEAN_SEND_BILL));
        return resultado;
    }

    Map<String, Object> updateExistingDocument(PaymentVoucherDto paymentVoucherDto, Long idUsuario) {
        Map<String, Object> resultado = new HashMap<>();
        String messageResponse = ConstantesParameter.MSG_EDICION_DOCUMENTO_OK;
        PaymentVoucherDto comprobanteCreado = null;
        ResponsePSE response;
        boolean status = true;
        try {
            paymentVoucherFormatter.formatPaymentVoucher(paymentVoucherDto);
            UserDto userLogged = userData.findUserById(idUsuario);
            CompanyModel companyModel = completarDatosEmisor(paymentVoucherDto);
            setCodigoTipoOperacionCatalog(paymentVoucherDto);
            setOficinaId(paymentVoucherDto, companyModel);
            setLeyenda(paymentVoucherDto);

            PaymentVoucherDto paymentVoucherDtoOld = paymentVoucherData.findPaymentVoucherByRucAndTipoComprobanteAndSerieAndNumero(
                    paymentVoucherDto.getRucEmisor(), paymentVoucherDto.getTipoComprobante(), paymentVoucherDto.getSerie(), paymentVoucherDto.getNumero());
            if (paymentVoucherDtoOld == null)
                throw new ServiceException("Este comprobante que desea editar, no existe en la base de datos del PSE");

            if ((!paymentVoucherDtoOld.getEstado().equals(EstadoComprobanteEnum.REGISTRADO.getCodigo()) &&
                    !paymentVoucherDtoOld.getEstado().equals(EstadoComprobanteEnum.ERROR.getCodigo()))
                    || paymentVoucherDtoOld.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())
                    || paymentVoucherDtoOld.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado())
            ) {
                throw new ServiceException("Este comprobante no se puede editar, ya fue declarado a Sunat.");
            }

            Map<String, String> plantillaGenerado = generarPlantillaXml(companyModel, paymentVoucherDto);
            paymentVoucherDto.setCodigoHash(plantillaGenerado.get(ConstantesParameter.CODIGO_HASH));

            RegisterFileUploadModel archivoSubido = subirXmlComprobante(companyModel, plantillaGenerado);

            comprobanteCreado = updateVoucher(paymentVoucherDto, paymentVoucherDtoOld, archivoSubido.getIdRegisterFileSend(), userLogged.getNombreUsuario());

            updateTmpVoucher(comprobanteCreado);
            SendBillDto dataSendBill = SendBillDto.builder()
                    .ruc(comprobanteCreado.getRucEmisor())
                    .idPaymentVoucher(comprobanteCreado.getIdPaymentVoucher())
                    .nameDocument(comprobanteCreado.getIdentificadorDocumento())
                    .envioAutomaticoSunat(companyModel.getEnvioAutomaticoSunat() == null || companyModel.getEnvioAutomaticoSunat())
                    .build();
            resultado.put(ConstantesParameter.PARAM_BEAN_SEND_BILL, dataSendBill);
        } catch (TemplateException | SignedException e) {
            status = false;
            messageResponse = "Error al generar plantilla del documento[" + paymentVoucherDto.getIdentificadorDocumento() + "] " + e.getMessage();
        }  catch (Exception e) {
            status = false;
            messageResponse = e.getMessage();
        }
        if(!status) {
            throw new ServiceException(messageResponse);
        }
        response = createResponsePse(messageResponse, status, comprobanteCreado);

        resultado.put("idPaymentVoucher", comprobanteCreado.getIdPaymentVoucher());
        resultado.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, response);

        validateAutomaticDelivery((SendBillDto) resultado.get(ConstantesParameter.PARAM_BEAN_SEND_BILL));
        return resultado;
    }

    private PaymentVoucherDto saveVoucher(PaymentVoucherDto paymentVoucherDto, Long idRegisterFile, String username) {

        paymentVoucherDto.setIdentificadorDocumento(paymentVoucherDto.getRucEmisor()+ "-" + paymentVoucherDto.getTipoComprobante()+ "-" +
                paymentVoucherDto.getSerie()+ "-" + paymentVoucherDto.getNumero());
        paymentVoucherDto.setEstado(EstadoComprobanteEnum.REGISTRADO.getCodigo());
        paymentVoucherDto.setEstadoAnterior(EstadoComprobanteEnum.REGISTRADO.getCodigo());
        paymentVoucherDto.setEstadoItem(null);
        paymentVoucherDto.setEstadoSunat(EstadoSunatEnum.NO_ENVIADO.getAbreviado());
        paymentVoucherDto.setMensajeRespuesta(ConstantesParameter.MSG_REGISTRO_DOCUMENTO_OK);
        paymentVoucherDto.setFechaRegistro(new Timestamp(Calendar.getInstance().getTime().getTime()));
        paymentVoucherDto.setUserName(username);
        paymentVoucherDto.setFechaModificacion(null);
        paymentVoucherDto.setUserNameModificacion(null);

        if (idRegisterFile != null) {
            paymentVoucherDto.addPaymentVoucherFile(PaymentVoucherFileModel.builder()
                    .orden(1)
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                    .idRegisterFileSend(idRegisterFile)
                    .tipoArchivo(TipoArchivoEnum.XML.name())
                    .build());
        }

        for (ComprobanteItem item : paymentVoucherDto.getItems()) {
            item.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
        }

        if (username != null && paymentVoucherDto.getOficinaId() == null) {
            if (!username.equals(ConstantesSunat.SUPERADMIN)) {
                UserDto user = userData.findUserByUsername(username);
                paymentVoucherDto.setOficinaId(user.getIdOficina());
            }
        }

        paymentVoucherDto.setUuid(UUIDGen.generate());
        paymentVoucherDto.setFechaEmisionDate(new Date());

        PaymentVoucherDto result = paymentVoucherData.savePaymentVoucher(paymentVoucherDto);
        LogHelper.infoLog(LogMessages.currentMethod(), "Se registro el paymentvoucher exitosamente");
        return result;
    }

    private PaymentVoucherDto updateVoucher(PaymentVoucherDto paymentVoucherDto, PaymentVoucherDto paymentVoucherDtoOld, Long idRegisterFile, String username) {
        List<ComprobanteItem> items = paymentVoucherDtoOld.getItems();
        List<Anticipo> anticipos = paymentVoucherDtoOld.getAnticipos();
        List<CampoAdicional> adicionales = paymentVoucherDtoOld.getCamposAdicionales();
        List<ComprobanteCuota> cuotas = paymentVoucherDtoOld.getCuotas();
        List<GuiaRelacionada> guias = paymentVoucherDtoOld.getGuiasRelacionadas();

        if (items != null && !items.isEmpty()) {
            for (ComprobanteItem item : items) {
                //historialStockService.eliminarHistorialStockByDetail(item);
                System.out.println("Eliminar items - stock del comprobante");
            }
            for (ComprobanteItem item : items) {
                detailPaymentVoucherData.deleteDetailPaymentVoucherById(item.getIdComprobanteDetalle());
                System.out.println("Eliminar items del comprobante");
            }
        }
        if (anticipos != null && !anticipos.isEmpty()) {
            for (Anticipo anticipo : anticipos) {
                anticipoData.deleteAnticipoById(anticipo.getIdAnticipoPayment());
                System.out.println("Eliminar anticipos del comprobante");
            }
        }
        if (adicionales != null && !adicionales.isEmpty()) {
            for (CampoAdicional adicional : adicionales) {
                additionalFieldData.deleteAditionalFieldPaymentById(Long.valueOf(adicional.getId()));
                System.out.println("Eliminar campos adicionales del comprobante");
            }
        }
        if (cuotas != null && !cuotas.isEmpty()) {
            for (ComprobanteCuota cuota : cuotas) {
                cuotaPaymentVoucherData.deletePaymentCuotaById(cuota.getIdCuotas());
                System.out.println("Eliminar cuotas del comprobante");
            }
        }
        if (guias != null && !guias.isEmpty()) {
            for (GuiaRelacionada guia : guias) {
                guiaPaymentData.deleteGuiaPaymentById(guia.getIdPaymentVoucher());
                System.out.println("Eliminar guias relacionadas del comprobante");
            }
        }
        paymentVoucherDto.setIdPaymentVoucher(paymentVoucherDtoOld.getIdPaymentVoucher());
        paymentVoucherDto.setUuid(paymentVoucherDtoOld.getUuid());
        paymentVoucherDto.setPaymentVoucherFileModelList(paymentVoucherDtoOld.getPaymentVoucherFileModelList());
        paymentVoucherDto.setFechaRegistro(new Timestamp(Calendar.getInstance().getTime().getTime()));

        paymentVoucherDto.setIdentificadorDocumento(paymentVoucherDto.getRucEmisor()+ "-" + paymentVoucherDto.getTipoComprobante()+ "-" +
                paymentVoucherDto.getSerie()+ "-" + paymentVoucherDto.getNumero());

        paymentVoucherDto.setEstado(EstadoComprobanteEnum.REGISTRADO.getCodigo());
        paymentVoucherDto.setEstadoAnterior(EstadoComprobanteEnum.REGISTRADO.getCodigo());
        paymentVoucherDto.setEstadoItem(null);
        paymentVoucherDto.setEstadoSunat(EstadoSunatEnum.NO_ENVIADO.getAbreviado());
        paymentVoucherDto.setMensajeRespuesta(ConstantesParameter.MSG_EDICION_DOCUMENTO_OK);

        paymentVoucherDto.setFechaModificacion(paymentVoucherDtoOld.getFechaModificacion());
        paymentVoucherDto.setUserName(paymentVoucherDtoOld.getUserName());
        paymentVoucherDto.setUserNameModificacion(username);

        if (idRegisterFile != null) {
            paymentVoucherDto.addPaymentVoucherFile(PaymentVoucherFileModel.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                    .idRegisterFileSend(idRegisterFile)
                    .tipoArchivo(TipoArchivoEnum.XML.name())
                    .build());
        }

        for (ComprobanteItem item : paymentVoucherDto.getItems()) {
            item.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
        }

        if (username != null && paymentVoucherDto.getOficinaId() == null) {
            if (!username.equals(ConstantesSunat.SUPERADMIN)) {
                UserDto user = userData.findUserByUsername(username);
                paymentVoucherDto.setOficinaId(user.getIdOficina());
            }
        }

        paymentVoucherDto.setUuid(UUIDGen.generate());
        paymentVoucherDto.setFechaEmisionDate(new Date());

        return paymentVoucherData.savePaymentVoucher(paymentVoucherDto);
    }

    private void createTmpVoucher(PaymentVoucherDto paymentVoucherDto) {
        TmpVoucherSendBillEntity tmpEntity = new TmpVoucherSendBillEntity();
        tmpEntity.setEstado(EstadoVoucherTmpEnum.PENDIENTE.getEstado());
        tmpEntity.setIdPaymentVoucher(paymentVoucherDto.getIdPaymentVoucher());
        tmpEntity.setNombreDocumento(paymentVoucherDto.getIdentificadorDocumento());
        tmpEntity.setUuidSaved(UUIDGen.generate());
        tmpEntity.setTipoComprobante(paymentVoucherDto.getTipoComprobante());

        int result = tmpVoucherData.saveTmpVoucher(tmpEntity);
        if(result == 0){
            throw new ServiceException("Ocurrio un error al momento de registrar el tmpvoucher");
        } else {
            LogHelper.infoLog(LogMessages.currentMethod(), "Se registro el tmpvoucher exitosamente");
        }
    }

    private void updateTmpVoucher(PaymentVoucherDto paymentVoucherDto) {
        TmpVoucherSendBillEntity tmpEntity = tmpVoucherData.findTmpVoucherByIdPaymentVoucher(paymentVoucherDto.getIdPaymentVoucher());
        if (tmpEntity == null) {
            throw new ServiceException("No existe un registro temporal para el comprobante con ID " + paymentVoucherDto.getIdPaymentVoucher());
        }
        tmpEntity.setEstado(EstadoVoucherTmpEnum.PENDIENTE.getEstado());
        tmpEntity.setNombreDocumento(paymentVoucherDto.getIdentificadorDocumento());
        tmpEntity.setUuidSaved(UUIDGen.generate());
        tmpEntity.setTipoComprobante(paymentVoucherDto.getTipoComprobante());
        tmpVoucherData.saveTmpVoucher(tmpEntity);
    }

    private ResponsePSE createResponsePse(
            String messageResponse, boolean status,
            PaymentVoucherDto paymentVoucherDto) {
        ResponsePSE response = ResponsePSE.builder()
                .mensaje(messageResponse)
                .estado(status)
                .nombre(paymentVoucherDto.getIdentificadorDocumento())
                .build();
        transformarUrlsAResponse(response, paymentVoucherDto);
        return response;
    }

    private void setLeyenda(PaymentVoucherDto paymentVoucherDto) {
        if(paymentVoucherDto.getCodigoTipoOperacion() != null) {
            if (paymentVoucherDto.getCodigoTipoOperacion().equals("1001") || paymentVoucherDto.getCodigoTipoOperacion().equals("1002") ||
                    paymentVoucherDto.getCodigoTipoOperacion().equals("1003") || paymentVoucherDto.getCodigoTipoOperacion().equals("1004")) {
                Leyenda leyendaDto = Leyenda.builder()
                        .descripcion("Operación sujeta al Sistema de Pago de Obligaciones Tributarias con el Gobierno Central")
                        .codigo("2006")
                        .build();
                paymentVoucherDto.setLeyendas(new ArrayList<>());
                paymentVoucherDto.getLeyendas().add(leyendaDto);
            }
        }
    }

    private Map<String, String> generarPlantillaXml(CompanyModel companyModel, PaymentVoucherDto comprobante) throws IOException, NoSuchAlgorithmException {
        Map<String, String> plantillaGenerado = new HashMap<>();
        if (companyModel.getOseId() != null && companyModel.getOseId() == 1) {
            plantillaGenerado = templateService.buildPaymentVoucherSignOse(comprobante);
        } else if (companyModel.getOseId() != null && companyModel.getOseId() == 2) {
            plantillaGenerado = templateService.buildPaymentVoucherSignOseBliz(comprobante);
        } else if (companyModel.getOseId() != null && (companyModel.getOseId() == 10 || companyModel.getOseId() == 12)) {
            plantillaGenerado = templateService.buildPaymentVoucherSignCerti(comprobante);
        } else {
            plantillaGenerado = templateService.buildPaymentVoucherSign(comprobante);
        }

        String preview = plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64) != null &&
                plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64).length() > 200
                ? plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64).substring(0, 100) + "..."
                : plantillaGenerado.get(ConstantesParameter.PARAM_FILE_XML_BASE64);

        LogHelper.infoLog(LogMessages.currentMethod(), "Plantilla xml generada y firmada exitosamente "+preview);

        return plantillaGenerado;
    }

    private void setOficinaId(PaymentVoucherDto comprobante, CompanyModel companyModel) {
        if (Boolean.TRUE.equals(companyModel.getAllowSaveOficina()) && comprobante.getOficinaId() == null) {
            try {
                BranchOfficesModel branchOfficesModel = branchOfficeData.obtenerOficinaPorEmpresaIdYSerieYTipoComprobante(
                        companyModel.getId(), comprobante.getSerie(), comprobante.getTipoComprobante());
                if(branchOfficesModel !=null) {
                    if (branchOfficesModel.getId() != null) {
                        comprobante.setOficinaId(branchOfficesModel.getId());
                    }
                }
            } catch (Exception e) {
                LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error inesperado", e);
                throw new ServiceException(LogMessages.ERROR_UNEXPECTED, e);
            }
        }
    }

    private static final Map<String, String> OPERACION_MAP = Map.of(
            "01", "0101",
            "02", "0200",
            "04", "0502"
    );

    private void setCodigoTipoOperacionCatalog(PaymentVoucherDto paymentVoucherDto) {
        String codigo = paymentVoucherDto.getCodigoTipoOperacion();
        if (codigo != null) {
            codigo = codigo.trim();
            if (codigo.length() == 4) {
                paymentVoucherDto.setCodigoTipoOperacionCatalogo51(codigo);
            } else {
                paymentVoucherDto.setCodigoTipoOperacionCatalogo51(
                        OPERACION_MAP.getOrDefault(codigo, "0101")
                );
            }
        } else {
            paymentVoucherDto.setCodigoTipoOperacionCatalogo51("0101");
        }
    }

    private void validateAutomaticDelivery(SendBillDto dataSendBill) {
        if (dataSendBill.getEnvioAutomaticoSunat()) {
            sqsProducer.produceSendBill(dataSendBill);
        }
    }

    private CompanyModel completarDatosEmisor(PaymentVoucherDto paymentVoucherDto) {
        if (paymentVoucherDto.getRucEmisor() == null) {
            LogHelper.warnLog(LogMessages.currentMethod(), "El ruc emisor es null");
            throw new ServiceException("El RUC del emisor no puede ser nulo");
        }
        CompanyModel companyModel = companyData.findCompanyByRuc(paymentVoucherDto.getRucEmisor());
        if (companyModel == null) {
            LogHelper.warnLog(LogMessages.currentMethod(), "Company es null");
            throw new ServiceException("No se encontró la empresa con RUC: " + paymentVoucherDto.getRucEmisor());
        }
        paymentVoucherDto.setRucEmisor(companyModel.getRuc());
        paymentVoucherDto.setDenominacionEmisor(companyModel.getRazon());
        paymentVoucherDto.setTipoDocumentoEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        paymentVoucherDto.setNombreComercialEmisor(companyModel.getNombreComer());
        paymentVoucherDto.setUblVersion(companyModel.getUblVersion() != null ? companyModel.getUblVersion() : ConstantesSunat.UBL_VERSION_2_0);
        return companyModel;
    }

    private Integer getProximoNumero(String tipoDocumento, String serie, String ruc) {
        Integer ultimoComprobante = paymentVoucherData.obtenerSiguienteNumeracionPorTipoComprobanteYSerieYRucEmisor(tipoDocumento, serie, ruc);
        if (ultimoComprobante != null) {
            return ultimoComprobante + 1;
        } else {
            return 1;
        }
    }

    private RegisterFileUploadModel subirXmlComprobante(CompanyModel companyModel, Map<String, String> plantillaGenerado) {
        String nombreDocumento = plantillaGenerado.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
        String fileXMLZipBase64 = plantillaGenerado.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
        RegisterFileUploadModel archivo = amazonS3ClientService.subirArchivoAlStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64),
                nombreDocumento, "invoice", companyModel);
        LogHelper.infoLog(LogMessages.currentMethod(), "El archivo ha sido subido exitosamente");
        return archivo;
    }

    private PaymentVoucherDto registrarComprobante(
            PaymentVoucherDto comprobante, Long idArchivoRegistro, Boolean isEdit,
            PaymentVoucherDto antiguoComprobante, String estado, String estadoAnterior, String estadoEnSunat,
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
                    detailPaymentVoucherData.deleteDetailPaymentVoucherById(item.getIdComprobanteDetalle());
                    System.out.println("Eliminar items del comprobante");
                }
            }
            if (anticipos != null && !anticipos.isEmpty()) {
                for (Anticipo anticipo : anticipos) {
                    anticipoData.deleteAnticipoById(anticipo.getIdAnticipoPayment());
                    System.out.println("Eliminar anticipos del comprobante");
                }
            }
            if (adicionales != null && !adicionales.isEmpty()) {
                for (CampoAdicional adicional : adicionales) {
                    additionalFieldData.deleteAditionalFieldPaymentById(Long.valueOf(adicional.getId()));
                    System.out.println("Eliminar campos adicionales del comprobante");
                }
            }
            if (cuotas != null && !cuotas.isEmpty()) {
                for (ComprobanteCuota cuota : cuotas) {
                    cuotaPaymentVoucherData.deletePaymentCuotaById(cuota.getIdCuotas());
                    System.out.println("Eliminar cuotas del comprobante");
                }
            }
            if (guias != null && !guias.isEmpty()) {
                for (GuiaRelacionada guia : guias) {
                    guiaPaymentData.deleteGuiaPaymentById(guia.getIdPaymentVoucher());
                    System.out.println("Eliminar guias relacionadas del comprobante");
                }
            }
            comprobante.setIdPaymentVoucher(antiguoComprobante.getIdPaymentVoucher());
            comprobante.setUuid(antiguoComprobante.getUuid());
            comprobante.setPaymentVoucherFileModelList(antiguoComprobante.getPaymentVoucherFileModelList());
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
            List<PaymentVoucherFileModel> comprobantesArchivosList = new ArrayList<>();
            PaymentVoucherFileModel comprobanteArchivo = PaymentVoucherFileModel.builder()
                    .orden(1)
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO.name())
                    .idRegisterFileSend(idArchivoRegistro)
                    .tipoArchivo(TipoArchivoEnum.XML.name())
                    .build();
            comprobantesArchivosList.add(comprobanteArchivo);
            //entity.setPaymentVoucherFileEntityList(comprobantesArchivosList);
            comprobante.setPaymentVoucherFileModelList(comprobantesArchivosList);
        }
        System.out.println("LISTA: "+comprobante.getPaymentVoucherFileModelList());

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
                UserDto user = userData.findUserByUsername(registroUsuario);
                comprobante.setOficinaId(user.getIdOficina());
                //entity.setOficinaId(user.getOficinaId());
            }
        }
        //entity.setUuid(UUIDGen.generate());
        comprobante.setUuid(UUIDGen.generate());
        comprobante.setFechaEmisionDate(new Date());
        PaymentVoucherDto comprobanteCreado = paymentVoucherData.savePaymentVoucher(comprobante);

        log.info("COMPROBANTE CREADO: {}", comprobanteCreado.toString());
        return comprobanteCreado;
    }

    private void transformarUrlsAResponse(ResponsePSE response, PaymentVoucherDto paymentVoucherDto) {
        if (paymentVoucherDto != null) {
            String urlTicket = urlServiceDownload + "descargapdfuuid/" + paymentVoucherDto.getIdPaymentVoucher() + "/" + paymentVoucherDto.getUuid() + "/ticket/" + paymentVoucherDto.getIdentificadorDocumento();
            String urlA4 = urlServiceDownload + "descargapdfuuid/" + paymentVoucherDto.getIdPaymentVoucher() + "/" + paymentVoucherDto.getUuid() + "/a4/" + paymentVoucherDto.getIdentificadorDocumento();
            String urlXml = urlServiceDownload + "descargaxmluuid/" + paymentVoucherDto.getIdPaymentVoucher() + "/" + paymentVoucherDto.getUuid() + "/" + paymentVoucherDto.getIdentificadorDocumento();
            response.setUrlPdfTicket(urlTicket);
            response.setUrlPdfA4(urlA4);
            response.setUrlXml(urlXml);
            response.setCodigoHash(paymentVoucherDto.getCodigoHash());
        }
    }

}
