package org.baddev.currency.security.user.impl;

import org.baddev.currency.jooq.schema.tables.daos.UserDao;
import org.baddev.currency.jooq.schema.tables.daos.UserDetailsDao;
import org.baddev.currency.jooq.schema.tables.pojos.User;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.jooq.schema.tables.records.UserDetailsRecord;
import org.baddev.currency.jooq.schema.tables.records.UserRecord;
import org.baddev.currency.security.user.UserService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.baddev.currency.jooq.schema.Tables.USER;
import static org.baddev.currency.jooq.schema.Tables.USER_DETAILS;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
@Service("appUserService")
public class UserServiceImpl implements UserService<User, UserDetails> {

    private final DSLContext context;
    private final UserDetailsDao detailsDao;
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDetailsDao detailsDao, UserDao userDao, DSLContext context) {
        this.detailsDao = detailsDao;
        this.userDao = userDao;
        this.context = context;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails findUserDetailsByUsername(String userName) {
        UserDetails details = context.select(USER_DETAILS.fields())
                .from(USER)
                .leftOuterJoin(USER_DETAILS).on(USER.ID.eq(USER_DETAILS.USER_ID))
                .where(USER.USERNAME.eq(userName))
                .fetchOneInto(UserDetails.class);
        return details;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> findByUsername(String... userNames) {
        return userDao.fetchByUsername(userNames);
    }

    @Override
    @Transactional(readOnly = true)
    public User findOneByUserName(String userName) {
        return userDao.fetchOneByUsername(userName);
    }

    @Override
    @Transactional
    public void update(User user, UserDetails details) {
        if (user != null && details != null) {
            context.batchUpdate(user.into(new UserRecord()), details.into(new UserDetailsRecord()));
        } else if (details != null) {
            detailsDao.update(details);
        } else if (user != null) {
            userDao.update(user);
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        return userDao.findAll();
    }
}
