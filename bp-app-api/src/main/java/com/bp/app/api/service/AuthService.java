package com.bp.app.api.service;

import com.bp.app.api.dto.AccessTokenDto;
import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.provider.JwtProvider;
import com.bp.app.api.request.auth.EmailCertificationVerificationRequest;
import com.bp.app.api.request.auth.EmailDuplicatedRequest;
import com.bp.app.api.request.auth.SignInRequest;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.auth.EmailDuplicatedResult;
import com.bp.app.api.response.auth.SignInResult;
import com.bp.app.api.utils.EncryptionUtils;
import com.bp.domain.mysql.entity.EmailCertification;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.repository.EmailCertificationAdapterRepository;
import com.bp.utils.Validator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Long MINUTE_TO_LONG = 1000L * 60L;

    private final MemberService memberService;
    private final EmailCertificationAdapterRepository emailCertificationAdapterRepository;
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
    public void verifyCertificationEmail(final EmailCertificationVerificationRequest request) {
        final EmailCertification foundEmailCertification = emailCertificationAdapterRepository.findByEmail(
            request.email());
        if (foundEmailCertification == null) {
            throw new BpCustomException(ErrorCode.EC002);
        }

        verifyEmailCertificationRequest(request.certificationNumber(),
            foundEmailCertification.getCertificationNumber());

        emailCertificationAdapterRepository.delete(foundEmailCertification);
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
        final Member foundMember = memberService.findMemberByEmailOrElseThrow(requestedEmail);

        validateSignInRequest(request, foundMember);

        final AccessTokenDto accessTokenDto = jwtProvider.generate(foundMember.getEmail(),
            Map.of("ROLE", foundMember.getRole().name()));

        return ResponseMessage.createResponseMessage(
            new SignInResult(accessTokenDto.accessToken(), accessTokenDto.accessTokenExpiresIn(),
                accessTokenDto.refreshToken(), accessTokenDto.refreshTokenExpiresIn()));
    }

    private void validateSignInRequest(final SignInRequest request, final Member foundMember) {
        if (foundMember == null || foundMember.getId() == null) {
            log.error("Member does not exist");
            throw new BpCustomException(ErrorCode.SI001);
        }

        if (!EncryptionUtils.matchesBCrypt(request.password(), foundMember.getPassword())) {
            log.error("Password does not match");
            throw new BpCustomException(ErrorCode.SI001);
        }
    }
}
