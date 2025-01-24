package com.bp.domain.mysql.entity;

import com.bp.domain.mysql.entity.listener.CustomEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(value = CustomEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailCertification extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "certification_number")
    private String certificationNumber;

    @Column(name = "email_certification_registered")
    private Long registeredTime;

    private EmailCertification(final String email, final String certificationNumber,
        final Long registeredTime) {
        this.email = email;
        this.certificationNumber = certificationNumber;
        this.registeredTime = registeredTime;
    }

    public static EmailCertification of(final String email, final String certificationNumber, final Long timeToRegister) {
        return new EmailCertification(email, certificationNumber, timeToRegister);
    }
}

