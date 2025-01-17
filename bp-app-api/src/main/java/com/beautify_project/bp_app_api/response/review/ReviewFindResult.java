package com.beautify_project.bp_app_api.response.review;

public record ReviewFindResult(
    String id,
    String rate,
    String content,
    Long reviewRegisteredDate,
    String memberEmail,
    String memberName,
    String operationId,
    String operationName,
    Long shopId,
    String shopName,
    String reservationId,
    Long reservationDate
) {

    @Override
    public String toString() {
        return "FindReviewResult{" +
            "id='" + id + '\'' +
            ", rate='" + rate + '\'' +
            ", content='" + content + '\'' +
            ", reviewRegisteredDate=" + reviewRegisteredDate +
            ", memberEmail='" + memberEmail + '\'' +
            ", memberName='" + memberName + '\'' +
            ", operationId='" + operationId + '\'' +
            ", operationName='" + operationName + '\'' +
            ", shopId='" + shopId + '\'' +
            ", shopName='" + shopName + '\'' +
            ", reservationDate=" + reservationDate +
            '}';
    }
}


