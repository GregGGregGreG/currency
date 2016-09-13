package org.baddev.currency.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.*;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.event.NotificationEvent;
import org.baddev.currency.core.listener.NotificationListener;
import org.baddev.currency.core.notifier.Notifier;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.security.RoleEnum;
import org.baddev.currency.security.SecurityService;
import org.baddev.currency.security.SecurityUtils;
import org.baddev.currency.ui.component.view.LoginView;
import org.baddev.currency.ui.component.view.RatesView;
import org.baddev.currency.ui.security.dto.LoginData;
import org.baddev.currency.ui.security.dto.SignUpData;
import org.baddev.currency.ui.security.event.LoginEvent;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import static org.baddev.currency.security.SecurityUtils.isLoggedIn;
import static org.baddev.currency.security.SecurityUtils.loggedInUserName;
import static org.baddev.currency.ui.util.AppSettingsUtils.initializeSettings;
import static org.baddev.currency.ui.util.AppSettingsUtils.toggleUINotifications;
import static org.baddev.currency.ui.util.NotificationUtils.*;

@Theme("mytheme")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@PreserveOnRefresh
@SpringUI
@Push(transport = Transport.WEBSOCKET_XHR)
public class CurrencyUI extends UI implements NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(CurrencyUI.class);

    @Autowired
    private SpringViewProvider viewProvider;
    @Autowired
    private Notifier notifier;
    @Autowired
    private EventBus bus;
    @Autowired
    private Iso4217CcyService ccyService;
    @Autowired
    private SecurityService security;

    public static CurrencyUI currencyUI() {
        return (CurrencyUI) UI.getCurrent();
    }

    public void registerListener(NotificationListener l) {
        notifier.subscribe(l);
    }

    public void unregisterListener(NotificationListener l) {
        notifier.unsubscribe(l);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);
        setNavigator(navigator);
        if (isLoggedIn())
            navigator.navigateTo(RatesView.NAME);
        else navigator.navigateTo(LoginView.NAME);
    }

    @Override
    public <T extends NotificationEvent> void onNotificationEventReceived(T e) {
        if (e instanceof ExchangeCompletionEvent) {
            ExchangeOperation operation = ((ExchangeCompletionEvent) e).getEventData();
            access(() -> {
                String fromCcyNames = FormatUtils.formatCcyParamValuesList(
                        ccyService.findCcyNamesByCode(operation.getFromCcy())
                );
                String toCcyNames = FormatUtils.formatCcyParamValuesList(
                        ccyService.findCcyNamesByCode(operation.getToCcy())
                );
                String exchInfo = String.format("%.2f %s(%s) <> %.2f %s(%s)",
                        operation.getFromAmount(),
                        fromCcyNames,
                        operation.getFromCcy(),
                        operation.getToAmount(),
                        toCcyNames,
                        operation.getToCcy());
                notifyTray("Exchange task completion", exchInfo);
            });
        }
    }

    @Subscribe
    private void login(LoginEvent event) {
        try {
            security.authenticate(
                    event.getEventData().getUsername(), event.getEventData().getPassword());
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            getNavigator().navigateTo(RatesView.NAME);
        } catch (AuthenticationException e) {
            log.debug("Authentication error", e);
            notifyFailure("Authentication Error", e.getMessage());
        }
    }

    @Subscribe
    private void logout(LogoutEvent event) {
        String userName = SecurityUtils.loggedInUserName();
        getSession().close();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        getPage().reload();
        showStyledNotification("Logout",
                String.format("User \"%s\" logged out", userName),
                3000,
                Position.BOTTOM_CENTER,
                ValoTheme.NOTIFICATION_WARNING);
    }

    @Subscribe
    private void signUp(SignUpEvent event) {
        SignUpData data = event.getEventData();
        UserDetails details = new UserDetails()
                .setFirstName(data.getFirstName())
                .setLastName(data.getLastName());
        try {
            security.signUp(data.getUsername(), data.getPassword(), details, RoleEnum.USER);
            login(new LoginEvent(this, new LoginData(data.getUsername(), data.getPassword())));
            notifySuccess("Account Creation",
                    String.format("Account \"%s\" successfully created", loggedInUserName()));
        } catch (Exception e) {
            log.debug("Sign up error", e);
            notifyFailure("Sign Up Error", e.getMessage());
        }
    }

    @Override
    public void attach() {
        super.attach();
        bus.register(this);
    }

    @Override
    public void detach() {
        toggleUINotifications(false);
        bus.unregister(this);
        super.detach();
    }

    @WebServlet(urlPatterns = "/*", name = "vaadinServlet", asyncSupported = true)
    @VaadinServletConfiguration(
            ui = CurrencyUI.class,
            productionMode = false,
            heartbeatInterval = 15,
            closeIdleSessions = true
    )
    public static class AppServlet extends SpringVaadinServlet
            implements SessionInitListener, SessionDestroyListener {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().setSystemMessagesProvider(systemMessagesInfo -> {
                CustomizedSystemMessages systemMessages = new CustomizedSystemMessages();
                systemMessages.setSessionExpiredNotificationEnabled(false);
                systemMessages.setCommunicationErrorNotificationEnabled(false);
                return systemMessages;
            });
            getService().addSessionInitListener(this);
            getService().addSessionDestroyListener(this);
        }

        @Override
        public void sessionInit(SessionInitEvent event) throws ServiceException {
            initializeSettings();
            log.debug("Session initialized, {}", event.getSession());
        }

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            log.debug("Session destroyed, {}", event.getSession());
        }

    }

    @WebListener
    public static class AppContextLoaderListener extends ContextLoaderListener {
    }

}
