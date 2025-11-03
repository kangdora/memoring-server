package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name="diary_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiaryEntry extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer weekOfYear;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Visibility visibility = Visibility.CARETEAM_ONLY;

    @OneToMany(mappedBy = "diaryEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryComment> comments;
}
