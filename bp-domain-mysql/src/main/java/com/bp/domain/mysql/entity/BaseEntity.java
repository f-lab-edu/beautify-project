package com.bp.domain.mysql.entity;

import com.bp.domain.mysql.entity.listener.CustomEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
@EntityListeners(CustomEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public void prePersist(Clock clock) {
        LocalDateTime now = LocalDateTime.now(clock);
        this.createdDate = now;
        this.lastModifiedDate = now;
    }

    public void preRemove() {
        createdDate = null;
        lastModifiedDate = null;
    }
}
