package com.beautify_project.bp_app_api.provider;

import com.beautify_project.bp_app_api.config.IOBoundAsyncThreadPoolConfiguration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender mailSender;
    private final IOBoundAsyncThreadPoolConfiguration ioBoundAsyncThreadPoolConfig;


    private static final String MAIL_SUBJECT = "[BP] 회원가입 인증메일입니다.";
    private static final String MAIL_CERTIFICATION_MESSAGE_FORMAT = "<h1 style='text-align: center;'>" + MAIL_SUBJECT
        + "<h3 style='text-align: center;'>인증코드 : <strong style='font-size: 32px; letter-spacing: 8px;'>"
        + "%s" + "</strong></h3>";

    @Async("ioBoundExecutor")
    public void sendCertificationMail(final String targetEmail, final String certificationNumber) {
        final String htmlContent = createHtmlContent(certificationNumber);
        try {
            send(targetEmail, htmlContent);
        } catch (Exception exception) {
            log.error("Failed to send mail - target: {}", targetEmail, exception);
        }
    }

    private static String createHtmlContent(final String certificationNumber) {
        return String.format(MAIL_CERTIFICATION_MESSAGE_FORMAT, certificationNumber);
    }

    private void send(final String targetEmail, final String htmlContent) throws MessagingException {
        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setTo(targetEmail);
        messageHelper.setSubject(MAIL_SUBJECT);
        messageHelper.setText(htmlContent, true);
        mailSender.send(message);
        log.debug("Succeed to send mail - target: {}", targetEmail);
    }
}
