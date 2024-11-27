package com.beautify_project.bp_app_api.fixtures;

import com.beautify_project.bp_app_api.entity.Operation;
import java.util.Arrays;
import java.util.Collections;

public class OperationTestFixture {

    public static Operation[] MOCKED_VALID_OPERATION_ENTITIES;

    public static void initMockedValidOperationEntitiesIfNotExists() {
        if (!CommonTestFixture.isArrayNullOrEmpty(MOCKED_VALID_OPERATION_ENTITIES)) {
            return;
        }

        CategoryTestFixture.initValidCategoryEntitiesIfNotExists();

        long currentTime = System.currentTimeMillis();
        // 두피문신 시술은 머리, 타투 카테고리에 속함
        MOCKED_VALID_OPERATION_ENTITIES = new Operation[] {
            Operation.createOperation("두피문신",
                "헤어라인이나 비어있는 공간을 매꿔줄 수 있는 반영구 시술입니다.",
                currentTime,
                Arrays.asList(CategoryTestFixture.MOCKED_VALID_CATEGORY_ENTITIES[0],
                    CategoryTestFixture.MOCKED_VALID_CATEGORY_ENTITIES[2])),
            // 주근꺠 제거 시술은 피부 카테고리에 속함
            Operation.createOperation("주근깨 제거", "피부에 있는 주근깨를 제거하는 시술입니다.",
                currentTime,
                Collections.singletonList(CategoryTestFixture.MOCKED_VALID_CATEGORY_ENTITIES[1]))
        };
    }

}
