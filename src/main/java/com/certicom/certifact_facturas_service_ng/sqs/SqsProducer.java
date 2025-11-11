package com.certicom.certifact_facturas_service_ng.sqs;

import com.certicom.certifact_facturas_service_ng.dto.others.EmailSendDto;
import com.certicom.certifact_facturas_service_ng.dto.others.SendBillDto;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.io.Serializable;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SqsProducer {

    private final ObjectMapper objectMapper;
    private final SqsClient sqsClient;

    @Value("${apifact.aws.sqs.sendBill}")
    private String sendBill;

    @Value("${apifact.aws.sqs.emailSender}")
    private String emailSender;

    public void produceSendBill(SendBillDto sendBillDto) {
        try {
            send(sendBill, sendBillDto);
        } catch (Exception e) {
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error al enviar mensaje a la cola, nameDocument: "+sendBillDto.getNameDocument(), e);
            throw new ServiceException("Ocurrio un error al enviar mensaje a la cola, nameDocument: "+sendBillDto.getNameDocument(), e);
        }
    }

    public void produceEnviarCorreo(EmailSendDto emailSendDTO) {
        try {
            send(emailSender, emailSendDTO);
        }catch (Exception e){
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error al enviar mensaje a la cola, email: "+emailSendDTO.getEmail(), e);
            throw new ServiceException("Ocurrio un error al enviar mensaje a la cola, email: "+emailSendDTO.getEmail(), e);
        }

    }

    public <MESSAGE extends Serializable> void send(String queueUrl, MESSAGE payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            SendMessageRequest.Builder requestBuilder = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonPayload)
                    .messageAttributes(Map.of(
                            "documentType",
                            MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue(payload.getClass().getName())
                                    .build()
                    ));

            if (queueUrl.endsWith(".fifo")) {
                requestBuilder
                        .messageGroupId("messageGroup1")
                        .messageDeduplicationId("1" + System.currentTimeMillis());
            }

            sqsClient.sendMessage(requestBuilder.build());
            LogHelper.infoLog(LogMessages.currentMethod(), "Se envio mensaje a la cola sqs: "+queueUrl);

        } catch (Exception e) {
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error al momento de enviar mensaje a sqs", e);
            throw new ServiceException("Ocurrio un error al momento de enviar mensaje a sqs", e);
        }
    }
}
