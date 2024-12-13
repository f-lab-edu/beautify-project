package com.beautify_project.bp_app_api.entity.embedded;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String dongCode;
    private String siDoName;
    private String siGoonGooName;
    private String eubMyunDongName;
    private String roadNameCode;
    private String roadName;
    private String underGround;
    private String roadMainNum;
    private String roadSubNum;
    private String siGoonGooBuildingName;
    private String zipCode;
    private String apartComplex;
    private String eubMyunDongSerialNumber;
    private String latitude;
    private String longitude;

    @Builder
    private Address(final String dongCode, final String siDoName, final String siGoonGooName,
        final String eubMyunDongName,
        final String roadNameCode, final String roadName, final String underGround,
        final String roadMainNum, final String roadSubNum,
        final String siGoonGooBuildingName, final String zipCode, final String apartComplex,
        final String eubMyunDongSerialNumber, final String latitude, final String longitude) {
        this.dongCode = dongCode;
        this.siDoName = siDoName;
        this.siGoonGooName = siGoonGooName;
        this.eubMyunDongName = eubMyunDongName;
        this.roadNameCode = roadNameCode;
        this.roadName = roadName;
        this.underGround = underGround;
        this.roadMainNum = roadMainNum;
        this.roadSubNum = roadSubNum;
        this.siGoonGooBuildingName = siGoonGooBuildingName;
        this.zipCode = zipCode;
        this.apartComplex = apartComplex;
        this.eubMyunDongSerialNumber = eubMyunDongSerialNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Address{" +
            "dongCode='" + dongCode + '\'' +
            ", siDoName='" + siDoName + '\'' +
            ", siGoonGooName='" + siGoonGooName + '\'' +
            ", eubMyunDongName='" + eubMyunDongName + '\'' +
            ", roadNameCode='" + roadNameCode + '\'' +
            ", roadName='" + roadName + '\'' +
            ", underGround='" + underGround + '\'' +
            ", roadMainNum='" + roadMainNum + '\'' +
            ", roadSubNum='" + roadSubNum + '\'' +
            ", siGoonGooBuildingName='" + siGoonGooBuildingName + '\'' +
            ", zipCode='" + zipCode + '\'' +
            ", apartComplex='" + apartComplex + '\'' +
            ", eubMyunDongSerialNumber='" + eubMyunDongSerialNumber + '\'' +
            ", latitude='" + latitude + '\'' +
            ", longitude='" + longitude + '\'' +
            '}';
    }
}
