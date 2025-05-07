package com.bp.app.api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bp.app.api.request.auth.EmailCertificationRequest;
import com.bp.app.api.request.auth.EmailCertificationVerificationRequest;
import com.bp.app.api.request.auth.EmailDuplicatedRequest;
import com.bp.app.api.request.member.UserRoleMemberRegistrationRequest;
import com.bp.app.api.service.MemberService;
import com.bp.app.api.testcontainers.TestContainerFactory;
import com.bp.domain.mysql.entity.EmailCertification;
import com.bp.domain.mysql.repository.EmailCertificationAdapterRepository;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.bp.utils.UUIDGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("integration-test")
public class AuthIntegrationTest {

    private static final String AUTH_EMAIL_DUPLICATED_URL = "/v1/auth/email/duplicated";
    private static final String AUTH_EMAIL_CERTIFICATION_URL = "/v1/auth/email/certification";
    private static final String AUTH_EMAIL_CERTIFICATION_VERIFICATION_URL = "/v1/auth/email/certification/verification";

    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = TestContainerFactory.createMySQLContainer();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberAdapterRepository memberAdapterRepository;

    @Autowired
    private EmailCertificationAdapterRepository emailCertificationAdapterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        TestContainerFactory.overrideDatasourceProps(registry, MYSQL_CONTAINER);
    }

    @BeforeEach
    void beforeEach() {
        deleteAll();
    }

    private void deleteAll() {
        memberAdapterRepository.deleteAllInBatch();
        emailCertificationAdapterRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원가입시 이메일 중복 체크에서 중복되지 않는 아이디이면 isDuplicated=false 로 응답을 받는다.")
    void given_emailDuplicatedCheckRequest_when_succeed_then_get_responseMessageWrappingEmailDuplicatedCheckResultAsFalse() throws Exception{
        // given
        final UserRoleMemberRegistrationRequest mockedRequest = new UserRoleMemberRegistrationRequest(
            "dev.sssukho@gmail.com", "1234", "이름", "010-1234-5678");

        memberAdapterRepository.saveAndFlush(MemberService.createNewSelfAuthMember(mockedRequest));

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

        memberAdapterRepository.saveAndFlush(MemberService.createNewSelfAuthMember(mockedRequest));

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
        final EmailCertification mockedEmailCertification = EmailCertification.newEmailCertification(
            insertedEmail, UUIDGenerator.generateEmailCertificationNumber());

        final Clock clockBeforeThirtyMinutes = Clock.offset(Clock.systemDefaultZone(),
            Duration.ofMinutes(-30));
        mockedEmailCertification.prePersist(clockBeforeThirtyMinutes);

        emailCertificationAdapterRepository.saveAndFlush(mockedEmailCertification);

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

    @Test
    @DisplayName("이메일 인증 확인 요청시 DB 에 있는 요청 번호와 요청으로 넘어온 인증 번호와 동일한 경우 NO_CONTENT 로 응답을 받는다.")
    void given_emailCertificationRequest_when_certificationNumberInDB_match_with_certificationNumberFromRequest_then_getNoContent() throws Exception{
        // given
        final String insertedEmail = "dev.sssukho@gmail.com";
        final String insertedCertificationNumber = UUIDGenerator.generateEmailCertificationNumber();
        final EmailCertification insertedEmailCertificationEntity = EmailCertification.newEmailCertification(
            insertedEmail, insertedCertificationNumber);

        emailCertificationAdapterRepository.saveAndFlush(insertedEmailCertificationEntity);

        final EmailCertificationVerificationRequest mockedRequest = new EmailCertificationVerificationRequest(
            insertedEmail, insertedCertificationNumber);

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(AUTH_EMAIL_CERTIFICATION_VERIFICATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedRequest))
        );

        // then
        resultActions
            .andExpect(status().isNoContent())
            .andDo(print());

        assertThat(emailCertificationAdapterRepository.findByEmail(
            insertedEmailCertificationEntity.getEmail())).isNull();
    }

    @Test
    @DisplayName("이메일 인증 확인 요청시 인증 번호 이메일 전송 이력 자체가 없는 경우 EC002 으로 응답을 받는다.")
    void given_emailCertificationRequest_when_certificationNumberInDB_not_match_with_certificationNumberFromRequest_then_getEC002ErrorMessage() throws Exception{
        // given
        final String notExistedEmail = "dev.sssukho@gmail.com";
        final String notExistedCertificationNumber = UUIDGenerator.generateEmailCertificationNumber();

        final EmailCertificationVerificationRequest mockedRequest = new EmailCertificationVerificationRequest(
            notExistedEmail, notExistedCertificationNumber);

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(AUTH_EMAIL_CERTIFICATION_VERIFICATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedRequest))
        );

        // then
        resultActions
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorCode").value("EC002"))
            .andDo(print());
    }

    @Test
    @DisplayName("이메일 인증 확인 요청시 DB 에 있는 요청 번호와 요청으로 넘어온 인증 번호와 동일하지 않은 경우 EC003 으로 응답을 받는다.")
    void given_emailCertificationRequest_when_emailCertificationRecord_does_not_exist_then_getEC003ErrorMessage() throws Exception{
        // given
        final String insertedEmail = "dev.sssukho@gmail.com";
        final String insertedCertificationNumber = UUIDGenerator.generateEmailCertificationNumber();
        final EmailCertification insertedEmailCertificationEntity = EmailCertification.newEmailCertification(
            insertedEmail, insertedCertificationNumber);

        emailCertificationAdapterRepository.saveAndFlush(insertedEmailCertificationEntity);

        final EmailCertificationVerificationRequest mockedRequest = new EmailCertificationVerificationRequest(
            insertedEmail, "WRONGNUMBER");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(AUTH_EMAIL_CERTIFICATION_VERIFICATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockedRequest))
        );

        // then
        resultActions
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorCode").value("EC003"))
            .andDo(print());
    }


}
