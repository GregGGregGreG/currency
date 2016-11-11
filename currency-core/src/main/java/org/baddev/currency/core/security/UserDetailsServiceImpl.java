package org.baddev.currency.core.security;

import org.baddev.currency.jooq.schema.tables.daos.UserDao;
import org.baddev.currency.jooq.schema.tables.pojos.User;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.Tables.*;
import static org.baddev.currency.jooq.schema.tables.UserRole.USER_ROLE;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final DSLContext dsl;
    private final UserDao    userDao;

    @Autowired
    public UserDetailsServiceImpl(DSLContext dsl, UserDao userDao) {
        Assert.notNull(dsl, "dsl can't be null");
        Assert.notNull(userDao, "userDao can't be null");
        this.dsl = dsl;
        this.userDao = userDao;
    }

    private static void notFound(String username){
        throw new UsernameNotFoundException("User with username " + username + " was not found");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(StringUtils.isEmpty(username)) notFound(username);

        User user = dsl.selectFrom(USER)
                .where(USER.USERNAME.eq(username))
                .fetchOptionalInto(User.class)
                .orElse(dsl.select(USER.fields())
                        .from(USER.leftOuterJoin(USER_DETAILS).on(USER.ID.eq(USER_DETAILS.USER_ID)))
                        .where(USER_DETAILS.EMAIL.eq(username))
                        .fetchOneInto(User.class));

        if (user == null) notFound(username);

        List<GrantedAuthority> permissions = dsl.selectFrom(ROLE.leftOuterJoin(USER_ROLE).on(ROLE.ID.eq(USER_ROLE.ROLE_ID)))
                .where(USER_ROLE.USER_ID.eq(user.getId()))
                .fetch(ROLE.ROLE_NAME)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new IdentityUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccNonExpired(),
                user.getCredNonExpired(),
                user.getAccNonLocked(),
                permissions);
    }
}
