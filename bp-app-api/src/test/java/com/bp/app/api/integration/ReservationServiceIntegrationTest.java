package com.bp.app.api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.bp.app.api.integration.config.TestContainerConfig;
import com.bp.app.api.producer.ReservationEventProducer;
import com.bp.app.api.request.reservation.ReservationRegistrationRequest;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.reservation.ReservationRegistrationResult;
import com.bp.app.api.service.ReservationService;
import com.bp.common.kafka.config.KafkaConsumerConfig;
import com.bp.common.kafka.config.properties.KafkaConfigurationProperties;
import com.bp.common.kakfa.event.ReservationEvent.ReservationEventProto;
import com.bp.common.kakfa.event.ReservationEvent.ReservationEventProto.ReservationType;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Operator;
import com.bp.domain.mysql.entity.Reservation;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.entity.enumerated.ReservationStatus;
import com.bp.domain.mysql.repository.OperationAdapterRepository;
import com.bp.domain.mysql.repository.OperatorAdaptorRepository;
import com.bp.domain.mysql.repository.ReservationAdapterRepository;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
@Tag("integration-test")
public class ReservationServiceIntegrationTest extends TestContainerConfig {

    private static final Long HOUR_TO_LONG = 1000L * 60 * 60;
    private static final Long DAY_TO_LONG = 24 * HOUR_TO_LONG;
    public static final String RESERVATION_EVENT_KAFKA_CONFIG_KEY = "RESERVATION-REGISTRATION-EVENT";

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationAdapterRepository reservationAdapterRepository;

    @Autowired
    private ShopAdapterRepository shopAdapterRepository;

    @Autowired
    private OperationAdapterRepository operationAdapterRepository;

    @Autowired
    private OperatorAdaptorRepository operatorAdaptorRepository;

    @Autowired
    private KafkaConsumerConfig kafkaConsumerConfig;

    @Autowired
    private KafkaConfigurationProperties kafkaConfigurationProperties;

    @SpyBean
    private ReservationEventProducer reservationEventProducer;

    @BeforeEach
    void beforeEach() {
        shopAdapterRepository.deleteAllInBatch();
        operationAdapterRepository.deleteAllInBatch();
        operatorAdaptorRepository.deleteAllInBatch();
        reservationAdapterRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자의 예약 등록 요청 성공시 reservation 엔티티가 pending 상태로 저장되고, 카프카에 이벤트가 produce 된다.")
    void pendingReservationEntitySavedAndKafkaEventProducedIfRequestSucceed() {
        // given
        final Shop mockedShop = shopAdapterRepository.saveAndFlush(
            Shop.newShop("시술소1", "010-1234-5678", "www.naver.com",
                "소개글1", Arrays.asList("image_id_1", "image_id_2"),
                null, null));

        final Operation mockedOperation = operationAdapterRepository.saveAndFlush(
            Operation.newOperation("시술1", "시술1설명"));

        final Operator mockedOperator = operatorAdaptorRepository.saveAndFlush(
            Operator.newOperator("operator@bp.com", "password", "시술선생님", "010-1111-2222"));

        long currentTime = System.currentTimeMillis();
        final ReservationRegistrationRequest mockedRequest = new ReservationRegistrationRequest(
            currentTime + DAY_TO_LONG,
            currentTime + DAY_TO_LONG + HOUR_TO_LONG,
            mockedShop.getId(),
            mockedOperation.getId(),
            mockedOperator.getId());

        final String requestedMemberEmail = "dev.sssukho@gmail.com";

        // when
        final ResponseMessage responseMessage = reservationService.registerReservationAndProduceEvent(
            mockedRequest, requestedMemberEmail);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(
            ReservationRegistrationResult.class);

        ReservationRegistrationResult result = (ReservationRegistrationResult) responseMessage.getReturnValue();
        final Reservation processedReservation = reservationAdapterRepository.findById(
            result.reservationId()).orElseThrow();

        assertThat(processedReservation.getStatus()).isEqualTo(ReservationStatus.PENDING);

        try (KafkaConsumer<String, ReservationEventProto> consumer = new KafkaConsumer<>(
            kafkaConsumerConfig.reservationEventConsumerConfig())) {
            consumer.subscribe(Collections.singleton(
                kafkaConfigurationProperties.getTopic().get(RESERVATION_EVENT_KAFKA_CONFIG_KEY)
                    .getTopicName()));
            ConsumerRecord<String, ReservationEventProto> consumedRecord = consumer.poll(
                Duration.ofSeconds(5)).iterator().next();

            final ReservationEventProto consumedReservationEvent = consumedRecord.value();

            assertThat(consumedReservationEvent.getReservationType()).isEqualTo(
                ReservationType.CREATE);
            assertThat(consumedReservationEvent.getReservationId()).isEqualTo(
                processedReservation.getId());
            assertThat(consumedReservationEvent.getStartDate()).isEqualTo(
                mockedRequest.startDate());
            assertThat(consumedReservationEvent.getEndDate()).isEqualTo(
                mockedRequest.endDate());
            assertThat(consumedReservationEvent.getRequestedMemberEmail()).isEqualTo(
                requestedMemberEmail);
        }
    }

    @Test
    @DisplayName("사용자의 예약 등록 요청시 reservation 엔티티는 저장했지만 카프카 이벤트 produce 에 실패할 경우 rollback 된다.")
    void rollbackIfFailToProduceEvent()  {
        // given
        final Shop mockedShop = shopAdapterRepository.saveAndFlush(
            Shop.newShop("시술소1", "010-1234-5678", "www.naver.com",
                "소개글1", Arrays.asList("image_id_1", "image_id_2"),
                null, null));

        final Operation mockedOperation = operationAdapterRepository.saveAndFlush(
            Operation.newOperation("시술1", "시술1설명"));

        final Operator mockedOperator = operatorAdaptorRepository.saveAndFlush(
            Operator.newOperator("operator@bp.com", "password", "시술선생님", "010-1111-2222"));

        long currentTime = System.currentTimeMillis();
        final ReservationRegistrationRequest mockedRequest = new ReservationRegistrationRequest(
            currentTime + DAY_TO_LONG,
            currentTime + DAY_TO_LONG + HOUR_TO_LONG,
            mockedShop.getId(),
            mockedOperation.getId(),
            mockedOperator.getId());

        final String requestedMemberEmail = "dev.sssukho@gmail.com";

        doThrow(new RuntimeException("강제 예외 발생")).when(reservationEventProducer)
            .publishReservationEvent(any(), any());

        // when
        assertThatThrownBy(
            () -> reservationService.registerReservationAndProduceEvent(mockedRequest,
                requestedMemberEmail)).isInstanceOf(RuntimeException.class);

        assertThat(reservationAdapterRepository.count()).isZero();

        try (KafkaConsumer<String, ReservationEventProto> consumer = new KafkaConsumer<>(
            kafkaConsumerConfig.reservationEventConsumerConfig())) {
            consumer.subscribe(Collections.singleton(
                kafkaConfigurationProperties.getTopic().get(RESERVATION_EVENT_KAFKA_CONFIG_KEY)
                    .getTopicName()));
            ConsumerRecords<String, ReservationEventProto> consumedRecords = consumer.poll(
                Duration.ofSeconds(5));

            assertThat(consumedRecords.count()).isZero();
        }
    }
}
