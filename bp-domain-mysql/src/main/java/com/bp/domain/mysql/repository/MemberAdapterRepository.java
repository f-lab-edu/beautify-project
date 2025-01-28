package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.annotation.ReadOnlyTransactional;
import com.bp.domain.mysql.entity.Member;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
