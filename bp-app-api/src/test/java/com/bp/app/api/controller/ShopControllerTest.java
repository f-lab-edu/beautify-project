package com.bp.app.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bp.app.api.producer.ShopLikeEventProducer;
import com.bp.app.api.provider.JwtProvider;
import com.bp.app.api.request.shop.ShopListFindRequestParameters;
import com.bp.app.api.request.shop.ShopRegistrationRequest;
import com.bp.app.api.request.shop.ShopRegistrationRequest.Address;
import com.bp.app.api.request.shop.ShopRegistrationRequest.BusinessTime;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.shop.ShopListFindResult;
import com.bp.app.api.response.shop.ShopRegistrationResult;
import com.bp.app.api.service.FacilityService;
import com.bp.app.api.service.OperationService;
import com.bp.app.api.service.ShopService;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ShopController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShopControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(
        new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopLikeEventProducer shopLikeEventProducer;

    @MockBean
    private ShopService shopService;

    @MockBean
    private ShopAdapterRepository shopAdapterRepository;

    @MockBean
    private OperationService operationService;

    @MockBean
    private FacilityService facilityService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private MemberAdapterRepository memberAdapterRepository;

    @Test
    @DisplayName("Shop 등록 요청 성공시 shopId 를 포함한 ResponseMessage 객체를 응답한다.")
    void given_shopRegistrationRequest_when_success_then_getResponseMessageWrappingShopId()
        throws Exception {
        // given
        final List<Long> mockedOperationIds = Arrays.asList(1L, 2L);
        final List<Long> mockedFacilityIds = Arrays.asList(1L, 2L);

        final ShopRegistrationRequest mockedRequest = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            mockedOperationIds,
            mockedFacilityIds,
            Arrays.asList("preSigned-url1", "preSigned-url2"),
            new BusinessTime(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                Arrays.asList("monday", "tuesday")),
            new Address(
                "111",
                "서울시",
                "마포구",
                "상암동",
                "481",
                "월드컵북로",
                "true",
                "131",
                "707",
                "오벨리스크",
                "134-070",
                "주상복합",
                "12345678",
                "34",
                "90"
            )
        );

        when(shopService.registerShop(mockedRequest)).thenReturn(
            ResponseMessage.createResponseMessage(new ShopRegistrationResult(1L)));

        final String requestBody = OBJECT_MAPPER.writeValueAsString(mockedRequest);

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/owner/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.shopId").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 등록 요청시 request body 가 없는 경우 errorCode BR002 로 응답을 받는다.")
    void given_shopRegistrationRequestWithoutRequestBody_when_failed_then_getErrorCodeBR002()
        throws Exception {

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/owner/shops")
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("BR002"))
            .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("invalidShopRegistrationRequestProvider")
    @DisplayName("Shop 등록 요청시 validation 에서 실패할 경우 errorCode BR001 로 응답을 받는다.")
    void given_shopRegistrationRequest_when_validationFailed_then_getErrorCdoeBR001(
        ShopRegistrationRequest invalidRequestBody) throws Exception {

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/owner/shops").contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(invalidRequestBody)));

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("BR001"))
            .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("validFindShopListParameterInControllerProvider")
    @DisplayName("Shop 리스트 조회 요청시 ShopListFindResult 를 wrapping 한 ResponseMessage 객체를 응답 받는다.")
    void given_requestFindShopList_when_succeed_then_getResponseMessageWrappingShopListFindResult(
        final String type, final String page, final String count, final String order)
        throws Exception {

        // given
        ShopListFindResult mockedShopListFindResult = new ShopListFindResult(
            1L,
            "미용시술소1",
            Arrays.asList("시술1", "시술2"),
            Arrays.asList("편의시설1", "편의시설2"),
            "4.5",
            132L,
            false,
            "www.file-link1.com"
        );

        when(shopService.findShopList(any(ShopListFindRequestParameters.class))).thenReturn(
            ResponseMessage.createResponseMessage(List.of(mockedShopListFindResult)));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/shops")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("type", type)
                .param("page", page)
                .param("count", count)
                .param("order", order)
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue[0].id").exists())
            .andExpect(jsonPath("$.returnValue[0].name").exists())
            .andExpect(jsonPath("$.returnValue[0].operations").isArray())
            .andExpect(jsonPath("$.returnValue[0].facilities").isArray())
            .andExpect(jsonPath("$.returnValue[0].rate").exists())
            .andExpect(jsonPath("$.returnValue[0].likePushed").exists())
            .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("invalidFindShopListParameterProvider")
    @DisplayName("Shop 리스트 조회 요청시 validation 에서 실패할 경우 errorCode BR001 로 응답을 받는다.")
    void given_requestFindShopList_when_validationFailed_then_getErrorCodeBR001(final String type,
        final String page, final String count, final String order) throws Exception {

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/v1/shops")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("type", type)
                .param("page", page)
                .param("count", count)
                .param("order", order)
        );

        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("BR001"))
            .andExpect(jsonPath("$.errorCode").exists())
            .andDo(print());
    }

    private static Stream<Arguments> invalidShopRegistrationRequestProvider() {
        return Stream.of(
            Arguments.of(createInvalidNameRequest()),
            Arguments.of(createInvalidIntroduction()),
            Arguments.of(createInvalidContactRequest())
        );
    }

    private static ShopRegistrationRequest createInvalidNameRequest() {
        final List<Long> mockedOperationIds = Arrays.asList(1L, 2L);
        final List<Long> mockedFacilityIds = Arrays.asList(1L, 2L);

        return new ShopRegistrationRequest(
            RandomStringUtils.randomAlphabetic(130),
            "010-1234-5678",
            "www.naver.com",
            "안녕하세요 미용시술소1입니다.",
            mockedOperationIds,
            mockedFacilityIds,
            Arrays.asList("preSigned-url1", "preSigned-url2"),
            new BusinessTime(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                Arrays.asList("monday", "tuesday")),
            new Address(
                "111",
                "서울시",
                "마포구",
                "상암동",
                "481",
                "월드컵북로",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "")
        );
    }

    private static ShopRegistrationRequest createInvalidContactRequest() {
        final List<Long> mockedOperationIds = Arrays.asList(1L, 2L);
        final List<Long> mockedFacilityIds = Arrays.asList(1L, 2L);

        return new ShopRegistrationRequest(
            "미용시술소1",
            RandomStringUtils.randomAlphabetic(14),
            "www.naver.com",
            "안녕하세요 미용시술소1입니다.",
            mockedOperationIds,
            mockedFacilityIds,
            Arrays.asList("preSigned-url1", "preSigned-url2"),
            new BusinessTime(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                Arrays.asList("monday", "tuesday")),
            new Address(
                "111",
                "서울시",
                "마포구",
                "상암동",
                "481",
                "월드컵북로",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "")
        );
    }

    private static ShopRegistrationRequest createInvalidIntroduction() {
        final List<Long> mockedOperationIds = Arrays.asList(1L, 2L);
        final List<Long> mockedFacilityIds = Arrays.asList(1L, 2L);

        return new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naver.com",
            RandomStringUtils.randomAlphabetic(2050),
            mockedOperationIds,
            mockedFacilityIds,
            Arrays.asList("preSigned-url1", "preSigned-url2"),
            new BusinessTime(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                Arrays.asList("monday", "tuesday")),
            new Address(
                "111",
                "서울시",
                "마포구",
                "상암동",
                "481",
                "월드컵북로",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "")
        );
    }

    private static Stream<Arguments> validFindShopListParameterInControllerProvider() {
        return Stream.of(
            Arguments.of("shopName", null, null, null),
            Arguments.of("shopName", null, null, "desc"),
            Arguments.of("location", null, "11", "desc"),
            Arguments.of("like", "1", null, "asc")
        );
    }

    private static Stream<Arguments> invalidFindShopListParameterProvider() {
        return Stream.of(
            Arguments.of(null, "0", "10", "asc"),
            Arguments.of("shopnname", "1", "10", "asc"),
            Arguments.of("location", "1", "10", "asccc"),
            Arguments.of("like", "alphabet", "alphabet", "desc"),
            Arguments.of("rate", "1", "10", "3")
        );
    }
}
