package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class PasswordChangeDTO {

    @NotEmpty(message = "must be filled")
    private String currentPassword;
    @Size(min = 6, max = 20)
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[#_-])).{6,}",
            message = "at least one lowercase, uppercase letters and one of special symbols -#_ required")
    private String password = "";

    @Size(min = 6, max = 20)
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[#_-])).{6,}",
            message = "at least one lowercase, uppercase letters and one of special symbols -#_ required")
    private String passwordConfirm = "";
}
