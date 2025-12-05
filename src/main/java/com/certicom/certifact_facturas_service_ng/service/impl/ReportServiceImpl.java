package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
import com.certicom.certifact_facturas_service_ng.dto.others.CampoAdicional;
import com.certicom.certifact_facturas_service_ng.entity.AditionalFieldEntity;
import com.certicom.certifact_facturas_service_ng.enums.TipoPdfEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.QRGenerationException;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.AdditionalFieldData;
import com.certicom.certifact_facturas_service_ng.feign.CompanyData;
import com.certicom.certifact_facturas_service_ng.feign.ParameterData;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherData;
import com.certicom.certifact_facturas_service_ng.model.CompanyModel;
import com.certicom.certifact_facturas_service_ng.model.ParameterModel;
import com.certicom.certifact_facturas_service_ng.service.ReportService;
import com.certicom.certifact_facturas_service_ng.util.Parameters;
import com.certicom.certifact_facturas_service_ng.util.StringsUtils;
import com.certicom.certifact_facturas_service_ng.util.UtilGenerateLetraNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReportServiceImpl implements ReportService {

    private final PaymentVoucherData paymentVoucherData;
    private final CompanyData companyData;
    private final ParameterData parameterData;
    private final AdditionalFieldData additionalFieldData;

    HashMap<Integer,String> mapTransaccion = new HashMap<Integer, String>();

    public ReportServiceImpl(PaymentVoucherData paymentVoucherData, CompanyData companyData, ParameterData parameterData, AdditionalFieldData additionalFieldData) {
        this.parameterData = parameterData;
        this.companyData = companyData;
        this.paymentVoucherData = paymentVoucherData;
        this.additionalFieldData = additionalFieldData;
        this.mapTransaccion.put(1,"CONTADO");
        this.mapTransaccion.put(2,"CREDITO");
    }

    @Override
    public ByteArrayInputStream getPdfComprobanteA4(String ruc, String tipo, String serie, Integer numero) throws QRGenerationException, ParseException {
        PaymentVoucherDto interDto = paymentVoucherData
                .findPaymentVoucherByRucAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento(ruc, tipo, serie, numero);
        if (interDto == null)
            throw new ServiceException("El comprobante que desea descargar no existe.");

        CompanyModel company = companyData.findCompanyByRuc(ruc);

        ByteArrayInputStream pdfStream;
        try {
            pdfStream = new ByteArrayInputStream(getPdfComprobantePorTipoFormatoInter(interDto, company, TipoPdfEnum.A4.getTipo()));

        } catch (Exception e) {

            throw e;
        }
        return pdfStream;
    }

    @Override
    public ByteArrayInputStream getPdfComprobanteuid(Long idPaymentVoucher, String uuid, String nameDocument, String tipoPdf) throws QRGenerationException, ParseException {
        /*
        PaymentVoucher comprobante = paymentVoucherFeign.findByIdPaymentVoucherAndUuid(idPaymentVoucher, uuid);
        if (comprobante == null) {
            throw new ServiceException("COMPROBANTE NO ENCONTRADO NULL");
        }
        if (!comprobante.getIdentificadorDocumento().trim().equalsIgnoreCase(nameDocument))
            throw new ServiceException("COMPROBANTE NO ENCONTRADO NAMEDOCUMENT");

        if (tipoPdf.equalsIgnoreCase(TipoPdfEnum.TICKET.getTipo()))
            return getPdfComprobanteTicket(comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
        else if (tipoPdf.equalsIgnoreCase(TipoPdfEnum.A4.getTipo()))
            return getPdfComprobanteA4(comprobante.getEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
        else throw new ServiceException("COMPROBANTE NO ENCONTRADO TICKETA4");
        * */
        return null;
    }

    @Override
    public ByteArrayInputStream getPdfComprobanteTicket(String ruc, String tipo, String serie, Integer numero) throws ServiceException, QRGenerationException, ParseException {
        /*
        PaymentVoucher interDto = paymentVoucherFeign
                .findByRucEmisorAndTipoComprobanteAndSerieAndNumero(ruc, tipo, serie, numero);
        if (interDto == null)
            throw new ServiceException("El comprobante que desea descargar no existe.");

        Company company = companyFeign.findCompanyByRuc(interDto.getRucEmisor());
        return new ByteArrayInputStream(getPdfComprobantePorTipoFormatoInter(interDto, company, TipoPdfEnum.TICKET.getTipo()));
        * */

        return null;
    }


    public byte[] getPdfComprobantePorTipoFormatoInter(PaymentVoucherDto comprobante, CompanyModel company, String tipoPdf)
            throws ServiceException, QRGenerationException, ParseException {

        /*
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00", otherSymbols);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, Object> params = new HashMap<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateVencimiento = null;
        if (comprobante.getFechaVencimiento() != null && !comprobante.getFechaVencimiento().isEmpty()) {
            dateVencimiento = format.parse(comprobante.getFechaVencimiento());
        }
        params.put("tipoComprobante", comprobante.getTipoComprobante());
        params.put("nombreComprobante", StringsUtils.getNombreTipoComprobante(comprobante.getTipoComprobante()));
        params.put("nombreComercial", company.getNombreComer());
        params.put("razonSocial", company.getRazon());
        params.put("telefono", company.getTelefono());
        params.put("email", company.getEmail());
        params.put("ruc", comprobante.getRucEmisor());
        params.put("direccion", company.getDireccion());
        params.put("ciudad", "");
        params.put("serie", comprobante.getSerie());
        ParameterModel parameterIGV = parameterData.findByName(company.getRubroHoreal()==true? Parameters.PARAM_IGV_HOREAL:Parameters.PARAM_IGV);
        params.put("porcentajeigv", new BigDecimal(parameterIGV.getValue()));
        params.put("numero", comprobante.getNumero());
        params.put("serieCorrelativo", String.format("%s - %s", comprobante.getSerie(), comprobante.getNumero().toString()));
        params.put("numDocCliente", comprobante.getNumeroDocumentoReceptor());
        if(comprobante.getTipoComprobante().equals("01") && comprobante.getTipoTransaccion()!=null){
            params.put("tipoTransaccion", this.mapTransaccion.get(comprobante.getTipoTransaccion().intValue()));
        }
        if (comprobante.getTipoDocumentoReceptor() != null)
            params.put("tipoDocCliente", StringsUtils.getNombreTipoDocumentoReceptor(comprobante.getTipoDocumentoReceptor()));
        params.put("codigoTipoDocumentoIdentidad", comprobante.getTipoDocumentoReceptor());
        params.put("nombreCliente", comprobante.getDenominacionReceptor());
        params.put("moneda", comprobante.getCodigoMoneda());
        params.put("direccionCliente", comprobante.getDireccionReceptor());
        params.put("cajero", "");
        params.put("fechaDate", comprobante.getFechaEmisionDate());
        params.put("fecha", comprobante.getFechaEmision());
        params.put("hora", comprobante.getHoraEmision());
        params.put("puntoVenta", "");
        String stringMoneda = comprobante.getCodigoMoneda() != null ? comprobante.getCodigoMoneda().equalsIgnoreCase("USD") ? "Dólares Americanos" : comprobante.getCodigoMoneda().equalsIgnoreCase("EUR") ? "Euros" :"Soles" : "Soles";
        String montoLetras = comprobante.getImporteTotalVenta() != null ? UtilGenerateLetraNumber.Convertir(comprobante.getImporteTotalVenta().setScale(2, BigDecimal.ROUND_HALF_UP).toString(), stringMoneda, true) : "";
        params.put("montoLetras", montoLetras);
        params.put("gratuita", comprobante.getTotalValorVentaGratuita() != null ? df.format(comprobante.getTotalValorVentaGratuita()) : "0.00");
        params.put("exonerada", comprobante.getTotalValorVentaExonerada() != null ? df.format(comprobante.getTotalValorVentaExonerada()) : "0.00");
        params.put("inafecta", comprobante.getTotalValorVentaInafecta() != null ? df.format(comprobante.getTotalValorVentaInafecta()) : "0.00");
        params.put("gravada", comprobante.getTotalValorVentaGravada() != null ? df.format(comprobante.getTotalValorVentaGravada()) : "0.00");
        params.put("exportada", comprobante.getTotalValorVentaExportacion() != null? df.format(comprobante.getTotalValorVentaExportacion()): "0.00");
        params.put("isc", comprobante.getTotalIsc() != null ? df.format(comprobante.getTotalIsc()) : "0.00");
        params.put("descuento", comprobante.getDescuentoGlobales() != null ? df.format(comprobante.getDescuentoGlobales()) : "0.00");
        params.put("subtotalpagar", df.format(
                        (comprobante.getImporteTotalVenta()!= null ?comprobante.getImporteTotalVenta():BigDecimal.ZERO).doubleValue() -
                                (comprobante.getDescuentoGlobales() != null ? comprobante.getDescuentoGlobales() : BigDecimal.ZERO ).doubleValue() -
                                (comprobante.getTotalIgv() != null ? comprobante.getTotalIgv() : BigDecimal.ZERO).doubleValue()
                )
        );
        params.put("totalPagar", comprobante.getImporteTotalVenta() != null ?
                df.format(((comprobante.getImporteTotalVenta().add(
                        (comprobante.getSumatoriaOtrosCargos() != null ?
                                comprobante.getSumatoriaOtrosCargos() : BigDecimal.ZERO))).setScale(2, RoundingMode.HALF_UP)).doubleValue()
                ) : null);
        params.put("subtotalDetraccion", comprobante.getImporteTotalVenta() != null ?
                df.format(comprobante.getImporteTotalVenta().subtract(comprobante.getMontoDetraccion()!=null?comprobante.getMontoDetraccion():BigDecimal.ZERO)) : "0.00");
        params.put("motivoemision", comprobante.getMotivoNota());
        params.put("detraccion", comprobante.getDetraccion() != null ? comprobante.getDetraccion(): "N");
        params.put("retencionDetraccion", comprobante.getRetencion() != null ? df.format(comprobante.getRetencion()): "0");
        params.put("montoDetraccion", comprobante.getMontoDetraccion() != null ? df.format(comprobante.getMontoDetraccion()): "0.00");
        params.put("montoRetencionDetraccion", comprobante.getMontoRetencion() != null ? df.format(comprobante.getMontoRetencion()): "0.00");
        params.put("montoPendiente", comprobante.getMontoPendiente() != null ? df.format(comprobante.getMontoPendiente()): "0.00");
        params.put("ocargos", comprobante.getSumatoriaOtrosCargos() != null ? df.format(comprobante.getSumatoriaOtrosCargos()) : "0.00");
        params.put("isProduction", "");
        params.put("ordenCompra", comprobante.getOrdenCompra());
        params.put("fechaVencimiento", comprobante.getFechaVencimiento());
        params.put("fechaVencimientoDate", dateVencimiento);
        params.put("estado_sunat", comprobante.getEstadoSunat());
        params.put("mensajeSunat", comprobante.getMensajeRespuesta());
        params.put("porcentajeDetraccion", comprobante.getPorcentajeDetraccion() != null ? comprobante.getPorcentajeDetraccion().toString() : "0");
        params.put("porcentajeRetencionDetraccion", comprobante.getPorcentajeRetencion() != null ? comprobante.getPorcentajeRetencion().toString() : "0");
        params.put("codigoBienDetraccion", comprobante.getCodigoBienDetraccion());
        params.put("descBienDetraccion", comprobante.getCodigoBienDetraccion()==null?"":StringsUtils.getDescBienDetraccion(comprobante.getCodigoBienDetraccion()));
        params.put("descMedioPago", comprobante.getCodigoMedioPago()==null?"":StringsUtils.getDescMedioPago(comprobante.getCodigoMedioPago()));
        params.put("codigoMedioPago", comprobante.getCodigoMedioPago()==null?"":comprobante.getCodigoMedioPago());
        params.put("codigoProducto", company.getViewCode() == null ? false : company.getViewCode());
        params.put("motivoNota", comprobante.getMotivoNota() == null ? "" : comprobante.getMotivoNota());

        params.put("codigoHash", comprobante.getCodigoHash());
        params.put("totalImpuestoOpGrat", comprobante.getTotalImpOperGratuita() != null ? df.format(comprobante.getTotalImpOperGratuita()) : "0.00");
        params.put("codigoTipoNotaCredito", comprobante.getCodigoTipoNotaCredito());
        params.put("codigoTipoNotaDebito", comprobante.getCodigoTipoNotaDebito());
        if (comprobante.getCodigoTipoNotaCredito() != null)
            params.put("motivoTipoNotaCredito", StringsUtils.getTipoNotaCredito(comprobante.getCodigoTipoNotaCredito()));
        if (comprobante.getCodigoTipoNotaDebito() != null)
            params.put("motivoTipoNotaDebito", StringsUtils.getTipoNotaDebito(comprobante.getCodigoTipoNotaDebito()));
        if (comprobante.getTipoComprobanteAfectado() != null && comprobante.getSerieAfectado() != null) {
            String documentoAfectado = StringsUtils.getNombreTipoComprobanteNoElectro(comprobante.getTipoComprobanteAfectado()) + " " + comprobante.getSerieAfectado() + "-" + comprobante.getNumeroAfectado();
            params.put("documentoAfectado", documentoAfectado);
            params.put("serieAfectado", comprobante.getSerieAfectado());
            params.put("numeroAfectado", comprobante.getNumeroAfectado());

        }
        params.put("numeroCuentaTodas", new ArrayList<>());


        List<AditionalFieldEntity> adicionales = new ArrayList<>();
        List<CampoAdicional> fieldInterDtos = additionalFieldData.getAditionalsByPaymentVoucher(comprobante.getId());
        for (CampoAdicional bean : fieldInterDtos) {
            AditionalFieldEntity result = new AditionalFieldEntity();
            result.setValorCampo(bean.getValorCampo());
            result.setNombreCampo(bean.getNameCampo());
            adicionales.add(result);
        }
        params.put("aditionalFields", adicionales);

        List<CuotasPaymentInterDto> cuotasPaymentInterDtos = cuotaPaymentVoucherRepository
                .getCuotasByPaymentVoucher(comprobante.getId());
        List<PaymentVoucherCuotaString> cuotas = new ArrayList<>();
        for (CuotasPaymentInterDto cuota : cuotasPaymentInterDtos) {
            PaymentVoucherCuotaString cuo = new PaymentVoucherCuotaString();
            cuo.setNumero(cuota.getNumero()==null?"":cuota.getNumero().toString());
            cuo.setFecha(cuota.getFecha()==null?"":cuota.getFecha());
            cuo.setMonto(cuota.getMonto()==null?"":df.format(cuota.getMonto()));
            cuotas.add(cuo);
        }
        params.put("cuotasList", cuotas);
        List<CuentaInterDto> cuentaInterDtos = cuentaRepository.getCuentasByCompany(company.getId().toString());
        List<String> cuentas = new ArrayList<>();
        cuentas.addAll(cuentaInterDtos.stream()
                .filter(item -> item.getDetraccion() != null && item.getDetraccion())
                .map(cu -> cu.getNombre() + "-" + cu.getNumero())
                .collect(Collectors.toList()));
        params.put("numeroCuenta", String.join(", ", cuentas));

        List<CuentaEntity> cuentasNoD = new ArrayList<>();
        CuentaEntity cuentaEntity = new CuentaEntity();
        for (CuentaInterDto cuenta : cuentaInterDtos){

            if (cuenta.getDetraccion()==null || !cuenta.getDetraccion()){
                cuentaEntity = new CuentaEntity();
                cuentaEntity.setName(cuenta.getNombre());
                cuentaEntity.setNumber(cuenta.getNumero());
                cuentaEntity.setDetraccion(cuenta.getDetraccion());
                cuentaEntity.setCci(cuenta.getCci());
                cuentasNoD.add(cuentaEntity);
            }
        }
        params.put("numeroCuentaNoD", cuentasNoD);

        Map<String, String> aditionalMap = new HashMap<>();
        for (AditionalFieldInterDto adit : fieldInterDtos) {
            aditionalMap.put(adit.getNameCampo(), adit.getValorCampo());
        }
        String listGuiasRemisionRemitente = "";
        List<GuiaRelacionInterDto> guiaRelacionInterDtos = guiaRelacionadaRepository.getGuiasRelacionByPayment(comprobante.getId());
        List<GuiaRelacionadaEntity> guiaRelacionadaEntities = new ArrayList<>();
        GuiaRelacionadaEntity guiaRelacionadaEntity = null;
        for (GuiaRelacionInterDto guia : guiaRelacionInterDtos) {
            guiaRelacionadaEntity = new GuiaRelacionadaEntity();
            guiaRelacionadaEntity.setSerieNumeroGuia(guia.getSerieNumeroGuia());
            guiaRelacionadaEntity.setCodigoTipoGuia(guia.getCodigoTipoGuia());
            aditionalMap.put(StringsUtils.getNombreTipoComprobante(guia.getCodigoTipoGuia()), guia.getSerieNumeroGuia());
            listGuiasRemisionRemitente = listGuiasRemisionRemitente + guia.getSerieNumeroGuia() + " , ";
            guiaRelacionadaEntities.add(guiaRelacionadaEntity);
        }
        params.put("guiasRemision", guiaRelacionadaEntities);
        params.put("aditionalMap", aditionalMap);

        if (listGuiasRemisionRemitente != null && listGuiasRemisionRemitente.length() > 0) {
            params.put("listGuiasRemisionRemitente", listGuiasRemisionRemitente.substring(0, listGuiasRemisionRemitente.length() - 2));
        }

        //AQUI CONTINUAR
        if (company.getIdRegisterFileSend() != null) {
            params.put("urlImage", String.format("%s%s", urlstorageURL, company.getIdRegisterFileSend().toString()));
        }


        File fileTemp = null;
        try {
            fileTemp = File.createTempFile("temp", ".png");
        } catch (IOException ex) {
            throw new QRGenerationException(ex.getMessage());
        }
        File fileTempGenerated = QR.generateQR(fileTemp, String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s", comprobante.getRucEmisor(),
                comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero().toString(),
                (comprobante.getSumatoriaIGV() == null ? "0.00" : comprobante.getSumatoriaIGV().toString()), comprobante.getMontoImporteTotalVenta(),
                comprobante.getFechaEmision(), (comprobante.getTipoDocIdentReceptor() == null ? "" : comprobante.getTipoDocIdentReceptor()),
                comprobante.getNumDocIdentReceptor()), 300, 300);
        params.put("qr", fileTempGenerated.getAbsoluteFile().toString());

        List<InvoicePrintLine> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        List<DetailsPaymentInterDto> detailsPaymentInterDtos = detailsPaymentVoucherRepository
                .getDetailsByPayment(comprobante.getId());


        if (company.getCantComproDina()>0) {
            int totalMax;
            if (company.getCantComproDina() > detailsPaymentInterDtos.size()) {
                totalMax = company.getCantComproDina();
            } else {
                totalMax = detailsPaymentInterDtos.size();
            }
            for (int i = 0; i < totalMax; i++) {
                if (detailsPaymentInterDtos.size() > i) {
                    InvoicePrintLine invoicePrintLine = new InvoicePrintLine();

                    String codigo = detailsPaymentInterDtos.get(i).getCodigoProducto();
                    String codetypeigv = detailsPaymentInterDtos.get(i).getCodigoTipoAfectacionIGV();
                    String descripcion = detailsPaymentInterDtos.get(i).getDescripcion();
                    String codigoSunat = detailsPaymentInterDtos.get(i).getCodigoProductoSunat();
                    String unidadManejo = detailsPaymentInterDtos.get(i).getUnidadManejo();
                    String instruccionesEspeciales = detailsPaymentInterDtos.get(i).getInstruccionesEspeciales();
                    String marca = detailsPaymentInterDtos.get(i).getMarca();
                    String montoref = detailsPaymentInterDtos.get(i).getMontoBaseGratuito()!=null?
                            detailsPaymentInterDtos.get(i).getMontoBaseGratuito().setScale(2, RoundingMode.HALF_UP).toString():"";
                    String codigoDescripcion = (codigo != null ? codigo : "") + " " + (descripcion != null ? descripcion : "");
                    invoicePrintLine.setNume(i + 1);
                    invoicePrintLine.setCodigo(codigo);
                    invoicePrintLine.setDescripcion(descripcion);
                    invoicePrintLine.setCodigoSunat(codigoSunat);
                    invoicePrintLine.setCodetypeigv(codetypeigv);
                    invoicePrintLine.setCodigoDescripcion(codigoDescripcion);
                    invoicePrintLine.setMontoReferencial(montoref);
                    invoicePrintLine.setAdicional(detailsPaymentInterDtos.get(i).getAdicional()!=null?detailsPaymentInterDtos.get(i).getAdicional():"");
                    if(unidadManejo != null){
                        invoicePrintLine.setUnidadManejo(unidadManejo);
                    }
                    if(instruccionesEspeciales != null){
                        invoicePrintLine.setInstruccionesEspeciales(instruccionesEspeciales);
                    }
                    if(marca != null){
                        invoicePrintLine.setMarca(marca);
                    }
                    invoicePrintLine.setCantidad(detailsPaymentInterDtos.get(i).getCantidad().toString());
                    invoicePrintLine.setCantidadsincero((detailsPaymentInterDtos.get(i).getCantidad().toString()).split("\\.")[0]);
                    invoicePrintLine.setDescuento(detailsPaymentInterDtos.get(i).getDescuento() != null ?
                            df.format(detailsPaymentInterDtos.get(i).getDescuento()) : "0.00");
                    invoicePrintLine.setTotal(df.format(detailsPaymentInterDtos.get(i).getValorVenta()
                            .add(detailsPaymentInterDtos.get(i).getAfectacionIGV() == null ? (BigDecimal.ZERO) :
                                    detailsPaymentInterDtos.get(i).getAfectacionIGV()).setScale(2, BigDecimal.ROUND_HALF_UP)));

                    if (detailsPaymentInterDtos.get(i).getPrecioVentaUnitario() != null) {
                        invoicePrintLine.setPrecioUnitario(df.format(detailsPaymentInterDtos.get(i).getPrecioVentaUnitario()));

                    } else {
                        invoicePrintLine.setPrecioUnitario(invoicePrintLine.getTotal());
                    }


                    if (detailsPaymentInterDtos.get(i).getValorUnitario().compareTo(new BigDecimal("0.00")) > 0) {
                        invoicePrintLine.setPrecioUnitarioSinIGV(df.format(detailsPaymentInterDtos.get(i).getValorUnitario()));

                    } else {
                        if (detailsPaymentInterDtos.get(i).getValorReferencialUnitario() != null) {
                            invoicePrintLine.setPrecioUnitarioSinIGV(df.format(detailsPaymentInterDtos.get(i).getValorReferencialUnitario()));
                        } else {
                            invoicePrintLine.setPrecioUnitarioSinIGV("0.00");
                        }
                    }

                    invoicePrintLine.setTotalSinIGV(df.format(detailsPaymentInterDtos.get(i).getValorVenta()));

                    if(Integer.parseInt(detailsPaymentInterDtos.get(i).getCodigoTipoAfectacionIGV()) != 21 &&
                            Integer.parseInt(detailsPaymentInterDtos.get(i).getCodigoTipoAfectacionIGV()) != 13){
                        subtotal = subtotal.add(detailsPaymentInterDtos.get(i).getValorVenta());
                    }

                    invoicePrintLine.setUnidad(detailsPaymentInterDtos.get(i).getCodigoUnidadMedida());

                    UnitCode unitCode = unitCodeRepository.findByCode(detailsPaymentInterDtos.get(i).getCodigoUnidadMedida());
                    invoicePrintLine.setUnidadNombre(unitCode != null ? unitCode.getDescription() : detailsPaymentInterDtos.get(i).getCodigoUnidadMedida());


                    items.add(invoicePrintLine);
                } else {
                    items.add(null);
                }
            }
        } else {
            Integer i = 1;
            for (DetailsPaymentInterDto item : detailsPaymentInterDtos) {
                InvoicePrintLine invoicePrintLine = new InvoicePrintLine();
                String codigo = item.getCodigoProducto();
                String descripcion = item.getDescripcion();
                String codigoDescripcion = (codigo != null ? codigo : "") + " " + (descripcion != null ? descripcion : "");
                invoicePrintLine.setNume(i);
                invoicePrintLine.setCodigo(codigo);
                invoicePrintLine.setDescripcion(item.getDescripcion());
                invoicePrintLine.setCodigoDescripcion(codigoDescripcion);
                invoicePrintLine.setCantidad(item.getCantidad().toString());
                invoicePrintLine.setCantidadsincero((item.getCantidad().toString()).split("\\.")[0]);
                invoicePrintLine.setCodetypeigv(item.getCodigoTipoAfectacionIGV());
                invoicePrintLine.setDescuento(item.getDescuento() != null ? df.format(item.getDescuento()) : "0.00");
                invoicePrintLine.setAdicional(item.getAdicional()!= null ? item.getAdicional():"");
                invoicePrintLine.setTotal(df.format(item.getValorVenta().add(item.getAfectacionIGV() == null ?
                        (new BigDecimal(0)) : item.getAfectacionIGV()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                if (item.getPrecioVentaUnitario() == null)
                    invoicePrintLine.setPrecioUnitario(invoicePrintLine.getTotal());
                else
                    invoicePrintLine.setPrecioUnitario(df.format(item.getPrecioVentaUnitario()));
                invoicePrintLine.setPrecioUnitarioSinIGV(df.format(item.getValorUnitario()));
                invoicePrintLine.setTotalSinIGV(df.format(item.getValorVenta()));


                if(item.getCodigoTipoAfectacionIGV()!=null){
                    if(Integer.parseInt(item.getCodigoTipoAfectacionIGV()) != 21 && Integer.parseInt(item.getCodigoTipoAfectacionIGV()) != 13){
                        subtotal = subtotal.add(item.getValorVenta());
                    }
                }


                invoicePrintLine.setUnidad(item.getCodigoUnidadMedida());

                UnitCode unitCode = unitCodeRepository.findByCode(item.getCodigoUnidadMedida());
                invoicePrintLine.setUnidadNombre(unitCode != null ? unitCode.getDescription() : item.getCodigoUnidadMedida());
                invoicePrintLine.setCodetypeigv(item.getCodigoTipoAfectacionIGV());
                invoicePrintLine.setCodigo(item.getCodigoProducto());
                items.add(invoicePrintLine);
                i++;
            }
        }
        BigDecimal subtotalDescuento = (comprobante.getTotalValorVentaOperacionGravada() != null ?
                (comprobante.getTotalValorVentaOperacionGravada()) : BigDecimal.ZERO).subtract(comprobante.getTotalDescuento() != null
                ? comprobante.getTotalDescuento() : BigDecimal.ZERO);
        BigDecimal subtotalConDescuento = (comprobante.getTotalValorVentaOperacionGravada() != null ?
                (comprobante.getTotalValorVentaOperacionGravada()) : BigDecimal.ZERO).add(comprobante.getTotalDescuento() != null
                ? comprobante.getTotalDescuento() : BigDecimal.ZERO);

        params.put("subtotalDescuento", df.format(subtotalDescuento).toString());
        params.put("subtotalConDescuento", df.format(subtotalConDescuento).toString());
        BigDecimal decimalsum = new BigDecimal(0);
        List<AnticipoPaymentInterDto> anticipoPaymentInterDtos = anticipoRepository.getAnticiposByPayment(comprobante.getId());
        AtomicReference<BigDecimal> sumatoriaAnticipos = new AtomicReference<>();
        sumatoriaAnticipos.set(BigDecimal.ZERO);
        anticipoPaymentInterDtos.forEach(ant -> {
            InvoicePrintLine invoicePrintLine = new InvoicePrintLine();
            String textAnticipo = String.format("%s : %s %s-%s", "Anticipo", StringsUtils.getNombreTipoComprobanteResumido(ant.getTipoDocumentoAnticipo()),
                    ant.getSerieAnticipo(), ant.getNumeroAnticipo().toString());
            PaymentVoucherInterDto compAnticipo = paymentVoucherRepository
                    .findByRucEmisorAndTipoComprobanteAndSerieAndNumeroV2(comprobante.getRucEmisor(),
                            ant.getTipoDocumentoAnticipo().equals(Catalogo12.FACTURA_ANTICIPOS.getCodigo()) ? "01" : "03",
                            ant.getSerieAnticipo(), ant.getNumeroAnticipo());
            invoicePrintLine.setDescripcion(compAnticipo != null ? textAnticipo + ", con fecha: (" + compAnticipo.getFechaEmision() + ")" : textAnticipo);
            invoicePrintLine.setCantidad("");
            invoicePrintLine.setPrecioUnitarioSinIGV(null);
            invoicePrintLine.setTotal("-" + df.format(ant.getMontoAnticipo()));

            invoicePrintLine.setTotalSinIGV("-" + df.format(ant.getMontoAnticipo().divide(new BigDecimal(1.18),2,RoundingMode.HALF_UP)));
            OptionalInt index = IntStream.range(0, items.size())
                    .filter(userInd -> items.get(userInd) == null)
                    .findFirst();
            if (index.isPresent()) {
                items.set(index.getAsInt(), invoicePrintLine);
            } else {
                items.add(invoicePrintLine);
            }

            sumatoriaAnticipos.set(sumatoriaAnticipos.get().add(ant.getMontoAnticipo()));

        });
        decimalsum = sumatoriaAnticipos.get().divide(new BigDecimal(1.18),2,RoundingMode.HALF_UP);
        params.put("anticipo", df.format(sumatoriaAnticipos.get()));

        if(decimalsum.compareTo(BigDecimal.ZERO)==1){
            subtotal = subtotal.subtract(decimalsum);
        }

        params.put("igv", comprobante.getSumatoriaIGV() != null ? df.format(comprobante.getSumatoriaIGV().doubleValue() +
                ((comprobante.getMontoSumatorioOtrosCargos() != null ?
                        comprobante.getMontoSumatorioOtrosCargos() : BigDecimal.ZERO).doubleValue() * 0.18)
        ) : "0.00");
        params.put("subtotal", String.format("%.2f", subtotal.setScale(2, BigDecimal.ROUND_HALF_UP)));
        params.put("ineto", String.format("%.2f",subtotal.doubleValue()-(comprobante.getTotalDescuento()==null?0:comprobante.getTotalDescuento().doubleValue())));

        params.put("listProducts", items);
        params.put("sexo", "Masculino");
        try {
            InputStream jasperStream = getJasperTemplateFromStorage(tipoPdf, company.getRuc(), company.getFormat());//companyEntity.getFormat());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Ocurrio un error al generar el PDF");
        }
        * */
        return null;
    }
}
