package com.beautify_project.bp_app_api.controller;

import com.beautify_project.bp_app_api.dto.auth.EmailCertificationRequest;
import com.beautify_project.bp_app_api.dto.auth.EmailDuplicatedRequest;
import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
        authService.sendCertificationEmail(request);
    }
}
