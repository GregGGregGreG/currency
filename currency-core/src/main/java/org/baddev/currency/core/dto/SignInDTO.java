package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class SignInDTO {
    private String username;
    private String password;
}
