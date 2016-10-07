package org.baddev.currency.security.user;

import org.baddev.currency.security.dto.LoginDTO;
import org.baddev.currency.security.dto.PasswordChangeDTO;
import org.baddev.currency.security.dto.SignUpDTO;
import org.baddev.currency.security.exception.PasswordsMismatchException;
import org.baddev.currency.security.exception.RoleNotFoundException;
import org.baddev.currency.security.exception.SuchUserExistsException;
import org.springframework.security.core.AuthenticationException;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface UserOperation {
    void authenticate(LoginDTO loginDTO) throws AuthenticationException;
    void signUp(SignUpDTO signUpDTO, String... roleNames) throws SuchUserExistsException, RoleNotFoundException;
    void changePassword(PasswordChangeDTO passwordChangeDTO) throws AuthenticationException, PasswordsMismatchException;
}
