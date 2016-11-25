package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IPotapchuk on 11/18/2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestrictionsDTO {
    private String userName;
    private boolean accountNotLocked;
    private boolean accountNotExpired;
    private boolean credentialsNotExpired;
    private boolean accountEnabled;
}
