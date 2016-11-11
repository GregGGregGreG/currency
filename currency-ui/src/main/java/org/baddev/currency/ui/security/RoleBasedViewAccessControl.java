package org.baddev.currency.ui.security;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.security.DeclareRoles;

import static org.baddev.currency.core.security.utils.SecurityUtils.hasAnyRole;
import static org.baddev.currency.core.security.utils.SecurityUtils.isLoggedIn;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
@Component
public class RoleBasedViewAccessControl implements ViewAccessControl {

    private final ApplicationContext ctx;

    @Autowired
    public RoleBasedViewAccessControl(ApplicationContext ctx) {
        Assert.notNull(ctx, "ctx can't be null");
        this.ctx = ctx;
    }

    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        if (ctx.findAnnotationOnBean(beanName, SpringView.class) == null)
            return false;
        final DeclareRoles rolesAnot = ctx.findAnnotationOnBean(beanName, DeclareRoles.class);
        return rolesAnot == null || isLoggedIn() && hasAnyRole(rolesAnot.value());
    }
}
