package org.baddev.currency.security;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public interface RoleEnum {

    String ADMIN = "ROLE_ADMIN";
    String USER = "ROLE_USER";
    String ANON = "ROLE_ANONYMOUS";

    String [] VALUES = {
            ADMIN,
            USER,
            ANON
    };

}
