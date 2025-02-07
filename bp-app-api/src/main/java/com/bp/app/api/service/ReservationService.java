package com.bp.app.api.service;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.producer.ReservationEventProducer;
import com.bp.app.api.request.reservation.ReservationRegistrationRequest;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.ResponseMessage;
import com.bp.app.api.response.reservation.ReservationConfirmResponse;
import com.bp.app.api.response.reservation.ReservationDetailFindResult;
import com.bp.app.api.response.reservation.ReservationRegistrationResult;
import com.bp.domain.mysql.entity.Member;
import com.bp.domain.mysql.entity.Operation;
import com.bp.domain.mysql.entity.Operator;
import com.bp.domain.mysql.entity.Reservation;
import com.bp.domain.mysql.entity.enumerated.ReservationStatus;
import com.bp.domain.mysql.repository.ReservationAdapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationAdapterRepository reservationAdapterRepository;
    private final ReservationEventProducer eventProducer;
    private final MemberService memberService;
    private final OperatorService operatorService;
    private final OperationService operationService;

    public Reservation findReservationById(final Long reservationId) {
        return reservationAdapterRepository.findById(reservationId)
            .orElseThrow(() -> new BpCustomException(ErrorCode.RS001));
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseMessage registerReservationAndProduceEvent(
        final ReservationRegistrationRequest registrationRequest, final String requestedMemberEmail) {

        final Reservation registeredAsPendingReservation = reservationAdapterRepository.save(
            createReservationEntityFromRegistrationRequest(registrationRequest, requestedMemberEmail));

        eventProducer.publishReservationEvent(registeredAsPendingReservation, requestedMemberEmail);

        return ResponseMessage.createResponseMessage(
            new ReservationRegistrationResult(registeredAsPendingReservation.getId()));
    }

    public Reservation createReservationEntityFromRegistrationRequest(
        final ReservationRegistrationRequest request, final String requestedMemberEmail) {
        return Reservation.newReservation(request.startDate(), request.endDate(), requestedMemberEmail,
            request.shopId(), request.operationId(), request.operatorId());
    }

    public ResponseMessage confirm(final Long reservationId) {
        final Reservation foundReservation = findReservationById(reservationId);
        foundReservation.changeStatus(ReservationStatus.CONFIRMED);
        reservationAdapterRepository.save(foundReservation);

        return ResponseMessage.createResponseMessage(new ReservationConfirmResponse(
            foundReservation.getId()));
    }

    private ReservationDetailFindResult findReservationDetail(final Long reservationId) {
        final Reservation foundReservation = findReservationById(reservationId);

        final Member foundMember = memberService.findMemberByEmailOrElseThrow(
            foundReservation.getRequestedMemberEmail());

        final Operator foundOperator = operatorService.findOperatorById(
            foundReservation.getOperatorId());

        final Operation foundOperation = operationService.findOperationById(
            foundReservation.getOperationId());

        return new ReservationDetailFindResult(reservationId,
            foundReservation.getStartDate(), foundReservation.getEndDate(),
            foundMember.getEmail(), foundMember.getName(),
            foundOperator.getEmail(), foundOperator.getName(),
            foundOperation.getName());
    }

    // TODO: 예약 상태별 리스트 조회
}
