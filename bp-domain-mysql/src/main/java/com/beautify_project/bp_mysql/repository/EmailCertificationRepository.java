package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.EmailCertification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailCertificationRepository extends JpaRepository<EmailCertification, String> {

    EmailCertification findByEmail(final String email);

    void deleteByEmail(@Param("email") final String email);

    List<EmailCertification> findByEmailIn(final Collection<String> emails);
}
