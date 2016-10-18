package org.baddev.currency.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.*;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;
import org.baddev.currency.core.event.Notifier;
import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.UserPreferencesDao;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.MailExchangeCompletionListener;
import org.baddev.currency.security.dto.LoginDTO;
import org.baddev.currency.security.dto.SignUpDTO;
import org.baddev.currency.security.user.UserService;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.view.LoginView;
import org.baddev.currency.ui.component.view.MainView;
import org.baddev.currency.ui.component.view.RatesView;
import org.baddev.currency.ui.listener.AppSessionInitListener;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.baddev.currency.ui.security.VaadinSessionSecurityContextHolderStrategy;
import org.baddev.currency.ui.security.event.LoginEvent;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.util.VaadinSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import static org.baddev.currency.security.utils.SecurityUtils.isLoggedIn;
import static org.baddev.currency.security.utils.SecurityUtils.loggedInUserName;
import static org.baddev.currency.ui.util.NotificationUtils.notifySuccess;

@Theme("valo-default")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@PreserveOnRefresh
@SpringUI
@Push(transport = Transport.WEBSOCKET_XHR)
public class CurrencyUI extends UI implements SessionDestroyListener {

    private static final long serialVersionUID = 2223841791673821941L;

    private static final Logger log = LoggerFactory.getLogger(CurrencyUI.class);

    @Autowired
    private EventBus bus;
    @Autowired
    private UserService userService;
    @Autowired
    private NavigatorFactory navigatorFactory;
    @Autowired
    private MainView mainView;
    @Autowired
    private Notifier notifier;
    @Autowired
    private UserPreferencesDao userPreferencesDao;
    @Autowired
    private MailExchangeCompletionListener mailListener;
    @Autowired
    private UIExchangeCompletionListener uiListener;

    public EventBus getEventBus() {
        return bus;
    }

    public static CurrencyUI get() {
        return (CurrencyUI) UI.getCurrent();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VaadinService.getCurrent().addSessionDestroyListener(this);
        setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                boolean handled = false;
                Throwable t = findRelevantThrowable(event.getThrowable());
                if (t instanceof Exception) {
                    handled = new UIErrorHandler().handle((Exception) t);
                }
                if (!handled) doDefault(event);
            }
        });
        setContent(mainView);
        setNavigator(navigatorFactory.create(this, mainView));
        if (isLoggedIn()) {
            UserPreferences prefs = VaadinSessionUtils.getAttribute(UserPreferences.class);
            setTheme(prefs.getThemeName());
            if (prefs.getUiNotifications())
                notifier.subscribe(uiListener);
            getNavigator().navigateTo(RatesView.NAME);
        } else getNavigator().navigateTo(LoginView.NAME);
    }

    @Override
    public void attach() {
        super.attach();
        bus.register(this);
    }

    @Override
    public void detach() {
        bus.unregister(this);
        notifier.unsubscribe(uiListener);
        super.detach();
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        userPreferencesDao.update(VaadinSessionUtils.getAttribute(UserPreferences.class));
        log.debug("Session destroyed, {}", event.getSession());
    }

    @Subscribe
    private void login(LoginEvent event) {
        try {
            userService.authenticate(event.getEventData());
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            UserPreferences preferences = userPreferencesDao.fetchOneByUserId(SecurityUtils.getIdentityUserPrincipal().getId());
            VaadinSessionUtils.setAttribute(UserPreferences.class, preferences);
            if(preferences.getMailNotifications())
                notifier.subscribe(mailListener);
            if(preferences.getUiNotifications())
                notifier.subscribe(uiListener);
            setTheme(preferences.getThemeName());
            getNavigator().navigateTo(RatesView.NAME);
        } catch (Exception e) {
            event.getBinder().clear();
            throw e;
        }
    }

    @Subscribe
    private void logout(LogoutEvent event) {
        getSession().close();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        getPage().reload();
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
            event.getBinder().getField("password").clear();
            event.getBinder().getField("confirmPassword").clear();
            throw e;
        }
    }

    @WebServlet(urlPatterns = "/*", name = "vaadinServlet", asyncSupported = true)
    @VaadinServletConfiguration(
            ui = CurrencyUI.class,
            productionMode = false,
            heartbeatInterval = 15,
            closeIdleSessions = true
    )
    public static class AppServlet extends SpringVaadinServlet {

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

    @WebListener
    public static class AppContextLoaderListener extends ContextLoaderListener {
    }

}
