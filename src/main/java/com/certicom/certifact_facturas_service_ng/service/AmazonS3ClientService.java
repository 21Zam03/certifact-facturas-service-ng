package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.model.EmpresaDto;
import com.certicom.certifact_facturas_service_ng.entity.SubidaRegistroArchivoEntity;

import java.io.InputStream;

public interface AmazonS3ClientService {

    SubidaRegistroArchivoEntity subirArchivoAlStorage(InputStream inputStream, String nameFile, String folder, EmpresaDto company);

}
