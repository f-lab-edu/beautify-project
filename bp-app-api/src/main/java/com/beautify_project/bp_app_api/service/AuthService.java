package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.dto.ResponseMessage;
import com.beautify_project.bp_app_api.dto.auth.EmailCertificationRequest;
import com.beautify_project.bp_app_api.dto.auth.EmailCertificationVerificationRequest;
import com.beautify_project.bp_app_api.dto.auth.EmailDuplicatedRequest;
import com.beautify_project.bp_app_api.dto.auth.EmailDuplicatedResult;
import com.beautify_project.bp_app_api.dto.auth.SignInRequest;
import com.beautify_project.bp_app_api.dto.auth.SignInResult;
import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_app_api.provider.EmailProvider;
import com.beautify_project.bp_security.dto.AccessTokenDto;
import com.beautify_project.bp_security.provider.JwtProvider;
import com.beautify_project.bp_security.utils.EncryptionUtils;
import com.beautify_project.bp_mysql.entity.EmailCertification;
import com.beautify_project.bp_mysql.entity.Member;
import com.beautify_project.bp_mysql.repository.EmailCertificationRepository;
import com.beautify_project.bp_utils.UUIDGenerator;
import com.beautify_project.bp_utils.Validator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final Long MINUTE_TO_LONG = 1000L * 60L;
    private static final Long CERTIFICATION_EMAIL_VALID_TIME = 3 * MINUTE_TO_LONG;

    private final MemberService memberService;
    private final EmailProvider emailProvider;
    private final EmailCertificationRepository emailCertificationRepository;
    private final JwtProvider jwtProvider;

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

        throw new BpCustomException(ErrorCode.EC001);
    }

    @Transactional(rollbackFor = Exception.class)
    public void verifyCertificationEmail(final EmailCertificationVerificationRequest request) {
        final EmailCertification foundEmailCertification = emailCertificationRepository.findById(
            request.email()).orElseThrow(() -> new BpCustomException(ErrorCode.EC002));

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

    public ResponseMessage signIn(final SignInRequest request) {
        final String requestedEmail = request.email();
        final Member foundMember = memberService.findMemberByEmail(requestedEmail);

        validateSignInRequest(request, foundMember);

        final AccessTokenDto accessTokenDto = jwtProvider.generate(foundMember.getEmail(),
            Map.of("ROLE", foundMember.getRole().name()));

        return ResponseMessage.createResponseMessage(new SignInResult(accessTokenDto.accessToken(),
            accessTokenDto.accessTokenExpiresIn(), accessTokenDto.refreshToken(),
            accessTokenDto.refreshTokenExpiresIn()));
    }

    private void validateSignInRequest(final SignInRequest request, final Member foundMember) {
        if (foundMember == null || Validator.isEmptyOrBlank(foundMember.getId())) {
            log.error("Member does not exist");
            throw new BpCustomException(ErrorCode.SI001);
        }

        if (!EncryptionUtils.matchesBCrypt(request.password(), foundMember.getPassword())) {
            log.error("Password does not match");
            throw new BpCustomException(ErrorCode.SI001);
        }
    }
}
