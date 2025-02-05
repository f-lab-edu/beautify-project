package com.bp.app.api.producer;

import com.bp.common.kafka.config.properties.KafkaConfigurationProperties;
import com.bp.common.kakfa.event.ReservationEvent.ReservationEventProto;
import com.bp.common.kakfa.event.ReservationEvent.ReservationEventProto.ReservationType;
import com.bp.domain.mysql.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventProducer {

    private static final String TOPIC_CONFIG_NAME_RESERVATION_REGISTRATION_EVENT = "RESERVATION-REGISTRATION-EVENT";

    private final KafkaTemplate<String, ReservationEventProto> reservationRegistrationEventProtoKafkaTemplate;
    private final KafkaConfigurationProperties kafkaConfigurationProperties;

    public void produceReservationEvent(final Reservation pendedReservation,
        final String requestMemberEmail) {

        final ReservationEventProto event = ReservationEventProto.newBuilder()
            .setStartDate(pendedReservation.getStartDate())
            .setEndDate(pendedReservation.getEndDate())
            .setReservationId(pendedReservation.getId())
            .setRequestedMemberEmail(requestMemberEmail)
            .setReservationType(ReservationType.CREATE)
            .build();

        reservationRegistrationEventProtoKafkaTemplate.send(
            kafkaConfigurationProperties.getTopic().get(TOPIC_CONFIG_NAME_RESERVATION_REGISTRATION_EVENT)
                .getTopicName(),
            requestMemberEmail,
            event
        ).exceptionally(exception -> {
            log.error("Failed to produce event: {}", event, exception);
            return null;
        });
        log.debug("Reservation create event published");
    }
}
