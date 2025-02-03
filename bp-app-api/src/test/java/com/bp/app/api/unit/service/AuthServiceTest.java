package com.bp.app.api.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bp.app.api.dto.AccessTokenDto;
import com.bp.app.api.provider.JwtProvider;
import com.bp.app.api.request.auth.EmailDuplicatedRequest;
import com.bp.app.api.request.auth.SignInRequest;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.auth.EmailDuplicatedResult;
import com.bp.app.api.response.auth.SignInResult;
import com.bp.app.api.service.AuthService;
import com.bp.app.api.service.MemberService;
import com.bp.app.api.utils.EncryptionUtils;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.MemberStatus;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @Mock
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("회원가입시 기존에 등록된 회원이면 isDuplicated 값을 true 로 응답 받는다.")
    void getResponseMessageWrappingEmailDuplicatedResultIsTrueIfEmailIsRegisteredWhenSignUp() {
        // given
        when(memberService.existByMemberMail(any())).thenReturn(true);
        final EmailDuplicatedRequest mockedRequest = new EmailDuplicatedRequest(
            "dev.sssukho@gmail.com");

        // when
        final ResponseMessage responseMessage = authService.checkEmailDuplicated(mockedRequest);
        final EmailDuplicatedResult returnValue = (EmailDuplicatedResult) responseMessage.getReturnValue();

        // then
        assertThat(returnValue.isDuplicated()).isEqualTo("TRUE");
        verify(memberService, times(1)).existByMemberMail(any());
    }

    @Test
    @DisplayName("회원가입시 기존에 등록되지 않은 회원이면 isDuplicated 값을 false 로 응답 받는다.")
    void getResponseMessageWrappingEmailDuplicatedResultIsFalseIfEmailIsRegisteredWhenSignUp() {
        // given
        when(memberService.existByMemberMail(any())).thenReturn(false);
        final EmailDuplicatedRequest mockedRequest = new EmailDuplicatedRequest(
            "dev.sssukho@gmail.com");

        // when
        final ResponseMessage responseMessage = authService.checkEmailDuplicated(mockedRequest);
        final EmailDuplicatedResult returnValue = (EmailDuplicatedResult) responseMessage.getReturnValue();

        // then
        assertThat(returnValue.isDuplicated()).isEqualTo("FALSE");
        verify(memberService, times(1)).existByMemberMail(any());
    }

    @Test
    @DisplayName("로그인(sign-in)에 성공하면 SignInResult 를 wrapping 한 ResponseMessage 객체를 리턴한다.")
    void returnResponseMessageWrappingSignInResultIfSignInSucceed() {

        // given
        final Member mockedMember = spy(
            Member.newMember("dev.sssukho@mgmail.com", EncryptionUtils.encodeBCrypt("password"),
                "이름", "010-1234-5678", AuthType.BP, UserRole.USER, MemberStatus.ACTIVE,
                System.currentTimeMillis()));

        when(memberService.findMemberByEmailOrElseThrow(any())).thenReturn(mockedMember);
        when(mockedMember.getId()).thenReturn(1L);
        when(jwtProvider.generate(any(), any())).thenReturn(
            new AccessTokenDto("temp", "temp", System.currentTimeMillis(), "temp",
                System.currentTimeMillis()));

        final SignInRequest mockedSignInRequest = new SignInRequest("dev.sssukho@gmail.com",
            "password");

        // when
        final ResponseMessage responseMessage = authService.signIn(mockedSignInRequest);

        // then
        assertThat(responseMessage.getReturnValue()).isInstanceOf(SignInResult.class);
        verify(memberService, times(1)).findMemberByEmailOrElseThrow(any());
        verify(jwtProvider, times(1)).generate(any(), any());
    }
}
