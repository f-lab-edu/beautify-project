package com.bp.domain.mysql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operator")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Operator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operator_id")
    private Long id;

    @Column(name = "operator_mail", nullable = false, unique = true)
    private String email;

    @Column(name = "operator_password")
    private String password;

    @Column(name = "operator_name", nullable = false)
    private String name;

    @Column(name = "operator_contact", nullable = false)
    private String contact;

    private Operator(final String email, final String password, final String name,
        final String contact) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contact = contact;
    }

    public static Operator newOperator(final String operatorMail, final String password,
        final String name, final String contact) {
        return new Operator(operatorMail, password, name, contact);
    }
}
