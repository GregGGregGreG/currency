package org.baddev.currency.core.dto;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
public class UserPasswordChangeDTO {

    private Long userId;
    @NotEmpty(message = "must be filled")
    private String newPassword;

    public UserPasswordChangeDTO() {
    }

    public UserPasswordChangeDTO(Long userId, String newPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public Long getUserId() {
        return userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
