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

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer quizProgress;

    @Column(nullable = false)
    private Integer quizStroke;

    @Column(nullable = false)
    private Long coin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserType userType;

    @Embedded
    private Address address;

    public static User create(String nickname, String username, String password, Role role, UserType userType, Address address) {
        User user = new User();
        user.nickname = nickname;
        user.username = username;
        user.password = password;
        user.quizProgress = 0;
        user.quizStroke = 0;
        user.coin = 0L;
        user.role = role;
        user.userType = userType;
        user.address = address;
        return user;
    }

    public void addCoin(Long count) {
        this.coin += count;
    }

    public void addQuizProgress() {
        this.quizProgress++;
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public boolean isCaregiver() {
        return UserType.CAREGIVER.equals(this.userType);
    }
}
