package com.beautify_project.bp_kafka_event_consumer.config.properties;

import com.beautify_project.bp_kafka_event_consumer.exception.CustomException;
import com.beautify_project.bp_utils.Validator;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka.consumer")
@Slf4j
@Getter
public class KafkaConsumerConfigProperties {

    private String broker;
    private Map<String, TopicProperties> topic;

    public void setBroker(final String broker) {
        if (Validator.isEmptyOrBlank(broker)) {
            throw new CustomException("broker 설정값이 올바르지 않습니다");
        }
        this.broker = broker;
    }

    public void setTopic(final Map<String, TopicProperties> topic) {
        if (Validator.isNullOrEmpty(topic)) {
            throw new CustomException("topic 설정값이 올바르지 않습니다.");
        }
        this.topic = topic;
    }

    @Getter
    public static class TopicProperties {
        private String topicName;
        private String groupId;
        private Integer batchSize;
        private Long fetchMaxWait;

        public void setTopicName(final String topicName) {
            if (Validator.isEmptyOrBlank(topicName)) {
                throw new CustomException("topic 이름 설정값이 올바르지 않습니다.");
            }
            this.topicName = topicName;
        }

        public void setGroupId(final String groupId) {
            if (Validator.isEmptyOrBlank(groupId)) {
                throw new CustomException("group id 설정값이 올바르지 않습니다.");
            }
            this.groupId = groupId;
        }

        public void setBatchSize(final Integer batchSize) {
            if (batchSize == null || batchSize == 0) {
                this.batchSize = 1;
            } else {
                this.batchSize = batchSize;
            }
        }

        public void setFetchMaxWait(final Long fetchMaxWait) {
            if (fetchMaxWait == null || fetchMaxWait == 0L) {
                this.fetchMaxWait = 500L;
            } else {
                this.fetchMaxWait = fetchMaxWait;
            }
        }
    }
}
