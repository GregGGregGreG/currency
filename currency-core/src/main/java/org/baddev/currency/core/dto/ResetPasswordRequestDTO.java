package org.baddev.currency.core.dto;

import org.hibernate.validator.constraints.Email;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
public class ResetPasswordRequestDTO {

    @Email
    private String email = "";

    public ResetPasswordRequestDTO() {
    }

    public ResetPasswordRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
