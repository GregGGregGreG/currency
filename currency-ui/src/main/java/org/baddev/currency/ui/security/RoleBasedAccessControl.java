package org.baddev.currency.ui.security;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.security.DeclareRoles;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
@Component
public class RoleBasedAccessControl implements ViewAccessControl {

    @Autowired
    private ApplicationContext ctx;

    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        final SpringView viewAnt = ctx.findAnnotationOnBean(beanName, SpringView.class);
        final DeclareRoles rolesAnt = ctx.findAnnotationOnBean(beanName, DeclareRoles.class);

        if (viewAnt == null)
            return false;

        if (rolesAnt == null) {
            return true;
        } else {
            String[] grantedToRoles = rolesAnt.value();
            List<String> actualRoles;
            Authentication current = SecurityContextHolder.getContext().getAuthentication();
            if (current != null) {
                actualRoles = current.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
                return current.isAuthenticated() && Arrays.asList(grantedToRoles).stream().anyMatch(actualRoles::contains);
            } else return false;
        }
    }
}
