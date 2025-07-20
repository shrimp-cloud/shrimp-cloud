package com.wkclz.spring.rest;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLSyntaxErrorException;

//全局异常捕捉处理
@RestControllerAdvice
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result httpHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        printErrorLog(request, response, status, e.getMessage());
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodHandler(HttpRequestMethodNotSupportedException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        printErrorLog(request, response, status, e.getMessage());
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result httpNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        printErrorLog(request, response, status, e.getMessage());
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Result httpSQLSyntaxErrorException(SQLSyntaxErrorException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        logger.error("SQLSyntaxErrorException: {}", e.getMessage());
        printErrorLog(request, response, status, "SQLSyntaxErrorException");
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public Result httpBadSqlGrammarException(BadSqlGrammarException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        logger.error("BadSqlGrammarException: {}", e.getMessage());
        printErrorLog(request, response, status, "BadSqlGrammarException");
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(MysqlDataTruncation.class)
    public Result httpMysqlDataTruncation(MysqlDataTruncation e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        logger.error("MysqlDataTruncation: {}", e.getMessage());
        printErrorLog(request, response, status, "MysqlDataTruncation");
        return Result.error(status.value(), status.getReasonPhrase());
    }


    @ExceptionHandler(BizException.class)
    public Result bizExceptionHandler(BizException e) {
        logger.error(e.getMessage(), e);
        return Result.error(-1, e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception e) {
        logger.error(e.getMessage(), e);

        BizException bizException = getBizException(e);
        if (bizException != null) {
            return Result.error(bizException);
        }

        String message = e.getMessage();
        if (message == null || message.trim().isEmpty() || "null".equals(message)) {
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out));
            message = out.toString();
        }
        return Result.error(message);
    }


    /**
     * Throwable 找 BizException，找二级原因
     *
     * @param throwable
     * @return
     */
    private static BizException getBizException(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        if (throwable instanceof BizException bizException) {
            return bizException;
        }
        Throwable cause = throwable.getCause();
        if (cause == null) {
            return null;
        }
        if (cause instanceof BizException bizException) {
            return bizException;
        }
        cause = cause.getCause();
        if (cause == null) {
            return null;
        }
        if (cause instanceof BizException bizException) {
            return bizException;
        }
        return null;
    }

    private static void printErrorLog(HttpServletRequest request, HttpServletResponse response, HttpStatus status, String errMsg) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        logger.error("error request: {} {}, {}", method, uri, errMsg);
        response.setStatus(status.value());
    }

}



