package com.wkclz.spring.rest;

import cn.hutool.core.date.DateUtil;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.exception.CommonException;
import com.wkclz.common.exception.DataException;
import com.wkclz.common.exception.SysException;
import com.wkclz.spring.config.SpringContextHolder;
import com.wkclz.spring.config.SystemConfig;
import com.wkclz.spring.utils.MailUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLSyntaxErrorException;
import java.util.Date;

//全局异常捕捉处理
@RestControllerAdvice
public class ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result httpHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        printErrorLog(request, response, status, e);
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpRequestMethodHandler(HttpRequestMethodNotSupportedException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        printErrorLog(request, response, status, e);
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result httpNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        printErrorLog(request, response, status, e);
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Result httpSQLSyntaxErrorException(SQLSyntaxErrorException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        printErrorLog(request, response, status, e);
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public Result httpBadSqlGrammarException(BadSqlGrammarException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        printErrorLog(request, response, status, e);
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(UncategorizedSQLException.class)
    public Result httpUncategorizedSQLException(UncategorizedSQLException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        printErrorLog(request, response, status, e);
        return Result.error(status.value(), status.getReasonPhrase());
    }


    @ExceptionHandler(MysqlDataTruncation.class)
    public Result httpMysqlDataTruncation(MysqlDataTruncation e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        printErrorLog(request, response, status, e);
        return Result.error(status.value(), status.getReasonPhrase());
    }

    @ExceptionHandler(SysException.class)
    public Result sysExceptionHandler(BizException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        printErrorLog(request, response, status, e);
        return Result.error(-1, e.getMessage());
    }
    @ExceptionHandler(DataException.class)
    public Result dataExceptionHandler(DataException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        printErrorLog(request, response, status, e);
        return Result.error(-1, e.getMessage());
    }
    @ExceptionHandler(BizException.class)
    public Result bizExceptionHandler(BizException e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        printErrorLog(request, response, status, e);
        return Result.error(-1, e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result errorHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        CommonException commonException = getCommonException(e);
        if (commonException != null) {
            printErrorLog(request, response, status, e);
            return Result.error(commonException);
        }

        String message = e.getMessage();
        if (message == null || message.trim().isEmpty() || "null".equals(message)) {
            StringWriter out = new StringWriter();
            e.printStackTrace(new PrintWriter(out));
            message = out.toString();
        }
        printErrorLog(request, response, status, e);
        return Result.error(message);
    }


    /**
     * Throwable 找 CommonException，找二级原因
     */
    private static CommonException getCommonException(Throwable throwable) {
        for (int i = 0; i < 3; i++) {
            if (throwable == null) {
                return null;
            }
            if (throwable instanceof SysException sysException) {
                return sysException;
            }
            if (throwable instanceof DataException dataException) {
                return dataException;
            }
            if (throwable instanceof BizException bizException) {
                return bizException;
            }
            if (throwable instanceof CommonException commonException) {
                return commonException;
            }
            throwable = throwable.getCause();
        }
        return null;
    }

    private static void printErrorLog(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            Exception e) {

        response.setStatus(status.value());
        String method = request.getMethod();
        String uri = request.getRequestURI();

        if (e instanceof BizException) {
            logger.error("error request: {} {}, {}", method, uri, e.getMessage());
            return;
        }

        logger.error("error request: {} {}, {}", method, uri, e.getMessage(), e);

        // 发送邮件消息
        SystemConfig bean = SpringContextHolder.getBean(SystemConfig.class);

        if (!bean.isAlarmEmailEnabled()) {
            return;
        }

        try {
            MailUtil mu = new MailUtil();
            mu.setEmailHost(bean.getAlarmEmailHost());
            mu.setEmailFrom(bean.getAlarmEmailFrom());
            mu.setEmailPassword(bean.getAlarmEmailPassword());
            mu.setToEmails(bean.getAlarmEmailTo());

            String applicationName = bean.getApplicationName();
            String now = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
            String subject = "【"+applicationName+"】日志异常警告@" + now;

            String html = """
            <html>
                <body>
                    <div>系统: ${applicationName}</div>
                    <div>时间: ${now}</div>
                    <div>URL: ${url}</div>
                    <div>内容: </div>
                    <pre>${stackTrace}</pre>
                </body>
            </html>
            """;
            html = html.replace("${applicationName}", applicationName);
            html = html.replace("${now}", now);
            html = html.replace("${url}", method + ":" + uri);
            html = html.replace("${stackTrace}", ExceptionUtils.getStackTrace(e));

            mu.setSubject(subject);
            mu.setContent(html);
            mu.sendEmail();
        } catch (Exception exception) {
            logger.error("发送邮件异常: {}", exception.getMessage());
        }

    }

}



