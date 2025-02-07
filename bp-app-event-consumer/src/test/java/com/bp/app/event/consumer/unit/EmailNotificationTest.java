package com.bp.app.event.consumer.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bp.app.event.consumer.listener.ReservationEventListener;
import com.bp.app.event.consumer.notification.EmailNotification;
import com.bp.app.event.consumer.notification.Notification;
import com.bp.app.event.consumer.notification.NotificationType;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationTest {

    @InjectMocks
    private EmailNotification emailNotification;

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @Captor
    private ArgumentCaptor<MimeMessage[]> mimeMessagesCaptor;

    @Test
    @DisplayName("예약 생성 알림 단건을 보낼 때 수신자는 NotificationType.KEY_TARGET_MAIL 에 해당하는 값이 되어야 한다.")
    void receiverShouldBeKeyTargetMailWhenSendingReservationCreateNotification() throws Exception{
        // given
        final String target = "target@bp.com";
        final Map<String, ?> mockedDataForReservationEvent = Map.of(
            Notification.KEY_NOTIFICATION_TYPE, NotificationType.RESERVATION_CREATE,
            Notification.KEY_TARGET_MAIL, target,
            Notification.KEY_TARGET_CONTACT, "010-1234-5678",
            ReservationEventListener.KEY_OPERATION_NAME, "operationName",
            ReservationEventListener.KEY_RESERVATION_START_TIME, "2025-01-01 13:00",
            ReservationEventListener.KEY_RESERVATION_END_TIME, "2025-01-01 14:00",
            ReservationEventListener.KEY_REQUESTED_MEMBER_EMAIL, "requestor@bp.com",
            ReservationEventListener.KEY_REQUESTED_MEMBER_NAME, "요청자이름",
            ReservationEventListener.KEY_REQUESTED_MEMBER_CONTACT, "010-1111-2222"
        );

        final MimeMessage mockedMimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mockedMimeMessage);

        // when
        final boolean sendResult = emailNotification.send(mockedDataForReservationEvent);

        // then
        assertThat(sendResult).isTrue();
        verify(mailSender).send(mimeMessageCaptor.capture());

        final MimeMessage capturedMessage = mimeMessageCaptor.getValue();
        final Address[] recipients = capturedMessage.getRecipients(Message.RecipientType.TO);
        assertThat(recipients).isNotNull();
        assertThat(recipients[0].toString()).isEqualTo(target);
    }
}
