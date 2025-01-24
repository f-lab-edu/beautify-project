package com.bp.domain.mysql.entity;

import com.bp.domain.mysql.entity.enumerated.AuthType;
import com.bp.domain.mysql.entity.enumerated.MemberStatus;
import com.bp.domain.mysql.entity.enumerated.UserRole;
import com.bp.domain.mysql.entity.listener.CustomEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "member")
@EntityListeners(CustomEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements Persistable<String> {

    @Id
    @Column(name = "member_email")
    private String email;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_contact")
    private String contact;

    @Column(name = "member_auth_type")
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "member_account_status")
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(name = "member_registered_time")
    private Long registeredTime;

    private Member(final String email, final String password, final String name,
        final String contact, final AuthType authType,
        final UserRole role, final MemberStatus status, final Long registeredTime) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.authType = authType;
        this.role = role;
        this.status = status;
        this.registeredTime = registeredTime;
    }

    public static Member createNewMember(final String email, final String password,
        final String name, final String contact, final AuthType authType, final UserRole userRole,
        final MemberStatus memberStatus, final Long registeredTime) {

        return new Member(email, password, name, contact, authType, userRole, memberStatus,
            registeredTime);
    }

    @Override
    public String getId() {
        return getEmail();
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }

}
