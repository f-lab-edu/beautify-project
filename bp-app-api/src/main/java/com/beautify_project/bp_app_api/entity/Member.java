package com.beautify_project.bp_app_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_email")
    private String email;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_contact")
    private String contact;

    @Column(name = "member_registered")
    private Long registered;

    @Builder
    private Member(final String email, final String name, final String contact,
        final Long registered) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.registered = registered;
    }
}
