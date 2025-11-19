package com.memoring.memoring_server.domain.comment;

import com.memoring.memoring_server.domain.comment.dto.CommentCreateRequestDto;
import com.memoring.memoring_server.domain.comment.dto.CommentResponseDto;
import com.memoring.memoring_server.domain.diary.Diary;
import com.memoring.memoring_server.domain.diary.DiaryRepository;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserRepository;
import com.memoring.memoring_server.global.exception.CommentNotFoundException;
import com.memoring.memoring_server.global.exception.DiaryNotFoundException;
import com.memoring.memoring_server.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDto createComment(CommentCreateRequestDto dto) {
        Diary diary = diaryRepository.findById(dto.diaryId())
                .orElseThrow(DiaryNotFoundException::new);
        User user = userRepository.findById(dto.userId())
                .orElseThrow(UserNotFoundException::new);

        Comment comment = Comment.create(diary, user, dto.content());
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(
                savedComment.getId(),
                diary.getId(),
                user.getId(),
                savedComment.getContent(),
                savedComment.getCreatedAt()
        );
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        commentRepository.delete(comment);
    }
}