package com.memoring.memoring_server.domain.message;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text", nullable = false)
    private String s3key;

    @Column(nullable = false)
    private Long sizeBytes;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    public static Image create(Message message, String s3key, Long sizeBytes) {
        Image image = new Image();
        image.message = message;
        image.s3key = s3key;
        image.sizeBytes = sizeBytes;
        return image;
    }
}
