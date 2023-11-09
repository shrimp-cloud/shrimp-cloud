package com.wkclz.spring.rest;

import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

//全局异常捕捉处理
@RestControllerAdvice
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result httpHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e,
            HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        printErrorLog(request, response, status);
        return new Result(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodHandler(HttpRequestMethodNotSupportedException e,
           HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        printErrorLog(request, response, status);
        return new Result(status.value(), status.getReasonPhrase());
    }


    @ExceptionHandler(BizException.class)
    public Result bizExceptionHandler(BizException e){
        logger.error(e.getMessage(), e);
        return new Result(-1, e.getMessage());
    }


    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception e) {
        logger.error(e.getMessage(), e);

        BizException bizException = getBizException(e);
        if (bizException != null){
            return Result.error(bizException);
        }

        String message = e.getMessage();
        if (message == null || "".equals(message.trim()) || "null".equals(message)){
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out));
            message = out.toString();
        }
        return Result.error(message);
    }


    /**
     * Throwable 找 BizException，找二级原因
     * @param throwable
     * @return
     */
    private static BizException getBizException(Throwable throwable){
        if (throwable == null){
            return null;
        }
        if (throwable instanceof BizException){
            return (BizException) throwable;
        }
        Throwable cause = throwable.getCause();
        if (cause == null){
            return null;
        }
        if (cause instanceof BizException){
            return (BizException) cause;
        }
        cause = cause.getCause();
        if (cause == null){
            return null;
        }
        if (cause instanceof BizException){
            return (BizException) cause;
        }
        return null;
    }

    private static void printErrorLog(HttpServletRequest request, HttpServletResponse response, HttpStatus status) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        logger.error("error request: {} {}, {}", method, uri, status.getReasonPhrase());
        response.setStatus(status.value());
    }

}



