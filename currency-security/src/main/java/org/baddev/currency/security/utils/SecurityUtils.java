package org.baddev.currency.security.utils;

import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

    public static UserDetails getPrincipal() {
        return getPrincipal(UserDetails.class);
    }

    public static <T extends IUserDetails> T getUserDetails(){
        return safeAuth().flatMap(auth -> Optional.of((T)auth.getDetails())).orElseThrow(notAuthorized());
    }

    public static String loggedInUserName() {
        return safeAuth().flatMap(auth -> Optional.ofNullable(((UserDetails) auth.getPrincipal()).getUsername()))
                .orElse("");
    }

    private static Supplier<? extends BadCredentialsException> notAuthorized() {
        return () -> new BadCredentialsException("Not authorized");
    }

}
