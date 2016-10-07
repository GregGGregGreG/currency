package org.baddev.currency.security.exception;

import org.baddev.currency.core.exception.ServiceException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 7/4/2016.
 */
public class RoleNotFoundException extends ServiceException {

    private static final String MSG_PART = "Role(s) %s not found";

    public RoleNotFoundException() {
        super("Roles not found");
    }

    public RoleNotFoundException(String roleName) {
        super(String.format(MSG_PART, roleName));
    }

    public RoleNotFoundException(Number id) {
        super(String.format(MSG_PART, id.toString()));
    }

    public RoleNotFoundException(Number... ids) {
        super(String.format(MSG_PART, Arrays.stream(ids).map(Object::toString).collect(Collectors.joining(", "))));
    }

    public RoleNotFoundException(String roleName, Throwable cause) {
        super(String.format(MSG_PART, roleName), cause);
    }
}
