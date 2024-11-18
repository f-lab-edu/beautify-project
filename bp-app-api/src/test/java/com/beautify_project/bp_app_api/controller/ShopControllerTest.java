package com.beautify_project.bp_app_api.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.beautify_project.bp_app_api.records.ImageFiles;
import com.beautify_project.bp_app_api.service.ShopService;
import com.beautify_project.bp_dto.response.ResponseMessage;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest.BusinessTime;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest.IdName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = ShopController.class)
class ShopControllerTest {

    static final String MOCKED_SUCCESS_RETURNED_SHOP_ID = "732e934";
    static final String TEST_IMAGE_FILE_DIRECTORY_PATH = "src/test/resources/files";

    static List<MockMultipartFile> MOCKED_IMAGE_FILES;
    static ResponseMessage MOCKED_SUCCESS_RESPONSE_MESSAGE;
    static ObjectMapper OBJECT_MAPPER;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShopService shopService;

    @BeforeAll
    public static void setUp() throws Exception {
        Map<String, String> responseSample = new HashMap<>();
        responseSample.put("shopId", MOCKED_SUCCESS_RETURNED_SHOP_ID);
        MOCKED_SUCCESS_RESPONSE_MESSAGE = ResponseMessage.createResponseMessage(HttpStatus.OK, responseSample);

        MOCKED_IMAGE_FILES = Arrays.asList(
            new MockMultipartFile("images", "image1.png", "image/png",
                Files.readAllBytes(Path.of(TEST_IMAGE_FILE_DIRECTORY_PATH + "/1.png"))),
            new MockMultipartFile("images", "image2.png", "image/png",
                Files.readAllBytes(Path.of(TEST_IMAGE_FILE_DIRECTORY_PATH + "/2.png")
                )));

        OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    }


    @Test
    @DisplayName("Shop 등록 validation 성공 테스트")
    void validTest_WhenResponseBodyIsValid() throws Exception {
        // given
        final String requestBody = OBJECT_MAPPER.writeValueAsString(generateValidShopRegistrationRequest());
        when(shopService.registerShop(any(ImageFiles.class),
            any(ShopRegistrationRequest.class))).thenReturn(MOCKED_SUCCESS_RESPONSE_MESSAGE);

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
        assertSuccessResponse(resultActions);
    }

    @Test
    @DisplayName("이미지 파일 없이 Shop 등록 테스트")
    void nullTest_WhenFileNotExist() throws Exception {
        // given
        final String requestBody = OBJECT_MAPPER.writeValueAsString(generateValidShopRegistrationRequest());
        when(shopService.registerShop(any(ImageFiles.class),
            any(ShopRegistrationRequest.class))).thenReturn(MOCKED_SUCCESS_RESPONSE_MESSAGE);

        // when
        ResultActions resultActions = mockMvc.perform(
            multipart("/v1/shops")
                .file(new MockMultipartFile("shopRegistrationInfo", "", "application/json",
                    requestBody.getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        // then
        assertSuccessResponse(resultActions);
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

    private ShopRegistrationRequest generateValidShopRegistrationRequest() {
        return new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "안녕하세요 미용시술소1입니다.",
            Arrays.asList(
                new IdName("4541403a", "시술1"),
                new IdName("0ced03cc", "시술2")),
            List.of(new IdName("f9a1aa26", "카테고리1")),
            List.of(new IdName("239a8cb9", "지원시설1")),
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

    private static Stream<Arguments> invalidShopRegistrationRequestProvider() {
        return Stream.of(
            Arguments.of(generateInvalidNameRequest()),
            Arguments.of(generateInvalidIntroduction()),
            Arguments.of(generateInvalidContactRequest())
        );
    }

    private static ShopRegistrationRequest generateInvalidNameRequest() {
        return new ShopRegistrationRequest(
            RandomStringUtils.randomAlphabetic(130),
            "010-1234-5678",
            "안녕하세요 미용시술소1입니다.",
            Arrays.asList(
                new IdName("4541403a", "시술1"),
                new IdName("0ced03cc", "시술2")),
            List.of(new IdName("f9a1aa26", "카테고리1")),
            List.of(new IdName("239a8cb9", "지원시설1")),
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

    private static ShopRegistrationRequest generateInvalidContactRequest() {
        return new ShopRegistrationRequest(
            "미용시술소1",
            RandomStringUtils.randomAlphabetic(14),
            "안녕하세요 미용시술소1입니다.",
            Arrays.asList(
                new IdName("4541403a", "시술1"),
                new IdName("0ced03cc", "시술2")),
            List.of(new IdName("f9a1aa26", "카테고리1")),
            List.of(new IdName("239a8cb9", "지원시설1")),
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

    private static ShopRegistrationRequest generateInvalidIntroduction() {
        return new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            RandomStringUtils.randomAlphabetic(2050),
            Arrays.asList(
                new IdName("4541403a", "시술1"),
                new IdName("0ced03cc", "시술2")),
            List.of(new IdName("f9a1aa26", "카테고리1")),
            List.of(new IdName("239a8cb9", "지원시설1")),
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



    private void assertSuccessResponse(final ResultActions resultActions) throws Exception {
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.shopId").exists())
            .andExpect(jsonPath("$.returnValue.shopId").value(MOCKED_SUCCESS_RETURNED_SHOP_ID))
            .andDo(print());
    }
}
