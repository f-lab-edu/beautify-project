package com.beautify_project.bp_app_api.producer;

import com.beautify_project.bp_app_api.config.properties.KafkaProducerConfigProperties;
import com.beuatify_project.bp_common.event.ShopLikeCancelEvent;
import com.beuatify_project.bp_common.event.ShopLikeEvent;
import com.beuatify_project.bp_common.event.SignUpCertificationMailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, ShopLikeEvent> shopLikeEventKafkaTemplate;
    private final KafkaTemplate<String, ShopLikeCancelEvent> shopLikeCancelEventKafkaTemplate;
    private final KafkaTemplate<String, SignUpCertificationMailEvent> mailSignUpCertificationEventKafkaTemplate;
    private final KafkaProducerConfigProperties configProperties;

    public void publishShopLikeEvent(final ShopLikeEvent event) {
        shopLikeEventKafkaTemplate.send(configProperties.getTopic().getShopLikeEvent(), event);
        log.debug("{} event published", event.toString());
    }

    public void publishShopLikeCancelEvent(final ShopLikeCancelEvent event) {
        shopLikeCancelEventKafkaTemplate.send(configProperties.getTopic().getShopLikeCancelEvent(), event);
    }

    public void publishSignUpCertificationMailEvent(final SignUpCertificationMailEvent event) {
        mailSignUpCertificationEventKafkaTemplate.send(
            configProperties.getTopic().getSignUpCertificationMailEvent(), event);
        log.debug("{} event published", event.toString());
    }
}
