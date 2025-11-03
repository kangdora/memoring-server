package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="diary_comments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiaryComment extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private DiaryEntry diaryEntry;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private User author;

    @ManyToOne(fetch=FetchType.LAZY)
    private DiaryComment parentComment;

    @Column(columnDefinition="text", nullable=false)
    private String content;
}
