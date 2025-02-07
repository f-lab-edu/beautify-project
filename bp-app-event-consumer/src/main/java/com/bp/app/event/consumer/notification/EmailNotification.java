package com.bp.app.event.consumer.notification;

import com.bp.app.event.consumer.listener.ReservationEventListener;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotification implements Notification {

    private final JavaMailSender mailSender;

    private static final String SIGNUP_CERTIFICATION_SUBJECT = "[BP] 회원가입 인증메일입니다.";
    private static final String SIGNUP_CERTIFICATION_CONTENT_FORMAT =
        "<h1 style='text-align: center;'>" + SIGNUP_CERTIFICATION_SUBJECT
            + "<h3 style='text-align: center;'>인증코드 : <strong style='font-size: 32px; letter-spacing: 8px;'>"
            + "%s" + "</strong></h3>";
    private static final String RESERVATION_CREATED_SUBJECT = "[BP] 예약 요청 알림 메일입니다.";
    private static final String RESERVATION_CREATE_CONTENT_FORMAT =
        "<h1 style='text-align: center;'> " + RESERVATION_CREATED_SUBJECT + "</h1>\n"
            + "<li style='text-align: center;'> <strong style='font-size: 24px;'> 예약 시술: ${operationName}</strong>\n"
            + "<li style='text-align: center;'> <strong style='font-size: 24px;'> 예약 시간: ${reservationStart} ~ ${reservationEnd} </strong>\n"
            + "<li style='text-align: center;'> <strong style='font-size: 24px;'> 요청 회원 이메일: ${requestedMemberEmail} </strong>\n"
            + "<li style='text-align: center;'> <strong style='font-size: 24px;'> 요청 회원 연락처: ${requestedMemberContact} </strong>\n"
            + "<li style='text-align: center;'> <strong style='font-size: 24px;'> 요청 회원 이름:  ${requestedMemberName}</strong>";

    @Override
    public boolean send(final Map<String, ?> dataToSend) {
        try {
            final MimeMessage mailToSend = createMimeMessage(dataToSend);
            mailSender.send(mailToSend);
        } catch (Exception exception) {
            String target = (String) dataToSend.get(KEY_TARGET_MAIL);
            NotificationType type = (NotificationType) dataToSend.get(KEY_NOTIFICATION_TYPE);
            log.error("Failed to send {} mail to {}", type, target, exception);
            return false;
        }
        return true;
    }

    @Override
    public boolean sendAll(final List<Map<String, ?>> dataListToSend) {
        try {
            final MimeMessage[] mailsToSend = createMimeMessages(dataListToSend);
            mailSender.send(mailsToSend);
        } catch (Exception exception) {
            log.error("Failed to send mails.", exception);
            return false;
        }
        return true;
    }

    private MimeMessage createMimeMessage(final Map<String, ?> dataToSend) throws Exception {
        final NotificationType type = (NotificationType) dataToSend.get(KEY_NOTIFICATION_TYPE);

        if (type == NotificationType.SIGNUP_CERTIFICATION) {
            return createSignupCertificationMimeMessage(dataToSend);
        }

        if (type == NotificationType.RESERVATION_CREATE) {
            return createReservationCreatedMimeMessage(dataToSend);
        }

        throw new IllegalArgumentException("지원하지 않는 알림 타입입니다.");
    }

    private MimeMessage[] createMimeMessages(final List<Map<String, ?>> dataListToSend)
        throws Exception {
        final MimeMessage[] messages = new MimeMessage[dataListToSend.size()];
        int idx = 0;
        for (Map<String, ?> dataToSend : dataListToSend) {
            messages[idx] = createMimeMessage(dataToSend);
            idx++;
        }
        return messages;
    }

    private MimeMessage createSignupCertificationMimeMessage(final Map<String, ?> dataToSend)
        throws Exception {

        final String target = (String) dataToSend.get(KEY_TARGET_MAIL);
        final String certificationNumber = (String) dataToSend.get(KEY_CONTENT);
        final String content = String.format(SIGNUP_CERTIFICATION_CONTENT_FORMAT,
            certificationNumber);

        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setTo(target);
        messageHelper.setSubject(SIGNUP_CERTIFICATION_SUBJECT);
        messageHelper.setText(content, true);
        return message;
    }

    private MimeMessage createReservationCreatedMimeMessage(final Map<String, ?> dataToSend)
        throws Exception {

        final String target = (String) dataToSend.get(KEY_TARGET_MAIL);
        final String operationName = (String) dataToSend.get(ReservationEventListener.KEY_OPERATION_NAME);
        final String reservationStart = (String) dataToSend.get(ReservationEventListener.KEY_RESERVATION_START_TIME);
        final String reservationEnd = (String) dataToSend.get(ReservationEventListener.KEY_RESERVATION_END_TIME);
        final String requestedMemberEmail = (String) dataToSend.get(ReservationEventListener.KEY_REQUESTED_MEMBER_EMAIL);
        final String requestedMemberContact = (String) dataToSend.get(ReservationEventListener.KEY_REQUESTED_MEMBER_CONTACT);
        final String requestedMemberName = (String) dataToSend.get(ReservationEventListener.KEY_REQUESTED_MEMBER_NAME);

        String content = StringUtils.replace(RESERVATION_CREATE_CONTENT_FORMAT, "${operationName}", operationName);
        content = StringUtils.replace(content, "${reservationStart}", reservationStart);
        content = StringUtils.replace(content, "${reservationEnd}", reservationEnd);
        content = StringUtils.replace(content, "${requestedMemberEmail}", requestedMemberEmail);
        content = StringUtils.replace(content, "${requestedMemberContact}", requestedMemberContact);
        content = StringUtils.replace(content, "${requestedMemberName}", requestedMemberName);

        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setTo(target);
        messageHelper.setSubject(RESERVATION_CREATED_SUBJECT);
        messageHelper.setText(content, true);
        return message;
    }
}
