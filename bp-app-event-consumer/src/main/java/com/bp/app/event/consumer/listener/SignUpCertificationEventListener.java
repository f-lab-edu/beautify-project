package com.bp.app.event.consumer.listener;

import com.bp.app.event.consumer.notification.EmailNotification;
import com.bp.app.event.consumer.notification.Notification;
import com.bp.app.event.consumer.notification.NotificationType;
import com.bp.common.kakfa.event.SignUpCertificationMailEvent.SignUpCertificationMailEventProto;
import com.bp.domain.mysql.entity.EmailCertification;
import com.bp.domain.mysql.repository.EmailCertificationAdapterRepository;
import com.bp.utils.UUIDGenerator;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SignUpCertificationEventListener {

    private static final Long MINUTE_TO_LONG = 1000 * 60L;
    private static final Long CERTIFICATION_EMAIL_VALID_TIME = 3 * MINUTE_TO_LONG;

    private final EmailNotification emailNotification;
    private final EmailCertificationAdapterRepository emailCertificationRepository;

    @KafkaListener(
        topics = "#{kafkaConfigurationProperties.topic['MAIL-SIGN-UP-CERTIFICATION-EVENT'].topicName}",
        groupId = "#{kafkaConfigurationProperties.topic['MAIL-SIGN-UP-CERTIFICATION-EVENT'].consumer.groupId}",
        containerFactory = "signUpCertificationMailEventConcurrentKafkaListenerContainerFactory"
    )
    @Transactional
    public void listenMailSignUpCertificationEvent(final List<SignUpCertificationMailEventProto> events)
        throws Exception{
        sendCertificationMail(events);
    }

    @Transactional(rollbackFor = Exception.class)
    private void sendCertificationMail(final List<SignUpCertificationMailEventProto> events) throws Exception {

        final Set<String> filteredDuplicatedEvents = events.stream()
            .distinct()
            .map(SignUpCertificationMailEventProto::getMemberEmail)
            .collect(Collectors.toSet());

        final Set<String> validTargets = filterInvalidTargets(filteredDuplicatedEvents);
        emailCertificationRepository.deleteAllByEmails(validTargets);

        log.debug("{} counts of certification mails will be sent", validTargets.size());

        // 바로 dataListToSend 생성
        final List<Map<String, ?>> dataListToSend = validTargets.stream()
            .<Map<String, ?>>map(targetMail -> Map.of(
                Notification.KEY_TARGET_MAIL, targetMail,
                Notification.KEY_CONTENT, UUIDGenerator.generateEmailCertificationNumber(),
                Notification.KEY_NOTIFICATION_TYPE, NotificationType.SIGNUP_CERTIFICATION
            ))
            .toList();

        emailNotification.sendAll(dataListToSend);

        // EmailCertification 객체 리스트 생성 후 저장
        final List<EmailCertification> emailCertifications = dataListToSend.stream()
            .map(data -> EmailCertification.newEmailCertification(
                (String) data.get(Notification.KEY_TARGET_MAIL),
                (String) data.get(Notification.KEY_CONTENT)
            ))
            .toList();

        emailCertificationRepository.saveAll(emailCertifications);
    }

    private Set<String> filterInvalidTargets(final Set<String> targetsFromEvents) {
        long now = System.currentTimeMillis();
        final List<EmailCertification> alreadySentEmailCertifications =
            emailCertificationRepository.findByEmailsIn(targetsFromEvents);

        for (EmailCertification alreadySent : alreadySentEmailCertifications) {
            if (!isValidTime(now,
                alreadySent.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli())) {

            }

            if (!isValidTime(now,
                alreadySent.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli())) {

                targetsFromEvents.remove(alreadySent.getEmail());
            }
        }
        return targetsFromEvents;
    }

    private boolean isValidTime(long now, long alreadySentRegisteredTime) {
        return now - alreadySentRegisteredTime > CERTIFICATION_EMAIL_VALID_TIME;
    }
}
