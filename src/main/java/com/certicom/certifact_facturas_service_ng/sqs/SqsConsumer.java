package com.certicom.certifact_facturas_service_ng.sqs;

import com.certicom.certifact_facturas_service_ng.dto.others.EmailSendDto;
import com.certicom.certifact_facturas_service_ng.dto.others.GetStatusCdrDto;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.dto.others.SendBillDto;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
import com.certicom.certifact_facturas_service_ng.service.EmailService;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SqsConsumer {

    private final ObjectMapper objectMapper;
    private final SqsClient sqsClient;
    private final ComunicationSunatService comunicationSunatService;
    private final EmailService emailService;
    private final SqsProducer sqsProducer;

    @Value("${apifact.aws.sqs.sendBill}")
    private String sendBill;

    @Value("${apifact.aws.sqs.emailSender}")
    private String emailSender;

    @Scheduled(fixedDelay = 5000)
    public void receiveMessageQueueSendBill () {
        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(sendBill)
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(10)
                    .messageAttributeNames("All")
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

            for (Message sqsMessage : messages) {
                try {
                    LogHelper.infoLog(LogMessages.currentMethod(), "Se recibió mensaje desde SQS sendBill >>> "+sqsMessage.body());

                    SendBillDto sendBillDTO = objectMapper.readValue(sqsMessage.body(), SendBillDto.class);

                    Thread.sleep(2000);

                    Map<String, Object> result = comunicationSunatService.sendDocumentBill(
                            sendBillDTO.getRuc(),
                            sendBillDTO.getIdPaymentVoucher()
                    );

                    ResponsePSE resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

                    if (resp != null && resp.getEstado()) {
                        sqsProducer.produceEnviarCorreo(
                                EmailSendDto.builder()
                                        .id(sendBillDTO.getIdPaymentVoucher())
                                        .build()
                        );
                    }

                    if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {
                        GetStatusCdrDto dataGetStatusCDR =
                                (GetStatusCdrDto) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
                        //messageProducer.produceGetStatusCDR(dataGetStatusCDR);
                    }

                    deleteMessage(sqsMessage.receiptHandle());
                    LogHelper.infoLog(LogMessages.currentMethod(), "Mensaje procesado y eliminado correctamente de la cola sendBill");

                } catch (Exception exMsg) {
                    LogHelper.errorLog(LogMessages.currentMethod(),"Error procesando mensaje SQS " + sqsMessage.messageId(), exMsg);
                }
            }

        } catch (Exception e) {
            LogHelper.errorLog(LogMessages.currentMethod(), "❌ Error al consumir mensajes de SQS", e);
            throw new ServiceException("Ocurrió un error al consumir los mensajes de SQS", e);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void receiveMessageQueueEmailSender() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(emailSender)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(10)
                .messageAttributeNames("All")
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        LogHelper.infoLog(LogMessages.currentMethod(), "Se recibio mensaje Para enviar correo Electronico >>>>>>>>>>>>>>> ");

        for (Message sqsMessage : messages) {
            try {
                EmailSendDto emailSendDto = objectMapper.readValue(sqsMessage.body(), EmailSendDto.class);
                emailService.sendEmailOnConfirmSunat(emailSendDto);
                deleteMessage(sqsMessage.receiptHandle());
                LogHelper.infoLog(LogMessages.currentMethod(), "Mensaje procesado y eliminado correctamente de la cola sendBill");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteMessage(String receiptHandle) {
        try {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(sendBill)
                    .receiptHandle(receiptHandle)
                    .build();
            sqsClient.deleteMessage(deleteRequest);
        } catch (Exception e) {
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error al eliminar mensaje de la cola sqs", e);
            throw new ServiceException("Ocurrio un error al eliminar mensaje de la cola sqs", e);
        }
    }

}
