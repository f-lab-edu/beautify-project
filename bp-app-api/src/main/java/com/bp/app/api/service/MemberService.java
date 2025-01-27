package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.ResponseMessage;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.MemberStatus;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.repository.MemberRepository;
import com.bp.app.api.request.member.UserRoleMemberRegistrationRequest;
import com.bp.app.api.response.member.UserRoleMemberRegistrationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final String DEFAULT_PASSWORD = "DEFAULT_PASSWORD";

    private final MemberRepository memberRepository;

    public Member findMemberByEmailOrElseThrow(final String memberEmail) {
        return memberRepository.findById(memberEmail)
            .orElseThrow(() -> new BpCustomException(ErrorCode.ME001));
    }

    public boolean existByMemberMail(final String memberEmail) {
        return memberRepository.existsById(memberEmail);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage signUpUserRoleMember(final UserRoleMemberRegistrationRequest request) {
        final Member registeredUserRoleMember = memberRepository.save(
            createNewSelfAuthMember(request));

        return ResponseMessage.createResponseMessage(
            new UserRoleMemberRegistrationResult(registeredUserRoleMember.getEmail()));
    }

    public static Member createNewSelfAuthMember(final UserRoleMemberRegistrationRequest request) {
        return Member.newMember(request.email(), generateEncryptedDefaultPassword(), request.name(),
            request.contact(), AuthType.BP, UserRole.USER, MemberStatus.ACTIVE,
            System.currentTimeMillis());
    }

    private static String generateEncryptedDefaultPassword() {
        return PASSWORD_ENCODER.encode(DEFAULT_PASSWORD);
    }

    public String encryptPassword(final String plainPassword) {
        return PASSWORD_ENCODER.encode(plainPassword);
    }

    public boolean passwordMatches(final String encryptedPassword, final String inputPassword) {
        return PASSWORD_ENCODER.matches(encryptedPassword, inputPassword);
    }
}
