package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.EmailCertification;
import com.beautify_project.bp_mysql.entity.adapter.EmailCertificationAdapter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailCertificationAdapterRepository {

    private final EmailCertificationRepository repository;

    public EmailCertificationAdapter findByEmail(final String email) {
        return EmailCertificationAdapter.toAdapter(repository.findByEmail(email));
    }

    public List<EmailCertificationAdapter> findByEmailIn(final Collection<String> emails) {
        final List<EmailCertification> results = repository.findByEmailIn(emails);
        return results.stream().map(EmailCertificationAdapter::toAdapter).toList();
    }

    public void deleteAllByEmails(final Set<String> emails) {
        repository.deleteAllById(emails);
    }

    @Transactional
    public void saveAll(final Collection<EmailCertificationAdapter> adapters) {
        final List<EmailCertification> entitiesToSave = adapters.stream()
            .map(EmailCertificationAdapter::toEntity).toList();
        repository.saveAllAndFlush(entitiesToSave);
    }
}
