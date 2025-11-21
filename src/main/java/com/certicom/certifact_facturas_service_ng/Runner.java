package com.certicom.certifact_facturas_service_ng;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    @Value("${eureka.client.service-url.defaultZone}")
    private String eurekaUrl;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String port;

    @Value("${sunat.endpoint}")
    private String sunatEndpoint;

    @Value("${external.services.factura-service-sp.base-url}")
    private String facturaServiceSp;

    @Override
    public void run(String... args) {
        System.out.println("🚀 Microservicio " + applicationName + " iniciado en el puerto 🔌 " + port);
        System.out.println("🧾 Endpoint SUNAT: " + sunatEndpoint);
        System.out.println("🛰️ Eureka Server URL: " + eurekaUrl);
        System.out.println("💼 Factura Service SP URL: " + facturaServiceSp);
    }

}
