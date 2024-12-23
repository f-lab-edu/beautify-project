package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.auth.EmailCertificationRequest;
import com.beautify_project.bp_app_api.dto.auth.EmailCertificationVerificationRequest;
import com.beautify_project.bp_app_api.dto.auth.EmailDuplicatedRequest;
import com.beautify_project.bp_app_api.dto.auth.EmailDuplicatedResult;
import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.entity.EmailCertification;
import com.beautify_project.bp_app_api.exception.InvalidRequestException;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.provider.EmailProvider;
import com.beautify_project.bp_app_api.repository.EmailCertificationRepository;
import com.beautify_project.bp_app_api.utils.UUIDGenerator;
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
    private final EmailProvider emailProvider;
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
    public void sendCertificationEmail(final EmailCertificationRequest request) {

        EmailCertification foundEmailCertification = emailCertificationRepository.findByEmail(
            request.email());

        validateEmailCertificationRequest(foundEmailCertification);
        final String generatedCertificationNumber = UUIDGenerator.generateEmailCertificationNumber();

        if (foundEmailCertification == null) {
            foundEmailCertification = EmailCertification.of(request.email(),
                generatedCertificationNumber, System.currentTimeMillis());
        }

        emailCertificationRepository.save(foundEmailCertification);

        emailProvider.sendCertificationMail(foundEmailCertification.getEmail(),
            UUIDGenerator.generateEmailCertificationNumber());
    }

    private void validateEmailCertificationRequest(
        final EmailCertification foundEmailCertification) {
        if (foundEmailCertification == null) {
            return;
        }

        if (System.currentTimeMillis() - foundEmailCertification.getRegisteredTime() > CERTIFICATION_EMAIL_VALID_TIME) {
            return;
        }

        throw new InvalidRequestException(ErrorCode.EC001);
    }

    @Transactional(rollbackFor = Exception.class)
    public void verifyCertificationEmail(final EmailCertificationVerificationRequest request) {
        final EmailCertification foundEmailCertification = emailCertificationRepository.findById(
            request.email()).orElseThrow(() -> new NotFoundException(ErrorCode.EC002));

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
        throw new InvalidRequestException(ErrorCode.EC003);
    }
}
