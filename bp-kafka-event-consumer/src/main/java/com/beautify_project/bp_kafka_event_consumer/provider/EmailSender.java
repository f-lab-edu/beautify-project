package com.beautify_project.bp_kafka_event_consumer.provider;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    private static final String MAIL_SIGN_UP_CERTIFICATION_SUBJECT = "[BP] 회원가입 인증메일입니다.";
    private static final String MAIL_SIGN_UP_CERTIFICATION_CONTENT_FORMAT =
        "<h1 style='text-align: center;'>" + MAIL_SIGN_UP_CERTIFICATION_SUBJECT
            + "<h3 style='text-align: center;'>인증코드 : <strong style='font-size: 32px; letter-spacing: 8px;'>"
            + "%s" + "</strong></h3>";

    public void sendAllSignUpCertificationMail(final Map<String, String> certificationNumberByTarget) {

        final MimeMessage[] messages = new MimeMessage[certificationNumberByTarget.size()];

        int idx = 0;
        for (Map.Entry<String, String> entry : certificationNumberByTarget.entrySet()) {
            try {
                createMessage(entry, messages, idx);
                idx++;
            } catch (MessagingException exception) {
                log.error("Failed to send SignUpCertificationMail: target - {}",
                    entry.getKey(), exception);
            }
        }

        mailSender.send(messages);
    }

    private void createMessage(final Entry<String, String> certificationNumberByTarget, final MimeMessage[] messages,
        final int idx) throws MessagingException {
        final String target = certificationNumberByTarget.getKey();
        final String certificationNumber = certificationNumberByTarget.getValue();
        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setTo(target);
        messageHelper.setSubject(MAIL_SIGN_UP_CERTIFICATION_SUBJECT);
        messageHelper.setText(
            String.format(MAIL_SIGN_UP_CERTIFICATION_CONTENT_FORMAT, certificationNumber), true);
        messages[idx] = message;
    }
}
