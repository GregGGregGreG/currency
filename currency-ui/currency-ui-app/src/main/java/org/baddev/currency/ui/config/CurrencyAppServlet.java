package org.baddev.currency.ui.config;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.spring.server.SpringVaadinServlet;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.ui.CurrencyUI;
import org.baddev.currency.ui.core.util.VaadinSessionUtils;
import org.baddev.currency.ui.security.VaadinSessionSecurityContextHolderStrategy;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final long   serialVersionUID = -2328685994490607984L;
    private static final Logger log              = LoggerFactory.getLogger(CurrencyAppServlet.class);

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
        getService().addSessionInitListener(event -> {
            VaadinSessionUtils.setAttribute(UserPreferences.class, new UserPreferences());
            event.getSession().addBootstrapListener(new BootstrapListener() {
                @Override
                public void modifyBootstrapPage(final BootstrapPageResponse response) {
                    final Element head = response.getDocument().head();
                    head.appendElement("meta")
                            .attr("name", "viewport")
                            .attr("content",
                                    "width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no");
                    head.appendElement("meta")
                            .attr("name", "apple-mobile-web-app-capable")
                            .attr("content", "yes");
                    head.appendElement("meta")
                            .attr("name", "apple-mobile-web-app-status-bar-style")
                            .attr("content", "black-translucent");
                }

                @Override
                public void modifyBootstrapFragment(BootstrapFragmentResponse response) {}
            });
        });
    }
}