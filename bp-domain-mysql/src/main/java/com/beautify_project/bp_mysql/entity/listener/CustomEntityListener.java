package com.beautify_project.bp_mysql.entity.listener;

import com.beautify_project.bp_mysql.entity.BaseEntity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.PrePersist;

@EntityListeners(value = CustomEntityListener.class)
public class CustomEntityListener {

    @PrePersist // insert 메서드 호출되기 전에 실행
    public void prePersist(BaseEntity baseEntity) {
        baseEntity.prePersist();
    }

}
