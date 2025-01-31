package com.bp.domain.mysql.entity.listener;

import com.bp.domain.mysql.entity.BaseEntity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.PrePersist;
import java.time.Clock;

@EntityListeners(value = CustomEntityListener.class)
public class CustomEntityListener {

    @PrePersist // insert 메서드 호출되기 전에 실행
    public void prePersist(BaseEntity baseEntity) {
        baseEntity.prePersist(Clock.systemDefaultZone());
    }
}
