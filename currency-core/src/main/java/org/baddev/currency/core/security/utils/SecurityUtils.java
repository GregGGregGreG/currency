package org.baddev.currency.core.security.utils;

import org.baddev.currency.core.security.IdentityUser;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    private static Optional<Authentication> auth() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Assert.notNull(ctx);
        return Optional.ofNullable(ctx.getAuthentication());
    }

    public static boolean isLoggedIn() {
        return auth().map(Authentication::isAuthenticated).orElse(false);
    }

    public static boolean hasAnyRole(String... roles) {
        Assert.notEmpty(roles);
        return auth().map(Authentication::getAuthorities)
                .flatMap(grAuthorities -> {
                    List<SimpleGrantedAuthority> expected =
                            Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    return Optional.of(grAuthorities.stream().anyMatch(expected::contains));
                }).orElse(false);
    }

    private static <T extends UserDetails> T getPrincipal(Class<T> clazz) {
        return auth().map(auth -> (T) auth.getPrincipal()).orElseThrow(notAuthorized());
    }

    public static IdentityUser getIdentityUserPrincipal() {
        return getPrincipal(IdentityUser.class);
    }

    public static UserDetails getPrincipal() {
        return getPrincipal(UserDetails.class);
    }

    public static <T extends IUserDetails> T getUserDetails() {
        return auth().map(auth -> (T) auth.getDetails()).orElseThrow(notAuthorized());
    }

    public static void setUserDetails(Object details){
        auth().map(a -> ((AbstractAuthenticationToken)a)).ifPresent(a -> a.setDetails(details));
    }

    public static void clearUserDetails(){
        setUserDetails(null);
    }

    public static String loggedInUserName() {
        return auth().map(auth -> (((UserDetails) auth.getPrincipal()).getUsername())).orElse("");
    }

    public static boolean isAccessGranted(RolesAllowed annotation){
        return annotation == null || (isLoggedIn() && hasAnyRole(annotation.value()));
    }

    public static boolean isAccessGranted(Class<?> clazz){
        return isAccessGranted(clazz.getAnnotation(RolesAllowed.class));
    }

    private static Supplier<? extends BadCredentialsException> notAuthorized() {
        return () -> new BadCredentialsException("Not authorized");
    }

}
