package org.baddev.currency.security.user.impl;

import org.baddev.currency.jooq.schema.tables.daos.UserDao;
import org.baddev.currency.security.user.IdentityUser;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.Tables.ROLE;
import static org.baddev.currency.jooq.schema.tables.UserRole.USER_ROLE;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private DSLContext dsl;
    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        org.baddev.currency.jooq.schema.tables.pojos.User user = userDao.fetchOneByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User with username " + username + " was not found");

        List<GrantedAuthority> permissions = dsl.selectFrom(
                ROLE.join(USER_ROLE).on(ROLE.ID.eq(USER_ROLE.ROLE_ID)))
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
