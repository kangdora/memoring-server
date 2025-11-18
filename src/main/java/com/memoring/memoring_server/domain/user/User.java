package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Integer quizProgress;

    @Column(nullable = false)
    private Integer quizStroke;

    @Column(nullable = false)
    private Long coin;

    public static User create(String nickname, String username, String password) {
        User user = new User();
        user.nickname = nickname;
        user.username = username;
        user.password = password;
        user.quizProgress = 0;
        user.quizStroke = 0;
        user.coin = 0L;
        return user;
    }
}
