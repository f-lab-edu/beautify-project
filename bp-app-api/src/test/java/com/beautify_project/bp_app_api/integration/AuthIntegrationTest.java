package com.beautify_project.bp_app_api.integration;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beautify_project.bp_app_api.dto.auth.EmailCertificationRequest;
import com.beautify_project.bp_app_api.dto.auth.EmailDuplicatedRequest;
import com.beautify_project.bp_app_api.dto.member.UserRoleMemberRegistrationRequest;
import com.beautify_project.bp_app_api.entity.EmailCertification;
import com.beautify_project.bp_app_api.entity.Member;
import com.beautify_project.bp_app_api.repository.EmailCertificationRepository;
import com.beautify_project.bp_app_api.repository.MemberRepository;
import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration-test")
public class AuthIntegrationTest {

    private static final String AUTH_EMAIL_DUPLICATED_URL = "/v1/auth/email/duplicated";
    private static final String AUTH_EMAIL_CERTIFICATION_URL = "/v1/auth/email/certification";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailCertificationRepository emailCertificationRepository;

    @BeforeEach
    void beforeEach() {
        deleteAll();
    }

    private void deleteAll() {
        memberRepository.deleteAllInBatch();
        emailCertificationRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원가입시 이메일 중복 체크에서 중복되지 않는 아이디이면 isDuplicated=false 로 응답을 받는다.")
    void given_emailDuplicatedCheckRequest_when_succeed_then_get_responseMessageWrappingEmailDuplicatedCheckResultAsFalse() throws Exception{
        // given
        final UserRoleMemberRegistrationRequest mockedRequest = new UserRoleMemberRegistrationRequest(
            "dev.sssukho@gmail.com", "1234", "이름", "010-1234-5678");
        final Member insertedMember = Member.createSelfAuthMember(mockedRequest);
        memberRepository.saveAndFlush(insertedMember);

        final EmailDuplicatedRequest request = new EmailDuplicatedRequest(
            "sssukho@gmail.com");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(AUTH_EMAIL_DUPLICATED_URL).contentType(
                MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.isDuplicated").value("FALSE"))
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입시 이메일 중복 체크에서 중복되는 아이디라면 isDuplicated=true 로 응답을 받는다.")
    void given_emailDuplicatedCheckRequest_when_duplicated_id_then_get_responseMessageWrappingEmailDuplicatedCheckResultAsTrue() throws Exception{
        // given
        final UserRoleMemberRegistrationRequest mockedRequest = new UserRoleMemberRegistrationRequest(
            "dev.sssukho@gmail.com", "1234", "이름", "010-1234-5678");

        final Member insertedMember = Member.createSelfAuthMember(mockedRequest);
        memberRepository.saveAndFlush(insertedMember);

        final EmailDuplicatedRequest request = new EmailDuplicatedRequest(
            "dev.sssukho@gmail.com");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(AUTH_EMAIL_DUPLICATED_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.isDuplicated").value("TRUE"))
            .andDo(print());
    }

    @Test
    @DisplayName("회원가입시 유효하지 않은 요청을 보내는 경우 BadRequest 로 응답을 받는다.")
    void given_invalidEmailDuplicatedCheckRequest_when_failed_then_get_responseMessageWrappingBadRequest() throws Exception {
        // given
        final EmailDuplicatedRequest request = new EmailDuplicatedRequest("    ");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(AUTH_EMAIL_DUPLICATED_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorCode").value("BR001"))
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

    @Disabled
    @Test
    @DisplayName("이메일 인증 번호 전송 요청시 인증 메일 요청 이력이 없을 경우, 메일을 전송하고, NO_CONTENT 로 응답을 받는다.")
    void given_sendingCertificationEmailRequest_when_request_record_does_not_exist_then_getNoContentResponse() throws Exception {
        // given
        final EmailCertificationRequest mockedRequest = new EmailCertificationRequest(
            "dev.sssukho@gmail.com");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(AUTH_EMAIL_CERTIFICATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedRequest))
        );

        // then
        resultActions
            .andExpect(status().isNoContent())
            .andDo(print());
    }

    @Disabled
    @Test
    @DisplayName("이메일 인증 번호 전송 요청시 인증 메일 요청 이력이 있지만 인증 요청 유효 시간이 지난 경우, 메일을 전송하고, NO_CONTENT 로 응답을 받는다.")
    void given_sendingCertificationEmailRequest_when_request_record_exist_but_validation_time_exceeded_then_getNoContentResponse() throws Exception{
        // given
        final String insertedEmail = "dev.sssukho@gmail.com";
        final EmailCertification insertedEmailCertificationEntity = EmailCertification.of(
            insertedEmail, UUIDGenerator.generateEmailCertificationNumber(),
            System.currentTimeMillis() - 1000 * 60 * 30);

        emailCertificationRepository.saveAndFlush(insertedEmailCertificationEntity);

        final EmailCertificationRequest mockedRequest = new EmailCertificationRequest(insertedEmail);

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(AUTH_EMAIL_CERTIFICATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedRequest))
        );

        // then
        resultActions
            .andExpect(status().isNoContent())
            .andDo(print());
    }
}
