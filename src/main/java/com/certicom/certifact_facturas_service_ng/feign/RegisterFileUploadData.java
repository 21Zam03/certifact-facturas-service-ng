package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.RegisterFileUploadModel;

public interface RegisterFileUploadData {

    public RegisterFileUploadModel findFirst1ByPaymentVoucherIdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc
            (Long idPayment, String tipoArchivo, String estadoArchivo);
    public RegisterFileUploadModel saveRegisterFileUpload(RegisterFileUploadModel registerFileUploadModelDto);
    public RegisterFileUploadModel findByIdPaymentVoucherAndUuidTipo(Long id, String uuid, String tipo);
    RegisterFileUploadModel findById(Long registerFileSendId);

}
