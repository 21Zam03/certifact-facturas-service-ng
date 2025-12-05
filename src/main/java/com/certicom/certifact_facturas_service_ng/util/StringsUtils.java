package com.certicom.certifact_facturas_service_ng.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringsUtils {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static String getRespuestaEstadoSunat(String codigoEstadoSunat) {

        if (codigoEstadoSunat == null) return "NO_ENVIADO";
        if (codigoEstadoSunat.equals("ACEPT")) return "ACEPTADO";
        if (codigoEstadoSunat.equals("RECHA")) return "RECHAZADO";
        if (codigoEstadoSunat.equals("ANULA")) return "ANULADO";
        if (codigoEstadoSunat.equals("N_ENV")) return "NO_ENVIADO";

        return "NO_ENVIADO";
    }

    public static boolean validateEmail(String emailStr) {
        if (emailStr == null) return false;
        if ((emailStr.trim()).length() == 0) return false;
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static String getNombreTipoDocumentoReceptor(String tipo) {

        String nombreDocumento;

        switch (tipo) {
            case "0":
                nombreDocumento = "DOC.TRIB.NO.DOM.SIN.RUC";
                break;
            case "1":
                nombreDocumento = "DNI";
                break;
            case "4":
                nombreDocumento = "Carnet Ext.";
                break;
            case "6":
                nombreDocumento = "RUC";
                break;
            case "7":
                nombreDocumento = "Pasaporte";
                break;
            case "A":
                nombreDocumento = "CED. DIPLOMATICA DE IDENTIDAD";
                break;
            default:
                nombreDocumento = "-";
                break;
        }

        return nombreDocumento;
    }
    public static String getDescBienDetraccion(String tipo) {

        String nombreDocumento;

        switch (tipo) {
            case "001":
                nombreDocumento = "Azúcar y melaza de caña";
                break;
            case "002":
                nombreDocumento = "Arroz";
                break;
            case "003":
                nombreDocumento = "Alcohol etílico";
                break;
            case "004":
                nombreDocumento = "Recursos hidrobiologicos";
                break;
            case "005":
                nombreDocumento = "Maíz amarillo duro";
                break;
            case "007":
                nombreDocumento = "Caña de azúcar";
                break;
            case "008":
                nombreDocumento = "Madera";
                break;
            case "009":
                nombreDocumento = "Arena y piedra";
                break;
            case "010":
                nombreDocumento = "Residuos, subproductos, desechos, recortes y desperdicios";
                break;
            case "011":
                nombreDocumento = "Bienes gravados con el IGV, o renuncia a la exoneración";
                break;
            case "012":
                nombreDocumento = "Intermediación laboral y tercerización";
                break;
            case "013":
                nombreDocumento = "Animales vivos";
                break;
            case "014":
                nombreDocumento = "Carnes y despojos comestibles";
                break;
            case "015":
                nombreDocumento = "Abonos, cueros y pieles de origen animal";
                break;
            case "016":
                nombreDocumento = "Aceite de pescado";
                break;
            case "017":
                nombreDocumento = "Harina, polvo y “pellets” de pescado, crustáceos, moluscos y demás invertebrados acuáticos";
                break;
            case "019":
                nombreDocumento = "Arrendamiento de bienes muebles";
                break;
            case "020":
                nombreDocumento = "Mantenimiento y reparación de bienes muebles";
                break;
            case "021":
                nombreDocumento = "Movimiento de carga";
                break;
            case "022":
                nombreDocumento = "Otros servicios empresariales";
                break;
            case "023":
                nombreDocumento = "Leche";
                break;
            case "024":
                nombreDocumento = "Comisión mercantil";
                break;
            case "025":
                nombreDocumento = "Fabricación de bienes por encargo";
                break;
            case "026":
                nombreDocumento = "Servicio de transporte de personas";
                break;
            case "027":
                nombreDocumento = "Servicio de transporte de carga";
                break;
            case "028":
                nombreDocumento = "Transporte de pasajeros";
                break;
            case "030":
                nombreDocumento = "Contratos de construcción";
                break;
            case "031":
                nombreDocumento = "Oro gravado con IGV";
                break;
            case "034":
                nombreDocumento = "Minerales metálicos no auríferos";
                break;
            case "035":
                nombreDocumento = "Bienes exonerados del IGV";
                break;
            case "036":
                nombreDocumento = "Oro y demás minerales metálicos exonerados del IGV";
                break;
            case "037":
                nombreDocumento = "Demás servicios gravados con el IGV";
                break;
            case "038":
                nombreDocumento = "Espectáculos públicos gravado con el IGV";
                break;
            case "039":
                nombreDocumento = "Minerales no metálicos";
                break;
            case "040":
                nombreDocumento = "Bien inmueble gravado con IGV";
                break;
            case "041":
                nombreDocumento = "Plomo";
                break;
            case "042":
                nombreDocumento = "Ladrillos de construcción, bovedillas, cubrevigas y artículos similares de cerámica";
                break;
            case "043":
                nombreDocumento = "Estructuras metálicas para la construcción";
                break;
            case "044":
                nombreDocumento = "Servicio de beneficio de minerales metálicos gravado con el IGV";
                break;
            case "045":
                nombreDocumento = "Minerales de oro y sus concentrados gravados con el IGV";
                break;
            default:
                nombreDocumento = "-";
                break;
        }
        return nombreDocumento;
    }
    public static String getDescMedioPago(String tipo) {

        String nombreDocumento;

        switch (tipo) {
            case "001":
                nombreDocumento = "Deposito a cuenta";
                break;
            case "002":
                nombreDocumento = "Giro";
                break;
            case "003":
                nombreDocumento = "Transferencia de fondos";
                break;
            case "004":
                nombreDocumento = "Orden de pago";
                break;
            case "005":
                nombreDocumento = "Tarjeta de debito";
                break;
            case "006":
                nombreDocumento = "Tarjeta de crédito emitida en el país por una empresa del sistema financiero";
                break;
            case "007":
                nombreDocumento = "Cheques con la cláusula de 'NO NEGOCIABLE', 'INTRANSFERIBLES', 'NO A LA ORDEN' u otra equivalente, a que se refiere el inciso g) del artículo 5° de la ley";
                break;
            case "008":
                nombreDocumento = "Efectivo, por operaciones en las que no existe obligación de utilizar medio de pago";
                break;
            case "009":
                nombreDocumento = "Efectivo, en los demás casos";
                break;
            case "010":
                nombreDocumento = "Medios de pago usados en comercio exterior";
                break;
            case "011":
                nombreDocumento = "Documentos emitidos por las EDPYMES y las cooperativas de ahorro y crédito no autorizadas a captar depósitos del público";
                break;
            case "012":
                nombreDocumento = "Tarjeta de crédito emitida en el país o en el exterior por una empresa no perteneciente al sistema financiero, cuyo objeto principal sea la emisión y administración de tarjetas de crédito";
                break;
            case "013":
                nombreDocumento = "Tarjetas de crédito emitidas en el exterior por empresas bancarias o financieras no domiciliadas";
                break;
            case "101":
                nombreDocumento = "Transferencias – Comercio exterior";
                break;
            case "102":
                nombreDocumento = "Cheques bancarios - Comercio exterior";
                break;
            case "103":
                nombreDocumento = "Orden de pago simple - Comercio exterior";
                break;
            case "104":
                nombreDocumento = "Orden de pago documentario - Comercio exterior";
                break;
            case "105":
                nombreDocumento = "Remesa simple - Comercio exterior";
                break;
            case "106":
                nombreDocumento = "Remesa documentaria - Comercio exterior";
                break;
            case "107":
                nombreDocumento = "Carta de crédito simple - Comercio exterior";
                break;
            case "108":
                nombreDocumento = "Carta de crédito documentario - Comercio exterior";
                break;
            case "999":
                nombreDocumento = "Otros medios de pago";
                break;
            default:
                nombreDocumento = "-";
                break;
        }

        return nombreDocumento;
    }
    public static String getNombreTipoComprobante(String tipo) {

        String nombreComprobante;

        switch (tipo) {
            case "01":
                nombreComprobante = "FACTURA ELECTRÓNICA";
                break;
            case "03":  nombreComprobante = "BOLETA DE VENTA ELECTRÓNICA";
                break;
            case "07":
                nombreComprobante = "NOTA DE CRÉDITO ELECTRÓNICA";
                break;
            case "08":
                nombreComprobante = "NOTA DE DÉBITO ELECTRÓNICA";
                break;
            case "09":
                nombreComprobante = "GUÍA DE REMISIÓN REMITENTE";
                break;
            case "20":
                nombreComprobante = "RETENCIÓN";
                break;
            case "40":
                nombreComprobante = "PERCEPCIÓN";
                break;
            case "31":
                nombreComprobante = "GUÍA DE REMISIÓN TRANSPORTISTA";
                break;
            default:
                nombreComprobante = "FACTURA ELECTRÓNICA";
                break;
        }

        return nombreComprobante;
    }

    public static String getNombreTipoComprobanteNoElectro(String tipo) {

        String nombreComprobante;

        switch (tipo) {
            case "01":
                nombreComprobante = "FACTURA";
                break;
            case "03":  nombreComprobante = "BOLETA DE VENTA";
                break;
            case "07":
                nombreComprobante = "NOTA DE CRÉDITO";
                break;
            case "08":
                nombreComprobante = "NOTA DE DÉBITO";
                break;
            case "09":
                nombreComprobante = "GUÍA DE REMISIÓN REMITENTE";
                break;
            case "20":
                nombreComprobante = "RETENCIÓN";
                break;
            case "40":
                nombreComprobante = "PERCEPCIÓN";
                break;
            case "31":
                nombreComprobante = "GUÍA DE REMISIÓN TRANSPORTISTA";
                break;
            default:
                nombreComprobante = "FACTURA";
                break;
        }

        return nombreComprobante;
    }
    public static String getModalidadTraslado(String tipo) {

        String nombreModalidad;

        switch (tipo) {
            case "01":
                nombreModalidad = "PÚBLICO";
                break;
            case "02":
                nombreModalidad = "PRIVADO";
                break;
            default:
                nombreModalidad = "ERROR";
                break;
        }

        return nombreModalidad;
    }

    public static String getTipoDocDAM(String tipo){
        String tipoDAM;
        switch(tipo){
            case "50":
                tipoDAM = "Declaración Aduanera de Mercancías";
                break;
            case "52":
                tipoDAM = "Declaración Simplificada";
                break;
            default:
                tipoDAM = "ERROR";
                break;
        }
        return tipoDAM;
    }
    public static String getMotivoTraslado(String tipo) {

        String nombreMotivo;

        switch (tipo) {
            case "01":
                nombreMotivo = "Venta";
                break;
            case "02":
                nombreMotivo = "Compra";
                break;
            case "03":
                nombreMotivo = "Venta con entrega a terceros";
                break;
            case "04":
                nombreMotivo = "Traslado entre establecimientos de la misma empresa";
                break;
            case "05":
                nombreMotivo = "Consignación";
                break;
            case "06":
                nombreMotivo = "Devolución";
                break;
            case "07":
                nombreMotivo = "Recojo de bienes transformados";
                break;
            case "08":
                nombreMotivo = "Importación";
                break;
            case "09":
                nombreMotivo = "Exportación";
                break;
            case "13":
                nombreMotivo = "Otros";
                break;
            case "14":
                nombreMotivo = "Venta sujeta a confirmación del comprador";
                break;
            case "17":
                nombreMotivo = "Traslado de bienes para transformacion";
                break;
            case "18":
                nombreMotivo = "Traslado emisor itinerante CP";
                break;
            case "19":
                nombreMotivo = "Traslado a zona primaria";
                break;
            default:
                nombreMotivo = "ERROR";
                break;
        }

        return nombreMotivo;
    }


    public static String getNombreTipoComprobanteResumido(String tipo) {

        String nombreComprobante;

        switch (tipo) {
            case "01":
                nombreComprobante = "Fac.";
                break;
            case "03":
                nombreComprobante = "Bol.";
                break;
            case "07":
                nombreComprobante = "Nota cre.";
                break;
            case "08":
                nombreComprobante = "Nota deb.";
                break;
            default:
                nombreComprobante = "Fac.";
                break;
        }

        return nombreComprobante;
    }


    public static String getNombreCortoTipoComprobante(String tipo) {

        String nombreComprobante;

        switch (tipo) {
            case "01":
                nombreComprobante = "Factura";
                break;
            case "03":
                nombreComprobante = "Boleta";
                break;
            case "07":
                nombreComprobante = "Nota de crédito.";
                break;
            case "08":
                nombreComprobante = "Nota de débito.";
                break;
            case "09":
                nombreComprobante = "Guia de remisión.";
                break;
            case "20":
                nombreComprobante = "Retención.";
                break;
            case "40":
                nombreComprobante = "Percepción.";
                break;
            default:
                nombreComprobante = "Fac.";
                break;
        }

        return nombreComprobante;
    }

    public static String getTipoNotaDebito(String tipo) {

        String nombreModalidad;

        switch (tipo) {
            case "01":
                nombreModalidad = "Intereses por mora";
                break;
            case "02":
                nombreModalidad = "Aumento en el valor";
                break;
            case "03":
                nombreModalidad = "Penalidades/ otros conceptos";
                break;
            default:
                nombreModalidad = "ERROR";
                break;
        }

        return nombreModalidad;
    }

    public static String getTipoNotaCredito(String tipo) {

        String nombreModalidad;

        switch (tipo) {
            case "01":
                nombreModalidad = "Anulación de la operación";
                break;
            case "02":
                nombreModalidad = "Anulación por error en el RUC";
                break;
            case "03":
                nombreModalidad = "Corrección por error en la descripción";
                break;
            case "04":
                nombreModalidad = "Descuento global";
                break;
            case "05":
                nombreModalidad = "Descuento por ítem";
                break;
            case "06":
                nombreModalidad = "Devolución total";
                break;
            case "07":
                nombreModalidad = "Devolución por ítem";
                break;
            case "08":
                nombreModalidad = "Bonificación";
                break;
            case "09":
                nombreModalidad = "Disminución en el valor";
                break;
            case "10":
                nombreModalidad = "Otros Conceptos";
                break;
            default:
                nombreModalidad = "ERROR";
                break;
        }
        return nombreModalidad;
    }

}
