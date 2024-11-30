//package com.beautify_project.bp_app_api.fixtures;
//
//import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
//import com.beautify_project.bp_app_api.dto.shop.ShopFindListRequestParameters;
//import com.beautify_project.bp_app_api.dto.shop.ShopFindResult;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.Address;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.BusinessTime;
//import com.beautify_project.bp_app_api.dto.shop.ShopRegistrationRequest.IdName;
//import com.beautify_project.bp_app_api.entity.Facility;
//import com.beautify_project.bp_app_api.entity.Operation;
//import com.beautify_project.bp_app_api.entity.Shop;
//import com.beautify_project.bp_app_api.enumeration.OrderType;
//import com.beautify_project.bp_app_api.enumeration.ShopSearchType;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.LocalTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Stream;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.junit.jupiter.params.provider.Arguments;
//import org.springframework.mock.web.MockMultipartFile;
//
//public class ShopTestFixture {
//    public static final String MOCKED_REGISTER_SUCCESS_RETURNED_SHOP_ID = "732e934";
//    public static final String[] MOCKED_FIND_LIST_SUCCESS_RETURNED_SHOP_IDS =
//        {"2360c169", "f4804d31"};
//
//    public static final String TEST_IMAGE_FILE_DIRECTORY_PATH = "src/test/resources/files";
//    public static final String TEST_FILE_SYSTEM_DATA_PATH =
//        TEST_IMAGE_FILE_DIRECTORY_PATH + "/data";
//
//    public static List<MockMultipartFile> MOCKED_IMAGE_FILES;
//    public static ResponseMessage MOCKED_REGISTER_SUCCESS_RESPONSE_MESSAGE;
//    public static ResponseMessage MOCKED_FIND_LIST_SUCCESS_RESPONSE_MESSAGE;
//    public static String BASE64_ENCODED_THUMBNAIL;
//
//    public static Shop[] MOCKED_VALID_SHOP_ENTITIES;
//
//    public static void initMockedImageFiles() throws IOException {
//        MOCKED_IMAGE_FILES = Arrays.asList(
//            new MockMultipartFile("images", "image1.png", "image/png",
//                Files.readAllBytes(Path.of(TEST_IMAGE_FILE_DIRECTORY_PATH + "/1.png"))),
//            new MockMultipartFile("images", "image2.png", "image/png",
//                Files.readAllBytes(Path.of(TEST_IMAGE_FILE_DIRECTORY_PATH + "/2.png")
//                )));
//    }
//
//    public static void initMockedRegisterSuccessResponseMessage() {
//        Map<String, String> returnValue = new HashMap<>();
//        returnValue.put("shopId", MOCKED_REGISTER_SUCCESS_RETURNED_SHOP_ID);
//        MOCKED_REGISTER_SUCCESS_RESPONSE_MESSAGE = ResponseMessage.createResponseMessage(returnValue);
//    }
//
//    public static void initBase64EncodedThumbnail() throws IOException{
//        BASE64_ENCODED_THUMBNAIL = Files.readString(
//            Path.of(TEST_IMAGE_FILE_DIRECTORY_PATH + "/thumbnail_base64.txt"),
//            StandardCharsets.UTF_8);
//    }
//
//    public static void initMockedFindListSuccessResponseMessage() {
//        ShopFindResult result1 = ShopFindResult.builder()
//            .id(MOCKED_FIND_LIST_SUCCESS_RETURNED_SHOP_IDS[0])
//            .name("시술소1")
//            .operations(Arrays.asList("두피문신", "눈썹문신", "입술문신"))
//            .supportFacilities(Arrays.asList("주차가능", "와이파이", "샤워실"))
//            .rate("4.5")
//            .likes(132)
//            .likePushed(false)
//            .thumbnail(BASE64_ENCODED_THUMBNAIL)
//            .build();
//
//        ShopFindResult result2 = ShopFindResult.builder()
//            .id(MOCKED_FIND_LIST_SUCCESS_RETURNED_SHOP_IDS[1])
//            .name("시술소2")
//            .operations(List.of("타투"))
//            .supportFacilities(List.of("와이파이"))
//            .rate("3.0")
//            .likes(20)
//            .likePushed(true)
//            .thumbnail(BASE64_ENCODED_THUMBNAIL)
//            .build();
//
//        MOCKED_FIND_LIST_SUCCESS_RESPONSE_MESSAGE = ResponseMessage.createResponseMessage(
//            Arrays.asList(result1, result2));
//    }
//
//    public static void initMockedValidShopEntitiesIfNotInitialized() {
//        OperationTestFixture.initMockedValidOperationEntitiesIfNotInitialized();
//        FacilityTestFixture.initValidFacilityEntitiesIfNotInitialized();
//
//        List<Operation> operations = Collections.singletonList(
//            OperationTestFixture.MOCKED_VALID_OPERATION_ENTITIES[0]);
//
//        List<Facility> facilities = Arrays.asList(
//            FacilityTestFixture.MOCKED_VALID_FACILITY_ENTITIES);
//
//        if (CommonTestFixture.isInitialized(MOCKED_VALID_SHOP_ENTITIES)) {
//            return;
//        }
//
//        MOCKED_VALID_SHOP_ENTITIES = new Shop[]{
//            Shop.createShop(createValidShopRegistrationRequest(), operations, facilities,
//                System.currentTimeMillis())
//        };
//
//    }
//
//    public static ShopRegistrationRequest createValidShopRegistrationRequest() {
//        return new ShopRegistrationRequest(
//            "미용시술소1",
//            "010-1234-5678",
//            "www.naver.com",
//            "안녕하세요 미용시술소1입니다.",
//            Arrays.asList(
//                new IdName("4541403a", "시술1"),
//                new IdName("0ced03cc", "시술2")),
//            List.of(new IdName("f9a1aa26", "카테고리1")),
//            List.of(new IdName("239a8cb9", "지원시설1")),
//            new BusinessTime(
//                LocalTime.of(9, 0),
//                LocalTime.of(18, 0),
//                LocalTime.of(12, 0),
//                LocalTime.of(13, 0),
//                Arrays.asList("monday", "tuesday")),
//            new Address(
//                "111",
//                "서울시",
//                "마포구",
//                "상암동",
//                "481",
//                "월드컵북로",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "")
//        );
//    }
//
//    public static Stream<Arguments> invalidShopRegistrationRequestProvider() {
//        return Stream.of(
//            Arguments.of(createInvalidNameRequest()),
//            Arguments.of(createInvalidIntroduction()),
//            Arguments.of(createInvalidContactRequest())
//        );
//    }
//
//    public static ShopRegistrationRequest createInvalidNameRequest() {
//        return new ShopRegistrationRequest(
//            RandomStringUtils.randomAlphabetic(130),
//            "010-1234-5678",
//            "www.naver.com",
//            "안녕하세요 미용시술소1입니다.",
//            Arrays.asList(
//                new IdName("4541403a", "시술1"),
//                new IdName("0ced03cc", "시술2")),
//            List.of(new IdName("f9a1aa26", "카테고리1")),
//            List.of(new IdName("239a8cb9", "지원시설1")),
//            new BusinessTime(
//                LocalTime.of(9, 0),
//                LocalTime.of(18, 0),
//                LocalTime.of(12, 0),
//                LocalTime.of(13, 0),
//                Arrays.asList("monday", "tuesday")),
//            new Address(
//                "111",
//                "서울시",
//                "마포구",
//                "상암동",
//                "481",
//                "월드컵북로",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "")
//        );
//    }
//
//    public static ShopRegistrationRequest createInvalidContactRequest() {
//        return new ShopRegistrationRequest(
//            "미용시술소1",
//            RandomStringUtils.randomAlphabetic(14),
//            "www.naver.com",
//            "안녕하세요 미용시술소1입니다.",
//            Arrays.asList(
//                new IdName("4541403a", "시술1"),
//                new IdName("0ced03cc", "시술2")),
//            List.of(new IdName("f9a1aa26", "카테고리1")),
//            List.of(new IdName("239a8cb9", "지원시설1")),
//            new BusinessTime(
//                LocalTime.of(9, 0),
//                LocalTime.of(18, 0),
//                LocalTime.of(12, 0),
//                LocalTime.of(13, 0),
//                Arrays.asList("monday", "tuesday")),
//            new Address(
//                "111",
//                "서울시",
//                "마포구",
//                "상암동",
//                "481",
//                "월드컵북로",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "")
//        );
//    }
//
//    public static ShopRegistrationRequest createInvalidIntroduction() {
//        return new ShopRegistrationRequest(
//            "미용시술소1",
//            "010-1234-5678",
//            "www.naver.com",
//            RandomStringUtils.randomAlphabetic(2050),
//            Arrays.asList(
//                new IdName("4541403a", "시술1"),
//                new IdName("0ced03cc", "시술2")),
//            List.of(new IdName("f9a1aa26", "카테고리1")),
//            List.of(new IdName("239a8cb9", "지원시설1")),
//            new BusinessTime(
//                LocalTime.of(9, 0),
//                LocalTime.of(18, 0),
//                LocalTime.of(12, 0),
//                LocalTime.of(13, 0),
//                Arrays.asList("monday", "tuesday")),
//            new Address(
//                "111",
//                "서울시",
//                "마포구",
//                "상암동",
//                "481",
//                "월드컵북로",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "",
//                "")
//        );
//    }
//
//    public static Stream<Arguments> invalidFindShopListParameterProvider() {
//        return Stream.of(
//            Arguments.of(null, "0", "10", "asc"),
//            Arguments.of("shopnname", "1", "10", "asc"),
//            Arguments.of("location", "1", "10", "asccc"),
//            Arguments.of("like", "alphabet", "alphabet", "desc"),
//            Arguments.of("rate", "1", "10", "3")
//        );
//    }
//
//    public static Stream<Arguments> validFindShopListParameterInControllerProvider() {
//        return Stream.of(
//            Arguments.of("shopName", null, null, null),
//            Arguments.of("shopName", null, null, "desc"),
//            Arguments.of("location", null, "11", "desc"),
//            Arguments.of("like", "1", null, "asc")
//        );
//    }
//
//    public static Stream<Arguments> validFindShopListParameterInServiceProvider() {
//        return Stream.of(
//            Arguments.arguments(
//                new ShopFindListRequestParameters(ShopSearchType.SHOP_NAME, 0, 5, OrderType.ASC),
//                new ShopFindListRequestParameters(ShopSearchType.LIKE, 0, 10, OrderType.ASC),
//                new ShopFindListRequestParameters(ShopSearchType.RATE, 0, 15, OrderType.DESC),
//                new ShopFindListRequestParameters(ShopSearchType.LOCATION, 0, 100, OrderType.DESC)
//            )
//        );
//    }
//
//
//}
