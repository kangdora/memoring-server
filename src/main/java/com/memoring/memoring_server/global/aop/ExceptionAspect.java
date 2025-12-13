package com.memoring.memoring_server.global.aop;

import com.memoring.memoring_server.global.exception.CustomException;
import com.memoring.memoring_server.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionAspect {

    @AfterThrowing(
            pointcut = "within(com.memoring.memoring_server.domain..*)",
            throwing = "ex"
    )
    public void logException(Throwable ex) {
        if (ex instanceof CustomException ce) {
            ErrorCode errorCode = ce.getErrorCode();

            if (errorCode.getLogLevel() == LogLevel.ERROR) {
                log.error("[{}] {}", errorCode.name(), errorCode.getMessage(), ex);
            } else {
                log.warn("[{}] {}", errorCode.name(), errorCode.getMessage());
            }

        } else {
            log.error("[예상되지 않은 오류]", ex);
        }
    }
}
