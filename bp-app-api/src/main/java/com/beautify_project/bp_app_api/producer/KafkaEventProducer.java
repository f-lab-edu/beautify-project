package com.beautify_project.bp_app_api.producer;

import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventProducer {

    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT = "SHOP-LIKE-EVENT";
    private static final String TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT = "MAIL-SIGN-UP-CERTIFICATION-EVENT";

    private final KafkaTemplate<String, ShopLikeEvent> shopLikeEventKafkaTemplate;
    private final KafkaTemplate<String, SignUpCertificationMailEvent> mailSignUpCertificationEventKafkaTemplate;
    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    public void publishShopLikeEvent(final ShopLikeEvent event) {

        final CompletableFuture<SendResult<String, ShopLikeEvent>> asyncSendResult =
            shopLikeEventKafkaTemplate.send(
                kafkaConfigurationProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT)
                    .getTopicName(), event);

        asyncSendResult.exceptionally(throwable -> {
            loggingPublishedFailed(throwable);
            return null;
        });
    }

    public void publishSignUpCertificationMailEvent(final SignUpCertificationMailEvent event) {
        final CompletableFuture<SendResult<String, SignUpCertificationMailEvent>> asyncSendResult =
            mailSignUpCertificationEventKafkaTemplate.send(kafkaConfigurationProperties.getTopic()
                .get(TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT).getTopicName(), event);

        asyncSendResult
            .exceptionally(throwable -> {
                loggingPublishedFailed(throwable);
                return null;
            });
    }

    private void loggingPublishedFailed(Throwable throwable) {
        log.error("Failed to published event - {}", throwable.toString(), throwable);
    }
}
