package com.beautify_project.bp_app_api.entity;

import com.beautify_project.bp_app_api.utils.UUIDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @Column(name = "review_id")
    private String id;

    @Column(name = "review_rate")
    private String rate;

    @Column(name = "review_content")
    private String content;

    private Long registered;

    private Review(final String rate, final String content, final Long registered) {
        this.id = UUIDGenerator.generate();
        this.rate = rate;
        this.content = content;
        this.registered = registered;
    }

    public static Review of(final String rate, final String content, final Long registered) {
        return new Review(rate, content, registered);
    }
}
