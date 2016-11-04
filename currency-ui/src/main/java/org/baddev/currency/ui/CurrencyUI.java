package org.baddev.currency.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.*;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.SignInDTO;
import org.baddev.currency.core.dto.SignUpDTO;
import org.baddev.currency.core.event.EventPublisher;
import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.UserPreferencesDao;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.listener.MailExchangeCompletionListener;
import org.baddev.currency.security.utils.SecurityUtils;
import org.baddev.currency.ui.component.view.MainView;
import org.baddev.currency.ui.component.view.RatesView;
import org.baddev.currency.ui.component.view.SignInView;
import org.baddev.currency.ui.listener.AppSessionInitListener;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.baddev.currency.ui.security.VaadinSessionSecurityContextHolderStrategy;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.baddev.currency.ui.security.event.SignInEvent;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.util.VaadinSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired private EventBus                       bus;
    @Autowired private UserService                    userService;
    @Autowired private NavigatorFactory               navigatorFactory;
    @Autowired private MainView                       mainView;
    @Autowired private EventPublisher                 userEventPublisher;
    @Autowired private UserPreferencesDao             userPreferencesDao;
    @Autowired private UIExchangeCompletionListener   uiListener;
    @Autowired private MailExchangeCompletionListener mailListener;

    @Value("${app.title}")
    private String pageTitle;

    public EventBus getEventBus() {
        return bus;
    }

    public static CurrencyUI get() {
        return (CurrencyUI) UI.getCurrent();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Page.getCurrent().setTitle(pageTitle);
        VaadinService.getCurrent().addSessionDestroyListener(this);
        setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                Throwable t = findRelevantThrowable(event.getThrowable());
                if (t instanceof Exception) {
                    new UIErrorHandler().handle((Exception) t);
                } else super.error(event);
            }
        });
        setContent(mainView);
        setNavigator(navigatorFactory.create(this, mainView));
        uiListener.setUI(this);
        if (isLoggedIn()) {
            applyUserPreferences();
            getNavigator().navigateTo(RatesView.NAME);
        } else getNavigator().navigateTo(SignInView.NAME);
    }

    @Subscribe
    private void signIn(SignInEvent event) {
        try {
            userService.authenticate(event.getEventData());
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            initUserPreferences();
            getNavigator().navigateTo(RatesView.NAME);
        } catch (Exception e) {
            event.getBinder().clear();
            throw e;
        }
    }

    @Subscribe
    private void signUp(SignUpEvent event) {
        SignUpDTO data = event.getEventData();
        try {
            userService.signUp(event.getEventData(), RoleEnum.USER);
            signIn(new SignInEvent(this, new SignInDTO(data.getUsername(), data.getPassword())));
            notifySuccess("Account Creation",
                    String.format("Account \"%s\" successfully created", loggedInUserName()));
        } catch (Exception e) {
            event.getBinder().getField("password").clear();
            event.getBinder().getField("confirmPassword").clear();
            throw e;
        }
    }

    @Subscribe
    private void logout(LogoutEvent event) {
        getSession().close();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        getPage().reload();
    }

    private void applyUserPreferences() {
        UserPreferences prefs = VaadinSessionUtils.getAttribute(UserPreferences.class);
        setTheme(prefs.getThemeName());
        if (prefs.getUiNotifications())
            userEventPublisher.subscribe(uiListener);
        if(prefs.getMailNotifications()) {
            mailListener.setEmail(SecurityUtils.getUserDetails().getEmail());
            userEventPublisher.subscribe(mailListener);
        }
    }

    private void initUserPreferences() {
        UserPreferences preferences = userPreferencesDao.fetchOneByUserId(SecurityUtils.getIdentityUserPrincipal().getId());
        VaadinSessionUtils.setAttribute(UserPreferences.class, preferences);
        applyUserPreferences();
    }

    @Override
    public void attach() {
        super.attach();
        bus.register(this);
    }

    @Override
    public void detach() {
        bus.unregister(this);
        userEventPublisher.unsubscribe(uiListener);
        super.detach();
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        userPreferencesDao.update(VaadinSessionUtils.getAttribute(UserPreferences.class));
        log.debug("Session destroyed, {}", event.getSession());
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
