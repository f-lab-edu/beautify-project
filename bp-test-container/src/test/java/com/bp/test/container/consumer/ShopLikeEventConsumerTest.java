package com.bp.test.container.consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.bp.app.api.producer.ShopLikeEventProducer;
import com.bp.common.kakfa.event.ShopLikeEvent.ShopLikeEventProto.LikeType;
import com.bp.domain.mysql.entity.Shop;
import com.bp.domain.mysql.entity.ShopLike;
import com.bp.domain.mysql.entity.ShopLike.ShopLikeId;
import com.bp.domain.mysql.entity.embedded.Address;
import com.bp.domain.mysql.entity.embedded.BusinessTime;
import com.bp.domain.mysql.repository.ShopAdapterRepository;
import com.bp.domain.mysql.repository.ShopLikeAdapterRepository;
import com.bp.test.container.config.TestContainerConfig;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ShopLikeEventConsumerTest extends TestContainerConfig {

    @Autowired
    private ShopAdapterRepository shopRepository;

    @Autowired
    private ShopLikeAdapterRepository shopLikeRepository;

    @Autowired
    private ShopLikeEventProducer shopLikeEventProducer;

    @BeforeEach
    void beforeEach() {
        shopRepository.deleteAllInBatch();
        shopLikeRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Shop 좋아요 이벤트 생성시 Shop entity 에는 like 수 + 1, shop_like 에는 사용자 계정과 샵 정보가 저장된다.")
    void aRowInsertedInShopLikeAndShopLikeCountIncreasedIfShopLikeEventProduced() throws Exception {
        // given
        final String givenMemberEmail = "dev.sssukho@gmail.com";
        final Shop givenShop = Shop.newShop("미용시술소1", "010-1234-5678", "www.naver.com", "소개글",
            Arrays.asList("presigned-url1", "presigned-url2"),
            Address.of("dongCode", "sidoname", "sigoongooname",
                "eubmyundongname", "roadnamecode", "roadName",
                "underGround", "roadMainNum", "roadSubNum",
                "sigoongoobuildingname", "zipcode", "appartComplex",
                "eubmyundongserialnumber", "latitude", "longitude"),
            BusinessTime.of(LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(),
                List.of("monday")));

        final Shop insertedShop = shopRepository.saveAndFlush(givenShop);

        // when
        shopLikeEventProducer.publishShopLikeEvent(insertedShop.getId(), givenMemberEmail,
            LikeType.LIKE);

        // then
        await()
            .atMost(5, SECONDS)
            .pollInterval(1, SECONDS)
            .untilAsserted(() -> {
                // like 수 비교
                final Shop consumedShop = shopRepository.findById(insertedShop.getId())
                    .orElseThrow();
                assertThat(consumedShop.getLikes()).isEqualTo(insertedShop.getLikes() + 1);

                // shop_like 테이블에 row 존재 여부 확인
                final ShopLikeId shopLikeId = ShopLikeId.newShopLikeId(consumedShop.getId(), givenMemberEmail);
                final List<ShopLike> foundShopLikes = shopLikeRepository.findByShopLikeIdIn(
                    List.of(shopLikeId));
                assertThat(foundShopLikes.size()).isEqualTo(1);
            });
    }

    @Test
    @DisplayName("존재하지 않는 Shop 에 좋아요 이벤트 생성시 shop_like 에는 아무것도 insert 되지 않는다.")
    void nothingInsertedIntoShopLikeTableIfShopLikeEventWithNotExistedShopProduced() throws Exception {
        // given
        final Long notExistedShopId = Long.MAX_VALUE;
        final String notExistedMemberEmail = "abcd@gmail.com";

        final long allShopCountBeforeProduceEvent = shopRepository.count();
        final long allShopLikeCountBeforeProduceEvent = shopLikeRepository.count();

        // when
        shopLikeEventProducer.publishShopLikeEvent(notExistedShopId, notExistedMemberEmail, LikeType.LIKE);

        // then
        int countToTest = 5;
        int testCount = 1;
        while (testCount < countToTest) {
            final long allShopCountAfterProducedEvent = shopRepository.count();
            assertThat(allShopCountBeforeProduceEvent).isEqualTo(allShopCountAfterProducedEvent);

            final long allShopLikeCountAfterProducedEvent = shopLikeRepository.count();
            assertThat(allShopLikeCountBeforeProduceEvent).isEqualTo(
                allShopLikeCountAfterProducedEvent);

            Thread.sleep(1000);
            testCount++;
        }
    }

    @Test
    @DisplayName("Shop 좋아요 취소 이벤트 생성시 Shop entity 에는 like 수 - 1, shop_like 에는 기존에 저장되어 있던 사용자 계정과 샵 정보가 삭제된다.")
    void aRowDeletedInShopLikeAndShopLikeCountDecreasedIfShopLikeCancelEventProduced() {
        // given
        final String givenMemberEmail = "dev.sssukho@gmail.com";
        final Shop givenShop = Shop.newShop("미용시술소1", "010-1234-5678", "www.naver.com", "소개글",
            Arrays.asList("presigned-url1", "presigned-url2"),
            Address.of("dongCode", "sidoname", "sigoongooname",
                "eubmyundongname", "roadnamecode", "roadName",
                "underGround", "roadMainNum", "roadSubNum",
                "sigoongoobuildingname", "zipcode", "appartComplex",
                "eubmyundongserialnumber", "latitude", "longitude"),
            BusinessTime.of(LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(),
                List.of("monday")));

        givenShop.increaseLikeCount(2);
        final Shop insertedShop = shopRepository.saveAndFlush(givenShop);

        final ShopLike givenShopLike = ShopLike.newShopLike(insertedShop.getId(), givenMemberEmail);
        final ShopLike insertedShopLike = shopLikeRepository.saveAndFlush(givenShopLike);

        // when
        shopLikeEventProducer.publishShopLikeEvent(insertedShop.getId(), givenMemberEmail,
            LikeType.LIKE_CANCEL);

        // then
        await()
            .atMost(5, SECONDS)
            .pollInterval(1, SECONDS)
            .untilAsserted(() -> {
                // like 수 비교
                final Shop consumedShop = shopRepository.findById(insertedShop.getId())
                    .orElseThrow();
                assertThat(consumedShop.getLikes()).isEqualTo(1);

                // shop_like 테이블에 row 존재 여부 확인 (삭제되어야 함)
                final ShopLikeId shopLikeId = ShopLikeId.newShopLikeId(consumedShop.getId(), givenMemberEmail);
                final List<ShopLike> foundShopLikes = shopLikeRepository.findByShopLikeIdIn(
                    List.of(shopLikeId));
                assertThat(foundShopLikes.size()).isZero();
            });
    }

    @Test
    @DisplayName("존재하지 않는 Shop 에 좋아요 취소 이벤트 생성시 shop_like 에는 아무것도 삭제되지 않는다.")
    void nothingDeletedIfShopLikeCancelEventWithNotExistedShopProduced() throws Exception{
        // given
        final long notExistedShopId = Long.MAX_VALUE;
        final String notExistedMemberEmail = "abcd@gmail.com";

        final long allShopCountBeforeProducedEvent = shopRepository.count();
        final long allShopLikeCountBeforeProducedEvent = shopLikeRepository.count();

        // when
        shopLikeEventProducer.publishShopLikeEvent(notExistedShopId, notExistedMemberEmail,
            LikeType.LIKE_CANCEL);

        // then
        int countToTest = 5;
        int testCount = 1;
        while (testCount < countToTest) {
            final long allShopCountAfterProducedEvent = shopRepository.count();
            assertThat(allShopCountBeforeProducedEvent).isEqualTo(allShopCountAfterProducedEvent);

            final long allShopLikeCountAfterProducedEvent = shopLikeRepository.count();
            assertThat(allShopLikeCountBeforeProducedEvent).isEqualTo(
                allShopLikeCountAfterProducedEvent);

            Thread.sleep(1000);
            testCount++;
        }
    }

    @Test
    @DisplayName("동일한 샵에 100개의 스레드가 동시에 좋아요 요청할 경우, 해당 샵의 좋아요 수는 정확히 100이 되어야 한다.")
    void shopLikeCountIs100If100ThreadsRequestShopLikeConcurrently() {
        // given
        final Shop givenShop = Shop.newShop("미용시술소1", "010-1234-5678", "www.naver.com", "소개글",
            Arrays.asList("presigned-url1", "presigned-url2"),
            Address.of("dongCode", "sidoname", "sigoongooname",
                "eubmyundongname", "roadnamecode", "roadName",
                "underGround", "roadMainNum", "roadSubNum",
                "sigoongoobuildingname", "zipcode", "appartComplex",
                "eubmyundongserialnumber", "latitude", "longitude"),
            BusinessTime.of(LocalTime.now(), LocalTime.now(), LocalTime.now(), LocalTime.now(),
                List.of("monday")));
        final Shop insertedShop = shopRepository.saveAndFlush(givenShop);
        final Long targetShopId = insertedShop.getId();

        final ExecutorService executorService = Executors.newFixedThreadPool(100);

        final List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            final String memberEmail = "dev.sssukho@gmail.com" + i;
            CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
                shopLikeEventProducer.publishShopLikeEvent(targetShopId, memberEmail,
                    LikeType.LIKE);
                return null;
            }, executorService);
            completableFutures.add(completableFuture);
        }

        await()
            .atMost(10, SECONDS)
            .pollInterval(5, SECONDS)
            .untilAsserted(() -> {
                final Shop processedShop = shopRepository.findById(targetShopId).orElseThrow();
                final Long afterProcessedLikeCount = processedShop.getLikes();
                assertThat(100L).isEqualTo(afterProcessedLikeCount);
            });

        executorService.shutdown();
    }
}
