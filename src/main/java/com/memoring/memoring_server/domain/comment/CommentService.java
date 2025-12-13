package com.memoring.memoring_server.domain.comment;

import com.memoring.memoring_server.domain.comment.dto.CommentCreateRequest;
import com.memoring.memoring_server.domain.comment.dto.CommentResponse;
import com.memoring.memoring_server.domain.diary.Diary;
import com.memoring.memoring_server.domain.diary.DiaryService;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.domain.comment.exception.CommentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryService diaryService;
    private final UserService userService;

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, String username) {
        User user = userService.getUserByUsername(username);
        Diary diary = diaryService.getDiaryById(request.diaryId());

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
