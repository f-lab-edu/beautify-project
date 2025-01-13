package com.beautify_project.bp_mysql.repository;

import com.beautify_project.bp_mysql.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Member findByEmail(final String email);

    @Modifying
    @Query(value = "truncate member", nativeQuery = true)
    void truncate(); // IMPORTANT: 테스트 코드에서만 사용
}
