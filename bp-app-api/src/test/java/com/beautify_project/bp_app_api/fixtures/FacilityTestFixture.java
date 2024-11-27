package com.beautify_project.bp_app_api.fixtures;

import com.beautify_project.bp_app_api.entity.Facility;

public class FacilityTestFixture {

    public static Facility[] MOCKED_VALID_FACILITY_ENTITIES;

    public static void initValidFacilityEntitiesIfNotExists() {
        if (!CommonTestFixture.isArrayNullOrEmpty(MOCKED_VALID_FACILITY_ENTITIES)) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        MOCKED_VALID_FACILITY_ENTITIES = new Facility[]{
            Facility.of("와이파이", currentTime),
            Facility.of("주차", currentTime),
            Facility.of("샤워실", currentTime)
        };
    }


}
