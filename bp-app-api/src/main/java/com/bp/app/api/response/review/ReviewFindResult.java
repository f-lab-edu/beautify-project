package com.bp.app.api.response.review;

public record ReviewFindResult(
    Long id,
    String rate,
    String content,
    Long reviewRegisteredDate,
    String memberEmail,
    String memberName,
    Long operationId,
    String operationName,
    Long shopId,
    String shopName,
    Long reservationId,
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


