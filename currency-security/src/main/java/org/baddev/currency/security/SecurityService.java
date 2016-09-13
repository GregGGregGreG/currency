package org.baddev.currency.security;

import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public interface SecurityService {
    void authenticate(String principal, String credentials);
    void signUp(String username, String password, UserDetails details, String... roleNames);
}
