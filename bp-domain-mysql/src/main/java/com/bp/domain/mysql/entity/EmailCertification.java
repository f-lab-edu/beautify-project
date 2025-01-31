package com.bp.domain.mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_certification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailCertification extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "certification_number")
    private String certificationNumber;

    private EmailCertification(final String email, final String certificationNumber) {
        this.email = email;
        this.certificationNumber = certificationNumber;
    }

    public static EmailCertification newEmailCertification(final String email,
        final String certificationNumber) {
        return new EmailCertification(email, certificationNumber);
    }
}

