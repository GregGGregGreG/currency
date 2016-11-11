package org.baddev.currency.core.dto;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
public class PasswordChangeDTO extends ResetPasswordDTO {

    @NotEmpty(message = "must be filled")
    private String currentPassword;

    public PasswordChangeDTO(String password, String passwordConfirm, String currentPassword) {
        super(password, passwordConfirm);
        this.currentPassword = currentPassword;
    }

    public PasswordChangeDTO() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
