package org.baddev.currency.security.service;

import org.baddev.currency.security.UserDetails;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public interface SecurityService {

    void authenticate(String principal, String credentials);

    void signUp(String username, String password, UserDetails details);

}
