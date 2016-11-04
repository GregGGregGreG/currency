package org.baddev.currency.core.dto;

import javax.validation.constraints.Size;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
public class UserPasswordChangeDTO {

    private Long userId;
    @Size(min = 1, max = 50)
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
