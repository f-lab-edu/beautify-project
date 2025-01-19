//package com.beautify_project.bp_common_kafka.producer;
//
//import com.beautify_project.bp_common_kafka.config.properties.KafkaConfigurationProperties;
//import com.beautify_project.bp_common_kafka.event.ShopLikeEvent;
//import com.beautify_project.bp_common_kafka.event.ShopLikeEvent.ShopLikeEventProto;
//import com.beautify_project.bp_common_kafka.event.ShopLikeEvent.ShopLikeEventProto.LikeType;
//import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent;
//import java.util.concurrent.CompletableFuture;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class KafkaEventProducer {
//
//    private static final String TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT = "SHOP-LIKE-EVENT";
//    private static final String TOPIC_CONFIG_NAME_SIGNUP_CERTIFICATION_MAIL_EVENT = "MAIL-SIGN-UP-CERTIFICATION-EVENT";
//
//    private final KafkaTemplate<String, ShopLikeEventProto> shopLikeEventProtoKafkaTemplate;
//    private final KafkaTemplate<String, SignUpCertificationMailEvent> mailSignUpCertificationEventKafkaTemplate;
//    private final KafkaConfigurationProperties kafkaConfigurationProperties;
//
//    public void publishShopLikeEvent(final Long shopId, final String memberEmail, final LikeType likeType) {
//        final ShopLikeEvent.ShopLikeEventProto shopLikeEventProto = ShopLikeEvent.ShopLikeEventProto.newBuilder()
//            .setShopId(shopId)
//            .setMemberEmail(memberEmail)
//            .setType(likeType)
//            .build();
//
//        final CompletableFuture<SendResult<String, ShopLikeEvent.ShopLikeEventProto>> asyncSendResult =
//            shopLikeEventProtoKafkaTemplate.send(
//                kafkaConfigurationProperties.getTopic().get(TOPIC_CONFIG_NAME_SHOP_LIKE_EVENT)
//                    .getTopicName(), shopLikeEventProto
//            );
//
//        asyncSendResult.exceptionally(throwable -> {
//            loggingPublishedFailed(throwable);
//            return null;
//        });
//    }
//
//    public void publishSignUpCertificationMailEvent(final String email) {
//
//    }
//
//    private void loggingPublishedFailed(Throwable throwable) {
//        log.error("Failed to published event - {}", throwable.toString(), throwable);
//    }
//}
