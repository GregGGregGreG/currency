package org.baddev.currency.ui.config;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.spring.server.SpringVaadinServlet;
import org.baddev.currency.ui.CurrencyUI;
import org.baddev.currency.ui.listener.AppSessionInitListener;
import org.baddev.currency.ui.security.VaadinSessionSecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/*", name = "vaadinServlet", asyncSupported = true)
@VaadinServletConfiguration(
        ui = CurrencyUI.class,
        productionMode = false,
        heartbeatInterval = 15,
        closeIdleSessions = true
)
public class CurrencyAppServlet extends SpringVaadinServlet {

    private static final long serialVersionUID = -2328685994490607984L;

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
        getService().setSystemMessagesProvider(systemMessagesInfo -> {
            CustomizedSystemMessages systemMessages = new CustomizedSystemMessages();
            systemMessages.setSessionExpiredNotificationEnabled(false);
            systemMessages.setCommunicationErrorNotificationEnabled(false);
            return systemMessages;
        });
        getService().addSessionInitListener(new AppSessionInitListener());
    }
}