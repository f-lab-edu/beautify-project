package com.bp.app.api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bp.app.api.AuthorizationHelper;
import com.bp.app.api.request.shop.ShopRegistrationRequest;
import com.bp.app.api.request.shop.ShopRegistrationRequest.Address;
import com.bp.app.api.request.shop.ShopRegistrationRequest.BusinessTime;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.service.ShopService;
import com.bp.app.api.testcontainers.TestContainerFactory;
import com.bp.domain.mysql.entity.Category;
import com.bp.domain.mysql.entity.Facility;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.OperationCategory;
import com.bp.domain.mysql.repository.CategoryAdapterRepository;
import com.bp.domain.mysql.repository.FacilityAdapterRepository;
import com.bp.domain.mysql.repository.OperationAdapterRepository;
import com.bp.domain.mysql.repository.OperationCategoryAdapterRepository;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
import com.bp.domain.mysql.repository.ShopCategoryAdapterRepository;
import com.bp.domain.mysql.repository.ShopFacilityAdapterRepository;
import com.bp.domain.mysql.repository.ShopOperationAdapterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration-test")
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ShopIntegrationTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(
        new JavaTimeModule());
    public static final String SHOP_REGISTRATION_URI = "/v1/owner/shops";

    @Container
    static final MySQLContainer<?> MYSQL_CONTAINER = TestContainerFactory.createMySQLContainer();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopAdapterRepository shopAdapterRepository;

    @Autowired
    private OperationAdapterRepository operationAdapterRepository;

    @Autowired
    private CategoryAdapterRepository categoryAdapterRepository;

    @Autowired
    private FacilityAdapterRepository facilityAdapterRepository;

    @Autowired
    private ShopOperationAdapterRepository shopOperationAdapterRepository;

    @Autowired
    private ShopCategoryAdapterRepository shopCategoryAdapterRepository;

    @Autowired
    private ShopFacilityAdapterRepository shopFacilityAdapterRepository;

    @Autowired
    private OperationCategoryAdapterRepository operationCategoryAdapterRepository;

    @Autowired
    private ShopService shopService;

    @Autowired
    private AuthorizationHelper authHelper;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        TestContainerFactory.overrideDatasourceProps(registry, MYSQL_CONTAINER);
    }

    @BeforeEach
    void beforeEach() {
        shopAdapterRepository.deleteAllInBatch();
        operationAdapterRepository.deleteAllInBatch();
        categoryAdapterRepository.deleteAllInBatch();
        facilityAdapterRepository.deleteAllInBatch();
        shopOperationAdapterRepository.deleteAllInBatch();
        shopCategoryAdapterRepository.deleteAllInBatch();
        shopFacilityAdapterRepository.deleteAllInBatch();
        operationCategoryAdapterRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Shop 등록 요청 성공시 200 OK 응답과 shop 관련 정보들이 DB에 정상적으로 저장된다.")
    void dataInsertedRelevanceWithShopAndGet200OKIfShopRegistrationRequestSucceed()
        throws Exception {
        // given
        final String ownerAccessToken = authHelper.provideOwnerRoleAccessToken("owner@bp.com");

        final List<Category> mockedCategories = categoryAdapterRepository.saveAll(
            Arrays.asList(
                Category.newCategory("카테고리1", "카테고리1 설명"),
                Category.newCategory("카테고리2", "카테고리2 설명")
            ));

        final List<Operation> mockedOperations = operationAdapterRepository.saveAll(Arrays.asList(
            Operation.newOperation("시술1", "시술1설명"),
            Operation.newOperation("시술2", "시술2설명"),
            Operation.newOperation("시술3","시술3설명")
        ));

        final List<Facility> mockedFacilities = facilityAdapterRepository.saveAll(Arrays.asList(
            Facility.newFacility("시설1"),
            Facility.newFacility("시설2")
        ));

        operationCategoryAdapterRepository.saveAll(Arrays.asList(
            OperationCategory.newOperationCategory(mockedOperations.get(0).getId(),
                mockedCategories.get(0).getId()),
            OperationCategory.newOperationCategory(mockedOperations.get(1).getId(),
                mockedCategories.get(0).getId()),
            OperationCategory.newOperationCategory(mockedOperations.get(2).getId(),
                mockedCategories.get(1).getId())
        ));

        final ShopRegistrationRequest mockedRequestBody = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            mockedOperations.stream().map(Operation::getId).toList(),
            mockedFacilities.stream().map(Facility::getId).toList(),
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

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(SHOP_REGISTRATION_URI)
                .header("Authorization","Bearer " + ownerAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(mockedRequestBody))
        );

        // then
        // response 검증
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.shopId").exists())
            .andDo(print());

        final ResponseMessage responseMessage = OBJECT_MAPPER.readValue(
            resultActions.andReturn().getResponse().getContentAsString(), ResponseMessage.class);
        final HashMap<String, Integer> responseContent = (HashMap<String, Integer>) responseMessage.getReturnValue();
        final Long registeredShopId = Long.valueOf(responseContent.get("shopId"));

        // DB 검증
        int registeredShopCategorySize = shopCategoryAdapterRepository.findByIdShopId(registeredShopId).size();
        int registeredShopFacilitySize = shopFacilityAdapterRepository.findByIdShopIdIn(
            List.of(registeredShopId)).size();
        int registeredShopOperationSize = shopOperationAdapterRepository.findByIdShopIdIn(
            List.of(registeredShopId)).size();

        assertThat(mockedCategories.size()).isEqualTo(registeredShopCategorySize);
        assertThat(mockedFacilities.size()).isEqualTo(registeredShopFacilitySize);
        assertThat(mockedOperations.size()).isEqualTo(registeredShopOperationSize);
    }

    @Test
    @DisplayName("일반 유저 권한으로 Shop 등록을 요청할 경우 403 에러 응답을 받는다.")
    void tt() throws Exception{
        // given
        final String userAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");

        final List<Category> mockedCategories = categoryAdapterRepository.saveAll(
            Arrays.asList(
                Category.newCategory("카테고리1", "카테고리1 설명"),
                Category.newCategory("카테고리2", "카테고리2 설명")
            ));

        final List<Operation> mockedOperations = operationAdapterRepository.saveAll(Arrays.asList(
            Operation.newOperation("시술1", "시술1설명"),
            Operation.newOperation("시술2", "시술2설명"),
            Operation.newOperation("시술3","시술3설명")
        ));

        final List<Facility> mockedFacilities = facilityAdapterRepository.saveAll(Arrays.asList(
            Facility.newFacility("시설1"),
            Facility.newFacility("시설2")
        ));

        operationCategoryAdapterRepository.saveAll(Arrays.asList(
            OperationCategory.newOperationCategory(mockedOperations.get(0).getId(),
                mockedCategories.get(0).getId()),
            OperationCategory.newOperationCategory(mockedOperations.get(1).getId(),
                mockedCategories.get(0).getId()),
            OperationCategory.newOperationCategory(mockedOperations.get(2).getId(),
                mockedCategories.get(1).getId())
        ));

        final ShopRegistrationRequest mockedRequestBody = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            mockedOperations.stream().map(Operation::getId).toList(),
            mockedFacilities.stream().map(Facility::getId).toList(),
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

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(SHOP_REGISTRATION_URI)
                .header("Authorization","Bearer " + userAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(mockedRequestBody))
        );

        // then
        // response 검증
        resultActions
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

    @ParameterizedTest
    @MethodSource("invalidShopRegistrationRequestProvider")
    @DisplayName("Shop 등록 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
    void getErrorResponseMessageIfShopRegistrationRequestFailed(
        final ShopRegistrationRequest request) throws Exception {

        final String ownerRoleAccessToken = authHelper.provideOwnerRoleAccessToken("owner@bp.com");

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(SHOP_REGISTRATION_URI)
                .header("Authorization", "Bearer " + ownerRoleAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(request))
        );

        // then
        resultActions
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 리스트 조회 요청 성공시 value 가 JSON Array 인 ResponseMessage 객체 응답을 받는다.")
    void getJsonArrayResponseMessageIfFindShopListSucceed()
        throws Exception {
        // given
        final String userRoleAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");

        final List<Category> mockedCategories = categoryAdapterRepository.saveAll(
            Arrays.asList(
                Category.newCategory("카테고리1", "카테고리1 설명"),
                Category.newCategory("카테고리2", "카테고리2 설명")
            ));

        final List<Operation> mockedOperations = operationAdapterRepository.saveAll(Arrays.asList(
            Operation.newOperation("시술1", "시술1설명"),
            Operation.newOperation("시술2", "시술2설명"),
            Operation.newOperation("시술3","시술3설명")
        ));

        final List<Facility> mockedFacilities = facilityAdapterRepository.saveAll(Arrays.asList(
            Facility.newFacility("시설1"),
            Facility.newFacility("시설2")
        ));

        operationCategoryAdapterRepository.saveAll(Arrays.asList(
            OperationCategory.newOperationCategory(mockedOperations.get(0).getId(),
                mockedCategories.get(0).getId()),
            OperationCategory.newOperationCategory(mockedOperations.get(1).getId(),
                mockedCategories.get(0).getId()),
            OperationCategory.newOperationCategory(mockedOperations.get(2).getId(),
                mockedCategories.get(1).getId())
        ));

        final ShopRegistrationRequest mockedRequestBody = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naer.com",
            "안녕하세요 미용시술소1입니다.",
            mockedOperations.stream().map(Operation::getId).toList(),
            mockedFacilities.stream().map(Facility::getId).toList(),
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

        shopService.registerShop(mockedRequestBody);

        final String type = "shopname";
        final String page = "0";
        final String count = "10";
        final String order = "asc";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
            .get("/v1/shops")
            .header("Authorization", "Bearer " + userRoleAccessToken)
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
            .andExpect(jsonPath("$.returnValue[0].likes").exists())
            .andExpect(jsonPath("$.returnValue[0].thumbnailLink").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("Shop 리스트 조회 요청 실패시 ErrorResponseMessage 객체 응답을 받는다.")
    void getErrorResponseMessageIfFindShopListFailed() throws Exception {

        // given
        final String userRoleAccessToken = authHelper.provideUserRoleAccessToken("user@bp.com");
        final String type = null;
        final String page = "0";
        final String count = "10";
        final String order = "asc";

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/v1/shops")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header("Authorization", "Bearer " + userRoleAccessToken)
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

    static Stream<Arguments> invalidShopRegistrationRequestProvider() {
        final ShopRegistrationRequest nameInvalidRequest = new ShopRegistrationRequest(
            null, "010-1234-5678", "www.naver.com", "introduction",
            null, null, null, null, null);

        final ShopRegistrationRequest contactInvalidRequest = new ShopRegistrationRequest(
            "name", null, "www.naver.com", "introduction",
            null, null, null, null, null);

        final ShopRegistrationRequest introductionInvalidRequest = new ShopRegistrationRequest(
            "name", "010-1234-5678", "www.naver.com",
            RandomStringUtils.random(2049, true, true),
            null, null, null, null, null);

        return Stream.of(
            Arguments.of(nameInvalidRequest),
            Arguments.of(contactInvalidRequest),
            Arguments.of(introductionInvalidRequest));
    }
}
