package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.EmailCertification;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailCertificationAdapterRepository {

    private final EmailCertificationRepository repository;

    public EmailCertification findByEmail(final String email) {
        return repository.findByEmail(email);
    }

    public List<EmailCertification> findByEmailsIn(final Collection<String> emails) {
        return repository.findByEmailIn(emails);
    }

    @Transactional
    public void deleteAllByEmails(final Collection<String> emails) {
        repository.deleteAllByEmailInBatch(emails);
    }

    @Transactional
    public void saveAll(final Collection<EmailCertification> emailCertifications) {
        repository.saveAll(emailCertifications);
    }
}
