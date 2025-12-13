package com.memoring.memoring_server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATE_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 로그인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 누군가가 사용하는 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.CONFLICT, "비밀번호가 같지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),

    MEMORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 기억을 찾을 수 없습니다."),
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 미션을 찾을 수 없습니다."),
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 일기를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글을 찾을 수 없습니다."),
    INVALID_MISSION_SELECTION(HttpStatus.BAD_REQUEST, "선택한 미션을 찾을 수 없습니다."),
    DIARY_OWNERSHIP_MISMATCH(HttpStatus.BAD_REQUEST, "미션과 메모리의 소유자가 일치하지 않습니다."),
    INVALID_USERNAME_CONFLICT(HttpStatus.BAD_REQUEST, "사용자 ID 형식을 확인해주세요."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST,"비밀번호 조건을 확인해 주세요."),

    QUIZ_SET_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 퀴즈 세트를 찾을 수 없습니다."),
    QUIZ_SET_LOCKED(HttpStatus.FORBIDDEN, "아직 열리지 않은 퀴즈 세트입니다."),
    QUIZ_ALREADY_TAKEN_TODAY(HttpStatus.BAD_REQUEST, "오늘 이미 해당 퀴즈를 완료했습니다."),
    QUIZ_ANSWER_REQUIRED(HttpStatus.BAD_REQUEST, "퀴즈 답안이 필요합니다."),
    QUIZ_GRADING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "퀴즈 답안을 채점하는 데 실패했습니다."),

    INVALID_ADMIN_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 관리자 요청입니다."),

    FILE_NAME_EMPTY(HttpStatus.BAD_REQUEST, "파일 이름이 비어 있습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파일을 찾을 수 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생하였습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
