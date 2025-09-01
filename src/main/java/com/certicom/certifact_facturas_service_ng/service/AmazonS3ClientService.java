package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.model.Company;
import com.certicom.certifact_facturas_service_ng.model.RegisterFileUploadDto;
import com.certicom.certifact_facturas_service_ng.model.RegisterFileUpload;
import com.certicom.certifact_facturas_service_ng.enums.TipoArchivoEnum;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface AmazonS3ClientService {

    RegisterFileUpload subirArchivoAlStorage(InputStream inputStream, String nameFile, String folder, Company company);
    String downloadFileStorageInB64(RegisterFileUpload fileStorage);
    ByteArrayInputStream downloadFileStorageDto(RegisterFileUpload fileStorage);
    RegisterFileUpload uploadFileStorage(InputStream inputStream, String nameFile, String folder, Company company);
    ByteArrayInputStream downloadFileInvoice(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum);
    ByteArrayInputStream downloadFileStorageInter(RegisterFileUpload fileStorage);

}
