package org.baddev.currency.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.*;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.listener.NotificationListener;
import org.baddev.currency.core.notifier.Notifier;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.ExchangeCompletionMailer;
import org.baddev.currency.security.dto.LoginDTO;
import org.baddev.currency.security.dto.SignUpDTO;
import org.baddev.currency.security.user.UserService;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.NavigationViewWrapper;
import org.baddev.currency.ui.component.view.AccessDeniedView;
import org.baddev.currency.ui.component.view.ErrorView;
import org.baddev.currency.ui.component.view.LoginView;
import org.baddev.currency.ui.component.view.RatesView;
import org.baddev.currency.ui.security.VaadinSessionSecurityContextHolderStrategy;
import org.baddev.currency.ui.security.event.LoginEvent;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.service.UserPreferencesService;
import org.baddev.currency.ui.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import static org.baddev.currency.security.utils.SecurityUtils.isLoggedIn;
import static org.baddev.currency.security.utils.SecurityUtils.loggedInUserName;
import static org.baddev.currency.ui.util.AppSettingsUtils.applyUserPreferences;
import static org.baddev.currency.ui.util.NotificationUtils.*;
import static org.baddev.currency.ui.util.VaadinSessionUtils.*;

@Theme("valo-default")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@PreserveOnRefresh
@SpringUI
@Push(transport = Transport.WEBSOCKET_XHR)
public class CurrencyUI extends UI implements NotificationListener<ExchangeCompletionEvent> {

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
    private UserService userService;
    @Autowired
    private ApplicationContext ctx;
    @Autowired
    private UserPreferencesService preferencesService;
    @Autowired
    private ExchangeCompletionMailer mailer;
    @Autowired
    private NavigationViewWrapper wrapper;

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
        viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
        setContent(wrapper);
        Navigator navigator = new Navigator(this, (ViewDisplay) wrapper);
        navigator.setErrorProvider(new ViewProvider() {
            @Override
            public String getViewName(String viewAndParameters) {
                return navigator.getState();
            }

            @Override
            public View getView(String viewName) {
                return ctx.getBean(ErrorView.class);
            }
        });
        navigator.addProvider(viewProvider);
        setNavigator(navigator);
        if (isLoggedIn()) {
            setTheme(getSessionAttribute(UserPreferences.class).getThemeName());
            navigator.navigateTo(RatesView.NAME);
        } else navigator.navigateTo(LoginView.NAME);
    }

    @Override
    protected void refresh(VaadinRequest request) {
        super.refresh(request);
    }

    @Override
    public void notificationReceived(ExchangeCompletionEvent e) {
        IExchangeOperation operation = e.getEventData();
        access(() -> {
            String fromCcyNames = FormatUtils.joinByComma(
                    ccyService.findCcyNamesByCode(operation.getFromCcy())
            );
            String toCcyNames = FormatUtils.joinByComma(
                    ccyService.findCcyNamesByCode(operation.getToCcy())
            );
            String exchInfo = String.format("%.2f %s(%s) <> %.2f %s(%s)",
                    operation.getFromAmount(),
                    fromCcyNames,
                    operation.getFromCcy(),
                    operation.getToAmount(),
                    toCcyNames,
                    operation.getToCcy());
            notifyTray("Exchange Task Completion", exchInfo);
        });
    }

    @Subscribe
    private void login(LoginEvent event) {
        try {
            userService.authenticate(event.getEventData());
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            preferencesService.loadPreferencesIntoSession();
            applyUserPreferences(mailer);
            getNavigator().navigateTo(RatesView.NAME);
        } catch (AuthenticationException e) {
            event.getBinder().clear();
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
        SignUpDTO data = event.getEventData();
        try {
            userService.signUp(event.getEventData(), RoleEnum.USER);
            login(new LoginEvent(this, new LoginDTO(data.getUsername(), data.getPassword())));
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
        if (!isSessionAttributeExist(UserPreferences.class))
            setSessionAttribute(UserPreferences.class, new UserPreferences());
    }

    @Override
    public void detach() {
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
            SecurityContextHolder.setStrategyName(VaadinSessionSecurityContextHolderStrategy.class.getName());
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
