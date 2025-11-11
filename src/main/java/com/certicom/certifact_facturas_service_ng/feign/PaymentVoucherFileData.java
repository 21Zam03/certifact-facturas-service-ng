package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherFileModel;

public interface PaymentVoucherFileData {

    public Integer save(PaymentVoucherFileModel paymentVoucherFileModel);
    public Long findActiveXMLIdRegisterFileSendByIdPaymentVoucher(Long id);

}
