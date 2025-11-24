package com.memoring.memoring_server.domain.comment;

import com.memoring.memoring_server.domain.comment.dto.CommentCreateRequest;
import com.memoring.memoring_server.domain.comment.dto.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "댓글", description = "댓글 작성 및 삭제 API")
public interface CommentApi {

    @Operation(summary = "댓글 생성", description = "새로운 댓글을 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "404", description = "대상 일기 또는 사용자를 찾을 수 없음")
    })
    ResponseEntity<CommentResponse> createComment(
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(summary = "댓글 삭제", description = "댓글 ID로 댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 댓글이 존재하지 않음")
    })
    ResponseEntity<Void> deleteComment(
            Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    );
}
