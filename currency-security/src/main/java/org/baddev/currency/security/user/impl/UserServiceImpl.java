package org.baddev.currency.security.user.impl;

import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.RoleDao;
import org.baddev.currency.jooq.schema.tables.daos.UserDao;
import org.baddev.currency.jooq.schema.tables.daos.UserDetailsDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.Role;
import org.baddev.currency.jooq.schema.tables.pojos.User;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.jooq.schema.tables.records.UserDetailsRecord;
import org.baddev.currency.jooq.schema.tables.records.UserRecord;
import org.baddev.currency.jooq.schema.tables.records.UserRoleRecord;
import org.baddev.currency.security.dto.LoginDTO;
import org.baddev.currency.security.dto.SignUpDTO;
import org.baddev.currency.security.exception.RoleAlreadyAssignedException;
import org.baddev.currency.security.exception.RoleNotFoundException;
import org.baddev.currency.security.exception.SuchUserExistsException;
import org.baddev.currency.security.exception.UserNotFoundException;
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

import static org.baddev.currency.jooq.schema.Tables.*;

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
    private final RoleDao roleDao;

    @Autowired
    public UserServiceImpl(AuthenticationManager manager,
                           @Qualifier("md5") MessageDigestPasswordEncoder encoder,
                           DSLContext dsl,
                           UserDao userDao,
                           UserDetailsDao detailsDao,
                           RoleDao roleDao) {
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

        detailsDao.insert(new UserDetails(created.getId(),
                signUpDTO.getFirstName(), signUpDTO.getLastName(), signUpDTO.getEmail()));

        if (roleNames != null && roleNames.length > 0) {
            if (roleNames.length == 1) {
                Role role = roleDao.fetchOneByRoleName(roleNames[0]);
                if (role == null)
                    throw new RoleNotFoundException(roleNames[0]);
                dsl.insertInto(USER_ROLE)
                        .set(USER_ROLE.USER_ID, created.getId())
                        .set(USER_ROLE.ROLE_ID, role.getId())
                        .execute();
            } else if (roleNames.length > 1) {
                List<Long> roleIds = findRoleIds(roleNames);
                if (roleIds.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    Arrays.asList(roleNames).forEach(rn -> sb.append(rn).append(" "));
                    throw new RoleNotFoundException(sb.toString());
                }
                dsl.batchInsert(roleIds.stream()
                        .map(roleId -> new UserRoleRecord(created.getId(), roleId))
                        .collect(Collectors.toList()))
                        .execute();
            }
        } else throw new RoleNotFoundException();

    }

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN})
    public void assignToRoles(Long userId, Long... roleIds) {
        validateRoles(userId, roleIds);
        List<Number> assigned = dsl.selectFrom(USER_ROLE)
                .where(USER_ROLE.USER_ID.eq(userId))
                .fetchInto(UserRoleRecord.class)
                .stream()
                .map(UserRoleRecord::getRoleId)
                .filter(Arrays.asList(roleIds)::contains)
                .collect(Collectors.toList());
        if (!assigned.isEmpty())
            throw new RoleAlreadyAssignedException(assigned);
        else {
            dsl.batchInsert(Arrays.stream(roleIds)
                    .map(rid -> {
                        UserRoleRecord rec = dsl.newRecord(USER_ROLE);
                        rec.setUserId(userId);
                        rec.setRoleId(rid);
                        return rec;
                    }).collect(Collectors.toList()))
                    .execute();
        }
    }

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN})
    public void unassignFromRoles(Long userId, Long... roleIds) {
        validateRoles(userId, roleIds);
        List<Number> assigned = dsl.selectFrom(USER_ROLE)
                .where(USER_ROLE.USER_ID.eq(userId))
                .fetchInto(UserRoleRecord.class)
                .stream()
                .map(UserRoleRecord::getRoleId)
                .filter(Arrays.asList(roleIds)::contains)
                .collect(Collectors.toList());
        if (assigned.isEmpty() || !assigned.containsAll(Arrays.asList(roleIds)))
            throw new RoleNotFoundException(roleIds);
        else {
            dsl.deleteFrom(USER_ROLE)
                    .where(USER_ROLE.USER_ID.eq(userId).and(USER_ROLE.ROLE_ID.in(roleIds)))
                    .execute();
        }
    }

    private void validateRoles(Long userId, Long... roleIds) {
        if (roleIds.length == 0) throw new IllegalArgumentException();
        if (!userDao.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        for (Long roleId : roleIds) {
            if (!roleDao.existsById(roleId)) {
                throw new RoleNotFoundException(roleId);
            }
        }
    }

    private List<Long> findRoleIds(String... roleNames) {
        return roleDao.fetchByRoleName(roleNames).stream().map(Role::getId).collect(Collectors.toList());
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
