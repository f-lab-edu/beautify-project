package com.bp.app.event.consumer.listener;

import com.bp.app.event.consumer.notification.EmailNotification;
import com.bp.app.event.consumer.notification.Notification;
import com.bp.app.event.consumer.notification.NotificationType;
import com.bp.common.kakfa.event.ReservationEvent.ReservationEventProto;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Operator;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.bp.domain.mysql.repository.OperationAdapterRepository;
import com.bp.domain.mysql.repository.OperatorAdaptorRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationEventListener {

    public static final String KEY_OPERATION_NAME = "operationName";
    public static final String KEY_RESERVATION_START_TIME = "reservationStart";
    public static final String KEY_RESERVATION_END_TIME = "reservationEnd";
    public static final String KEY_REQUESTED_MEMBER_EMAIL = "requestedMemberEmail";
    public static final String KEY_REQUESTED_MEMBER_CONTACT = "requestedMemberContact";
    public static final String KEY_REQUESTED_MEMBER_NAME = "requestedMemberName";
    private static SimpleDateFormat DATE_FORMATTER;

    static {
        DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }

    private final OperationAdapterRepository operationAdapterRepository;
    private final OperatorAdaptorRepository operatorAdaptorRepository;
    private final MemberAdapterRepository memberAdapterRepository;
    private final EmailNotification emailNotification;

    @KafkaListener(
        topics = "#{kafkaConfigurationProperties.topic['RESERVATION-REGISTRATION-EVENT'].topicName}",
        groupId = "#{kafkaConfigurationProperties.topic['RESERVATION-REGISTRATION-EVENT'].consumer.groupId}",
        containerFactory = "reservationEventProtoConcurrentKafkaListenerContainerFactory")
    public void listenReservationEvent(final List<ReservationEventProto> events) {
        log.debug("{} counts of event consumed", events.size());
        sendReservationNotification(events);
    }

    private void sendReservationNotification(final List<ReservationEventProto> events) {
        final List<Map<String, ?>> dataListToSend = new ArrayList<>();

        events.forEach(event -> {

            final String reservationStartTime = DATE_FORMATTER.format(new Date(event.getStartDate()));
            final String reservationEndTime = DATE_FORMATTER.format(new Date(event.getEndDate()));

            final Operation foundOperation = operationAdapterRepository.findById(
                event.getOperationId()).orElseThrow();
            final String operationName = foundOperation.getName();

            final Long operatorId = event.getOperatorId();
            final Operator foundOperator = operatorAdaptorRepository.findById(operatorId)
                .orElseThrow();
            final String operatorEmail = foundOperator.getEmail();
            final String operatorContact = foundOperator.getContact();

            final String requestedMemberEmail = event.getRequestedMemberEmail();
            final Member foundMember = memberAdapterRepository.findByEmail(requestedMemberEmail)
                .orElseThrow();
            final String requestedMemberContact = foundMember.getContact();
            final String requestedMemberName = foundMember.getName();


            dataListToSend.add(Map.of(
                Notification.KEY_NOTIFICATION_TYPE, NotificationType.RESERVATION_CREATE,
                Notification.KEY_TARGET_MAIL, operatorEmail,
                Notification.KEY_TARGET_CONTACT, operatorContact,
                KEY_OPERATION_NAME, operationName,
                KEY_RESERVATION_START_TIME, reservationStartTime,
                KEY_RESERVATION_END_TIME, reservationEndTime,
                KEY_REQUESTED_MEMBER_EMAIL, requestedMemberEmail,
                KEY_REQUESTED_MEMBER_CONTACT, requestedMemberContact,
                KEY_REQUESTED_MEMBER_NAME, requestedMemberName
            ));
        });

        boolean sendResult = emailNotification.sendAll(dataListToSend);
        if (!sendResult) {
            processAfterFailed(dataListToSend);
        }
    }

    private void processAfterFailed(final List<Map<String, ?>> dataListToSend) {
        // TODO: implementation
    }

}
