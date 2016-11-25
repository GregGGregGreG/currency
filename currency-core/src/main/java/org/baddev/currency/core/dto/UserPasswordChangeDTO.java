package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class UserPasswordChangeDTO {

    private Long userId;
    @Size(min = 1, max = 50)
    private String newPassword;
}
