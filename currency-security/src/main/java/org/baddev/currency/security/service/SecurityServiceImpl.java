package org.baddev.currency.security.service;

import org.baddev.currency.jooq.schema.tables.records.UserRecord;
import org.baddev.currency.jooq.schema.tables.records.UserUserRoleRecord;
import org.baddev.currency.security.SuchUserExistsException;
import org.baddev.currency.security.UserDetails;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.tables.User.USER;
import static org.baddev.currency.jooq.schema.tables.UserDetails.USER_DETAILS;
import static org.baddev.currency.jooq.schema.tables.UserRole.USER_ROLE;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    @Resource(name = "md5Encoder")
    private MessageDigestPasswordEncoder encoder;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private DSLContext dsl;

    @Override
    @Transactional(readOnly = true)
    public void authenticate(String principal, String credentials) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, credentials);
        Authentication auth = manager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    @Transactional
    public void signUp(String username, String password, UserDetails details) {
        UserRecord record = dsl.selectFrom(USER).where(USER.USERNAME.eq(username)).fetchOne();

        if (record != null) {
            throw new SuchUserExistsException(username);
        }

        String encPwd = encoder.encodePassword(password, null);

        UserRecord created = dsl.insertInto(USER).set(USER.USERNAME, username)
                .set(USER.PASSWORD, encPwd)
                .returning().fetchOne();

        dsl.insertInto(USER_DETAILS)
                .set(USER_DETAILS.USER_ID, created.getId())
                .set(USER_DETAILS.FIRST_NAME, details.getFirstName())
                .set(USER_DETAILS.LAST_NAME, details.getLastName())
                .execute();

        if (!details.getPermissions().isEmpty()) {
            List<Long> roleIds = findRoleIds(details.getPermissions());
            dsl.batchInsert(roleIds.stream()
                    .map(roleId -> new UserUserRoleRecord(created.getId(), roleId))
                    .collect(Collectors.toList()))
                    .execute();
        }
    }

    private List<Long> findRoleIds(List<String> roleNames) {
        return dsl.selectFrom(USER_ROLE).where(USER_ROLE.ROLE_NAME.in(roleNames)).fetch(USER_ROLE.ID);
    }

}
