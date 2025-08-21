package com.certicom.certifact_facturas_service_ng.util;

import java.util.UUID;

public class UUIDGen {
    public static String generate(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
