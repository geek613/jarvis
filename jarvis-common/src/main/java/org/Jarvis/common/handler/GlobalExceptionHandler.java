package org.Jarvis.common.handler;

import com.alibaba.nacos.api.model.v2.Result;
import lombok.extern.slf4j.Slf4j;
import org.Jarvis.common.exception.BusinessException;
import org.Jarvis.common.result.JarvisResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.annotation.Annotation;

@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler{
    //处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public JarvisResult<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return JarvisResult.error(e.getMessage());
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public JarvisResult<?> handleResourceException(NoResourceFoundException e) {
        log.warn("资源服务异常: {}", e.getMessage());
        return JarvisResult.error("资源未知错误，请联系开发人员");
    }

    // 兜底的未知系统异常
    @ExceptionHandler(Exception.class)
    public JarvisResult<?> handleException(Exception e) {
        log.error("系统未知异常: ", e);
        return JarvisResult.error(e.getMessage());
    }

}
