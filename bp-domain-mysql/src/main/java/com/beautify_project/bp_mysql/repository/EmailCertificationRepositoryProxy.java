package com.beautify_project.bp_mysql.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class EmailCertificationRepositoryProxy {

    private final EmailCertificationRepository repository;
}
