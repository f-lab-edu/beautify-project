package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.producer.KafkaEventProducer;
import com.beautify_project.bp_app_api.request.auth.EmailCertificationRequest;
import com.beautify_project.bp_app_api.request.auth.EmailCertificationVerificationRequest;
import com.beautify_project.bp_app_api.request.auth.EmailDuplicatedRequest;
import com.beautify_project.bp_app_api.response.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.response.ResponseMessage;
import com.beautify_project.bp_app_api.response.auth.EmailDuplicatedResult;
import com.beautify_project.bp_mysql.entity.EmailCertification;
import com.beautify_project.bp_mysql.repository.EmailCertificationRepository;
import com.beuatify_project.bp_common.event.SignUpCertificationMailEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final Long MINUTE_TO_LONG = 1000L * 60L;
    private static final Long CERTIFICATION_EMAIL_VALID_TIME = 3 * MINUTE_TO_LONG;

    private final MemberService memberService;
    private final KafkaEventProducer eventProducer;
    private final EmailCertificationRepository emailCertificationRepository;

    public ResponseMessage checkEmailDuplicated(final EmailDuplicatedRequest emailDuplicatedRequest) {
        if (isEmailCertificationExist(emailDuplicatedRequest.email())) {
            return ResponseMessage.createResponseMessage(new EmailDuplicatedResult("TRUE"));
        }
        return ResponseMessage.createResponseMessage(new EmailDuplicatedResult("FALSE"));
    }

    private boolean isEmailCertificationExist(final String email) {
        return memberService.existByMemberMail(email);
    }

    @Transactional(rollbackFor = Exception.class)
    public void verifyCertificationEmail(final EmailCertificationVerificationRequest request) {
        final EmailCertification foundEmailCertification = emailCertificationRepository.findByEmail(
            request.email());
        if (foundEmailCertification == null) {
            throw new BpCustomException(ErrorCode.EC002);
        }

        verifyEmailCertificationRequest(request.certificationNumber(),
            foundEmailCertification.getCertificationNumber());

        emailCertificationRepository.delete(foundEmailCertification);
    }

    private void verifyEmailCertificationRequest(final String requestedCertificationNumber,
        final String registeredCertificationNumber) {

        if (StringUtils.equals(registeredCertificationNumber,
            requestedCertificationNumber.toUpperCase())) {
            return;
        }
        throw new BpCustomException(ErrorCode.EC003);
    }

    public void produceSignUpEmailCertificationEvent(final EmailCertificationRequest request) {
        eventProducer.publishSignUpCertificationMailEvent(new SignUpCertificationMailEvent(
            request.email()));
    }
}
