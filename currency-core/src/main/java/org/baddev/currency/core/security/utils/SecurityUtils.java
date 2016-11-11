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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    private static Optional<Authentication> safeAuth() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Objects.requireNonNull(ctx, "Security Context not found");
        return Optional.ofNullable(ctx.getAuthentication());
    }

    public static boolean isLoggedIn() {
        return safeAuth().map(Authentication::isAuthenticated).orElse(false);
    }

    public static boolean hasAnyRole(String... roles) {
        Assert.notEmpty(roles);
        return safeAuth().map(Authentication::getAuthorities)
                .flatMap(grAuthorities -> {
                    List<SimpleGrantedAuthority> expected =
                            Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                    return Optional.of(grAuthorities.stream().anyMatch(expected::contains));
                }).orElse(false);
    }

    public static <T extends UserDetails> T getPrincipal(Class<T> clazz) {
        return safeAuth().flatMap(auth -> Optional.ofNullable((T) auth.getPrincipal()))
                .orElseThrow(notAuthorized());
    }

    public static IdentityUser getIdentityUserPrincipal() {
        return getPrincipal(IdentityUser.class);
    }

    public static UserDetails getPrincipal() {
        return getPrincipal(UserDetails.class);
    }

    public static <T extends IUserDetails> T getUserDetails() {
        return safeAuth().flatMap(auth -> Optional.of((T) auth.getDetails())).orElseThrow(notAuthorized());
    }

    public static void setUserDetails(Object details){
        safeAuth().map(a -> ((AbstractAuthenticationToken)a)).ifPresent(a -> a.setDetails(details));
    }

    public static void clearUserDetails(){
        setUserDetails(null);
    }

    public static String loggedInUserName() {
        return safeAuth().flatMap(auth -> Optional.ofNullable(((UserDetails) auth.getPrincipal()).getUsername()))
                .orElse("");
    }

    private static Supplier<? extends BadCredentialsException> notAuthorized() {
        return () -> new BadCredentialsException("Not authorized");
    }

}
