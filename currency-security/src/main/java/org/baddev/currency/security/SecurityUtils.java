package org.baddev.currency.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

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

    public static boolean hasRoles(String role) {
        return safeAuth().map(Authentication::getAuthorities)
                .flatMap(grAuthorities -> Optional.of(grAuthorities.contains(new SimpleGrantedAuthority(role))))
                .orElse(false);
    }

    public static <T extends UserDetails> T getUserDetails(Class<T> clazz) {
        return safeAuth().flatMap(auth -> Optional.ofNullable((T) auth.getPrincipal()))
                .orElseThrow(notAuthorized());
    }

    public static UserDetails getUserDetails() {
        return getUserDetails(UserDetails.class);
    }

    public static String loggedInUserName() {
        return safeAuth().flatMap(auth -> Optional.ofNullable(((UserDetails)auth.getPrincipal()).getUsername()))
                .orElse("");
    }

    private static Supplier<? extends BadCredentialsException> notAuthorized(){
        return () -> new BadCredentialsException("Not authorized");
    }

}
