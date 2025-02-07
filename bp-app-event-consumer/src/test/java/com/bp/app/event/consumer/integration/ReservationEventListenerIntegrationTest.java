package com.bp.app.event.consumer.integration;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bp.app.event.consumer.integration.config.TestContainerConfig;
import com.bp.app.event.consumer.listener.ReservationEventListener;
import com.bp.app.event.consumer.notification.EmailNotification;
import com.bp.common.kafka.config.properties.KafkaConfigurationProperties;
import com.bp.common.kakfa.event.ReservationEvent.ReservationEventProto;
import com.bp.common.kakfa.event.ReservationEvent.ReservationEventProto.ReservationType;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Operator;
import com.bp.domain.mysql.entity.Reservation;
import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.MemberStatus;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.bp.domain.mysql.repository.OperationAdapterRepository;
import com.bp.domain.mysql.repository.OperatorAdaptorRepository;
import com.bp.domain.mysql.repository.ReservationAdapterRepository;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
@Slf4j
@Tag("integration-test")
class ReservationEventListenerIntegrationTest extends TestContainerConfig {

    private static final String TOPIC_CONFIG_NAME_RESERVATION_EVENT = "RESERVATION-REGISTRATION-EVENT";
    private static final Long HOUR_TO_LONG = 1000L * 60 * 60;
    private static final Long DAY_TO_LONG = 24 * HOUR_TO_LONG;

    @Autowired
    private KafkaTemplate<Long, ReservationEventProto> reservationEventProtoKafkaTemplate;

    @Autowired
    private KafkaConfigurationProperties kafkaConfigurationProperties;

    @Autowired
    private OperatorAdaptorRepository operatorAdaptorRepository;

    @Autowired
    private OperationAdapterRepository operationAdapterRepository;

    @Autowired
    private MemberAdapterRepository memberAdapterRepository;

    @Autowired
    private ReservationAdapterRepository reservationAdapterRepository;

    @SpyBean
    private ReservationEventListener eventListener;

    @SpyBean
    private EmailNotification emailNotification;

    @BeforeEach
    void beforeEach() {
        operatorAdaptorRepository.deleteAllInBatch();
        operationAdapterRepository.deleteAllInBatch();
        memberAdapterRepository.deleteAllInBatch();
        reservationAdapterRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("예약 이벤트 생성시 시술 선생님 이메일로 메일 알림이 간다.")
    void sendEmailToOperatorIfReservationEventConsumed() throws Exception {
        final Member mockedMember = memberAdapterRepository.saveAndFlush(
            Member.newMember("sssukho@gmail.com", "password",
                "이름", "010-1234-5678", AuthType.BP, UserRole.USER, MemberStatus.ACTIVE,
                System.currentTimeMillis()));

        final Operation mockedOperation = operationAdapterRepository.saveAndFlush(
            Operation.newOperation("시술1", "시술1설명"));

        final Operator mockedOperator = operatorAdaptorRepository.saveAndFlush(
            Operator.newOperator("dev.sssukho@gmail.com", "password",
                "시술선생님1", "010-1234-5678"));

        final long startDate = System.currentTimeMillis() + DAY_TO_LONG;
        final long endDate = startDate + HOUR_TO_LONG;

        final Reservation mockedReservation = reservationAdapterRepository.saveAndFlush(
            Reservation.newReservation(startDate, endDate,
                mockedMember.getEmail(), 1L, mockedOperation.getId(),
                mockedOperator.getId())
        );

        final ReservationEventProto event = ReservationEventProto.newBuilder()
            .setStartDate(startDate)
            .setEndDate(endDate)
            .setOperatorId(mockedOperator.getId())
            .setOperationId(mockedOperation.getId())
            .setReservationId(mockedReservation.getId())
            .setShopId(mockedReservation.getShopId())
            .setRequestedMemberEmail(mockedMember.getEmail())
            .setReservationType(ReservationType.CREATE)
            .build();

        // when
        reservationEventProtoKafkaTemplate.send(
            kafkaConfigurationProperties.getTopic().get(TOPIC_CONFIG_NAME_RESERVATION_EVENT)
                .getTopicName(), mockedOperator.getId(), event);

        // then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                verify(eventListener, times(1)).listenReservationEvent(any());
                verify(emailNotification, times(1)).sendAll(any());
            });
    }
}
