package com.beautify_project.bp_app_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.dto.shop.ShopListFindRequestParameters;
import com.beautify_project.bp_app_api.dto.shop.ShopListFindResult;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationResult;
import com.beautify_project.bp_app_api.entity.Category;
import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import com.beautify_project.bp_app_api.service.FacilityService;
import com.beautify_project.bp_app_api.service.OperationService;
import com.beautify_project.bp_app_api.service.ShopService;
import com.beautify_project.bp_app_api.utils.UUIDGenerator;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ShopController.class)
class ShopControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(
        new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopService shopService;

    @MockBean
    private ShopRepository shopRepository;

    @MockBean
    private OperationService operationService;

    @MockBean
    private FacilityService facilityService;

    @Test
    @DisplayName("Shop 등록 요청 성공시 shopId 를 포함한 ResponseMessage 객체를 응답한다.")
    void given_shopRegistrationRequest_when_success_then_getResponseMessageWrappingShopId()
        throws Exception {
        // given
        Category mockedCategory1 = Category.of("카테고리1", "카테고리1 설명", System.currentTimeMillis());
        Category mockedCategory2 = Category.of("카테고리2", "카테고리2 설명", System.currentTimeMillis());

        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("시술1", "시술1 설명", System.currentTimeMillis(),
                List.of(mockedCategory1)),
            Operation.of("시술2", "시술2 설명", System.currentTimeMillis(),
                Arrays.asList(mockedCategory1, mockedCategory2)));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.of("시설1", System.currentTimeMillis()),
            Facility.of("시설2", System.currentTimeMillis())
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

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

        Shop mockedShop = Shop.createShop(mockedRequest, mockedOperationEntities,
            mockedFacilityEntities, System.currentTimeMillis());
        when(shopService.registerShop(mockedRequest)).thenReturn(
            ResponseMessage.createResponseMessage(new ShopRegistrationResult(mockedShop.getId())));

        final String requestBody = OBJECT_MAPPER.writeValueAsString(mockedRequest);

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/shops").contentType(MediaType.APPLICATION_JSON)
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
            MockMvcRequestBuilders.post("/v1/shops").contentType(MediaType.APPLICATION_JSON)
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
            MockMvcRequestBuilders.post("/v1/shops").contentType(MediaType.APPLICATION_JSON)
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
            UUIDGenerator.generate(),
            "미용시술소1",
            Arrays.asList(UUIDGenerator.generate(), UUIDGenerator.generate()),
            Arrays.asList(UUIDGenerator.generate(), UUIDGenerator.generate()),
            "4.5",
            132,
            false
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
        Category mockedCategory1 = Category.of("카테고리1", "카테고리1 설명", System.currentTimeMillis());
        Category mockedCategory2 = Category.of("카테고리2", "카테고리2 설명", System.currentTimeMillis());

        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("시술1", "시술1 설명", System.currentTimeMillis(),
                List.of(mockedCategory1)),
            Operation.of("시술2", "시술2 설명", System.currentTimeMillis(),
                Arrays.asList(mockedCategory1, mockedCategory2)));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.of("시설1", System.currentTimeMillis()),
            Facility.of("시설2", System.currentTimeMillis())
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

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
        Category mockedCategory1 = Category.of("카테고리1", "카테고리1 설명", System.currentTimeMillis());
        Category mockedCategory2 = Category.of("카테고리2", "카테고리2 설명", System.currentTimeMillis());

        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("시술1", "시술1 설명", System.currentTimeMillis(),
                List.of(mockedCategory1)),
            Operation.of("시술2", "시술2 설명", System.currentTimeMillis(),
                Arrays.asList(mockedCategory1, mockedCategory2)));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.of("시설1", System.currentTimeMillis()),
            Facility.of("시설2", System.currentTimeMillis())
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

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
        Category mockedCategory1 = Category.of("카테고리1", "카테고리1 설명", System.currentTimeMillis());
        Category mockedCategory2 = Category.of("카테고리2", "카테고리2 설명", System.currentTimeMillis());

        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("시술1", "시술1 설명", System.currentTimeMillis(),
                List.of(mockedCategory1)),
            Operation.of("시술2", "시술2 설명", System.currentTimeMillis(),
                Arrays.asList(mockedCategory1, mockedCategory2)));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.of("시설1", System.currentTimeMillis()),
            Facility.of("시설2", System.currentTimeMillis())
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

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
