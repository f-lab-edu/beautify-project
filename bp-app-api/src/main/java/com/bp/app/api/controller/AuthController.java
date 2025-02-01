package com.bp.app.api.controller;

import com.bp.app.api.request.auth.EmailCertificationRequest;
import com.bp.app.api.request.auth.EmailCertificationVerificationRequest;
import com.bp.app.api.request.auth.EmailDuplicatedRequest;
import com.bp.app.api.request.auth.SignInRequest;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.service.AuthService;
import com.bp.app.api.producer.SignUpCertificationMailEventProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SignUpCertificationMailEventProducer eventProducer;

    /**
     * 회원가입시 이메일 중복 체크
     */
    @PostMapping("/v1/auth/email/duplicated")
    @ResponseStatus(code = HttpStatus.OK)
    ResponseMessage checkEmailDuplicated(@Valid @RequestBody final EmailDuplicatedRequest request) {
        return authService.checkEmailDuplicated(request);
    }

    /**
     * 인증 번호 이메일 전송
     */
    @PostMapping("/v1/auth/email/certification")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void sendCertificationEmail(@Valid @RequestBody final EmailCertificationRequest request) {
        eventProducer.publishSignUpCertificationMailEvent(request.email());
    }

    /**
     * 이메일 인증 확인
     */
    @PostMapping("/v1/auth/email/certification/verification")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void verifyCertificationEmail(
        @Valid @RequestBody final EmailCertificationVerificationRequest request) {
        authService.verifyCertificationEmail(request);
    }

    /**
     * 로그인
     */
    @PostMapping("/v1/auth/sign-in")
    @ResponseStatus(code = HttpStatus.OK)
    ResponseMessage signIn(@Valid @RequestBody final SignInRequest signInRequest) {
        return authService.signIn(signInRequest);
    }

    /**
     * oauth2 로그인 성공시 redirect url 테스트용
     * FIXME: 추후 client 붙으면 삭제
     */
    @GetMapping("/v1/auth/test/{token}")
    @ResponseStatus(code = HttpStatus.OK)
    ResponseMessage test(@PathVariable(value = "token") final String token) {
        return ResponseMessage.createResponseMessage(token);
    }
}
