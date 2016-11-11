package org.baddev.currency.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.baddev.common.event.EventPublisher;
import org.baddev.common.mail.ApplicationMailer;
import org.baddev.common.utils.AssertUtils;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.dto.SignUpDTO;
import org.baddev.currency.core.exception.UserNotFoundException;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.jooq.schema.tables.daos.UserPreferencesDao;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.listener.MailExchangeCompletionListener;
import org.baddev.currency.ui.component.view.MainView;
import org.baddev.currency.ui.component.view.RatesView;
import org.baddev.currency.ui.component.view.ResetPasswordStep2View;
import org.baddev.currency.ui.component.view.SignInView;
import org.baddev.currency.ui.listener.UIExchangeCompletionListener;
import org.baddev.currency.ui.security.event.*;
import org.baddev.currency.ui.util.NotificationUtils;
import org.baddev.currency.ui.util.VaadinSessionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.UUID;

import static org.baddev.currency.core.security.utils.SecurityUtils.isLoggedIn;
import static org.baddev.currency.ui.util.NotificationUtils.notifySuccess;

@Theme("valo-default")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@PreserveOnRefresh
@SpringUI
@Push(transport = Transport.WEBSOCKET_XHR)
public class CurrencyUI extends UI implements SessionDestroyListener {

    private static final long serialVersionUID = 2223841791673821941L;

    @Autowired private Logger                         log;
    @Autowired private EventBus                       bus;
    @Autowired private UserService                    userService;
    @Autowired private NavigatorFactory               navigatorFactory;
    @Autowired private MainView                       mainView;
    @Autowired private EventPublisher                 userEventPublisher;
    @Autowired private UserPreferencesDao             userPreferencesDao;
    @Autowired private UIExchangeCompletionListener   uiListener;
    @Autowired private MailExchangeCompletionListener mailListener;
    @Autowired private ApplicationMailer              mailer;
    @Autowired private MessageDigestPasswordEncoder   encoder;

    @Value("${app.title}")
    private String pageTitle;

    @PostConstruct
    public void postConstruct(){
        AssertUtils.objectsNotNull(log, bus, userService, navigatorFactory, mainView,
                userEventPublisher, userPreferencesDao, uiListener, mailListener, pageTitle, mailer);
    }

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
                Throwable relevant = findRelevantThrowable(event.getThrowable());
                Throwable cause = ExceptionUtils.getRootCause(relevant);
                relevant = (cause == null) ? relevant : cause;
                if (relevant instanceof Exception) {
                    new UIErrorHandler().handle((Exception) relevant);
                } else super.error(event);
            }
        });
        setContent(mainView);
        setNavigator(navigatorFactory.create(this, mainView));
        uiListener.setUI(this);
        String fragment = Page.getCurrent().getLocation().getFragment();
        if (isLoggedIn()) {
            applyUserPreferences();
            getNavigator().navigateTo(RatesView.NAME);
        } else if (!StringUtils.isEmpty(fragment) && fragment.startsWith("!reset")) {
            getNavigator().navigateTo(fragment);
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
            notifySuccess("Account Creation",
                    String.format("Account \"%s\" successfully created. Please, use your credentials to sign in",
                            data.getUsername()));
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

    @Subscribe
    private void requestPasswordReset(ResetPwdStep1Event event) {
        String email = event.getEventData().getEmail();

        String rawToken = UUID.randomUUID().toString();

        try {
            userService.createPasswordResetToken(email, encoder.encodePassword(rawToken, null), 60);
        } catch (UserNotFoundException e){
            event.getBinder().clear();
            throw e;
        }

        String baseUrl = Page.getCurrent().getLocation().getScheme() + ":" + Page.getCurrent().getLocation().getSchemeSpecificPart();
        String url = baseUrl + "#!" + ResetPasswordStep2View.NAME + "/" + "?token=" + rawToken;

        mailer.sendMail(email, "Password Reset", "To reset your password click the link below: \n" +
                url);

        event.getBinder().clear();
        NotificationUtils.notifySuccessCloseable("Password Reset",
                "We sent an email to "+email+". Please, follow an instructions in the letter");
    }

    @Subscribe
    private void passwordReset(ResetPwdStep2Event event){
        userService.resetPassword(event.getEventData());
        getNavigator().navigateTo(SignInView.NAME);
        NotificationUtils.notifySuccess("Password Reset", "Password updated. Please, use your new credentials to login");
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

}
