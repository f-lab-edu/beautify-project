package com.beautify_project;

import com.beautify_project.bp_dto.common.response.ResponseMessage;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest.Address;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest.BusinessTime;
import com.beautify_project.bp_dto.shop.ShopRegistrationRequest.IdName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

public class ShopTestFixture {
    public static final String MOCKED_REGISTER_SUCCESS_RETURNED_SHOP_ID = "732e934";
    public static final String[] MOCKED_FIND_LIST_SUCCESS_RETURNED_SHOP_IDS =
        {"2360c169", "f4804d31"};

    public static final String TEST_IMAGE_FILE_DIRECTORY_PATH = "src/test/resources/files";

    public static List<MockMultipartFile> MOCKED_IMAGE_FILES;
    public static ResponseMessage MOCKED_REGISTER_SUCCESS_RESPONSE_MESSAGE;
    public static ResponseMessage MOCKED_FIND_LIST_SUCCESS_RESPONSE_MESSAGE;
    public static String BASE64_ENCODED_THUMBNAIL;

    public static ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());


    public static void loadMockedImageFiles() throws IOException {
        MOCKED_IMAGE_FILES = Arrays.asList(
            new MockMultipartFile("images", "image1.png", "image/png",
                Files.readAllBytes(Path.of(TEST_IMAGE_FILE_DIRECTORY_PATH + "/1.png"))),
            new MockMultipartFile("images", "image2.png", "image/png",
                Files.readAllBytes(Path.of(TEST_IMAGE_FILE_DIRECTORY_PATH + "/2.png")
                )));
    }

    public static void createMockedRegisterSuccessResponseMessage() {
        Map<String, String> returnValue = new HashMap<>();
        returnValue.put("shopId", MOCKED_REGISTER_SUCCESS_RETURNED_SHOP_ID);
        MOCKED_REGISTER_SUCCESS_RESPONSE_MESSAGE = ResponseMessage.createResponseMessage(HttpStatus.OK, returnValue);
    }

    public static void loadBase64EncodedThumbnail() throws IOException{
        BASE64_ENCODED_THUMBNAIL = Files.readString(
            Path.of(TEST_IMAGE_FILE_DIRECTORY_PATH + "/thumbnail_base64.txt"),
            StandardCharsets.UTF_8);
    }

    public static void createMockedFindListSuccessResponseMessage() {
        List<Map<String, Object>> returnValue = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("id", MOCKED_FIND_LIST_SUCCESS_RETURNED_SHOP_IDS[0]);
        data1.put("name", "시술소1");
        data1.put("operations", Arrays.asList("두피문신", "눈썹문신", "입술문신"));
        data1.put("supportFacilities", Arrays.asList("주차가능", "와이파이", "샤워실"));
        data1.put("rate", "4.5");
        data1.put("likes", 132);
        data1.put("likePushed", false);
        data1.put("thumbnail", BASE64_ENCODED_THUMBNAIL);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("id", MOCKED_FIND_LIST_SUCCESS_RETURNED_SHOP_IDS[1]);
        data2.put("name", "시술소2");
        data2.put("operations", List.of("타투"));
        data2.put("supportFacilities", List.of("와이파이"));
        data2.put("rate", "3.0");
        data2.put("likes", 20);
        data2.put("likePushed", true);
        data2.put("thumbnail", BASE64_ENCODED_THUMBNAIL);

        returnValue.add(data1);
        returnValue.add(data2);

        MOCKED_FIND_LIST_SUCCESS_RESPONSE_MESSAGE = ResponseMessage.createResponseMessage(
            HttpStatus.OK, returnValue);
    }

    public static ShopRegistrationRequest createValidShopRegistrationRequest() {
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

    public static Stream<Arguments> invalidShopRegistrationRequestProvider() {
        return Stream.of(
            Arguments.of(createInvalidNameRequest()),
            Arguments.of(createInvalidIntroduction()),
            Arguments.of(createInvalidContactRequest())
        );
    }

    public static ShopRegistrationRequest createInvalidNameRequest() {
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

    public static ShopRegistrationRequest createInvalidContactRequest() {
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

    public static ShopRegistrationRequest createInvalidIntroduction() {
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

    public static Stream<Arguments> invalidFindShopListParameterProvider() {
        return Stream.of(
            Arguments.of(null, "0", "10", "asc"),
            Arguments.of("shopnname", "1", "10", "asc"),
            Arguments.of("location", "1", "10", "asccc"),
            Arguments.of("like", "alphabet", "alphabet", "desc"),
            Arguments.of("rate", "1", "10", "3")
        );
    }

    public static Stream<Arguments> validFindShopListParameterProvider() {
        return Stream.of(
            Arguments.of("shopName", null, null, null),
            Arguments.of("shopName", null, null, "desc"),
            Arguments.of("location", null, "11", "desc"),
            Arguments.of("like", "1", null, "asc")
        );
    }
}
