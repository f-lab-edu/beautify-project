package com.beautify_project.bp_common_kafka.producer;

import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
import com.beautify_project.bp_common_kafka.event.ShopLikeEvent.ShopLikeEventProto.LikeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShopLikeEventProducer {

    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT = "SHOP-LIKE-EVENT";

    private final KafkaTemplate<String, ShopLikeEvent.ShopLikeEventProto> shopLikeEventProtoKafkaTemplate;
    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    @Async("ioBoundExecutor")
    public void publishShopLikeEvent(final Long shopId, final String memberEmail, final LikeType likeType) {
        final ShopLikeEvent.ShopLikeEventProto shopLikeEventProto = ShopLikeEvent.ShopLikeEventProto.newBuilder()
            .setShopId(shopId)
            .setMemberEmail(memberEmail)
            .setType(likeType)
            .build();

        shopLikeEventProtoKafkaTemplate.send(
            kafkaConfigurationProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT)
                .getTopicName(), shopLikeEventProto).exceptionally(throwable -> {
            log.error("Failed to publish event - {}", shopLikeEventProto, throwable);
            return null;
        });
    }
}
