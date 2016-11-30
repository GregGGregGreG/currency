package org.baddev.currency.ui.security;

import com.vaadin.spring.access.ViewAccessControl;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;
import lombok.RequiredArgsConstructor;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.springframework.context.ApplicationContext;

import javax.annotation.security.RolesAllowed;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
@SpringComponent
@RequiredArgsConstructor
public class RoleBasedViewAccessControl implements ViewAccessControl {

    private final ApplicationContext ctx;

    @Override
    public boolean isAccessGranted(UI ui, String beanName) {
        if (ctx.findAnnotationOnBean(beanName, SpringView.class) == null)
            return false;
        return SecurityUtils.isAccessGranted(ctx.findAnnotationOnBean(beanName, RolesAllowed.class));
    }
}
