package com.memoring.memoring_server.domain.comment;

import com.memoring.memoring_server.domain.comment.dto.CommentCreateRequestDto;
import com.memoring.memoring_server.domain.comment.dto.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @Override
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentCreateRequestDto dto) {
        return ResponseEntity.ok(commentService.createComment(dto));
    }

    @Override
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
