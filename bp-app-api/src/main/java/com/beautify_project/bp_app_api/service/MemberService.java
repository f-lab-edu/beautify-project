package com.beautify_project.bp_app_api.service;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.member.UserRoleMemberRegistrationRequest;
import com.beautify_project.bp_app_api.dto.member.UserRoleMemberRegistrationResult;
import com.beautify_project.bp_app_api.entity.Member;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.MemberRepository;
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
            .orElseThrow(() -> new NotFoundException(
                ErrorCode.ME001));
    }

    public boolean existByMemberMail(final String memberEmail) {
        return memberRepository.existsById(memberEmail);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage signUpUserRoleMember(final UserRoleMemberRegistrationRequest request) {
        final Member userRoleMemberToRegister = Member.createSelfAuthMember(request);
        final Member registeredUserRoleMember = memberRepository.save(userRoleMemberToRegister);
        return ResponseMessage.createResponseMessage(
            new UserRoleMemberRegistrationResult(registeredUserRoleMember.getEmail()));
    }
}
