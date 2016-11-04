package org.baddev.currency.core.action;


import org.baddev.currency.core.dto.PasswordChangeDTO;
import org.baddev.currency.core.dto.SignInDTO;
import org.baddev.currency.core.dto.SignUpDTO;
import org.baddev.currency.core.exception.PasswordsMismatchException;
import org.baddev.currency.core.exception.RoleNotFoundException;
import org.baddev.currency.core.exception.SuchUserExistsException;

import javax.naming.AuthenticationException;

/**
 * Created by IPotapchuk on 9/16/2016.
 */
public interface UserAction {
    void authenticate(SignInDTO signInDTO);
    void signUp(SignUpDTO signUpDTO, String... roleNames) throws SuchUserExistsException, RoleNotFoundException;
    void changePassword(PasswordChangeDTO passwordChangeDTO) throws AuthenticationException, PasswordsMismatchException;
}
