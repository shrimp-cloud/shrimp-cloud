package com.wkclz.spring.utils;


// import com.sun.mail.util.MailSSLSocketFactory;
import com.wkclz.spring.config.Sys;
import com.wkclz.spring.enums.EnvType;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.eclipse.angus.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MailUtil {

    private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

    // 发件人邮箱服务器
    private String emailHost;

    // 发件人邮箱
    private String emailFrom;

    // 发件人密码
    private String emailPassword;

    // 收件人邮箱，多个邮箱以“;”分隔
    private String toEmails;

    // 邮件主题
    private String subject;

    //邮件内容
    private String content;

    // 邮件中的图片，为空时无图片。map中的key为图片ID，value为图片地址
    private Map<String, String> pictures;

    // 邮件中的附件，为空时无附件。map中的key为附件ID，value为附件地址
    private Map<String, String> attachments;


    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getToEmails() {
        return toEmails;
    }

    public void setToEmails(String toEmails) {
        this.toEmails = toEmails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getPictures() {
        return pictures;
    }

    public void setPictures(Map<String, String> pictures) {
        this.pictures = pictures;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "MailUtil{" +
            "emailHost='" + emailHost + '\'' +
            ", emailFrom='" + emailFrom + '\'' +
            ", emailPassword='" + emailPassword + '\'' +
            ", toEmails='" + toEmails + '\'' +
            ", subject='" + subject + '\'' +
            ", content='" + content + '\'' +
            ", pictures=" + pictures +
            ", attachments=" + attachments +
            '}';
    }

    public void sendEmail() {
        try {
            if (null == this.getEmailHost() || "".equals(this.getEmailHost()) || null == this.getEmailFrom()
                || "".equals(this.getEmailFrom()) || null == this.getEmailPassword() || "".equals(this.getEmailPassword())) {
                throw new RuntimeException("发件人信息不完全，请确认发件人信息！");
            }

            // 收件人邮箱
            String[] toEmailArray = toEmails.split(";");
            if (toEmailArray.length < 1) {
                throw new RuntimeException("收件人不能为空！");
            }
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            Properties properties = System.getProperties();
            properties.setProperty("mail.smtp.host", emailHost);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailFrom, emailPassword);
                }
            });

            // 开发环境，开启调试
            session.setDebug(Sys.CURRENT_ENV == EnvType.DEV);
            MimeMessage mimeMessage = new MimeMessage(session);
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            // 设置发件人用户名
            messageHelper.setFrom(emailFrom);
            // 设置收件人邮箱
            messageHelper.setTo(toEmailArray);
            // 邮件主题
            messageHelper.setSubject(subject);
            // true 表示启动HTML格式的邮件
            messageHelper.setText(content, true);

            // 添加图片
            if (null != pictures) {
                for (Map.Entry<String, String> entry : pictures.entrySet()) {
                    String cid = entry.getKey();
                    String filePath = entry.getValue();
                    if (null == cid || null == filePath) {
                        throw new RuntimeException("请确认每张图片的ID和图片地址是否齐全！");
                    }
                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new RuntimeException("图片" + filePath + "不存在！");
                    }
                    FileSystemResource img = new FileSystemResource(file);
                    messageHelper.addInline(cid, img);
                }
            }
            // 添加附件
            if (null != attachments) {
                for (Map.Entry<String, String> entry : attachments.entrySet()) {
                    String cid = entry.getKey();
                    String filePath = entry.getValue();
                    if (null == cid || null == filePath) {
                        throw new RuntimeException("请确认每个附件的ID和地址是否齐全！");
                    }
                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new RuntimeException("附件" + filePath + "不存在！");
                    }
                    FileSystemResource fileResource = new FileSystemResource(file);
                    messageHelper.addAttachment(cid, fileResource);
                }
            }
            // 发送邮件
            Transport.send(mimeMessage);
        } catch (MessagingException | GeneralSecurityException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void mainFun(String[] args) throws Exception {
        MailUtil mu = new MailUtil();
        mu.setEmailHost("smtp.exmail.qq.com");
        mu.setEmailFrom("test@wkclz.com");
        mu.setEmailPassword("testPassword");
        mu.setToEmails("wkclz@qq.com");

        // test1(mu);
        test2(mu);
        // test3(mu);
        // test4(mu);
        // test5(mu);
        // test6(mu);
    }

    public static void test1(MailUtil mu) throws Exception {
        String subject = "第一封，简单文本邮件";
        StringBuilder builder = new StringBuilder();
        builder.append("我相信天上不会掉馅饼");
        String content = builder.toString();

        mu.setSubject(subject);
        mu.setContent(content);

        mu.sendEmail();
    }

    public static void test2(MailUtil mu) throws Exception {
        String subject = "第二封，HTML邮件";
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>老婆：<br />我是你的老公吗？<br />是的，是很久了。<br /></body></html>");
        String content = builder.toString();

        mu.setSubject(subject);
        mu.setContent(content);

        mu.sendEmail();
    }

    public static void test3(MailUtil mu) throws Exception {
        String subject = "第三封，图片邮件";

        Map<String, String> pictures = new HashMap<String, String>();
        pictures.put("d1", "D:/work/download/d1.jpg");
        pictures.put("d2", "D:/work/download/测试图片2.jpg");
        pictures.put("d3", "D:/work/download/d3.jpg");

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>看看下面的图，你会知道花儿为什么是这样红的：<br />");
        builder.append("<img src=\"cid:d1\" /><br />");
        builder.append("<img src=\"cid:d2\" /><br />");
        builder.append("<img src=\"cid:d3\" /><br />");
        builder.append("</body></html>");
        String content = builder.toString();

        mu.setSubject(subject);
        mu.setContent(content);
        mu.setPictures(pictures);

        mu.sendEmail();

    }

    public static void test4(MailUtil mu) throws Exception {
        String subject = "第四封，附件邮件";
        Map<String, String> attachments = new HashMap<String, String>();
        attachments.put("d1.jar", "D:/work/download/activation.jar");
        attachments.put("d2.doc", "C:/Documents and Settings/Administrator/桌面/Java设计模式.doc");
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>看看附件中的资料，你会知道世界为什么是平的。</body></html>");
        String content = builder.toString();

        mu.setSubject(subject);
        mu.setContent(content);
        mu.setAttachments(attachments);

        mu.sendEmail();
    }

    public static void test5(MailUtil mu) throws Exception {
        String subject = "第五封，综合邮件";

        Map<String, String> attachments = new HashMap<String, String>();
        attachments.put("d1.jar", "D:/work/download/activation.jar");
        attachments.put("d2.doc",
            "C:/Documents and Settings/Administrator/桌面/Java设计模式.doc");

        Map<String, String> pictures = new HashMap<String, String>();
        pictures.put("d1", "D:/work/download/d1.jpg");
        pictures.put("d2", "D:/work/download/测试图片2.jpg");
        pictures.put("d3", "D:/work/download/d3.jpg");

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>看看附件中的资料，你会知道世界为什么是平的。<br />");
        builder.append("看看下面的图，你会知道花儿为什么是这样红的：<br />");
        builder.append("<img src=\"cid:d1\" /><br />");
        builder.append("<img src=\"cid:d2\" /><br />");
        builder.append("<img src=\"cid:d3\" /><br />");
        builder.append("</body></html>");
        String content = builder.toString();

        mu.setSubject(subject);
        mu.setContent(content);
        mu.setPictures(pictures);
        mu.setAttachments(attachments);

        mu.sendEmail();
    }

    public static void test6(MailUtil mu) throws Exception {
        String subject = "第五封，群发邮件";

        Map<String, String> attachments = new HashMap<String, String>();
        attachments.put("d1.jar", "D:/work/download/activation.jar");
        attachments.put("d2.doc",
            "C:/Documents and Settings/Administrator/桌面/Java设计模式.doc");

        Map<String, String> pictures = new HashMap<String, String>();
        pictures.put("d1", "D:/work/download/d1.jpg");
        pictures.put("d2", "D:/work/download/测试图片2.jpg");
        pictures.put("d3", "D:/work/download/d3.jpg");

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body>看看附件中的资料，你会知道世界为什么是平的。<br />");
        builder.append("看看下面的图，你会知道花儿为什么是这样红的：<br />");
        builder.append("<img src=\"cid:d1\" /><br />");
        builder.append("<img src=\"cid:d2\" /><br />");
        builder.append("<img src=\"cid:d3\" /><br />");
        builder.append("</body></html>");
        String content = builder.toString();

        mu.setSubject(subject);
        mu.setContent(content);
        mu.setPictures(pictures);
        mu.setAttachments(attachments);

        mu.sendEmail();
    }

}