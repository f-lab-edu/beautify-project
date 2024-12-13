package com.beautify_project.bp_app_api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
import com.beautify_project.bp_app_api.entity.Category;
import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.entity.OperationCategory;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.entity.ShopLike;
import com.beautify_project.bp_app_api.entity.ShopLike.ShopLikeId;
import com.beautify_project.bp_app_api.exception.AlreadyProcessedException;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.CategoryRepository;
import com.beautify_project.bp_app_api.repository.FacilityRepository;
import com.beautify_project.bp_app_api.repository.OperationCategoryRepository;
import com.beautify_project.bp_app_api.repository.OperationRepository;
import com.beautify_project.bp_app_api.repository.ShopCategoryRepository;
import com.beautify_project.bp_app_api.repository.ShopFacilityRepository;
import com.beautify_project.bp_app_api.repository.ShopLikeRepository;
import com.beautify_project.bp_app_api.repository.ShopOperationRepository;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import com.beautify_project.bp_app_api.service.ShopService;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
@Slf4j
class ShopServiceRepositoryIntegrationTest {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopLikeRepository shopLikeRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private OperationCategoryRepository operationCategoryRepository;

    @Autowired
    private ShopCategoryRepository shopCategoryRepository;

    @Autowired
    private ShopOperationRepository shopOperationRepository;

    @Autowired
    private ShopFacilityRepository shopFacilityRepository;

    @BeforeEach
    void beforeEach() {
        deleteAll();
    }

    @AfterEach
    void afterEach() {
        deleteAll();
    }

    private void deleteAll() {
        shopRepository.deleteAllInBatch();
        shopLikeRepository.deleteAllInBatch();
        operationRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        facilityRepository.deleteAllInBatch();
        operationCategoryRepository.deleteAllInBatch();
        shopCategoryRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Shop 좋아요시 db에 기존 샵의 좋아요 수 + 1 과 ShopLike db에 저장되어야 한다.")
    void given_shopLike_when_succeed_then_db_inserted() {
        // given
        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("두피 문신 시술", "두피 문신 시술 설명"),
            Operation.of("점 제거 시술", "점 제거 시술 설명"));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.withName("와이파이"),
            Facility.withName("샤워실")
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

        final ShopRegistrationRequest requestForTest = new ShopRegistrationRequest(
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

        final Shop testShop = Shop.from(requestForTest);
        shopRepository.saveAndFlush(testShop); // DB 에 있는 상태 가정

        final String memberEmail = "sssukho@gmail.com";

        // when
        shopService.likeShop(testShop.getId(), memberEmail);

        // then
        final Shop shopFromDb = shopRepository.findById(testShop.getId())
            .orElseThrow(() -> new NotFoundException(
                ErrorCode.SH001));

        final Long previousLikeCount = testShop.getLikes();
        final Long postLikeCount = shopFromDb.getLikes();

        final ShopLike shopLikeFromDb = shopLikeRepository.findAll().get(0);

        shopLikeRepository.findById(ShopLikeId.of(shopFromDb.getId(), memberEmail));

        assertThat(postLikeCount).isEqualTo(previousLikeCount + 1);
        assertThat(shopLikeFromDb.getId().getShopId()).isEqualTo(shopFromDb.getId());
        assertThat(shopLikeFromDb.getId().getMemberEmail()).isEqualTo(memberEmail);
    }

    @Test
    @DisplayName("Shop 좋아요 취소시 DB에 기존 샵의 좋아요 수 - 1 과 ShopLike db에 저장되어 있지 않아야 한다.")
    void given_shopLikeCancel_when_succeed_then_db_deleted() {
        // given
        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("두피 문신 시술", "두피 문신 시술 설명"),
            Operation.of("점 제거 시술", "점 제거 시술 설명"));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.withName("와이파이"),
            Facility.withName("샤워실")
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

        final ShopRegistrationRequest requestForTest = new ShopRegistrationRequest(
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

        final Shop testShop = Shop.from(requestForTest);
        testShop.increaseLikeCount();
        shopRepository.saveAndFlush(testShop);

        final String memberEmail = "sssukho@gmail.com";

        ShopLike shopLike = ShopLike.of(testShop.getId(), memberEmail);
        shopLikeRepository.saveAndFlush(shopLike);

        // when
        shopService.cancelLikeShop(testShop.getId(), memberEmail);

        // then
        assert testShop.getId() != null;
        final Shop shopFromDB = shopRepository.findById(testShop.getId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.SH001));

        final Long previousLikeCount = testShop.getLikes();
        final Long postLikeCount = shopFromDB.getLikes();

        assertThat(shopLikeRepository.count()).isZero();
        assertThat(postLikeCount).isEqualTo(previousLikeCount - 1);
    }

    @Test
    @DisplayName("Shop 좋아요시 기존에 좋아요 한 이력이 있으면 AlreadyProcessedException 을 던진다")
    void given_shopLike_when_already_shop_like_exists_then_throw_alreadyProcessedException() {
        // given
        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("두피 문신 시술", "두피 문신 시술 설명"),
            Operation.of("점 제거 시술", "점 제거 시술 설명"));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.withName("와이파이"),
            Facility.withName("샤워실")
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

        final ShopRegistrationRequest requestForTest = new ShopRegistrationRequest(
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

        final Shop testShop = Shop.from(requestForTest);
        final String memberEmail = "sssukho@gmail.com";
        shopRepository.saveAndFlush(testShop);
        shopService.likeShop(testShop.getId(), memberEmail);

        // when & then
        assertThatThrownBy(() -> shopService.likeShop(testShop.getId(), memberEmail)).isInstanceOf(
            AlreadyProcessedException.class);
    }

    @Test
    @DisplayName("Shop 좋아요 취소시 기존에 좋아요 했던 이력이 없으면 AlreadyProcessedException 을 던진다")
    void given_shopLikeCancel_when_shop_like_not_exists_then_throw_alreadyProcessedException() {
        // given
        final List<Operation> mockedOperationEntities = Arrays.asList(
            Operation.of("두피 문신 시술", "두피 문신 시술 설명"),
            Operation.of("점 제거 시술", "점 제거 시술 설명"));

        final List<Facility> mockedFacilityEntities = Arrays.asList(
            Facility.withName("와이파이"),
            Facility.withName("샤워실")
        );

        final List<String> mockedOperationIds = Arrays.asList(
            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
        final List<String> mockedFacilityIds = Arrays.asList(
            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
        );

        final ShopRegistrationRequest requestForTest = new ShopRegistrationRequest(
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

        final Shop testShop = Shop.from(requestForTest);
        testShop.increaseLikeCount();
        shopRepository.saveAndFlush(testShop);

        final String memberEmail = "sssukho@gmail.com";

        // when & then
        assertThatThrownBy(
            () -> shopService.cancelLikeShop(testShop.getId(), memberEmail)).isInstanceOf(
            AlreadyProcessedException.class);
    }

    @Test
    @DisplayName("Shop 등록시 모든 필드값이 있는 경우 등록에 성공 후 DB 에 모든 필드가 정상적으로 저장되어 있다.")
    void given_shopRegistrationWithAllFields_when_succeed_then_allFieldsAreStoredInDB() {
        // given
        final List<Operation> insertedOperations = Arrays.asList(
            Operation.of("시술1","시술1설명"),
            Operation.of("시술2", "시술2설명")
        );
        operationRepository.saveAllAndFlush(insertedOperations);

        final List<Category> insertedCategories = Arrays.asList(
            Category.of("카테고리1", "카테고리1설명"),
            Category.of("카테고리2", "카테고리2설명"),
            Category.of("카테고리3", "카테고리3설명")
        );
        categoryRepository.saveAllAndFlush(insertedCategories);

        final List<OperationCategory> insertedOperationCategories = Arrays.asList(
            // 시술1 => 카테고리1, 카테고리2 에 속함
            OperationCategory.of(insertedOperations.get(0).getId(),
                insertedCategories.get(0).getId()),
            OperationCategory.of(insertedOperations.get(0).getId(),
                insertedCategories.get(1).getId()),
            // 시술2 => 카테고리3 에 속함
            OperationCategory.of(insertedOperations.get(1).getId(),
                insertedCategories.get(2).getId()));
        operationCategoryRepository.saveAllAndFlush(insertedOperationCategories);

        final List<Facility> insertedFacilities = Arrays.asList(
            Facility.withName("편의시설1"), Facility.withName("편의시설2")
        );
        facilityRepository.saveAllAndFlush(insertedFacilities);

        final List<String> operationIds = insertedOperations.stream()
            .map(operation -> operation.getId()).toList();

        final List<String> facilityIds = insertedFacilities.stream()
            .map((facility -> facility.getId())).toList();

        final ShopRegistrationRequest registrationRequest = new ShopRegistrationRequest(
            "미용시술소1",
            "010-1234-5678",
            "www.naver.com",
            "안녕하세요 미용시술소입니다.",
            operationIds,
            facilityIds,
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
        log.debug("=====================");
        shopService.registerShop(registrationRequest);

        // then
        final Shop insertedShop = shopRepository.findAll().get(0);
        assert insertedShop != null;

        assertThat(insertedShop.getId()).isNotBlank();
        assertThat(insertedShop.getName()).isNotBlank();
        assertThat(insertedShop.getContact()).isNotBlank();
        assertThat(insertedShop.getUrl()).isNotBlank();
        assertThat(insertedShop.getIntroduction()).isNotBlank();
        assertThat(insertedShop.getRate()).isEqualTo("0.0");
        assertThat(insertedShop.getLikes()).isZero();
        assertThat(insertedShop.getRegisteredTime()).isLessThan(System.currentTimeMillis());
        assertThat(insertedShop.getUpdated()).isLessThan(System.currentTimeMillis());

        assertThat(insertedShop.getBusinessTime().openTime()).hasHour(9);
        assertThat(insertedShop.getBusinessTime().closeTime()).hasHour(18);
        assertThat(insertedShop.getBusinessTime().breakBeginTime()).hasHour(13);
        assertThat(insertedShop.getBusinessTime().breakEndTime()).hasHour(14);
        assertThat(insertedShop.getBusinessTime().offDayOfWeek()).hasSize(2);

        assertThat(insertedShop.getShopAddress().getDongCode()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getSiDoName()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getSiGoonGooName()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getEubMyunDongName()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getRoadNameCode()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getUnderGround()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getRoadMainNum()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getRoadSubNum()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getSiGoonGooBuildingName()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getZipCode()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getApartComplex()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getEubMyunDongSerialNumber()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getLatitude()).isNotBlank();
        assertThat(insertedShop.getShopAddress().getLongitude()).isNotBlank();

        assertThat(shopCategoryRepository.count()).isEqualTo(3);
        assertThat(shopFacilityRepository.count()).isEqualTo(2);
        assertThat(shopOperationRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Shop 등록 요청 데이터에 시술 아이디가 없는 경우에도 등록에 성공하고, ShopOperation 에는 데이터가 존재하지 않아야 한다.")
    void given_shopRegistrationWithoutOperation_when_succeed_then_allFieldsAreStoredInDB_and_ShopOperation_count_isZero() {
        // TODO: 구현
    }

    @Test
    @DisplayName("Shop 등록 요청 데이터에 지원 시설 아이디가 없는 경우에도 등록에 성공하고, ShopFacility 에는 데이터가 존재하지 않아야 한다.")
    void given_shopRegistrationWithoutFacilities_when_succeed_then_allFieldsAreStoredInDB_and_ShopFacility_count_isZero() {
        // TODO: 구현
    }
}
