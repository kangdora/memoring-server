package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.message.Emotion;
import com.memoring.memoring_server.domain.memory.Memory;
import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "diaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "memory_id", nullable = false)
    private Memory memory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Emotion mood;

    public static Diary create(
            User user,
            Memory memory,
            Mission mission,
            String content,
            Emotion mood
    ) {
        Diary diary = new Diary();
        diary.user = user;
        diary.memory = memory;
        diary.mission = mission;
        diary.content = content;
        diary.mood = mood;
        return diary;
    }
}
