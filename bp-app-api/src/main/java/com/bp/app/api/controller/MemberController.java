package com.bp.app.api.controller;

import com.bp.app.api.request.member.UserRoleMemberRegistrationRequest;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/v1/members/user")
    @ResponseStatus(code = HttpStatus.OK)
    ResponseMessage signUpUserRoleMember(@Valid @RequestBody final UserRoleMemberRegistrationRequest request) {
        return memberService.signUpUserRoleMember(request);
    }
}
