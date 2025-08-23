package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.model.CompanyDto;
import com.certicom.certifact_facturas_service_ng.dto.model.RegisterFileUploadDto;
import com.certicom.certifact_facturas_service_ng.entity.RegisterFileUploadEntity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface AmazonS3ClientService {

    RegisterFileUploadEntity subirArchivoAlStorage(InputStream inputStream, String nameFile, String folder, CompanyDto company);
    String downloadFileStorageInB64(RegisterFileUploadDto fileStorage);
    ByteArrayInputStream downloadFileStorageDto(RegisterFileUploadDto fileStorage);
    RegisterFileUploadEntity uploadFileStorage(InputStream inputStream, String nameFile, String folder, CompanyDto company);

}
