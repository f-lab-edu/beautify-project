package com.bp.app.api.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bp.app.api.controller.ReservationController;
import com.bp.app.api.provider.JwtProvider;
import com.bp.app.api.request.reservation.ReservationRegistrationRequest;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.reservation.ReservationRegistrationResult;
import com.bp.app.api.service.ReservationService;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc
class ReservationControllerTest {

    private static final Long HOUR_TO_LONG = 1000L * 60 * 60;
    private static final Long DAY_TO_LONG = 24 * HOUR_TO_LONG;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(
        new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private MemberAdapterRepository memberAdapterRepository;


    @Test
    @DisplayName("사용자의 예약 등록 요청시 reservationId 를 포함한 ResponseMessage 객체를 응답받는다.")
    void getResponseMessageWrappingReservationIdIfReservationRegistrationRequestSucceed()
        throws Exception {

        // given
        final ReservationRegistrationRequest mockedRequest = new ReservationRegistrationRequest(
            System.currentTimeMillis() + DAY_TO_LONG,
            System.currentTimeMillis() + DAY_TO_LONG + HOUR_TO_LONG,
            1L,
            1L,
            "operator@bp.com"
        );

        when(reservationService.registerReservationAndProduceEvent(any(), any()))
            .thenReturn(ResponseMessage.createResponseMessage(new ReservationRegistrationResult(1L))
        );

        final String requestBody = OBJECT_MAPPER.writeValueAsString(mockedRequest);

        setUserRoleMockedAuthentication();

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/user/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf())
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.reservationId").exists())
            .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("invalidReservationRegistrationRequestProvider")
    @DisplayName("사용자의 예약 등록 요청시 validation 에 실패시 400 에러를 응답받는다.")
    void get400ErrorResponseIfReservationRegistrationRequestValidationFailed(
        final ReservationRegistrationRequest mockedRequest) throws Exception {

        // given
        final String requestBody = OBJECT_MAPPER.writeValueAsString(mockedRequest);
        setUserRoleMockedAuthentication();

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/user/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(csrf())
        );

        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

    private static void setUserRoleMockedAuthentication() {
        final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(UserRole.USER.name()));
        final Authentication authentication = new UsernamePasswordAuthenticationToken("dev.sssukho@gmail.com", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static Stream<Arguments> invalidReservationRegistrationRequestProvider() {
        ReservationRegistrationRequest pastStartDateRequest = new ReservationRegistrationRequest(
            System.currentTimeMillis() - DAY_TO_LONG,
            System.currentTimeMillis() + DAY_TO_LONG,
            1L, 1L, "operator@bp.com");

        ReservationRegistrationRequest pastEndDateRequest = new ReservationRegistrationRequest(
            System.currentTimeMillis() + HOUR_TO_LONG,
            System.currentTimeMillis() - DAY_TO_LONG,
            1L, 1L, "operator@bp.com");

        ReservationRegistrationRequest shopIdNullRequest = new ReservationRegistrationRequest(
            System.currentTimeMillis() + HOUR_TO_LONG,
            System.currentTimeMillis() + DAY_TO_LONG,
            null, 1L, "operator@bp.com");

        ReservationRegistrationRequest operationIdNullRequest = new ReservationRegistrationRequest(
            System.currentTimeMillis() + HOUR_TO_LONG,
            System.currentTimeMillis() + DAY_TO_LONG,
            1L, null, "operator@bp.com");

        ReservationRegistrationRequest operatorEmailEmptyRequest = new ReservationRegistrationRequest(
            System.currentTimeMillis() + HOUR_TO_LONG,
            System.currentTimeMillis() + DAY_TO_LONG,
            1L, 1L, "   ");

        ReservationRegistrationRequest operatorEmailNullRequest = new ReservationRegistrationRequest(
            System.currentTimeMillis() + HOUR_TO_LONG,
            System.currentTimeMillis() + DAY_TO_LONG,
            1L, 1L, null
        );

        return Stream.of(
            Arguments.of(pastStartDateRequest),
            Arguments.of(pastEndDateRequest),
            Arguments.of(shopIdNullRequest),
            Arguments.of(operationIdNullRequest),
            Arguments.of(operatorEmailEmptyRequest),
            Arguments.of(operatorEmailNullRequest)
        );
    }

}
