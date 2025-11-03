package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.media.MediaFile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="diary_media",
        uniqueConstraints=@UniqueConstraint(name="uk_diary_media",
                columnNames={"diary_entry_id","media_file_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiaryMedia extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private DiaryEntry diaryEntry;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private MediaFile mediaFile;
}
