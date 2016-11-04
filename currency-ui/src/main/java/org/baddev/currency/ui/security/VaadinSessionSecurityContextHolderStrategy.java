package org.baddev.currency.ui.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;

import static org.baddev.currency.ui.util.VaadinSessionUtils.getAttribute;
import static org.baddev.currency.ui.util.VaadinSessionUtils.setAttribute;

/**
 * Created by IPotapchuk on 7/1/2016.
 */
public class VaadinSessionSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

    @Override
    public void clearContext() {
        setAttribute(SecurityContext.class, null);
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
        setAttribute(SecurityContext.class, context);
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }

}
