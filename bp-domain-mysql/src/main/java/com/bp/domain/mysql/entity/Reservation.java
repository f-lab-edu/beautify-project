package com.bp.domain.mysql.entity;

import com.bp.domain.mysql.entity.enumerated.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "reservation",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "reservation_unique_key",
            columnNames = {"reservation_start_date", "operator_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(name = "reservation_start_date")
    private Long startDate;

    @Column(name = "reservation_end_date")
    private Long endDate;

    @Column(name = "reservation_member_email")
    private String requestedMemberEmail;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "operation_id")
    private Long operationId;

    @Column(name = "operator_id")
    private Long operatorId; // 시술 선생님

    @Column(name = "reservation_status")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Reservation(final Long startDate, final Long endDate, final String requestedMemberEmail,
        final Long shopId, final Long operationId, final Long operatorId, final ReservationStatus status) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestedMemberEmail = requestedMemberEmail;
        this.shopId = shopId;
        this.operationId = operationId;
        this.operatorId = operatorId;
        this.status = status;
    }

    public static Reservation newReservation(final Long startDate, final Long endDate,
        final String memberEmail, final Long shopId, final Long operationId, final Long operatorId) {
        return new Reservation(startDate, endDate, memberEmail, shopId, operationId, operatorId,
            ReservationStatus.PENDING);
    }

    public void changeStatus(final ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + id +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", memberEmail='" + requestedMemberEmail + '\'' +
            ", shopId=" + shopId +
            ", operationId=" + operationId +
            ", operatorId='" + operatorId + '\'' +
            '}';
    }
}
