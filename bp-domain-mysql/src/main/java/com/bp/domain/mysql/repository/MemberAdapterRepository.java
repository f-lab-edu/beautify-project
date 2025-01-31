package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Member;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberAdapterRepository {

    private final MemberRepository defaultRepository;

    @ReadOnlyTransactional
    public Optional<Member> findByEmail(final String memberEmailToFind) {
        return defaultRepository.findByEmail(memberEmailToFind);
    }

    @ReadOnlyTransactional
    public boolean existsByEmail(final String memberEmail) {
        return defaultRepository.existsByEmail(memberEmail);
    }

    public Member save(final Member memberToSave) {
        return defaultRepository.save(memberToSave);
    }

    @Transactional
    public void deleteAllInBatch() {
        defaultRepository.deleteAllInBatch();
    }

    public Member saveAndFlush(final Member memberToSave) {
        return defaultRepository.saveAndFlush(memberToSave);
    }

    public long count() {
        return defaultRepository.count();
    }
}
