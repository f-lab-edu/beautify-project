package com.beautify_project.bp_common_kafka.config.properties;

import com.beautify_project.bp_utils.Validator;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "kafka")
@Configuration
@Slf4j
@Getter
public class KafkaConfigurationProperties {

    private String brokerUrl;
    private String schemaRegistryUrl;
    private Map<String, TopicConfigurationProperties> topic;

    public void setBrokerUrl(final String brokerUrl) {
        if (Validator.isEmptyOrBlank(brokerUrl)) {
            throw new IllegalStateException("broker 설정값이 올바르지 않습니다.");
        }
        this.brokerUrl = brokerUrl;
    }

    public void setSchemaRegistryUrl(final String schemaRegistryUrl) {
        if (Validator.isEmptyOrBlank(schemaRegistryUrl)) {
            throw new IllegalStateException("schema registry url 설정값이 올바르지 않습니다.");
        }
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    public void setTopic(
        final Map<String, TopicConfigurationProperties> topic) {
        if (topic == null) {
            throw new IllegalStateException("topic 설정값이 올바르지 않습니다.");
        }
        this.topic = topic;
    }

    @Getter
    public static class TopicConfigurationProperties {

        private String topicName;
        private Consumer consumer;

        public void setTopicName(final String topicName) {
            if (Validator.isEmptyOrBlank(topicName)) {
                throw new IllegalStateException("topic 이름 설정값이 올바르지 않습니다");
            }
            this.topicName = topicName;
        }

        public void setConsumer(final Consumer consumer) {
            if (consumer == null) {
                throw new IllegalStateException("consumer 설정값이 올바르지 않습니다.");
            }
            this.consumer = consumer;
        }

        @Getter
        public static class Consumer {

            private String groupId;
            private Integer batchSize;
            private Long fetchMaxWait;

            public void setGroupId(final String groupId) {
                if (Validator.isEmptyOrBlank(groupId)) {
                    throw new IllegalStateException("consumer group id 설정값이 올바르지 않습니다.");
                }
                this.groupId = groupId;
            }

            public void setBatchSize(final Integer batchSize) {
                if (batchSize == null || batchSize == 0) {
                    throw new IllegalStateException("consumer batch-size 설정값이 올바르지 않습니다.");
                }
                this.batchSize = batchSize;
            }

            public void setFetchMaxWait(final Long fetchMaxWait) {
                if (fetchMaxWait == null || fetchMaxWait == 0) {
                    throw new IllegalStateException("consumer fetch max wait 설정값이 올바르지 않습니다.");
                }
                this.fetchMaxWait = fetchMaxWait;
            }
        }
    }
}
