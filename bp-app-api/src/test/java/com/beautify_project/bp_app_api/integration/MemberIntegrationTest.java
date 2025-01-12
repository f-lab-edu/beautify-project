package com.beautify_project.bp_app_api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beautify_project.bp_app_api.request.member.UserRoleMemberRegistrationRequest;
import com.beautify_project.bp_mysql.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Tag("integration-test")
public class MemberIntegrationTest {

    private static final String MEMBER_SING_UP_URL = "/v1/members/user";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Member 등록 요청(회원가입) 성공시 회원 이메일을 응답으로 받는다.")
    void given_signUpUserRoleMember_when_succeed_then_getMemberEmail() throws Exception {
        // given
        final UserRoleMemberRegistrationRequest request = new UserRoleMemberRegistrationRequest(
            "dev.sssukho@gmail.com", "password", "임석호", "010-1234-5678");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(MEMBER_SING_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.email").exists());

        assertThat(memberRepository.count()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("invalidUserRoleMemberRegistrationRequest")
    @DisplayName("Member 등록 요청(회원가입)시 요청 데이터에 필수값이 존재하지 않으면 BAD_REQUEST 응답을 받는다.")
    void given_signUpUserRoleMemberWithoutNecessaryFields_when_failed_then_getBadRequestResponse(
        final String email, final String password, final String name, final String contact) throws Exception {

        // given
        final UserRoleMemberRegistrationRequest request = new UserRoleMemberRegistrationRequest(
            email, password, name, contact);

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(MEMBER_SING_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("BR001"))
            .andExpect(jsonPath("$.errorMessage").exists());
    }

    static Stream<Arguments> invalidUserRoleMemberRegistrationRequest() {
        return Stream.of(
            Arguments.of("", "password", "name", "contact"),
            Arguments.of("    ", "password", "name", "contact"),
            Arguments.of(null, "password", "name", "contact"),
            Arguments.of("email", "   ", "name", "contact"),
            Arguments.of("email", "", "name", "contact"),
            Arguments.of("email", null, "name", "contact"),
            Arguments.of("email", "password", "", "contact"),
            Arguments.of("email", "password", "   ", "contact"),
            Arguments.of("email", "password", null, "contact")
        );
    }
}
