package com.memoring.memoring_server.domain.comment;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.diary.Diary;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    public static Comment create(Diary diary, User user, String content) {
        Comment comment = new Comment();
        comment.diary = diary;
        comment.user = user;
        comment.content = content;
        return comment;
    }
}
