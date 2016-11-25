package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class SignUpDTO {

    @Size(min = 5, max = 50)
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
}
