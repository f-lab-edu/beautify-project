package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.dto.ResponseMessage;
import com.beautify_project.bp_app_api.dto.member.UserRoleMemberRegistrationRequest;
import com.beautify_project.bp_app_api.dto.member.UserRoleMemberRegistrationResult;
import com.beautify_project.bp_app_api.exception.BpCustomException;
import com.beautify_project.bp_mysql.adapter.MemberAdapter;
import com.beautify_project.bp_security.utils.EncryptionUtils;
import com.beautify_project.bp_mysql.entity.Member;
import com.beautify_project.bp_mysql.entity.enumerated.AuthType;
import com.beautify_project.bp_mysql.entity.enumerated.MemberStatus;
import com.beautify_project.bp_mysql.entity.enumerated.UserRole;
import com.beautify_project.bp_mysql.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findMemberByEmailOrElseThrow(final String memberEmail) {
        return memberRepository.findById(memberEmail)
            .orElseThrow(() -> new BpCustomException(ErrorCode.ME001));
    }

    public Member findMemberByEmail(final String memberEmail) {
        return memberRepository.findByEmail(memberEmail);
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

    public Member createNewSelfAuthMember(final UserRoleMemberRegistrationRequest request) {
        return Member.createNewMember(request.email(), encryptPassword(request.password()), request.name(),
            request.contact(), AuthType.BP, UserRole.USER, MemberStatus.ACTIVE,
            System.currentTimeMillis());
    }

    public String encryptPassword(final String plainPassword) {
        return EncryptionUtils.encodeBCrypt(plainPassword);
    }
}
