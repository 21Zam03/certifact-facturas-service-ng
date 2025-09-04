package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.model.CompanyModel;
import com.certicom.certifact_facturas_service_ng.model.RegisterFileUploadModel;
import com.certicom.certifact_facturas_service_ng.enums.TipoArchivoEnum;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface AmazonS3ClientService {

    RegisterFileUploadModel subirArchivoAlStorage(InputStream inputStream, String nameFile, String folder, CompanyModel companyModel);
    String downloadFileStorageInB64(RegisterFileUploadModel fileStorage);
    ByteArrayInputStream downloadFileStorageDto(RegisterFileUploadModel fileStorage);
    RegisterFileUploadModel uploadFileStorage(InputStream inputStream, String nameFile, String folder, CompanyModel companyModel);
    ByteArrayInputStream downloadFileInvoice(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum);
    ByteArrayInputStream downloadFileStorageInter(RegisterFileUploadModel fileStorage);

}
