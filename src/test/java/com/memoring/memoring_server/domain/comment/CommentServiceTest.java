package com.memoring.memoring_server.domain.comment;

import com.memoring.memoring_server.domain.comment.dto.CommentCreateRequest;
import com.memoring.memoring_server.domain.comment.exception.CommentNotFoundException;
import com.memoring.memoring_server.domain.diary.Diary;
import com.memoring.memoring_server.domain.diary.Emotion;
import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private com.memoring.memoring_server.domain.diary.DiaryService diaryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    @DisplayName("댓글을 생성한다")
    @Test
    void createComment() {
        User user = createUser("tester");
        Diary diary = createDiary(user);
        Comment comment = Comment.create(diary, user, "hello");
        ReflectionTestUtils.setField(comment, "id", 1L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(diaryService.getDiaryById(1L)).willReturn(diary);
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        var response = commentService.createComment(new CommentCreateRequest(1L, "hello"), "tester");

        assertThat(response.commentId()).isEqualTo(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @DisplayName("댓글 삭제 시 작성자가 아니면 예외를 던진다")
    @Test
    void deleteCommentFailsWhenNotOwner() {
        User owner = createUser("owner");
        User other = createUser("other");
        Comment comment = Comment.create(createDiary(owner), owner, "hi");

        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(1L, other.getUsername()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @DisplayName("존재하지 않는 댓글 삭제 시 예외를 던진다")
    @Test
    void deleteCommentFailsWhenMissing() {
        given(commentRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.deleteComment(1L, "tester"))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @DisplayName("작성자는 댓글을 삭제할 수 있다")
    @Test
    void deleteComment() {
        User owner = createUser("owner");
        Comment comment = Comment.create(createDiary(owner), owner, "hi");

        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        commentService.deleteComment(1L, owner.getUsername());

        verify(commentRepository).delete(comment);
    }

    private User createUser(String username) {
        User user = User.create("nick", username, "pass");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Diary createDiary(User user) {
        Mission mission = Mission.create("mission");
        var memory = com.memoring.memoring_server.domain.memory.Memory.create(user, "mem");
        return Diary.create(user, memory, mission, "content", Emotion.HAPPY);
    }
}
