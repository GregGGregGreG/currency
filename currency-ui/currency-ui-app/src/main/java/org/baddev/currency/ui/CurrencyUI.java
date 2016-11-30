package org.baddev.currency.ui;

import com.google.common.eventbus.EventBus;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.baddev.common.event.EventPublisher;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.jooq.schema.tables.daos.UserPreferencesDao;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.listener.MailExchangeCompletionListener;
import org.baddev.currency.ui.component.view.MainView;
import org.baddev.currency.ui.component.view.feature.RatesView;
import org.baddev.currency.ui.component.view.user.SignInView;
import org.baddev.currency.ui.core.EventBusAwareUI;
import org.baddev.currency.ui.core.component.view.AbstractView;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.baddev.currency.ui.core.util.VaadinSessionUtils;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.baddev.currency.ui.listener.eventbus.DeadEventListener;
import org.baddev.currency.ui.listener.eventbus.SettingsChangeListener;
import org.baddev.currency.ui.listener.eventbus.UserOperationsListener;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

import static org.baddev.currency.core.security.utils.SecurityUtils.isLoggedIn;
import static org.baddev.currency.ui.core.util.UIUtils.findComponent;

@SpringUI
@PreserveOnRefresh
@Theme("valo-default")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@Push(transport = Transport.WEBSOCKET_XHR)
public class CurrencyUI extends UI implements SessionDestroyListener, EventBusAwareUI {

    private static final long serialVersionUID = 2223841791673821941L;

    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private Logger log;
    @Getter @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private EventBus eventBus;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private MainView mainView;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private EventPublisher eventPublisher;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private UserPreferencesDao userPreferencesDao;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private NavigationConfigurer navigationConfigurer;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private UIExchangeCompletionListener uiListener;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private MailExchangeCompletionListener mailListener;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private UserOperationsListener userOperationsListener;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private SettingsChangeListener settingsChangeListener;
    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    private DeadEventListener deadEventListener;

    @Value("${app.title}")
    private String pageTitle;

    public static CurrencyUI get() {
        return (CurrencyUI) UI.getCurrent();
    }

    @PostConstruct
    private void setup(){
        getPage().setTitle(pageTitle);
        navigationConfigurer.configure();
        setErrorHandler(new DefaultErrorHandler() {
            @Override
            public void error(com.vaadin.server.ErrorEvent event) {
                Throwable relevant = findRelevantThrowable(event.getThrowable());
                Throwable cause = ExceptionUtils.getRootCause(relevant);
                relevant = (cause == null) ? relevant : cause;
                if (relevant instanceof Exception) {
                    new UIErrorHandler().handle((Exception) relevant);
                } else super.error(event);
            }
        });
        setupListeners();
        setContent(mainView);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        String fragment = Page.getCurrent().getLocation().getFragment();
        if (isLoggedIn()) {
            applyPreferences();
            getNavigator().navigateTo(RatesView.NAME);
        } else if (!StringUtils.isEmpty(fragment) && fragment.startsWith("!reset")) {
            getNavigator().navigateTo(fragment);
        } else getNavigator().navigateTo(SignInView.NAME);
    }

    private void setupListeners(){
        VaadinService.getCurrent().addSessionDestroyListener(this);
        uiListener.setUi(this);
        eventBus.register(userOperationsListener);
        eventBus.register(settingsChangeListener);
        eventBus.register(deadEventListener);
    }

    public void initPreferences() {
        UserPreferences preferences = userPreferencesDao.fetchOneByUserId(SecurityUtils.getIdentityUserPrincipal().getId());
        VaadinSessionUtils.setAttribute(UserPreferences.class, preferences);
        applyPreferences();
    }

    public void applyPreferences() {
        UserPreferences prefs = VaadinSessionUtils.getAttribute(UserPreferences.class);
        UI.getCurrent().setTheme(prefs.getThemeName());
        applyNotificationPreferences();
    }

    public void applyNotificationPreferences(){
        UserPreferences prefs = VaadinSessionUtils.getAttribute(UserPreferences.class);
        if (prefs.getUiNotifications())
            eventPublisher.subscribe(uiListener);
        if (prefs.getMailNotifications()) {
            mailListener.setEmail(SecurityUtils.getUserDetails().getEmail());
            eventPublisher.subscribe(mailListener);
        }
    }

    @Override
    public void attach() {
        super.attach();
        eventBus.register(this);
    }

    @Override
    public void detach() {
        eventBus.unregister(this);
        eventPublisher.unsubscribe(uiListener);
        super.detach();
    }

    @Override
    public void addWindow(Window window) throws IllegalArgumentException, NullPointerException {
        AbstractView view = findComponent(window, AbstractView.class);
        if (view == null || SecurityUtils.isAccessGranted(view.getClass())) {
            super.addWindow(window);
        } else
            NotificationUtils.notifyFailure("Access Denied", "Sorry, but <b>you don't have permissions</b> to access the requested window");
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        userPreferencesDao.update(VaadinSessionUtils.getAttribute(UserPreferences.class));
        log.debug("Session destroyed, {}", event.getSession());
    }

}
