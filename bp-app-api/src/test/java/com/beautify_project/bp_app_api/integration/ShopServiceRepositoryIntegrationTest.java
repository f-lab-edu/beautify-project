package com.beautify_project.bp_app_api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.beautify_project.bp_app_api.dto.common.ErrorResponseMessage.ErrorCode;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
import com.beautify_project.bp_app_api.entity.Facility;
import com.beautify_project.bp_app_api.entity.Operation;
import com.beautify_project.bp_app_api.entity.Shop;
import com.beautify_project.bp_app_api.entity.ShopLike;
import com.beautify_project.bp_app_api.entity.ShopLike.ShopLikeId;
import com.beautify_project.bp_app_api.exception.AlreadyProcessedException;
import com.beautify_project.bp_app_api.exception.NotFoundException;
import com.beautify_project.bp_app_api.repository.ShopLikeRepository;
import com.beautify_project.bp_app_api.repository.ShopRepository;
import com.beautify_project.bp_app_api.service.ShopService;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class ShopServiceRepositoryIntegrationTest {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopLikeRepository shopLikeRepository;

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
        final Shop shopFromDB = shopRepository.findById(testShop.getId())
            .orElseThrow(() -> new NotFoundException(ErrorCode.SH001));

        final Long previousLikeCount = testShop.getLikes();
        final Long postLikeCount = shopFromDB.getLikes();

        assertThat(shopLikeRepository.count()).isEqualTo(0);
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
}
