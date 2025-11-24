package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diary_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryImage extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text", nullable = false)
    private String s3key;

    @Column(nullable = false)
    private Long sizeBytes;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diary_id", nullable = false, unique = true)
    private Diary diary;

    public static DiaryImage create(String s3key, Long sizeBytes, Diary diary) {
        DiaryImage image = new DiaryImage();
        image.s3key = s3key;
        image.sizeBytes = sizeBytes;
        image.diary = diary;
        return image;
    }

    public void update(String s3key, Long sizeBytes) {
        this.s3key = s3key;
        this.sizeBytes = sizeBytes;
    }
}
