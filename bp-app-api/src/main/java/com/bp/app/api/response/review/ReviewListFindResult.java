package com.bp.app.api.response.review;

public record ReviewListFindResult(
    Long id,
    String rate,
    Long registeredDate,
    String memberName,
    String operationName,
    Long reservationDate
){

    @Override
    public String toString() {
        return "ReviewListFindResult{" +
            "id='" + id + '\'' +
            ", rate='" + rate + '\'' +
            ", registeredDate=" + registeredDate +
            ", memberName='" + memberName + '\'' +
            ", operationName='" + operationName + '\'' +
            ", reservationDate=" + reservationDate +
            '}';
    }
}


