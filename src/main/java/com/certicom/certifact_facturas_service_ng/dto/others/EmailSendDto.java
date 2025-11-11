package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EmailSendDto implements Serializable {

    private Long id;
    private String email;
    private Integer tipo;

}
