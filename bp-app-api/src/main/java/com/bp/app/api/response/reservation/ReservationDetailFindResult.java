package com.bp.app.api.response.reservation;

public record ReservationDetailFindResult(Long reservationId, Long startDate, Long endDate,
                                          String memberEmail, String memberName, String operatorEmail,
                                          String operatorName, String operationName) {

}
