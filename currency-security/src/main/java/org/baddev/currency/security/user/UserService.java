package org.baddev.currency.security.user;

import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
public interface UserService<T extends IUser, E extends IUserDetails> {
    E findUserDetailsByUsername(String userName);
    Collection<T> findByUsername(String... userNames);
    T findOneByUserName(String userName);
    void update(T user, E userDetails);
    Collection<T> findAllUsers();
}
