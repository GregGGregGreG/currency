package org.baddev.common.mail;

public interface ApplicationMailer {
    void sendMail(String to, String subject, String content);
}