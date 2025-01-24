package com.bp.app.api.producer;

import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent.SignUpCertificationMailEventProto;
import com.bp.common.kafka.config.properties.KafkaConfigurationProperties;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SignUpCertificationMailEventProducer {

    private static final String TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT = "MAIL-SIGN-UP-CERTIFICATION-EVENT";

    private final KafkaConfigurationProperties kafkaConfigurationProperties;
    private final KafkaTemplate<String, SignUpCertificationMailEventProto> mailSignUpCertificationEventKafkaTemplate;

    public void publishSignUpCertificationMailEvent(final String email) {
        final SignUpCertificationMailEventProto signUpCertificationMailEventProto =
            SignUpCertificationMailEventProto.newBuilder()
                .setMemberEmail(email)
                .build();

        final CompletableFuture<SendResult<String, SignUpCertificationMailEventProto>> asyncSendResult =
            mailSignUpCertificationEventKafkaTemplate.send(
                kafkaConfigurationProperties.getTopic()
                    .get(TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT)
                    .getTopicName(), signUpCertificationMailEventProto
            );

        asyncSendResult.exceptionally(throwable -> {
            log.error("Failed to publish event - {}", signUpCertificationMailEventProto,
                throwable);
            return null;
        });
    }
}
