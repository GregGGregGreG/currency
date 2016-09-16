package org.baddev.currency.security.user.impl;

import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.UserDao;
import org.baddev.currency.jooq.schema.tables.daos.UserDetailsDao;
import org.baddev.currency.jooq.schema.tables.daos.UserRoleDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.User;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.UserRole;
import org.baddev.currency.jooq.schema.tables.records.UserDetailsRecord;
import org.baddev.currency.jooq.schema.tables.records.UserRecord;
import org.baddev.currency.jooq.schema.tables.records.UserUserRoleRecord;
import org.baddev.currency.security.dto.LoginDTO;
import org.baddev.currency.security.dto.SignUpDTO;
import org.baddev.currency.security.exception.SuchUserExistsException;
import org.baddev.currency.security.exception.UserRoleNotFoundException;
import org.baddev.currency.security.user.UserService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.Tables.USER;
import static org.baddev.currency.jooq.schema.Tables.USER_DETAILS;
import static org.baddev.currency.jooq.schema.tables.UserUserRole.USER_USER_ROLE;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private final MessageDigestPasswordEncoder encoder;
    private final AuthenticationManager manager;
    private final DSLContext dsl;
    private final UserDetailsDao detailsDao;
    private final UserDao userDao;
    private final UserRoleDao roleDao;

    @Autowired
    public UserServiceImpl(AuthenticationManager manager,
                           @Qualifier("md5") MessageDigestPasswordEncoder encoder,
                           DSLContext dsl,
                           UserDao userDao,
                           UserDetailsDao detailsDao,
                           UserRoleDao roleDao) {
        this.manager = manager;
        this.encoder = encoder;
        this.dsl = dsl;
        this.userDao = userDao;
        this.detailsDao = detailsDao;
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public void authenticate(LoginDTO loginDTO) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                loginDTO.getPassword());
        Authentication auth = manager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(auth);
        ((AbstractAuthenticationToken) auth).setDetails(findUserDetailsByUsername(loginDTO.getUsername()));
    }

    @Override
    @Transactional
    public void signUp(SignUpDTO signUpDTO, String... roleNames) {
        User user = userDao.fetchOneByUsername(signUpDTO.getUsername());

        if (user != null)
            throw new SuchUserExistsException(signUpDTO.getUsername());

        String encPwd = encoder.encodePassword(signUpDTO.getPassword(), null);

        UserRecord created = dsl.insertInto(USER)
                .set(USER.USERNAME, signUpDTO.getUsername())
                .set(USER.PASSWORD, encPwd)
                .returning(USER.ID)
                .fetchOne();

        detailsDao.insert(new UserDetails()
                .setFirstName(signUpDTO.getFirstName())
                .setLastName(signUpDTO.getLastName())
                .setEmail(signUpDTO.getEmail())
                .setUserId(created.getId())
        );

        if (roleNames != null && roleNames.length > 0) {
            if (roleNames.length == 1) {
                UserRole role = roleDao.fetchOneByRoleName(roleNames[0]);
                if (role == null)
                    throw new UserRoleNotFoundException(roleNames[0]);
                dsl.insertInto(USER_USER_ROLE)
                        .set(USER_USER_ROLE.USER_ID, created.getId())
                        .set(USER_USER_ROLE.ROLE_ID, role.getId())
                        .execute();
            } else if (roleNames.length > 1) {
                List<Long> roleIds = findRoleIds(roleNames);
                if (roleIds.isEmpty()) {
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
        return roleDao.fetchByRoleName(roleNames).stream().map(UserRole::getId).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public IUserDetails findUserDetailsByUsername(String userName) {
        UserDetails details = dsl.select(USER_DETAILS.fields())
                .from(USER)
                .leftOuterJoin(USER_DETAILS).on(USER.ID.eq(USER_DETAILS.USER_ID))
                .where(USER.USERNAME.eq(userName))
                .fetchOneInto(UserDetails.class);
        return details;
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Collection<? extends IUser> findByUsername(String... userNames) {
        return userDao.fetchByUsername(userNames);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public IUser findOneByUserName(String userName) {
        return userDao.fetchOneByUsername(userName);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void update(IUser user, IUserDetails details) {
        if (user != null && details != null) {
            dsl.batchUpdate(user.into(new UserRecord()), details.into(new UserDetailsRecord()));
        } else if (details != null) {
            detailsDao.update(details.into(new UserDetails()));
        } else if (user != null) {
            userDao.update(user.into(new User()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN})
    public Collection<? extends IUser> findAll() {
        return userDao.findAll();
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void update(IUser entity) {
        userDao.update(entity.into(new User()));
    }

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN})
    public void delete(String... strings) {
        dsl.deleteFrom(USER).where(USER.USERNAME.in(strings)).execute();
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN, RoleEnum.USER})
    public Collection<? extends IUser> find(Long... ids) {
        return userDao.fetchById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN, RoleEnum.USER})
    public IUser findOne(Long aLong) {
        return null;
    }
}
