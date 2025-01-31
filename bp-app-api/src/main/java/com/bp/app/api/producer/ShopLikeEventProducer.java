package com.bp.app.api.producer;

import com.bp.common.kafka.config.properties.KafkaConfigurationProperties;
import com.bp.common.kakfa.event.ShopLikeEvent.ShopLikeEventProto;
import com.bp.common.kakfa.event.ShopLikeEvent.ShopLikeEventProto.LikeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShopLikeEventProducer {

    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT = "SHOP-LIKE-EVENT";

    private final KafkaTemplate<Long, ShopLikeEventProto> shopLikeEventProtoKafkaTemplate;
    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    public void publishShopLikeEvent(final Long shopId, final String memberEmail, final LikeType likeType) {
        log.debug("shop like event will be published");

        final ShopLikeEventProto shopLikeEventProto = ShopLikeEventProto.newBuilder()
            .setShopId(shopId)
            .setMemberEmail(memberEmail)
            .setType(likeType)
            .build();

        shopLikeEventProtoKafkaTemplate.send(
            kafkaConfigurationProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT)
                .getTopicName(),
            shopId,
            shopLikeEventProto
        ).exceptionally(exception -> {
            log.error("Failed to publish event: {}", shopLikeEventProto, exception);
            return null;
        });

    }
}
