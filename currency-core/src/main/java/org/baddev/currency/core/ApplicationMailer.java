package org.baddev.currency.core;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
public interface ApplicationMailer {
    void sendMail(String to, String subject, String content);
}
