package com.mykidevs.notiservice.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@AllArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendVerifyMail(String token, String to) {
        sendMail(to,
                "Verify your mail.",
                "So please went here http://localhost:8080/api/v1/users/verify-token?token=" + token,
                token);
    }
    private void sendMail(String to, String subject, String text, String token) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(text);
        mailSender.send(msg);
    }
}
