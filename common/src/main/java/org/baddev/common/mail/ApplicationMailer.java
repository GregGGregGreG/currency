package org.baddev.common.mail;

import org.baddev.common.CommonErrorHandler;

public interface ApplicationMailer {
    void sendMail(String to, String subject, String content);
    void setErrorHandler(CommonErrorHandler errorHandler);
}