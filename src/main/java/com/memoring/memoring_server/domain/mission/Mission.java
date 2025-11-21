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

    @Column(nullable = false)
    private String content;

    public static Mission create(String content) {
        Mission mission = new Mission();
        mission.content = content;
        return mission;
    }
}
