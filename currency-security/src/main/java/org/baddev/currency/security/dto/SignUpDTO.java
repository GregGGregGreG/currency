package org.baddev.currency.security.dto;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public class SignUpDTO {

    @Size(min = 6, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$",
            message = "letters, numbers, special symbols \"_-\" ")
    private String username = "";
    @Size(min = 6, max = 20)
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[#_-])).{6,}",
            message = "at least one lowercase, uppercase letters and one of special symbols -#_ required")
    private String password = "";
    @Size(min = 6, max = 20)
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[#_-])).{6,}",
            message = "at least one lowercase, uppercase letters and one of special symbols -#_ required")
    private String confirmPassword = "";
    @Size(min = 2, max = 50)
    @Pattern(regexp = "[A-Z][a-zA-Z]*",
            message = "must start with uppercase letter, numbers are forbidden")
    private String firstName = "";
    @Size(min = 2, max = 50)
    @Pattern(regexp = "[a-zA-z]+([ '-][a-zA-Z]+)*")
    private String lastName = "";
    @Email
    private String email = "";

    public SignUpDTO() {
    }

    public SignUpDTO(String username, String email, String password, String confirmPassword, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.confirmPassword = confirmPassword;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
