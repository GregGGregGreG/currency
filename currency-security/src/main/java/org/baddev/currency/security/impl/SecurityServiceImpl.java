package org.baddev.currency.security.impl;

import org.baddev.currency.jooq.schema.tables.daos.UserDao;
import org.baddev.currency.jooq.schema.tables.daos.UserDetailsDao;
import org.baddev.currency.jooq.schema.tables.daos.UserRoleDao;
import org.baddev.currency.jooq.schema.tables.pojos.User;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.UserRole;
import org.baddev.currency.jooq.schema.tables.records.UserRecord;
import org.baddev.currency.jooq.schema.tables.records.UserUserRoleRecord;
import org.baddev.currency.security.SecurityService;
import org.baddev.currency.security.SuchUserExistsException;
import org.baddev.currency.security.UserRoleNotFoundException;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.tables.User.USER;
import static org.baddev.currency.jooq.schema.tables.UserUserRole.USER_USER_ROLE;

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
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserDetailsDao userDetailsDao;
    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    @Transactional(readOnly = true)
    public void authenticate(String principal, String credentials) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, credentials);
        Authentication auth = manager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    @Transactional
    public void signUp(String username, String password, UserDetails details, String... roleNames) {
        User user = userDao.fetchOneByUsername(username);

        if (user != null)
            throw new SuchUserExistsException(username);

        String encPwd = encoder.encodePassword(password, null);

        UserRecord created = dsl.insertInto(USER).set(USER.USERNAME, username)
                .set(USER.PASSWORD, encPwd)
                .returning(USER.ID).fetchOne();

        userDetailsDao.insert(details.setUserId(created.getId()));

        if (roleNames != null && roleNames.length > 0) {
            if (roleNames.length == 1) {
                UserRole role = userRoleDao.fetchOneByRoleName(roleNames[0]);
                if (role == null)
                    throw new UserRoleNotFoundException(roleNames[0]);
                dsl.insertInto(USER_USER_ROLE)
                        .set(USER_USER_ROLE.USER_ID, created.getId())
                        .set(USER_USER_ROLE.ROLE_ID, role.getId())
                        .execute();
            } else if (roleNames.length > 1) {
                List<Long> roleIds = findRoleIds(roleNames);
                if(roleIds.isEmpty()){
                    StringBuilder sb = new StringBuilder();
                    Arrays.asList(roleNames).forEach(rn -> sb.append(rn).append(" "));
                    throw new UserRoleNotFoundException(sb.toString());
                }
                dsl.batchInsert(roleIds.stream()
                        .map(roleId -> new UserUserRoleRecord(created.getId(), roleId))
                        .collect(Collectors.toList()))
                        .execute();
            }
        } else throw new UserRoleNotFoundException();

    }

    private List<Long> findRoleIds(String... roleNames) {
        return userRoleDao.fetchByRoleName(roleNames).stream().map(UserRole::getId).collect(Collectors.toList());
    }

}
