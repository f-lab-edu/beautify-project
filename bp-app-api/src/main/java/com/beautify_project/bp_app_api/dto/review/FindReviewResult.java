package com.beautify_project.bp_app_api.dto.review;

import lombok.Builder;

@Builder
public record FindReviewResult(
    String id,
    String rate,
    Long registeredDate,
    Member member,
    Operation operation
){

    public record Member (String id, String name) {}

    public record Operation (String id, String name, Long date) {}

    @Override
    public String toString() {
        return "FindReviewResponse{" +
            "id='" + id + '\'' +
            ", rate='" + rate + '\'' +
            ", registeredDate=" + registeredDate +
            ", member=" + member +
            ", operation=" + operation +
            '}';
    }
}


