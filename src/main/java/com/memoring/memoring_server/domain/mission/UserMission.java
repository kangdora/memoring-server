package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_missions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    public static UserMission create(User user, Mission mission) {
        UserMission userMission = new UserMission();
        userMission.user = user;
        userMission.mission = mission;
        return userMission;
    }

    public void updateMission(Mission mission) {
        this.mission = mission;
    }

    public void clearMission() {
        this.mission = null;
    }
}