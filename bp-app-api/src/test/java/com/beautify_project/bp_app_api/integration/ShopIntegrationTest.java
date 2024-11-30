//package com.beautify_project.bp_app_api.integration;
//
//import static com.beautify_project.bp_app_api.fixtures.CommonTestFixture.OBJECT_MAPPER;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
//import com.beautify_project.bp_app_api.service.ShopService;
//import java.io.File;
//import java.nio.charset.StandardCharsets;
//import java.util.stream.Collectors;
//import org.apache.commons.io.FileUtils;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//@SpringBootTest
//@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
//@AutoConfigureMockMvc
//@Transactional(readOnly = true)
//@Tag("integration-test")
//public class ShopIntegrationTest {
//
//    @Value("${storage.path:null}")
//    private String storagePath;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ShopService shopService;
//
//    @BeforeAll
//    public static void setUp() throws Exception {
//        ShopTestFixture.initMockedImageFiles();
//        ShopTestFixture.initMockedRegisterSuccessResponseMessage();
//        ShopTestFixture.initBase64EncodedThumbnail();
//        ShopTestFixture.initMockedFindListSuccessResponseMessage();
//    }
//
//    @BeforeEach
//    void initTestEnvironment() throws Exception {
//        FileUtils.deleteDirectory(new File(storagePath));
//    }
//
//    @Test
//    @DisplayName("Shop 등록 요청 성공시 ShopRegistrationResponse 를 wrapping 한 ResponseMessage 객체 응답을 받는다.")
//    @Transactional
//    void given_shopRegistrationRequest_when_succeed_then_getResponseMessage()
//        throws Exception {
//        // given
//        final String shopRegistrationInfo = OBJECT_MAPPER.writeValueAsString(
//            ShopTestFixture.createValidShopRegistrationRequest());
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            multipart("/v1/shops")
//                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(0))
//                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(1))
//                .file(new MockMultipartFile("shopRegistrationInfo", "", "application/json",
//                    shopRegistrationInfo.getBytes(StandardCharsets.UTF_8)))
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//        );
//
//        // then
//        resultActions
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.returnValue").exists())
//            .andExpect(jsonPath("$.returnValue.shopId").exists())
//            .andDo(print());
//    }
//
//    @Test
//    @DisplayName("Shop 등록 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
//    @Transactional
//    void given_shopRegistrationRequest_when_failed_then_getErrorResponseMessage() throws Exception {
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            multipart("/v1/shops")
//                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(0))
//                .file(ShopTestFixture.MOCKED_IMAGE_FILES.get(1))
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//        );
//
//        // then
//        resultActions
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("$.errorCode").exists())
//            .andExpect(jsonPath("$.errorMessage").exists())
//            .andDo(print());
//    }
//
//    @Test
//    @DisplayName("Shop 리스트 조회 요청 성공시 value 가 JSON Array 인 ResponseMessage 객체 응답을 받는다.")
//    @Transactional
//    void given_shopFindListRequest_when_succeed_then_getShopFindListResponseWrappedInResponseMessage()
//        throws Exception {
//        // given
//        registerShop();
//
//        final String type = "shopname";
//        final String page = "0";
//        final String count = "10";
//        final String order = "asc";
//
//        // when
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
//            .get("/v1/shops")
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//            .param("type", type)
//            .param("page", page)
//            .param("count", count)
//            .param("order", order)
//        );
//
//        // then
//        resultActions
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.returnValue").exists())
//            .andExpect(jsonPath("$.returnValue[0].id").exists())
//            .andExpect(jsonPath("$.returnValue[0].name").exists())
//            .andExpect(jsonPath("$.returnValue[0].operations").isArray())
//            .andExpect(jsonPath("$.returnValue[0].supportFacilities").isArray())
//            .andExpect(jsonPath("$.returnValue[0].rate").exists())
//            .andExpect(jsonPath("$.returnValue[0].likes").exists())
//            .andExpect(jsonPath("$.returnValue[0].likePushed").exists())
//            .andDo(print());
//    }
//
//    @Test
//    @DisplayName("Shop 리스트 조회 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
//    @Transactional
//    void given_shopFindListRequest_when_failed_then_getErrorResponseMessage() throws Exception {
//
//        // given
//        final String type = null;
//        final String page = "0";
//        final String count = "10";
//        final String order = "asc";
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            MockMvcRequestBuilders
//                .get("/v1/shops")
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .param("type", type)
//                .param("page", page)
//                .param("count", count)
//                .param("order", order)
//        );
//
//        resultActions
//            .andExpect(status().isBadRequest())
//            .andExpect(jsonPath("$.errorCode").value("BR001"))
//            .andExpect(jsonPath("$.errorCode").exists())
//            .andDo(print());
//    }
//
//    private void registerShop() throws Exception{
//        final ShopRegistrationRequest registrationRequest = ShopTestFixture.createValidShopRegistrationRequest();
//        final ImageFiles imageFileRequest = ShopTestFixture.MOCKED_IMAGE_FILES.stream()
//            .map(mockedImageFile -> (MultipartFile) mockedImageFile)
//            .collect(Collectors.collectingAndThen(Collectors.toList(), ImageFiles::new));
//
//        shopService.registerShop(imageFileRequest, registrationRequest);
//    }
//}
