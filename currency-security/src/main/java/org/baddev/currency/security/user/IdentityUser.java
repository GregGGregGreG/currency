package org.baddev.currency.security.user;

import org.baddev.currency.core.Identity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by IPotapchuk on 9/13/2016.
 */
public class IdentityUser extends User implements Identity<Long> {

    private Long id;

    public IdentityUser(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public IdentityUser(Long id, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityUser)) return false;
        if (!super.equals(o)) return false;

        IdentityUser that = (IdentityUser) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "IdentityUser{" +
                "id=" + id +
                "} " + super.toString();
    }
}
