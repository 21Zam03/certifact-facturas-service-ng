package com.certicom.certifact_facturas_service_ng.validation;

import com.certicom.certifact_facturas_service_ng.dto.others.Tipo;

import java.math.BigDecimal;

public class ConstantesSunat {

    //Referencia Catalogo 01
    public static final String TIPO_DOCUMENTO_FACTURA = "01";
    public static final String TIPO_DOCUMENTO_BOLETA = "03";
    public static final String TIPO_DOCUMENTO_NOTA_CREDITO = "07";
    public static final String TIPO_DOCUMENTO_NOTA_DEBITO = "08";
    public static final String TIPO_DOCUMENTO_GUIA_REMISION = "09";
    public static final String TIPO_DOCUMENTO_GUIA_TRANSPORTISTA = "31";
    public static final String TIPO_DOCUMENTO_TICKET_MAQ_REGISTRADORA = "12";
    public static final String TIPO_DOCUMENTO_RETENTION = "20";
    public static final String TIPO_DOCUMENTO_PERCEPTION = "40";

    //Referencia Catalogo 06
    public static final String TIPO_DOCUMENTO_IDENTIDAD_RUC = "6";
    public static final String TIPO_DOCUMENTO_IDENTIDAD_DNI = "1";

    public static final String TIPO_DOCUMENTO_NO_DOMI_SIN_RUC = "0";

    public static final BigDecimal IGV_INCLUIDO = BigDecimal.valueOf(1.18);
    public static final String PRECIO_UNITARIO_INCLUYE_IGV = "01";
    public static final Integer TIPO_AFCETACION_IGV_GRAVADO_ONEROSA = 10;
    public static final String TIPO_AFCETACION_IGV_EXONERADO = "20";
    public static final String TIPO_AFCETACION_IGV_INAFECTO = "30";

    public static final Tipo TRIBUTO_IGV = new Tipo(1000, "IGV", "VAT");
    public static final Tipo TRIBUTO_IVAP = new Tipo(1016, "IVAP", "VAT");
    public static final Tipo TRIBUTO_ISC = new Tipo(2000, "ISC", "EXC");
    public static final Tipo TRIBUTO_EXPORTACION = new Tipo(9995, "EXP", "FRE");
    public static final Tipo TRIBUTO_GRATUITO = new Tipo(9996, "GRA", "FRE");
    public static final Tipo TRIBUTO_EXONERADO = new Tipo(9997, "EXO", "VAT");
    public static final Tipo TRIBUTO_INAFECTO = new Tipo(9998, "INA", "FRE");
    public static final Tipo TRIBUTO_BOLSAS = new Tipo(7152, "ICBPER", "OTH");
    public static final Tipo TRIBUTO_OTROS = new Tipo(9999, "OTROS", "OTH");

    //Refencia la catalogo 05
    public static final String CODIGO_TRIBUTO_IGV = "1000";
    public static final String CODIGO_TRIBUTO_IVAP = "1016";
    public static final String CODIGO_TRIBUTO_ISC = "2000";
    public static final String CODIGO_TRIBUTO_EXPORTACION = "9995";
    public static final String CODIGO_TRIBUTO_GRATUITO = "9996";
    public static final String CODIGO_TRIBUTO_EXONERADO = "9997";
    public static final String CODIGO_TRIBUTO_INAFECTO = "9998";
    public static final String CODIGO_TRIBUTO_BOLSAS = "7152";
    public static final String CODIGO_TRIBUTO_OTROS = "9999";

    // Referencia catalogo 12
    public static final String CODIGO_DOCUMENTO_RELACIONADO_FACTURA_ANTICIPO = "02";
    public static final String CODIGO_DOCUMENTO_RELACIONADO_BOLETA_ANTICIPO = "03";


    // Referencia catalogo 14
    public static final String TOTAL_VALOR_VENTA_OPE_EXPORTADA = "1000";
    public static final String TOTAL_VALOR_VENTA_OPE_GRAVADA = "1001";
    public static final String TOTAL_VALOR_VENTA_OPE_INAFECTA = "1002";
    public static final String TOTAL_VALOR_VENTA_OPE_EXONERADA = "1003";
    public static final String TOTAL_VALOR_VENTA_OPE_GRATUITA = "1004";
    public static final String TOTAL_DESCUENTO = "2005";

    // Referencia catalogo 21
    public static final String NUMERACION_DAM = "01";
    public static final String NUMERO_MANIFIESTO_CARGA = "04";

    //Catalogo 16
    /**
     * Incluye el IGV
     */
    public static final String CODIGO_TIPO_PRECIO_PRECIO_UNITARIO = "01";
    /**
     * En operaciones No onerosas(gratuita)
     */
    public static final String CODIGO_TIPO_PRECIO_VALOR_REFERENCIAL = "02";

    //Catalogo 17
    public static final String CODIGO_TIPO_OPERACION_VENTA_INTERNA = "01";
    public static final String CODIGO_TIPO_OPERACION_VENTA_INTERNA_ANTICIPOS = "04";

    public static final String CODIGO_DOMICILIO_DEFAULT = "0000";

    public static final String UBL_VERSION_ID_VOIDED_DOCUMENTS = "2.0";
    public static final String CUSTOMIZATION_ID_VOIDED_DOCUMENTS = "1.0";
    public static final String UBL_VERSION_ID_CREDIT_NOTE = "2.0";
    public static final String CUSTOMIZATION_ID_CREDIT_NOTE = "1.0";
    public static final String CUSTOMIZATION_ID_CREDIT_NOTE_20 = "2.0";
    public static final String UBL_VERSION_ID_DEBIT_NOTE = "2.0";
    public static final String CUSTOMIZATION_ID_DEDIT_NOTE = "1.0";
    public static final String UBL_VERSION_ID_RETENTION = "2.0";
    public static final String CUSTOMIZATION_ID_RETENTION = "1.0";
    public static final String UBL_VERSION_ID_PERCEPTION = "2.0";
    public static final String CUSTOMIZATION_ID_PERCEPTION = "1.0";
    public static final String CUSTOMIZATION_VERSION_1_0 = "1.0";

    public static final String ATTRIBUTE_LANGUAGE_LOCALE_ID = "languageLocaleID";
    public static final String ATTRIBUTE_LIST_AGENCY_NAME = "listAgencyName";
    public static final String ATTRIBUTE_LIST_NAME = "listName";
    public static final String ATTRIBUTE_LIST_ID = "listID";
    public static final String ATTRIBUTE_LIST_SCHEMA_URI = "listSchemeURI";
    public static final String ATTRIBUTE_LIST_URI = "listURI";
    public static final String ATTRIBUTE_CURRENCY_ID = "currencyID";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_UNIT_CODE = "unitCode";
    public static final String ATTRIBUTE_UNIT_CODE_LIST_ID = "unitCodeListID";
    public static final String ATTRIBUTE_UNIT_CODE_LIST_AGENCY_NAME = "unitCodeListAgencyName";
    public static final String ATTRIBUTE_SCHEME_ID = "schemeID";
    public static final String ATTRIBUTE_ID 			= "ID";
    public static final String ATTRIBUTE_SCHEME_AGENCY_ID	= "schemeAgencyId";
    public static final String ATTRIBUTE_SCHEME_NAME = "schemeName";
    public static final String ATTRIBUTE_SCHEME_AGENCY_NAME = "schemeAgencyName";
    public static final String ATTRIBUTE_SCHEME_URI = "schemeURI";
    public static final String ATTRIBUTE_TAG_CBC_DESCRIPTION = "cbc:Description";
    public static final String ATTRIBUTE_TAG_CBC_RESPONSE_CODE = "cbc:ResponseCode";
    public static final String ATTRIBUTE_TAG_CBC_DESCRIPTION_OSE	= "Description";
    public static final String ATTRIBUTE_TAG_CBC_RESPONSE_CODE_OSE	= "ResponseCode";
    public static final String ATTRIBUTE_TAG_CBC_RESPONSE_DOCUMENTDESC = "cbc:DocumentDescription";
    public static final String ATTRIBUTE_TAG_CBC_RESPONSE_DOCUMENTDESC_OSE = "DocumentDescription";

    public static final String ATTRIBUTE_TAG_CBC_REFERENCE_ID = "cbc:ReferenceID";

    public static final String COMUNICACION_BAJA = "RA";
    public static final String COMUNICACION_BAJA_PER = "RR";
    public static final String RESUMEN_DIARIO_BOLETAS = "RC";

    public static final Integer STATE_ITEM_ADICIONAR = 1;
    public static final Integer STATE_ITEM_MODIFICAR = 2;
    public static final Integer STATE_ITEM_ANULADO = 3;

    public static final String TIPO_VALOR_VENTA_GRAVADO = "01";
    public static final String TIPO_VALOR_VENTA_EXONERADO = "02";
    public static final String TIPO_VALOR_VENTA_INAFECTO = "03";
    public static final String TIPO_VALOR_VENTA_EXPORTACION = "04";
    public static final String TIPO_VALOR_VENTA_GRATUITA = "05";

    public static final String UBL_VERSION_2_0 = "2.0";
    public static final String UBL_VERSION_2_1 = "2.1";
    public static final String CUSTOMIZATION_VERSION_2_0 = "2.0";

    //public static final String SUPERADMIN = "s-admin";

    public static final String CODIGO_VALOR_REFERENCIAL_DETRACCION_SERVICIO_TRANSPORTE = "01";
    public static final String CODIGO_VALOR_REFERENCIAL_DETRACCION_CARGA_EFECTIVA = "02";
    public static final String CODIGO_VALOR_REFERENCIAL_DETRACCION_CARGA_UTIL = "03";

    public static final String SUPERADMIN = "s-admin";
    public static final String DESCRIPCION_ANULA= "Se anula guia transportista ";
    public static final String DESCRIPCION_TIPO_GUIA="Guía de Remisión Remitente";

}
