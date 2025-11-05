package com.certicom.certifact_facturas_service_ng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class CertifactFacturasServiceNgApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertifactFacturasServiceNgApplication.class, args);
	}

}
