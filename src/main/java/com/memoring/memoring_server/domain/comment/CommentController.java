package com.memoring.memoring_server.domain.comment;

import com.memoring.memoring_server.domain.comment.dto.CommentCreateRequest;
import com.memoring.memoring_server.domain.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController implements CommentApi {

    private final CommentService commentService;

    @Override
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(commentService.createComment(request, userDetails.getUsername()));
    }

    @Override
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
