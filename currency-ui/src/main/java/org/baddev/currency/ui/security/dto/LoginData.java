package org.baddev.currency.ui.security.dto;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public class LoginData {

    private String username;
    private String password;

    public LoginData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
