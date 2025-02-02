package com.bp.domain.mysql.repository;

import com.bp.domain.mysql.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(final String memberEmail);

    boolean existsByEmail(final String email);
}
