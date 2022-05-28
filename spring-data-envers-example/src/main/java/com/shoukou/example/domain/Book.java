package com.shoukou.example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

// 이력을 관리하고 싶은 엔티티, 혹은 특정 attribute에 @Audited를 붙임.
@Audited
@Getter
@NoArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @Column
    private LocalDateTime publishedAt;

    @Column
    private LocalDateTime modifiedAt;

    @Builder
    public Book(String title) {
        this.title = title;
        this.publishedAt = LocalDateTime.now();
        this.modifiedAt = this.publishedAt;
    }

    public String modifyTitle(String newTitle) {
        this.title = newTitle;
        this.modifiedAt = LocalDateTime.now();

        return this.title;
    }
}
