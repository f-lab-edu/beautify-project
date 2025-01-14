package com.beautify_project.bp_kafka_event_consumer.consumer;

import com.beautify_project.bp_kafka_event_consumer.provider.EmailSender;
import com.beautify_project.bp_mysql.entity.EmailCertification;
import com.beautify_project.bp_mysql.entity.adapter.EmailCertificationAdapter;
import com.beautify_project.bp_mysql.repository.EmailCertificationAdapterRepository;
import com.beautify_project.bp_utils.UUIDGenerator;
import com.beuatify_project.bp_common.event.SignUpCertificationMailEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        // 1. 보내야 할 대상 (중복 제거)
        final Set<String> targetsFromEvents = events.stream().distinct()
            .map(SignUpCertificationMailEvent::email).collect(Collectors.toSet());

        // 2. 유효한 대상만 필터링 (이미 메일 전송했으나 유효시간 지난 대상들)
        final Set<String> validTargets = filterInvalidTargets(targetsFromEvents);
        // 시간 지난 애들은 삭제해줘야 나중에 db 에 넣어줄 수 있음
        adaptorRepository.deleteAllByEmails(validTargets);

        log.debug("{} counts of certification mails will be sent", validTargets.size());

        // 3. 타겟별 메일 내용 추가
        final Map<String, String> certificationNumberByTargetMail = new HashMap<>();
        for (String targetMail : validTargets) {
            certificationNumberByTargetMail.put(targetMail,
                UUIDGenerator.generateEmailCertificationNumber());
        }
        emailSender.sendAllSignUpCertificationMail(certificationNumberByTargetMail);

        // 4. DB에 전송 이력 저장
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
        if (now - alreadySentRegisteredTime <= CERTIFICATION_EMAIL_VALID_TIME) {
            return false;
        }
        return true;
    }
}
