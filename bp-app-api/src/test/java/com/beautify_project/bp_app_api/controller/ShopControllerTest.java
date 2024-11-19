package com.beautify_project.bp_app_api.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class ShopControllerTest extends ShopFixture {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopService shopService;

    @BeforeAll
    public static void setUp() throws Exception {
        loadMockedImageFiles();
        createMockedRegisterSuccessResponseMessage();
        loadBase64EncodedThumbnail();
        createMockedFindListSuccessResponseMessage();
    }

    @Test
    @DisplayName("Shop 등록 validation 성공 테스트")
    void validTest_WhenResponseBodyIsValid() throws Exception {
        // given
        final String requestBody = OBJECT_MAPPER.writeValueAsString(
            createValidShopRegistrationRequest());
        when(shopService.registerShop(any(ImageFiles.class),
            any(ShopRegistrationRequest.class))).thenReturn(
            MOCKED_REGISTER_SUCCESS_RESPONSE_MESSAGE);

        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(MOCKED_IMAGE_FILES.get(0))
                .file(MOCKED_IMAGE_FILES.get(1))
                .file(new MockMultipartFile("shopRegistrationInfo", "", "application/json",
                    requestBody.getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // then
        assertRegisterSuccessResponse(resultActions);
    }

    @Test
    @DisplayName("이미지 파일 없이 Shop 등록 테스트")
    void nullTest_WhenFileNotExist() throws Exception {
        // given
        final String requestBody = OBJECT_MAPPER.writeValueAsString(
            createValidShopRegistrationRequest());
        when(shopService.registerShop(any(ImageFiles.class),
            any(ShopRegistrationRequest.class))).thenReturn(
            MOCKED_REGISTER_SUCCESS_RESPONSE_MESSAGE);

        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(new MockMultipartFile("shopRegistrationInfo", "", "application/json",
                    requestBody.getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // then
        assertRegisterSuccessResponse(resultActions);
    }

    @Test
    @DisplayName("샵 등록 요청 DTO 없는 경우 테스트")
    void nullTest_WhenShopRegistrationRequestNotExist() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(MOCKED_IMAGE_FILES.get(0))
                .file(MOCKED_IMAGE_FILES.get(1))
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
    @DisplayName("Shop 등록 validation 실패 테스트")
    @MethodSource("invalidShopRegistrationRequestProvider")
    void validTest_WhenFieldInRequestBodyIsInvalid(ShopRegistrationRequest invalidShopRegistrationRequest)
        throws Exception {

        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(MOCKED_IMAGE_FILES.get(0))
                .file(MOCKED_IMAGE_FILES.get(1))
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
    @DisplayName("Shop 리스트 조회 validation 실패 테스트")
    @MethodSource("invalidFindShopListParameterProvider")
    void validTest_WhenFindShopListRequestNotInvalid(final String type, final String page,
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

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("BR001"))
            .andExpect(jsonPath("$.errorCode").exists())
            .andDo(print());
    }

    @ParameterizedTest
    @DisplayName("Shop 조회 validation 성공 및 응답 메세지 테스트")
    @MethodSource("validFindShopListParameterProvider")
    void validTest_WhenFindShopRequestValid(final String type, final String page,
        final String count, final String order) throws Exception {

        // given
        when(shopService.findShopList(any(ShopFindListRequestParameters.class))).thenReturn(
            MOCKED_FIND_LIST_SUCCESS_RESPONSE_MESSAGE);

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
            .andExpect(jsonPath("$.returnValue[0].id").value(MOCKED_FIND_LIST_SUCCESS_RETURNED_SHOP_IDS[0]))
            .andExpect(jsonPath("$.returnValue[0].name").value("시술소1"))
            .andExpect(jsonPath("$.returnValue[0].operations").isArray())
            .andExpect(jsonPath("$.returnValue[0].supportFacilities").isArray())
            .andExpect(jsonPath("$.returnValue[0].rate").value("4.5"))
            .andExpect(jsonPath("$.returnValue[0].likes").value(132))
            .andExpect(jsonPath("$.returnValue[0].likePushed").value(false))
            .andExpect(jsonPath("$.returnValue[0].thumbnail").exists())
            .andDo(print());
    }

    private void assertRegisterSuccessResponse(final ResultActions resultActions) throws Exception {
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.shopId").exists())
            .andExpect(jsonPath("$.returnValue.shopId").value(
                MOCKED_REGISTER_SUCCESS_RETURNED_SHOP_ID))
            .andDo(print());
    }
}
