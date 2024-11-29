package com.beautify_project.bp_app_api.fixtures;

import com.beautify_project.bp_app_api.dto.common.ResponseMessage;
import com.beautify_project.bp_app_api.entity.Review;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewTestFixture {

    public static final String MOCKED_REVIEW_ID = "bd1cc4f9";
    public static final String MOCKED_SHOP_ID = "732e934";
    public static final String[] MOCKED_REVIEW_IDS = {"bd1cc4f9", "9f142f61"};
    public static final String[] MOCKED_MEMBER_IDS = {"sssukho1", "sssukho2"};
    public static final String[] MOCKED_OPERATION_IDS = {"c73ab5e8", "7fced931"};

    public static ResponseMessage MOCKED_EMPTY_RESPONSE_MESSAGE;
    public static ResponseMessage MOCKED_FIND_REVIEW_SUCCESS_RESPONSE;
    public static ResponseMessage MOCKED_FIND_REVIEW_LIST_SUCCESS_RESPONSE;

    public static Review[] MOCKED_VALID_REVIEW_ENTITIES;

    public static void initMockedEmptyResponseMessage() {
        MOCKED_EMPTY_RESPONSE_MESSAGE = ResponseMessage.createResponseMessage(new HashMap<>());
    }

    public static void initMockedFindReviewSuccessResponse() {
        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put("id", MOCKED_REVIEW_ID);
        returnValue.put("rate", "4.5");

        Map<String, Object> member = new HashMap<>();
        member.put("id", "sssukho");
        member.put("name", "임석호");
        returnValue.put("member", member);

        Map<String, Object> operation = new HashMap<>();
        operation.put("id", "d939f8ed");
        operation.put("name", "두피문신");
        operation.put("date", 1730437200000L);
        returnValue.put("operation", operation);

        MOCKED_FIND_REVIEW_SUCCESS_RESPONSE = ResponseMessage.createResponseMessage(returnValue);
    }

    public static void initMockedFindReviewListSuccessResponse() {
        List<Map<String, Object>> returnValue = new ArrayList<>();

        Map<String, Object> data1 = new HashMap<>();
        data1.put("id", MOCKED_REVIEW_IDS[0]);
        data1.put("rate", "4.3");

        Map<String, Object> m1 = new HashMap<>();
        m1.put("id", MOCKED_MEMBER_IDS[0]);
        m1.put("name", "임석호1");
        data1.put("member", m1);

        Map<String, Object> o1 = new HashMap<>();
        o1.put("id", MOCKED_OPERATION_IDS[0]);
        o1.put("name", "두피문신1");
        data1.put("operation", o1);

        returnValue.add(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("id", MOCKED_REVIEW_IDS[1]);
        data2.put("rate", "4.5");

        Map<String, Object> m2 = new HashMap<>();
        m2.put("id", MOCKED_MEMBER_IDS[1]);
        m2.put("name", "임석호2");
        data2.put("member", m2);

        Map<String, Object> o2 = new HashMap<>();
        o2.put("id", MOCKED_OPERATION_IDS[1]);
        o2.put("name", "두피문신2");
        data2.put("operation", o2);

        returnValue.add(data2);

        MOCKED_FIND_REVIEW_LIST_SUCCESS_RESPONSE = ResponseMessage.createResponseMessage(
            returnValue);
    }

    public static void initMockedValidReviewEntitiesIfNotInitialized() {
        if (CommonTestFixture.isInitialized(MOCKED_VALID_REVIEW_ENTITIES)) {
            return;
        }

        MOCKED_VALID_REVIEW_ENTITIES = new Review[]{
            Review.of("4", "깔끔한 시술이었습니다.", System.currentTimeMillis()),
            Review.of("3", "편의시설이 좋았어요.", System.currentTimeMillis()),
            Review.of("1", "최악입니다.", System.currentTimeMillis())
        };

    }

//    public static Stream<Arguments> invalidFindReviewListRequestProvider() {
//
//    }

}

