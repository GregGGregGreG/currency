package org.baddev.currency.core.api;

import org.baddev.common.action.DeleteAction;
import org.baddev.common.action.FindAction;
import org.baddev.common.action.UpdateAction;
import org.baddev.currency.core.action.UserAction;
import org.baddev.currency.core.dto.ResetPasswordDTO;
import org.baddev.currency.core.dto.UserPasswordChangeDTO;
import org.baddev.currency.core.exception.RoleAlreadyAssignedException;
import org.baddev.currency.core.exception.RoleNotFoundException;
import org.baddev.currency.core.exception.UserNotFoundException;
import org.baddev.currency.jooq.schema.tables.interfaces.IRole;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
public interface UserService extends FindAction<IUser, Long>, UpdateAction<IUser>, DeleteAction<String>,
        UserAction {
    Optional<IUserDetails> findUserDetailsByUsername(String userName);
    Collection<? extends IUser> findUserByUsername(String... userNames);
    Optional<IUser> findOneUserByUserName(String userName);
    void update(IUser user, IUserDetails userDetails);
    Collection<IRole> findUserRoles(Long userId);
    void assignToRoles(Long userId, Long... roleIds) throws RoleAlreadyAssignedException, RoleNotFoundException, UserNotFoundException;
    void unassignFromRoles(Long userId, Long... roleIds) throws RoleNotFoundException, UserNotFoundException;
    void updateUserRoles(Long userId, Collection<Long> allUserRoles) throws RoleNotFoundException, UserNotFoundException;
    void changeUserPassword(UserPasswordChangeDTO dto);
    void createPasswordResetToken(String userEmail, String token, int expirationDuration);
    boolean isPasswordResetTokenValid(String token);
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
}
