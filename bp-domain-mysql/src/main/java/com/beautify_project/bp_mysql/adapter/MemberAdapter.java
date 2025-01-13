package com.beautify_project.bp_mysql.adapter;


import com.beautify_project.bp_mysql.entity.Member;
import com.beautify_project.bp_mysql.entity.enumerated.AuthType;
import com.beautify_project.bp_mysql.entity.enumerated.MemberStatus;
import com.beautify_project.bp_mysql.entity.enumerated.UserRole;
import lombok.Getter;

@Getter
public class MemberAdapter {

    private String email;
    private String password;
    private String name;
    private String contact;
    private AuthType authType;
    private UserRole role;
    private MemberStatus memberStatus;
    private Long registeredTime;

    private MemberAdapter(final String email, final String password, final String name,
        final String contact, final AuthType authType,
        final UserRole role, final MemberStatus memberStatus, final Long registeredTime) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.authType = authType;
        this.role = role;
        this.memberStatus = memberStatus;
        this.registeredTime = registeredTime;
    }

    public static MemberAdapter createNewMemberAdapter(final String email, final String password,
        final String name, final String contact, final AuthType authType, final UserRole userRole,
        final MemberStatus memberStatus, final Long registeredTime) {

        return new MemberAdapter(email, password, name, contact, authType, userRole, memberStatus,
            registeredTime);
    }

    public static MemberAdapter from(final Member foundMember) {
        return new MemberAdapter(foundMember.getEmail(), foundMember.getPassword(),
            foundMember.getName(),
            foundMember.getContact(), foundMember.getAuthType(), foundMember.getRole(),
            foundMember.getStatus(),
            foundMember.getRegisteredTime());
    }
}
