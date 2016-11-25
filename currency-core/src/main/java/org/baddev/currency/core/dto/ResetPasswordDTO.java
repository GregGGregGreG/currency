package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class ResetPasswordDTO {

    private String token;
    @Size(min = 6, max = 20)
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[#_-])).{6,}",
            message = "at least one lowercase, uppercase letters and one of special symbols -#_ required")
    private String password = "";

    @Size(min = 6, max = 20)
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[#_-])).{6,}",
            message = "at least one lowercase, uppercase letters and one of special symbols -#_ required")
    private String passwordConfirm = "";
}
