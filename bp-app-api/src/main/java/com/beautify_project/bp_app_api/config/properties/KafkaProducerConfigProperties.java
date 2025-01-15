package com.beautify_project.bp_app_api.config.properties;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.response.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_utils.Validator;
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
            throw new BpCustomException("broker 설정값이 올바르지 않습니다.", ErrorCode.IS001);
        }
        this.broker = broker;
    }

    public void setTopic(final Topic topic) {
        if (topic == null) {
            throw new BpCustomException("topic 설정값이 올바르지 않습니다.", ErrorCode.IS001);
        }
        this.topic = topic;
    }

    @Getter
    public static class Topic {
        private String shopLikeEvent;
        private String shopLikeCancelEvent;
        private String signUpCertificationMailEvent;

        public void setShopLikeEvent(final String shopLikeEvent) {
            if (Validator.isEmptyOrBlank(shopLikeEvent)) {
                throw new BpCustomException("shop-like-event 설정값이 올바르지 않습니다.", ErrorCode.IS001);
            }
            this.shopLikeEvent = shopLikeEvent;
        }

        public void setShopLikeCancelEvent(final String shopLikeCancelEvent) {
            if (Validator.isEmptyOrBlank(shopLikeCancelEvent)) {
                throw new BpCustomException("shop-like-cancel-event 설정값이 올바르지 않습니다.", ErrorCode.IS001);
            }
            this.shopLikeCancelEvent = shopLikeCancelEvent;
        }

        public void setSignUpCertificationMailEvent(final String signUpCertificationMailEvent) {
            if (Validator.isEmptyOrBlank(signUpCertificationMailEvent)) {
                throw new BpCustomException("sign-up-certification-mail 설정값이 올바르지 않습니다.",
                    ErrorCode.IS001);
            }
            this.signUpCertificationMailEvent = signUpCertificationMailEvent;
        }
    }
}
