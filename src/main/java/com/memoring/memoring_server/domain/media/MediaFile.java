package com.memoring.memoring_server.domain.media;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="media_files")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MediaFile extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaKind kind; // IMAGE / AUDIO

    /** S3 object key */
    @Column(nullable = false, length = 1024)
    private String storageKey;

    /// 예시로 application/json 같은 타입 판단할때 씀.
    @Column(length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Long sizeBytes;

    /** 동일한 영상인지 해시값으로 판단 */
    @Column(nullable = false, length = 64)
    private String sha256;

    // 사진
    private Integer width;
    private Integer height;

    // 녹음
    private Integer durationMs;
}
