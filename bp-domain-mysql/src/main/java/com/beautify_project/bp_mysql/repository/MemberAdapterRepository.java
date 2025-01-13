package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.adapter.MemberAdapter;
import com.beautify_project.bp_mysql.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAdapterRepository {

    private final MemberRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public MemberAdapter save(final MemberAdapter memberAdapter) {
        repository.save(com.beautify_project.bp_mysql.entity.Member.from(memberAdapter));
        return memberAdapter;
    }

    public boolean existsByEmail(final String email) {
        return repository.existsById(email);
    }

    public MemberAdapter findByEmail(final String email) {
        final Member foundMember = repository.findByEmail(email);
        if (foundMember == null) {
            throw new EntityNotFoundException("{} 에 해당하는 사용자를 찾지 못했습니다");
        }
        return MemberAdapter.from(foundMember);
    }

    @Transactional
    public void truncate() {
        repository.truncate();
    }

}
