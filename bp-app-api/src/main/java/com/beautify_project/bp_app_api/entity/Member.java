package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.dto.member.UserRoleMemberRegistrationRequest;
import com.beautify_project.bp_app_api.enumeration.AuthType;
import com.beautify_project.bp_app_api.enumeration.MemberStatus;
import com.beautify_project.bp_app_api.enumeration.Role;
import com.beautify_project.bp_app_api.listener.CustomEntityListener;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@EntityListeners(CustomEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements Persistable<String> {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    public static final String DEFAULT_PASSWORD = "DEFAULT_PASSWORD";

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
    private Role role;

    @Column(name = "member_account_status")
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(name = "member_registered_time")
    private Long registeredTime;

    public Member(final String email, final String password, final String name,
        final String contact, final AuthType authType,
        final Role role, final MemberStatus status, final Long registeredTime) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.authType = authType;
        this.role = role;
        this.status = status;
        this.registeredTime = registeredTime;
    }

    public static Member createSelfAuthMember(UserRoleMemberRegistrationRequest request) {
        return new Member(request.email(), encryptPassword(request.password()), request.name(),
            request.contact(), AuthType.BP, Role.USER, MemberStatus.ACTIVE,
            System.currentTimeMillis());
    }

    public static Member createThirdPartyAuthMember(final String email, AuthType authType) {
        return new Member(email, encryptPassword(DEFAULT_PASSWORD), null, null, authType, Role.USER,
            MemberStatus.NEED_TO_SIGN_UP, System.currentTimeMillis());
    }

    private static String encryptPassword(final String plainPassword) {
        return PASSWORD_ENCODER.encode(plainPassword);
    }

    public boolean passwordMatches(final String inputPassword) {
        return PASSWORD_ENCODER.matches(password, inputPassword);
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
