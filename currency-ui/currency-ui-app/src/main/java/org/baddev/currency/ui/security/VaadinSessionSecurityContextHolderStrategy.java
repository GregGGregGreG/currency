package org.baddev.currency.ui.security;

import org.baddev.currency.ui.core.util.VaadinSessionUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.baddev.currency.ui.core.util.VaadinSessionUtils.getAttribute;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
public class VaadinSessionSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

    @Override
    public void clearContext() {
        VaadinSessionUtils.setAttribute(SecurityContext.class, null);
    }

    @Override
    public SecurityContext getContext() {
        SecurityContext context;
        try {
            context = getAttribute(SecurityContext.class);
        } catch (Exception e) {
            context = createEmptyContext();
            setContext(context);
        }
        return context;
    }

    @Override
    public void setContext(SecurityContext context) {
        VaadinSessionUtils.setAttribute(SecurityContext.class, context);
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }

}
