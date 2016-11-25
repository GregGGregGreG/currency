package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IPotapchuk on 11/15/2016.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class SecuritySettingsDTO {
    private boolean twoFactorAuth;
    private boolean signInWithEmail;
}
