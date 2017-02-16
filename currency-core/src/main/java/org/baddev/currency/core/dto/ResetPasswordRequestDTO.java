package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class ResetPasswordRequestDTO {
    @Email
    private String email = "";
}
