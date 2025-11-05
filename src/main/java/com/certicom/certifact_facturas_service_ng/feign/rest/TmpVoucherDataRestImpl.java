package com.certicom.certifact_facturas_service_ng.feign.rest;

import com.certicom.certifact_facturas_service_ng.entity.TmpVoucherSendBillEntity;
import com.certicom.certifact_facturas_service_ng.feign.TmpVoucherData;
import org.springframework.stereotype.Service;

@Service
public class TmpVoucherDataRestImpl implements TmpVoucherData {



    @Override
    public TmpVoucherSendBillEntity findTmpVoucherByIdPaymentVoucher(Long id) {
        return null;
    }

    @Override
    public int saveTmpVoucher(TmpVoucherSendBillEntity tmpVoucherSendBillEntity) {
        return 0;
    }

    @Override
    public int updateStatusVoucherTmp(Long identificador, String estado) {
        return 0;
    }

    @Override
    public int deleteTmpVoucherById(Long tmpVoucherId) {
        return 0;
    }
}
