package com.memoring.memoring_server.domain.comment;

import com.memoring.memoring_server.domain.comment.dto.CommentCreateRequest;
import com.memoring.memoring_server.domain.comment.dto.CommentResponse;
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
    public CommentResponse createComment(CommentCreateRequest request, String username) {
        Diary diary = diaryRepository.findById(request.diaryId())
                .orElseThrow(DiaryNotFoundException::new);
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Comment comment = Comment.create(diary, user, request.content());
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponse(
                savedComment.getId(),
                diary.getId(),
                user.getId(),
                savedComment.getContent(),
                savedComment.getCreatedAt()
        );
    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getUser().getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("댓글 삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }
}
