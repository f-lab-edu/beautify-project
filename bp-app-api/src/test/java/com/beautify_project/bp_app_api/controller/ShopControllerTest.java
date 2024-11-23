package com.beautify_project.bp_app_api.controller;

import static com.beautify_project.CommonTestFixture.OBJECT_MAPPER;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beautify_project.ShopTestFixture;
import com.beautify_project.bp_app_api.service.ShopService;
import com.beautify_project.bp_dto.shop.ImageFiles;
import com.beautify_project.bp_dto.shop.ShopFindListRequestParameters;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ShopController.class)
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopService shopService;

    @BeforeAll
    public static void setUp() throws Exception {
        ShopTestFixture.loadMockedImageFiles();
        ShopTestFixture.createMockedRegisterSuccessResponseMessage();
        ShopTestFixture.loadBase64EncodedThumbnail();
        ShopTestFixture.createMockedFindListSuccessResponseMessage();
    }

    @Test
    @DisplayName("Shop 등록 요청 성공시 ResponseMessage 객체 형태로 응답이 나간다.")
    void given_shopRegistrationRequest_when_succeed_then_getResponseMessage()
        throws Exception {
        // given
        final String shopRegistrationInfo = OBJECT_MAPPER.writeValueAsString(
            ShopTestFixture.createValidShopRegistrationRequest());

        when(shopService.registerShop(any(ImageFiles.class),
            any(ShopRegistrationRequest.class))).thenReturn(
            ShopTestFixture.MOCKED_REGISTER_SUCCESS_RESPONSE_MESSAGE);

        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(0))
                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(1))
                .file(new MockMultipartFile("shopRegistrationInfo", "", "application/json",
                    shopRegistrationInfo.getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 등록 요청 실패시 ErrorResponseMessage 객체 형태로 응답이 나간다.")
    void given_shopRegistrationRequest_when_failed_then_getErrorResponseMessage() throws Exception {
        // given
        // shpRegistrationInfo 가 없는 경우

        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(0))
                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("이미지 파일 없이 Shop 등록 요청시 성공 후 shopId 를 응답으로 받는다.")
    void given_shopRegistrationRequestWithoutImageFiles_when_succeed_then_getResponseMessage() throws Exception {
        // given
        final String requestBody = OBJECT_MAPPER.writeValueAsString(
            ShopTestFixture.createValidShopRegistrationRequest());
        when(shopService.registerShop(any(ImageFiles.class),
            any(ShopRegistrationRequest.class))).thenReturn(
            ShopTestFixture.MOCKED_REGISTER_SUCCESS_RESPONSE_MESSAGE);

        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(new MockMultipartFile("shopRegistrationInfo", "", "application/json",
                    requestBody.getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.shopId").exists())
            .andExpect(jsonPath("$.returnValue.shopId").value(
                ShopTestFixture.MOCKED_REGISTER_SUCCESS_RETURNED_SHOP_ID))
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 등록 요청 DTO(ShopRegistrationInfo) 값이 없는 경우 errorCode BR002 로 응답을 받는다.")
    void given_shopRegistrationRequestWithoutShopRegistrationInfo_when_failed_then_getErrorCodeBR002() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(0))
                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("BR002"))
            .andExpect(jsonPath("$.errorMessage").value("본문 내 'shopRegistrationInfo' 은 필수값입니다."))
            .andDo(print());
    }

    @ParameterizedTest
    @DisplayName("Shop 등록 요청시 validation 에서 실패하고 BR001 로 응답을 받는다.")
    @MethodSource("com.beautify_project.ShopTestFixture#invalidShopRegistrationRequestProvider")
    void given_requestShopRegistration_when_validationFailed_then_getErrorCodeBR001(ShopRegistrationRequest invalidShopRegistrationRequest)
        throws Exception {

        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(0))
                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(1))
                .file(new MockMultipartFile("shopRegistrationInfo", "", "application/json",
                    OBJECT_MAPPER.writeValueAsString(invalidShopRegistrationRequest).getBytes(
                        StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("BR001"))
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

    @ParameterizedTest
    @DisplayName("Shop 리스트 조회 요청시 validation 성공 후 mocking 한 응답 메시지(조회 결과)를 받는다.")
    @MethodSource("com.beautify_project.ShopTestFixture#validFindShopListParameterProvider")
    void given_requestFindShopList_when_validationSucceed_then_getMockedFindListSuccessResponseMessage(final String type, final String page,
        final String count, final String order) throws Exception {

        // given
        when(shopService.findShopList(any(ShopFindListRequestParameters.class))).thenReturn(
            ShopTestFixture.MOCKED_FIND_LIST_SUCCESS_RESPONSE_MESSAGE);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/shops")
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
            .andExpect(jsonPath("$.returnValue[0].id").value(ShopTestFixture.MOCKED_FIND_LIST_SUCCESS_RETURNED_SHOP_IDS[0]))
            .andExpect(jsonPath("$.returnValue[0].name").value("시술소1"))
            .andExpect(jsonPath("$.returnValue[0].operations").isArray())
            .andExpect(jsonPath("$.returnValue[0].supportFacilities").isArray())
            .andExpect(jsonPath("$.returnValue[0].rate").value("4.5"))
            .andExpect(jsonPath("$.returnValue[0].likes").value(132))
            .andExpect(jsonPath("$.returnValue[0].likePushed").value(false))
            .andExpect(jsonPath("$.returnValue[0].thumbnail").exists())
            .andDo(print());
    }

    @ParameterizedTest
    @DisplayName("Shop 리스트 조회 요청시 validation 실패 후 BR001 에러 코드를 포함한 에러 메시지를 받는다.")
    @MethodSource("com.beautify_project.ShopTestFixture#invalidFindShopListParameterProvider")
    void given_requestFindShopList_when_validationFailed_then_getErrorCodeBR001(final String type, final String page,
        final String count, final String order) throws Exception {

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


}
