package com.certicom.certifact_facturas_service_ng.util;

public class ConstantesParametro {

    public static final String RANGO_DIAS_BAJA_DOCUMENTOS = "RANGO_DIAS_BAJA_DOCUMENTOS";
    public static final String RANGO_DIAS_RESUMEN_BOLETAS = "RANGO_DIAS_RESUMEN_BOLETAS";

    public static final String PARAM_BEAN_RESPONSE_PSE 	 = "responsePse";
    public static final String PARAM_BEAN_GET_STATUS_CDR = "getStatusCDR";
    public static final String PARAM_BEAN_GET_STATUS_EXL = "getStatusExcel";
    public static final String PARAM_BEAN_SEND_BILL	 	 = "sendBill";
    public static final String PARAM_BEAN_SEND_BOLETA	= "sendBoleta";
    public static final String PARAM_BEAN_SEND_OTRO_CPE  = "sendOtherCPE";
    public static final String PARAM_BEAN_SUMMARY 	 = "summary";
    public static final String PARAM_DESCRIPTION	 = "description";
    public static final String PARAM_ESTADO			 = "estado";
    public static final String PARAM_STATUS_REGISTRO = "statusRegistro";
    public static final String PARAM_FILE_ZIP_BASE64 = "fileBase64";
    public static final String PARAM_FILE_XML_BASE64 = "fileXmlBase64";
    public static final String PARAM_STRING_HASH = "stringHash";
    public static final String PARAM_FILE_EXCEL = "fileXmlB";
    public static final String PARAM_LIST_IDS 		 = "ids";
    public static final String PARAM_NAME_DOCUMENT	 = "nameDocument";
    public static final String ATTRIBUTE_SCHEME_AGENCY_ID	= "schemeAgencyId";
    public static final String PARAM_NUM_TICKET		 = "numTicket";
    public static final String PARAM_NUMERO			 = "numero";
    public static final String PARAM_RESPONSE_CODE	 = "responseCode";
    public static final String PARAM_RUC_EMISOR		 = "ruc";
    public static final String PARAM_SERIE			 = "serie";
    public static final String PARAM_TIPO_ARCHIVO	 = "tipoArchivo";
    public static final String PARAM_TIPO_COMPROBANTE= "tipoComprobante";
    public static final String PARAM_USER_NAME		 = "userName";
    public static final String PARAM_UUID_SAVED		 = "uuidSaved";
    public static final String PARAM_EXTENSION_FILE	 = "extension";
    public static final String PARAM_DOCUMENT_DESCRIPTION = "documentDescription";
    public static final String FIELD_RUC_EMISOR		 = "rucEmisor";
    public static final String FIELD_FECHA_EMISION	 = "fechaEmisionDate";
    public static final String FIELD_TIPO_COMPROBANTE= "tipoComprobante";
    public static final String FIELD_CODIGO_MONEDA	 = "codigoMoneda";
    public static final String FIELD_SERIE			 = "serie";
    public static final String FIELD_NUMERO			 = "numero";
    public static final String FIELD_ESTADO			 = "estado";

    public static final String TYPE_FILE_XML = "xml";
    public static final String TYPE_FILE_ZIP = "zip";
    public static final String TYPE_FILE_EXCEL = "xls";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String MSG_RESP_SUB_PROCESO_OK 	= "OK";
    public static final String CODE_RESP_OK 	= "00";
    public static final String CODE_RESP_ERROR 	= "99";
    public static final String CODE_RESP_ERROR_DESERIALIZACION 	= "01";
    public static final String MSG_RESP_ERROR_DESERIALIZACION_STRING	= "Se espera una cadena para el campo valor ";
    public static final String MSG_RESP_ERROR_DESERIALIZACION_INTEGER	= "Se espera un valor entero de tipo numero para el campo ";
    public static final String MSG_ERROR_DESERIALIZACION_NUMBER	= "Se espera un valor numerico para el campo ";
    public static final String MSG_RESP_OK 		= "Se realizó la operación correctamente.";
    public static final String MSG_REGISTRO_DOCUMENTO_OK	= "Se registró el documento correctamente y será enviado"
            + " a la Sunat para su aceptación.";

    public static final String MSG_EDICION_DOCUMENTO_OK	= "Se editó el documento correctamente y será enviado"
            + " a la Sunat para su aceptación.";

    public static final String MSG_MODIFICACION_DOCUMENTO_OK	= "Se modificó el documento correctamente y sera enviado"
            + " a la Sunat para su aceptación en el resumen diario.";
    public static final String MSG_SUMMARY_VACIO= "No hay registro para ser enviados en el documento resumen de boleta.";

    public static final String STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK 	= "0";
    public static final String STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_ERROR = "99";
    public static final String STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO	= "98";

    public static final String CODE_RESPONSE_OK 							= "0";
    /**
     * Comprobante es aceptado por la sunat, pero tiene warnings
     */
    public static final String STATE_PAYMENT_VOUCHER_ACEPTADO_WARNING_SUNAT	= "WARN";
    /**
     * Comprobante es aceptado por la sunat, dicho comprobante previamente
     * ha sido registrado o han estado pendiente de modificacion
     */
    public static final String STATE_PAYMENT_VOUCHER_ACEPTADO_SUNAT	= "ACEP";
    /**
     * El comprobante ha sido anulado, con la aceptacion de la sunat
     */
    public static final String STATE_PAYMENT_VOUCHER_ANULADO_SUNAT 	= "ANUL";
    /**
     * Comprobante es rechazado por la sunat.
     */
    public static final String STATE_PAYMENT_VOUCHER_RECHAZADO_SUNAT= "RECH";
    /**
     * El comprobante es enviado a la sunat para su aprobacion
     */
    public static final String STATE_PAYMENT_VOUCHER_ENVIADO_SUNAT	= "ENVI";
    /**
     * Cuando un comprobante es registrado en el sistema,
     * pero aun esta pendiente la aceptación de la sunat.
     */
    public static final String STATE_PAYMENT_VOUCHER_REGISTRADO		= "01";

    public static final int STATE_ITEM_PENDIENTE_ADICION		= 1;
    /**
     * Despues de haber sido aceptado la boleta se ha modificado,
     * pero aun esta pendiente la aceptación de la sunat.
     */
    public static final int STATE_ITEM_PENDIENTE_MODIFICACION	= 2;
    /**
     * Despues de haber sido aceptado la boleta y q no ha sido otorgado al adquirente
     * se anula, pero aun esta pendiente la aceptación de la sunat.
     */
    public static final int STATE_ITEM_PENDIENTE_ANULACION		= 3;
    /**
     * Anulado antes de informar el comprobante
     */
    public static final int STATE_ITEM_ANULADO_DIA				= 4;
    /**
     * La boleta tiene una respuesta de parte de la sunat, sea con rechazo, aceptacion, etc
     */
    public static final int STATE_ITEM_RESPUESTA_SUNAT			= 0;

    public static final String REGISTRO_ACTIVO	= "A";
    public static final String REGISTRO_INACTIVO= "I";

    public static final String USER_API_SCHEDULER= "user_api_scheduler";
    public static final String USER_API_QUEUE	 = "user_api_queue";

    public static final String OPERADOR_MAYOR		= ">";
    public static final String OPERADOR_MAYOR_IGUAL	= ">=";
    public static final String OPERADOR_MENOR_IGUAL	= "<=";
    public static final String OPERADOR_MENOR		= "<";
    public static final String OPERADOR_IGUAL		= "=";
    public static final String OPERADOR_LIKE		= "%";

    public static final String REGISTRO_STATUS_NUEVO = "N";
    public static final String REGISTRO_STATUS_MODIFICAR_DOCUMENTO_ACEPTADO = "A";
    public static final String REGISTRO_STATUS_MODIFICAR_DOCUMENTO_REGISTRADO = "R";

    /**
     * El proceso de envio a la sunat fue completada, para comprobantes de
     * envio sincronos como Factura y Notas asociadas a Factura
     */
    public static final String STATUS_PROCESS_SEND_SUNAT_COMPLETO 		= "00";
    /**
     * El proceso se realizo correctamente, pero no retorno CDR, para comprobantes de
     * envio sincronos como Factura y Notas asociadas a Factura
     */
    public static final String STATUS_PROCESS_SEND_SUNAT_PENDIENTE_CDR	= "01";
    /**
     * No se realizo envio a la Sunat, por motivo de problemas de conexion,
     * para comprobantes de envio sincronos como Factura y Notas asociadas a Factura
     */
    public static final String STATUS_PROCESS_SEND_SUNAT_PENDIENTE_SEND	= "02";

    public static final String TAG_SEND_BILL_APPLICATION_RESPONSE	= "applicationResponse";
    public static final String TAG_SEND_SUMMARY_TICKET				= "ticket";
    public static final String TAG_GET_STATUS_CONTENT				= "content";
    public static final String TAG_STATUS_CODE						= "statusCode";

    public static final String PARAM_CODE_RESPONSE_STORAGE 			= "codeResponse";
    public static final String PARAM_MESSAGE_RESPONSE_STORAGE 		= "messageResponse";
    public static final String PARAM_FILE_RESPONSE_STORAGE 			= "fileResponse";
    public static final String CODE_STORAGE_RESP_OK 				= "00";
    public static final String CODE_STORAGE_RESP_ERROR 				= "99";


    public static final String ESTADO_SUNAT_NO_ENVIADO 				= "N_ENV";
    public static final String ESTADO_SUNAT_ANULADO 				= "ANULA";
    public static final String ESTADO_SUNAT_RECHAZADO 				= "RECHA";
    public static final String ESTADO_COMPROBANTE_PROCESO_ENVIO		= "07";
    public static final String ESTADO_COMPROBANTE_RECHAZADO			= "05";
    public static final String ESTADO_COMPROBANTE_ANULADO			= "08";
    public static final String ESTADO_COMPROBANTE_ACEPTADO			= "02";

    public static final String CODIGO_ACEPTADO_FROM_CDR				= "0";
    public static final String CODIGO_NO_FOUND_CODE_ERROR_FROM_CDR	= "-1";
    public static final String MENSAJE_NO_FOUND_CODE_FROM_CDR		= "Codigo de respuesta en el CDR, no se encuentra en la tabla[error_catalog]";
    public static final String MENSAJE_NO_FOUND_CDR					= "El contenido CDR se encuentra vacio.";
    public static final String GUIA_REMISION						= "Guia de Remision";

    public static final String MSG_RESP_ERROR_DESERIALIZACION_BOOLEAN	= "Se espera una cadena valor true o false";

    public static final String CODIGO_HASH	= "CODIGO_HASH";

    public static final String ESTADO_SUNAT_ACEPTADO= "ACEPT";

    public static final String ESTADO_COMPROBANTE_REGISTRADO		= "01";

    public static final String ESTADO_COMPROBANTE_NO_ENVIADO			= "06";

    public static final String CODIGO_COMPROBANTE_FUE_REGISTRADO="1033";

}
