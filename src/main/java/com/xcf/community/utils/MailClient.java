package com.xcf.community.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Joe
 * @version 1.0.0
 * @ClassName MailClient.java
 * @Description
 * @createTime 2022年05月12日 14:39:00
 */
@Slf4j
@Component
public class MailClient {
    @Autowired
    private JavaMailSender mailSender; //调用接口

    @Value("${spring.mail.username}")
    private String from; //发件人

    /**
     * 发送邮件
     * @param to 收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendMail(String to,String subject,String content){

        try {
            //MimeMessage：用于封装邮件
            MimeMessage message = mailSender.createMimeMessage();
            //MimeMessageHelper:帮助封装邮件内容
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(from);// 发送者
            helper.setTo(to);// 接收者
            helper.setSubject(subject);// 邮件主题
            helper.setText(content,true);// 邮件内容,第二个参数true表示支持html格式

            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("发送邮件失败: " + e.getMessage());
        }
    }
}
