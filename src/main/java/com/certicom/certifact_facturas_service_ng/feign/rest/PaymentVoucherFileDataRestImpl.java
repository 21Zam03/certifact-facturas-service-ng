package com.certicom.certifact_facturas_service_ng.feign.rest;

import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherFileData;
import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherFileModel;
import org.springframework.stereotype.Service;

@Service
public class PaymentVoucherFileDataRestImpl implements PaymentVoucherFileData {


    @Override
    public int save(PaymentVoucherFileModel paymentVoucherFileModel) {
        return 0;
    }
}
