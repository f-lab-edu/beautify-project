package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.request.auth.EmailCertificationVerificationRequest;
import com.bp.app.api.request.auth.EmailDuplicatedRequest;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.auth.EmailDuplicatedResult;
import com.bp.domain.mysql.entity.EmailCertification;
import com.bp.domain.mysql.repository.EmailCertificationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final Long MINUTE_TO_LONG = 1000L * 60L;

    private final MemberService memberService;
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
}
