//package com.beautify_project.bp_app_api.integration;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.beautify_project.bp_app_api.dto.shop.ImageFiles;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
//import com.beautify_project.bp_app_api.entity.Category;
//import com.beautify_project.bp_app_api.entity.Facility;
//import com.beautify_project.bp_app_api.entity.Operation;
//import com.beautify_project.bp_app_api.entity.Shop;
//import com.beautify_project.bp_app_api.repository.ShopRepository;
//import com.beautify_project.bp_app_api.service.ShopService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import java.time.LocalTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
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
//class ShopIntegrationTest {
//
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(
//        new JavaTimeModule());
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ShopRepository shopRepository;
//
//    @Test
//    @DisplayName("Shop 등록 요청 성공")
//    @Transactional
//    void given_shopRegistrationRequest_when_succeed()
//        throws Exception {
//        // given
//        Category mockedCategory1 = Category.of("카테고리1", "카테고리1 설명", System.currentTimeMillis());
//        Category mockedCategory2 = Category.of("카테고리2", "카테고리2 설명", System.currentTimeMillis());
//
//        final List<Operation> mockedOperationEntities = Arrays.asList(
//            Operation.createOperation("시술1", "시술1 설명", System.currentTimeMillis(),
//                List.of(mockedCategory1)),
//            Operation.createOperation("시술2", "시술2 설명", System.currentTimeMillis(),
//                Arrays.asList(mockedCategory1, mockedCategory2)));
//
//        final List<Facility> mockedFacilityEntities = Arrays.asList(
//            Facility.of("시설1", System.currentTimeMillis()),
//            Facility.of("시설2", System.currentTimeMillis())
//        );
//
//        final List<String> mockedOperationIds = Arrays.asList(
//            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
//        final List<String> mockedFacilityIds = Arrays.asList(
//            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
//        );
//
//        final ShopRegistrationRequest mockedRequestBody = new ShopRegistrationRequest(
//            "미용시술소1",
//            "010-1234-5678",
//            "www.naer.com",
//            "안녕하세요 미용시술소1입니다.",
//            mockedOperationIds,
//            mockedFacilityIds,
//            Arrays.asList("preSigned-url1", "preSigned-url2"),
//            new BusinessTime(
//                LocalTime.of(9, 0),
//                LocalTime.of(18, 0),
//                LocalTime.of(13, 0),
//                LocalTime.of(14, 0),
//                Arrays.asList("monday", "tuesday")),
//            new Address(
//                "111",
//                "서울시",
//                "마포구",
//                "상암동",
//                "481",
//                "월드컵북로",
//                "true",
//                "131",
//                "707",
//                "오벨리스크",
//                "134-070",
//                "주상복합",
//                "12345678",
//                "34",
//                "90"
//            )
//        );
//
//        // when
//        ResultActions resultActions = mockMvc.perform(
//            MockMvcRequestBuilders.post("/v1/shops").contentType(MediaType.APPLICATION_JSON)
//                .content(OBJECT_MAPPER.writeValueAsString(mockedRequestBody))
//        );
//
//        // then
//        // response 검증
//
//        // 서비스 호출 검증
//        // db 저장 검증
//
//
//        List<Shop> insertedShops = shopRepository.findAll();
//        insertedShops.get(0)
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
