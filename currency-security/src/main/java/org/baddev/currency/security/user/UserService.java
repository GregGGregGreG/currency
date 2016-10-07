package org.baddev.currency.security.user;

import org.baddev.currency.core.operation.DeleteOperation;
import org.baddev.currency.core.operation.FindOperation;
import org.baddev.currency.core.operation.UpdateOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.Role;
import org.baddev.currency.security.dto.UserPasswordChangeDTO;
import org.baddev.currency.security.exception.RoleAlreadyAssignedException;
import org.baddev.currency.security.exception.RoleNotFoundException;
import org.baddev.currency.security.exception.UserNotFoundException;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
public interface UserService extends FindOperation<IUser, Long>, UpdateOperation<IUser>, DeleteOperation<String>,
        UserOperation {
    Optional<IUserDetails> findUserDetailsByUsername(String userName);
    Collection<? extends IUser> findUserByUsername(String... userNames);
    Optional<IUser> findOneUserByUserName(String userName);
    void update(IUser user, IUserDetails userDetails);
    Collection<Role> findUserRoles(Long userId);
    void assignToRoles(Long userId, Long... roleIds) throws RoleAlreadyAssignedException, RoleNotFoundException, UserNotFoundException;
    void unassignFromRoles(Long userId, Long... roleIds) throws RoleNotFoundException, UserNotFoundException;
    void updateUserRoles(Long userId, Collection<Long> allUserRoles) throws RoleNotFoundException, UserNotFoundException;
    void changeUserPassword(UserPasswordChangeDTO dto);
}
