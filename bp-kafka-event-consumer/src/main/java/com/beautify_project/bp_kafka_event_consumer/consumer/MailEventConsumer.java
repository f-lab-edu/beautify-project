package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_common_kafka.event.SignUpCertificationMailEvent.SignUpCertificationMailEventProto;
import com.beautify_project.bp_kafka_event_consumer.provider.EmailSender;
import com.beautify_project.bp_mysql.entity.EmailCertification;
import com.beautify_project.bp_mysql.repository.EmailCertificationAdapterRepository;
import com.beautify_project.bp_utils.UUIDGenerator;
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
public class MailEventConsumer {

    private static final Long MINUTE_TO_LONG = 1000 * 60L;
    private static final Long CERTIFICATION_EMAIL_VALID_TIME = 3 * MINUTE_TO_LONG;

    private final EmailSender emailSender;
    private final EmailCertificationAdapterRepository emailCertificationRepository;

    @KafkaListener(
        topics = "#{kafkaConfigurationProperties.topic['MAIL-SIGN-UP-CERTIFICATION-EVENT'].topicName}",
        groupId = "#{kafkaConfigurationProperties.topic['MAIL-SIGN-UP-CERTIFICATION-EVENT'].consumer.groupId}",
        containerFactory = "signUpCertificationMailEventConcurrentKafkaListenerContainerFactory"
    )
    public void listenMailSignUpCertificationEvent(
        final List<SignUpCertificationMailEventProto> events) {
        sendCertificationMail(events);
    }

    @Transactional
    private void sendCertificationMail(final List<SignUpCertificationMailEventProto> events) {
        final Set<String> targetsFromEvents = events.stream()
            .distinct()
            .map(SignUpCertificationMailEventProto::getMemberEmail)
            .collect(Collectors.toSet());

        final Set<String> validTargets = filterInvalidTargets(targetsFromEvents);
        emailCertificationRepository.deleteAllByEmails(validTargets);

        log.debug("{} counts of certification mails will be sent", validTargets.size());

        final Map<String, String> certificationNumberByTargetMail = validTargets.stream()
            .collect(
                Collectors.toMap(
                    targetMail -> targetMail,
                    targetMail -> UUIDGenerator.generateEmailCertificationNumber()
            ));

        emailSender.sendAllSignUpCertificationMail(certificationNumberByTargetMail);

        long now = System.currentTimeMillis();
        final List<EmailCertification> emailCertifications =
            certificationNumberByTargetMail.entrySet().stream().map(entrySet -> {
                return EmailCertification.of(entrySet.getKey(), entrySet.getValue(), now);
            }).toList();

        emailCertificationRepository.saveAll(emailCertifications);
    }

    private Set<String> filterInvalidTargets(final Set<String> targetsFromEvents) {
        long now = System.currentTimeMillis();
        final List<EmailCertification> alreadySentEmailCertifications =
            emailCertificationRepository.findByEmailsIn(targetsFromEvents);

        for (EmailCertification alreadySent : alreadySentEmailCertifications) {
            if (!isValidTime(now, alreadySent.getRegisteredTime())) {
                targetsFromEvents.remove(alreadySent.getEmail());
            }
        }
        return targetsFromEvents;
    }

    private boolean isValidTime(long now, long alreadySentRegisteredTime) {
        return now - alreadySentRegisteredTime > CERTIFICATION_EMAIL_VALID_TIME;
    }
}
