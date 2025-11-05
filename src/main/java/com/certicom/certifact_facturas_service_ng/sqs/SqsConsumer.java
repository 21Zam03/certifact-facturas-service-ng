package com.certicom.certifact_facturas_service_ng.sqs;

import com.certicom.certifact_facturas_service_ng.dto.others.GetStatusCdrDto;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;
import com.certicom.certifact_facturas_service_ng.dto.others.SendBillDto;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.service.ComunicationSunatService;
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

    @Value("${apifact.aws.sqs.sendBill}")
    private String sendBill;

    @Scheduled(fixedDelay = 5000)
    public void receiveMessageQueueProcessSummary () {
        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(sendBill)
                    .maxNumberOfMessages(5)
                    .waitTimeSeconds(10)
                    .messageAttributeNames("All")
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

            for (Message sqsMessage : messages) {
                processMessage(sqsMessage);
            }

        } catch (Exception e) {
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error al momento de consumir el mensaje de sqs", e);
            throw new ServiceException("Ocurrio un error al momento de consumir el mensaje de sqs", e);
        }
    }

    private void processMessage(Message sqsMessage) {
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
                /*
                messageProducer.produceEnviarCorreo(
                        EmailSendDTO.builder()
                                .id(sendBillDTO.getIdPaymentVoucher())
                                .build()
                );
                * */
                //Producir mensaje sqs para envio de correo
            }

            if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {
                GetStatusCdrDto dataGetStatusCDR =
                        (GetStatusCdrDto) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
                //messageProducer.produceGetStatusCDR(dataGetStatusCDR);
            }

            // ✅ Eliminar el mensaje de la cola solo si se procesó bien
            deleteMessage(sqsMessage.receiptHandle());
            LogHelper.infoLog(LogMessages.currentMethod(), "Mensaje procesado y eliminado correctamente de la cola sendBill");

        } catch (Exception e) {
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error al procesar mensaje de la cola sendbill", e);
            throw new ServiceException("Ocurrio un error al procesar mensaje de la cola sendbill", e);
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
