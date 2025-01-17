package com.beautify_project.bp_app_api.producer;

import com.beautify_project.bp_app_api.config.properties.KafkaProducerConfigProperties;
import com.beuatify_project.bp_common.event.ShopLikeCancelEvent;
import com.beuatify_project.bp_common.event.ShopLikeEvent;
import com.beuatify_project.bp_common.event.SignUpCertificationMailEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Async(value = "ioBoundExecutor")
public class KafkaEventProducer {

    private final KafkaTemplate<String, ShopLikeEvent> shopLikeEventKafkaTemplate;
    private final KafkaTemplate<String, ShopLikeCancelEvent> shopLikeCancelEventKafkaTemplate;
    private final KafkaTemplate<String, SignUpCertificationMailEvent> mailSignUpCertificationEventKafkaTemplate;
    private final KafkaProducerConfigProperties configProperties;

    public void publishShopLikeEvent(final ShopLikeEvent event) {
        final CompletableFuture<SendResult<String, ShopLikeEvent>> asyncSendResult =
            shopLikeEventKafkaTemplate.send(configProperties.getTopic().getShopLikeEvent(), event);

        asyncSendResult.thenAccept(this::loggingPublishedSuccess)
            .exceptionally(throwable -> {
                loggingPublishedFailed(throwable);
                return null;
        });
    }

    public void publishShopLikeCancelEvent(final ShopLikeCancelEvent event) {
        final CompletableFuture<SendResult<String, ShopLikeCancelEvent>> asyncSendResult =
            shopLikeCancelEventKafkaTemplate.send(
                configProperties.getTopic().getShopLikeCancelEvent(), event);

        asyncSendResult.thenAccept(this::loggingPublishedSuccess)
            .exceptionally(throwable -> {
                loggingPublishedFailed(throwable);
                return null;
            });
    }

    public void publishSignUpCertificationMailEvent(final SignUpCertificationMailEvent event) {
        final CompletableFuture<SendResult<String, SignUpCertificationMailEvent>> asyncSendResult =
            mailSignUpCertificationEventKafkaTemplate.send(
                configProperties.getTopic().getSignUpCertificationMailEvent(), event);

        asyncSendResult.thenAccept(this::loggingPublishedSuccess)
                .exceptionally(throwable -> {
                    loggingPublishedFailed(throwable);
                    return null;
                });
    }

    private void loggingPublishedSuccess(final SendResult<String, ?> sendResult) {
        final RecordMetadata metadata = sendResult.getRecordMetadata();
        log.debug("Event published to topic: {} | partition: {} | offset: {}", metadata.topic(),
            metadata.partition(), metadata.offset());
    }

    private void loggingPublishedFailed(Throwable throwable) {
        log.error("Failed to published event - {}", throwable.toString(), throwable);
    }

}
