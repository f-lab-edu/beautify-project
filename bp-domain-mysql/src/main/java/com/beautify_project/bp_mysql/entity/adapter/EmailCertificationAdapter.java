package com.beautify_project.bp_mysql.entity.adapter;

import com.beautify_project.bp_mysql.entity.EmailCertification;
import lombok.Getter;

@Getter
public class EmailCertificationAdapter {

    private final String email;
    private final String certificationNumber;
    private final Long registeredTime;

    private EmailCertificationAdapter(final String email, final String certificationNumber,
        final Long registeredTime) {
        this.email = email;
        this.certificationNumber = certificationNumber;
        this.registeredTime = registeredTime;
    }

    public static EmailCertification toEntity(final EmailCertificationAdapter adapter) {
        return EmailCertification.of(adapter.getEmail(), adapter.getCertificationNumber(),
            adapter.registeredTime);
    }

    public static EmailCertificationAdapter toAdapter(final EmailCertification entity) {
        return new EmailCertificationAdapter(entity.getEmail(), entity.getCertificationNumber(),
            entity.getRegisteredTime());
    }

    public static EmailCertificationAdapter of(final String email, final String certificationNumber,
        final Long registeredTime) {
        return new EmailCertificationAdapter(email, certificationNumber, registeredTime);
    }
}
