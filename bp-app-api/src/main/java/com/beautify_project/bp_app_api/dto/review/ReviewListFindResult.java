package com.beautify_project.bp_app_api.dto.review;

public record ReviewListFindResult(
    String id,
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


