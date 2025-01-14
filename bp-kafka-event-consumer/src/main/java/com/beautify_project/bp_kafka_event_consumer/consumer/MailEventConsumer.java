package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_kafka_event_consumer.provider.EmailSender;
import com.beautify_project.bp_mysql.entity.adapter.EmailCertificationAdapter;
import com.beautify_project.bp_mysql.repository.EmailCertificationAdapterRepository;
import com.beautify_project.bp_utils.UUIDGenerator;
import com.beuatify_project.bp_common.event.SignUpCertificationMailEvent;
import java.util.HashMap;
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
public class MailEventConsumer {


    private static final Long MINUTE_TO_LONG = 1000L * 60;
    private static final Long CERTIFICATION_EMAIL_VALID_TIME = 3 * MINUTE_TO_LONG;

    private final EmailSender emailSender;
    private final EmailCertificationAdapterRepository adaptorRepository;

    @KafkaListener(
        topics = "#{kafkaConsumerConfigProperties.topic['MAIL-SIGN-UP-CERTIFICATION-EVENT'].topicName}",
        groupId = "#{kafkaConsumerConfigProperties.topic['MAIL-SIGN-UP-CERTIFICATION-EVENT'].groupId}",
        containerFactory = "signUpCertificationMailEventConcurrentKafkaListenerContainerFactory"
    )
    public void listenMailSignUpCertificationEvent(
        final List<SignUpCertificationMailEvent> events) {
        log.debug("{} counts of event consumed", events.size());

        sendCertificationMail(events);
    }

    @Transactional
    private void sendCertificationMail(final List<SignUpCertificationMailEvent> events) {
        final Set<String> targetsFromEvents = events.stream().distinct()
            .map(SignUpCertificationMailEvent::email).collect(Collectors.toSet());

        final Set<String> validTargets = filterInvalidTargets(targetsFromEvents);
        adaptorRepository.deleteAllByEmails(validTargets);

        log.debug("{} counts of certification mails will be sent", validTargets.size());

        final Map<String, String> certificationNumberByTargetMail = new HashMap<>();
        for (String targetMail : validTargets) {
            certificationNumberByTargetMail.put(targetMail,
                UUIDGenerator.generateEmailCertificationNumber());
        }
        emailSender.sendAllSignUpCertificationMail(certificationNumberByTargetMail);

        long now = System.currentTimeMillis();
        final List<EmailCertificationAdapter> emailCertificationAdapters =
            certificationNumberByTargetMail.entrySet().stream().map(entrySet -> {
                return EmailCertificationAdapter.of(entrySet.getKey(), entrySet.getValue(), now);
            }).toList();
        adaptorRepository.saveAll(emailCertificationAdapters);
    }

    private Set<String> filterInvalidTargets(final Set<String> targetsFromEvents) {
        long now = System.currentTimeMillis();
        final List<EmailCertificationAdapter> alreadySentEmailCertifications = adaptorRepository.findByEmailIn(
            targetsFromEvents);

        for (EmailCertificationAdapter alreadySent : alreadySentEmailCertifications) {
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
