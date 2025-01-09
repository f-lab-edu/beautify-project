package com.beautify_project.bp_app_api.repository;

import com.beautify_project.bp_app_api.entity.EmailCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailCertificationRepository extends JpaRepository<EmailCertification, String> {

    EmailCertification findByEmail(final String email);

    void deleteByEmail(@Param("email") final String email);
}
