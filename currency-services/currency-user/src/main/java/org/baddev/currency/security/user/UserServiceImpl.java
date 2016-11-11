package org.baddev.currency.security.user;

import org.baddev.common.utils.AssertUtils;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.*;
import org.baddev.currency.core.exception.*;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.jooq.schema.tables.daos.RoleDao;
import org.baddev.currency.jooq.schema.tables.daos.UserDao;
import org.baddev.currency.jooq.schema.tables.daos.UserDetailsDao;
import org.baddev.currency.jooq.schema.tables.daos.UserPreferencesDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IRole;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.*;
import org.baddev.currency.jooq.schema.tables.records.PasswordResetTokenRecord;
import org.baddev.currency.jooq.schema.tables.records.UserDetailsRecord;
import org.baddev.currency.jooq.schema.tables.records.UserRecord;
import org.baddev.currency.jooq.schema.tables.records.UserRoleRecord;
import org.joda.time.LocalDateTime;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
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
    private final UserPreferencesDao preferencesDao;

    @Autowired
    public UserServiceImpl(AuthenticationManager manager,
                           @Qualifier("md5") MessageDigestPasswordEncoder encoder,
                           DSLContext dsl,
                           UserDao userDao,
                           UserDetailsDao detailsDao,
                           RoleDao roleDao,
                           UserPreferencesDao preferencesDao) {
        AssertUtils.objectsNotNull(manager, encoder, dsl, userDao, detailsDao, roleDao, preferencesDao);
        this.manager = manager;
        this.encoder = encoder;
        this.dsl = dsl;
        this.userDao = userDao;
        this.detailsDao = detailsDao;
        this.roleDao = roleDao;
        this.preferencesDao = preferencesDao;
    }

    @Override
    @Transactional(readOnly = true)
    public void authenticate(SignInDTO signInDTO) throws AuthenticationException {
        authenticate(signInDTO.getUsername(), signInDTO.getPassword());
    }

    private void authenticate(String username, String password) {
        Authentication auth = manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
        findUserDetailsByUsername(SecurityUtils.loggedInUserName())
                .ifPresent(((AbstractAuthenticationToken) auth)::setDetails);
    }

    @Override
    @Transactional
    public void signUp(SignUpDTO signUpDTO, String... roleNames) throws SuchUserExistsException, RoleNotFoundException {
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

        preferencesDao.insert(new UserPreferences(created.getId(), true, false, null));
    }

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN, RoleEnum.USER})
    public void changePassword(PasswordChangeDTO dto) throws AuthenticationException, PasswordsMismatchException {
        String userName = SecurityUtils.loggedInUserName();
        Long currentUserId = SecurityUtils.getIdentityUserPrincipal().getId();
        if (!Objects.equals(dto.getPassword(), dto.getPasswordConfirm()))
            throw new PasswordsMismatchException(currentUserId);
        User user = userDao.fetchOneById(currentUserId);
        String oldPwd = user.getPassword();
        if (!Objects.equals(encoder.encodePassword(dto.getCurrentPassword(), null), oldPwd))
            throw new BadCredentialsException("Current password is invalid");
        String newPwdEnc = encoder.encodePassword(dto.getPassword(), null);
        user.setPassword(newPwdEnc);
        userDao.update(user);
        savePwdHist(currentUserId, oldPwd);
        authenticate(userName, newPwdEnc);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN})
    public void changeUserPassword(UserPasswordChangeDTO dto) {
        User user = userDao.fetchOneById(dto.getUserId());
        String oldPwd = user.getPassword();
        String newPwdEnc = encoder.encodePassword(dto.getNewPassword(), null);
        user.setPassword(newPwdEnc);
        userDao.update(user);
        savePwdHist(dto.getUserId(), oldPwd);
    }

    private void savePwdHist(Long userId, String pwd) {
        dsl.insertInto(USER_PASSWORD_HISTORY)
                .set(USER_PASSWORD_HISTORY.USER_ID, userId)
                .set(USER_PASSWORD_HISTORY.PASSWORD, pwd)
                .execute();
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN})
    public Collection<IRole> findUserRoles(Long userId) {
        return dsl.select(ROLE.fields())
                .from(USER)
                .leftOuterJoin(USER_ROLE).on(USER.ID.eq(USER_ROLE.USER_ID))
                .leftOuterJoin(ROLE).on(USER_ROLE.ROLE_ID.eq(ROLE.ID))
                .where(USER.ID.eq(userId))
                .fetchInto(Role.class);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN})
    public void assignToRoles(Long userId, Long... roleIds) throws RoleAlreadyAssignedException, RoleNotFoundException, UserNotFoundException {
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
    public void unassignFromRoles(Long userId, Long... roleIds) throws RoleNotFoundException, UserNotFoundException{
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

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN})
    public void updateUserRoles(Long userId, Collection<Long> allUserRoleIds) throws RoleNotFoundException, UserNotFoundException {
        validateRoles(userId, allUserRoleIds);
        dsl.deleteFrom(USER_ROLE).where(USER_ROLE.USER_ID.eq(userId)).execute();
        dsl.batchInsert(allUserRoleIds.stream()
                .map(rid -> {
                    UserRoleRecord rec = dsl.newRecord(USER_ROLE);
                    rec.setUserId(userId);
                    rec.setRoleId(rid);
                    return rec;
                }).collect(Collectors.toList()))
                .execute();
    }

    private void validateRoles(Long userId, Collection<Long> roleIds){
        if (roleIds.size() == 0) throw new IllegalArgumentException();
        if (!userDao.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        for (Long roleId : roleIds) {
            if (!roleDao.existsById(roleId)) {
                throw new RoleNotFoundException(roleId);
            }
        }
    }

    private void validateRoles(Long userId, Long... roleIds) {
        validateRoles(userId, Arrays.asList(roleIds));
    }

    private List<Long> findRoleIds(String... roleNames) {
        return roleDao.fetchByRoleName(roleNames).stream().map(Role::getId).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Optional<IUserDetails> findUserDetailsByUsername(String userName) {
        UserDetails details = dsl.select(USER_DETAILS.fields())
                .from(USER)
                .leftOuterJoin(USER_DETAILS).on(USER.ID.eq(USER_DETAILS.USER_ID))
                .where(USER.USERNAME.eq(userName))
                .fetchOneInto(UserDetails.class);
        return Optional.ofNullable(details);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Collection<? extends IUser> findUserByUsername(String... userNames) {
        return userDao.fetchByUsername(userNames);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Optional<IUser> findOneUserByUserName(String userName) {
        return Optional.ofNullable(userDao.fetchOneByUsername(userName));
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
    public Collection<? extends IUser> findById(Long... ids) {
        return userDao.fetchById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN, RoleEnum.USER})
    public Optional<IUser> findOneById(Long id) {
        return Optional.ofNullable(userDao.fetchOneById(id));
    }

    @Override
    @Transactional
    public void createPasswordResetToken(String userEmail, String token, int expirationDuration) {
        UserDetails userDetails = detailsDao.fetchOneByEmail(userEmail);

        if (userDetails == null || !userDao.existsById(userDetails.getUserId())) {
            throw new UserNotFoundException(userEmail);
        }

        dsl.deleteFrom(PASSWORD_RESET_TOKEN).where(PASSWORD_RESET_TOKEN.USER_ID.eq(userDetails.getUserId())).execute();

        PasswordResetToken resetToken = new PasswordResetToken(
                userDetails.getUserId(), token, LocalDateTime.now().plusMinutes(expirationDuration));
        PasswordResetTokenRecord tokenRecord = dsl.newRecord(PASSWORD_RESET_TOKEN);
        tokenRecord.from(resetToken);
        tokenRecord.store();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPasswordResetTokenValid(String token) {
        return isTokenValid(findToken(token));
    }

    private PasswordResetTokenRecord findToken(String rawToken) {
        return StringUtils.isEmpty(rawToken) ? null :
                dsl.selectFrom(PASSWORD_RESET_TOKEN).where(PASSWORD_RESET_TOKEN.TOKEN.eq(encoder.encodePassword(rawToken, null))).fetchOne();
    }

    private boolean isTokenValid(PasswordResetTokenRecord record) {
        if (record == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return !(now.isEqual(record.getExpirationDate()) || now.isAfter(record.getExpirationDate()));
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        PasswordResetTokenRecord tokenRecord = findToken(resetPasswordDTO.getToken());

        if (!isTokenValid(tokenRecord))
            throw new ServiceException("Can't reset password. Given token is invalid or expired");

        if (!Objects.equals(resetPasswordDTO.getPassword(), resetPasswordDTO.getPasswordConfirm()))
            throw new PasswordsMismatchException(tokenRecord.getUserId());

        UserRecord userRecord = dsl.selectFrom(USER).where(USER.ID.eq(tokenRecord.getUserId())).fetchOne();
        String oldPwd = userRecord.getPassword();
        userRecord.setPassword(encoder.encodePassword(resetPasswordDTO.getPassword(), null));
        userRecord.store();
        savePwdHist(userRecord.getId(), oldPwd);
        tokenRecord.delete();
    }

}
