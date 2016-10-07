package org.baddev.currency.security.exception;

import org.baddev.currency.core.exception.ServiceException;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
public class RoleAlreadyAssignedException extends ServiceException {

    public static final String MSG_PART = "Role(s) %s already assigned";

    public RoleAlreadyAssignedException(String... roleNames) {
        super(String.format(MSG_PART, Arrays.stream(roleNames).collect(Collectors.joining(", "))));
    }

    public RoleAlreadyAssignedException(Number... ids){
        super(String.format(MSG_PART, Arrays.stream(ids).map(Object::toString).collect(Collectors.joining(", "))));
    }

    public RoleAlreadyAssignedException(Collection<Number> ids) {
        this(ids.toArray(new Number[ids.size()]));
    }
}
