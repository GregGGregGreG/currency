package org.baddev.currency.core.exception;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
public class PasswordsMismatchException extends ServiceException {

    private Number userId;
    public static final String MSG = "Given passwords do not match";

    public PasswordsMismatchException(Number id) {
        super(MSG);
        this.userId = id;
    }

    public Number getUserId() {
        return userId;
    }
}
