package org.baddev.currency.security;

import org.baddev.currency.jooq.schema.tables.records.UserRecord;
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

import static org.baddev.currency.jooq.schema.tables.User.USER;
import static org.baddev.currency.jooq.schema.tables.UserRole.USER_ROLE;
import static org.baddev.currency.jooq.schema.tables.UserUserRole.USER_USER_ROLE;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private DSLContext dsl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRecord record = dsl.selectFrom(USER).where(USER.USERNAME.eq(username)).fetchOne();

        if (record == null)
            throw new UsernameNotFoundException("User with username " + username + " was not found");

        List<GrantedAuthority> permissions = dsl.selectFrom(
                USER_USER_ROLE.join(USER_ROLE).on(USER_USER_ROLE.ROLE_ID.eq(USER_ROLE.ID)))
                .where(USER_USER_ROLE.USER_ID.eq(record.getId()))
                .fetch(USER_ROLE.ROLE_NAME)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UserEntity(record.getId(),
                record.getUsername(),
                record.getPassword(),
                record.getEnabled(),
                record.getAccNonExpired(),
                record.getCredNonExpired(),
                record.getAccNonLocked(),
                permissions);
    }
}
