package org.baddev.currency.security.exception;

/**
 * Created by IPotapchuk on 7/4/2016.
 */
public class UserRoleNotFoundException extends RuntimeException {

    private static final String MSG_PART = "Role(s) with name(s) %s was not found";

    public UserRoleNotFoundException() {
        super("Roles not found");
    }

    public UserRoleNotFoundException(String roleName) {
        super(String.format(MSG_PART, roleName));
    }

    public UserRoleNotFoundException(String roleName, Throwable cause) {
        super(String.format(MSG_PART, roleName), cause);
    }
}
