package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.EmailCertification;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class EmailCertificationAdapterRepository {

    private final EmailCertificationRepository defaultRepository;

    @ReadOnlyTransactional
    public EmailCertification findByEmail(final String email) {
        return defaultRepository.findByEmail(email);
    }

    @ReadOnlyTransactional
    public List<EmailCertification> findByEmailsIn(final Collection<String> emails) {
        return defaultRepository.findByEmailIn(emails);
    }

    @Transactional
    public void deleteAllByEmails(final Collection<String> emails) {
        defaultRepository.deleteAllByEmailInBatch(emails);
    }

    @Transactional
    public void deleteAllByEmailInBatch(final Collection<String> emails) {
        defaultRepository.deleteAllByEmailInBatch(emails);
    }

    @Transactional
    public void saveAll(final Collection<EmailCertification> emailCertifications) {
        defaultRepository.saveAll(emailCertifications);
    }

    public void delete(final EmailCertification emailCertificationToDelete) {
        defaultRepository.delete(emailCertificationToDelete);
    }

    @Transactional
    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }

    public EmailCertification saveAndFlush(final EmailCertification emailCertificationToSave) {
        return defaultRepository.saveAndFlush(emailCertificationToSave);
    }
}
