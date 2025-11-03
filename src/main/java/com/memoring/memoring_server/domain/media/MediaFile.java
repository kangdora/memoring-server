package com.memoring.memoring_server.domain.media;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "media_files")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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

    @Column(length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Long sizeBytes;

    /** hex 64 */
    @Column(nullable = false, length = 64)
    private String sha256;

    // image
    private Integer width;
    private Integer height;

    // audio
    private Integer durationMs;
}
