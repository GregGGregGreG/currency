package org.baddev.currency.security.dto;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class LoginDTO {

    @NotEmpty(message = "must be filled")
    private String username = "";
    @NotEmpty(message = "must be filled")
    private String password = "";

    public LoginDTO() {
    }

    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}