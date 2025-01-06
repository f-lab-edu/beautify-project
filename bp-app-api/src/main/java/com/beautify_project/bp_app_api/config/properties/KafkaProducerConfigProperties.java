package com.beautify_project.bp_app_api.config.properties;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.exception.ConfigurationException;
import com.beautify_project.bp_app_api.utils.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.producer")
@Slf4j
@Getter
public class KafkaProducerConfigProperties {

    private String broker;
    private Topic topic;

    public void setBroker(final String broker) {
        if (Validator.isEmptyOrBlank(broker)) {
            throw new ConfigurationException("broker 설정값이 올바르지 않습니다.", ErrorCode.IS001);
        }
        this.broker = broker;
    }

    public void setTopic(final Topic topic) {
        if (topic == null) {
            throw new ConfigurationException("topic 설정값이 올바르지 않습니다.", ErrorCode.IS001);
        }
        this.topic = topic;
    }

    @Getter
    public static class Topic {
        private String shopLikeEvent;

        public void setShopLikeEvent(final String shopLikeEvent) {
            if (Validator.isEmptyOrBlank(shopLikeEvent)) {
                throw new ConfigurationException("shop-like-event 설정값이 올바르지 않습니다.", ErrorCode.IS001);
            }
            this.shopLikeEvent = shopLikeEvent;
        }
    }
}
