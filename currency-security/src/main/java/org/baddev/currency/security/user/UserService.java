package org.baddev.currency.security.user;

import org.baddev.currency.core.operation.DeleteOperation;
import org.baddev.currency.core.operation.FindOperation;
import org.baddev.currency.core.operation.UpdateOperation;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
public interface UserService extends FindOperation<IUser, Long>, UpdateOperation<IUser>, DeleteOperation<String>,
        UserOperation {
    IUserDetails findUserDetailsByUsername(String userName);
    Collection<? extends IUser> findByUsername(String... userNames);
    IUser findOneByUserName(String userName);
    void update(IUser user, IUserDetails userDetails);
}
