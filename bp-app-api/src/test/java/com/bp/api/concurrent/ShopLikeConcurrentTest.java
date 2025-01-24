//package com.beautify_project.bp_app_api.concurrent;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
//import com.beautify_project.bp_app_api.entity.Facility;
//import com.beautify_project.bp_app_api.entity.Operation;
//import com.beautify_project.bp_app_api.entity.Shop;
//import com.beautify_project.bp_app_api.repository.ShopLikeRepository;
//import com.beautify_project.bp_app_api.repository.ShopRepository;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.RepeatedTest;
//import org.junit.jupiter.api.Tag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.client.RestTemplate;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//@Slf4j
//@Tag("integration-test")
//public class ShopLikeConcurrentTest {
//
//    /**
//     * bp-app-api, bp-kafka-event-consumer 인스턴스가 동작하는 상태에서 실행되어야 하는 테스트
//     */
//    private static ExecutorService executorService;
//
//    @Autowired
//    private ShopRepository shopRepository;
//
//    @Autowired
//    private ShopLikeRepository shopLikeRepository;
//
//    @BeforeEach
//    void beforeEach() {
//        executorService = Executors.newFixedThreadPool(100);
//        shopRepository.deleteAllInBatch();
//        shopLikeRepository.deleteAllInBatch();
//    }
//
//    @AfterEach
//    void afterEach() throws Exception {
//        executorService.shutdown();
//    }
//
//    @DisplayName("샵 좋아요 동시성 테스트")
//    @RepeatedTest(10)
//    void shopLikeConcurrentTest() throws Exception {
//        final List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
//        final String shopId = saveShop();
//
//        for (int i = 1; i <= 100; i++) {
//            CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
//                log.info(">>>>> executed");
//                try {
//                    requestShopLike(shopId);
//                } catch (Exception e) {
//                    log.error("Failed to request");
//                }
//                log.info(">>>>> end");
//                return null;
//            }, executorService);
//            completableFutures.add(completableFuture);
//        }
//
//        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
//
//        long sleepSecond = 3;
//        log.info("Thread sleep {} seconds", sleepSecond);
//        Thread.sleep(sleepSecond * 1000);
//
//        Shop foundShop = shopRepository.findById(shopId).orElseThrow();
//        assertThat(foundShop.getLikes()).isEqualTo(100);
//    }
//
//    private String saveShop() {
//        final List<Operation> mockedOperationEntities = Arrays.asList(
//            Operation.of("두피 문신 시술", "두피 문신 시술 설명"),
//            Operation.of("점 제거 시술", "점 제거 시술 설명"));
//
//        final List<Facility> mockedFacilityEntities = Arrays.asList(
//            Facility.withName("와이파이"),
//            Facility.withName("샤워실")
//        );
//
//        final List<String> mockedOperationIds = Arrays.asList(
//            mockedOperationEntities.get(0).getId(), mockedOperationEntities.get(1).getId());
//        final List<String> mockedFacilityIds = Arrays.asList(
//            mockedFacilityEntities.get(0).getId(), mockedFacilityEntities.get(1).getId()
//        );
//
//        final ShopRegistrationRequest requestForTest = new ShopRegistrationRequest(
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
//        final Shop testShop = Shop.from(requestForTest);
//        shopRepository.saveAndFlush(testShop);
//        return testShop.getId();
//    }
//
//    private void requestShopLike(final String shopId) throws Exception {
//        // TODO: 로그인 붙이면 수정 필요
//        final String url = "http://127.0.0.1:8080/v1/shops/likes/{id}";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity<?> entity = new HttpEntity<>(null, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.POST, entity,
//            String.class, shopId);
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new RuntimeException();
//        }
//    }
//}
