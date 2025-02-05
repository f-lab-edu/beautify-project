package com.bp.domain.mysql.entity.enumerated;

import com.bp.domain.mysql.exception.EnumMismatchException;

public enum ReservationStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELED;

    public static ReservationStatus from(final String input) {
        try {
            return valueOf(input.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new EnumMismatchException(ReservationStatus.class.getName(), input);
        }
    }
}
