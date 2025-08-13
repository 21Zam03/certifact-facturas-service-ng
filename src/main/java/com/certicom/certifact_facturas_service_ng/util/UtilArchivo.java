package com.certicom.certifact_facturas_service_ng.util;

import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionNegocio;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class UtilArchivo {

    public static File createFileTemp(String extencion) {
        try {
            return File.createTempFile("temp", "." + extencion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generarCodigoHash(String nombreArchivo) {

        String codigoHash = null;

        try {

            codigoHash=nombreArchivo.substring(nombreArchivo.indexOf("<ds:DigestValue>")+16, nombreArchivo.lastIndexOf("</ds:DigestValue>"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return codigoHash;
    }

    public static File comprimir(ByteArrayOutputStream value, String extencionOrigen, String nombre) {
        try {
            File fileZipeado = File.createTempFile("temp", ".zip");

            File fileToZip = new File(com.google.common.io.Files.createTempDir(), nombre + "." + extencionOrigen);
            Path path = Paths.get(fileToZip.getAbsoluteFile().toString());
            Files.write(path, value.toByteArray());
            try(
                    FileOutputStream fos = new FileOutputStream(fileZipeado);
                    ZipOutputStream zipOut = new ZipOutputStream(fos);
                    FileInputStream fis = new FileInputStream(fileToZip)) {
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);
                final byte[] bytes = new byte[fis.available()];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            }
            return fileZipeado;
        } catch (IOException ex) {
            log.error("Error comprimiendo archivo: {}", ex.getMessage());
            return null;
        }
    }

    public static ByteArrayInputStream b64ToByteArrayInputStream(String encodedString) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedByteArray = decoder.decode(encodedString);
        try {
            return new ByteArrayInputStream(decodedByteArray);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExcepcionNegocio("Error en el formato del archivo xml");
        }
    }

}
