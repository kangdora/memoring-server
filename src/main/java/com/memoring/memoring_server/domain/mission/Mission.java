package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "missions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false, length = 50)
    private String title;

    public static Mission create(User user, String title, String content) {
        Mission mission = new Mission();
        mission.user = user;
        mission.title = title;
        mission.content = content;
        return mission;
    }
}
