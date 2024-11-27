package com.beautify_project.bp_app_api.fixtures;

import com.beautify_project.bp_app_api.entity.Category;

public class CategoryTestFixture {

    public static Category[] MOCKED_VALID_CATEGORY_ENTITIES;

    public static void initValidCategoryEntitiesIfNotExists() {
        if (!CommonTestFixture.isArrayNullOrEmpty(MOCKED_VALID_CATEGORY_ENTITIES)) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        MOCKED_VALID_CATEGORY_ENTITIES = new Category[] {
            Category.of("머리", "모발이식, 두피문신 등등의 시술이 포함된 카테고리 입니다.", currentTime),
            Category.of("피부", "주근깨 제거, 점 제거, 잡티 제거 등등의 시술이 포함된 카테고리입니다.", currentTime),
            Category.of("타투", "타투와 관련된 시술이 포함된 카테고리입니다.", currentTime)
        };
    }

}
