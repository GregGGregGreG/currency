package org.baddev.currency.ui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.*;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;
import org.baddev.currency.core.event.ExchangeCompletionEvent;
import org.baddev.currency.core.event.NotificationListener;
import org.baddev.currency.core.event.Notifier;
import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeOperation;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.ExchangeCompletionMailer;
import org.baddev.currency.security.dto.LoginDTO;
import org.baddev.currency.security.dto.SignUpDTO;
import org.baddev.currency.security.user.UserService;
import org.baddev.currency.ui.component.view.ErrorView;
import org.baddev.currency.ui.component.view.LoginView;
import org.baddev.currency.ui.component.view.MainView;
import org.baddev.currency.ui.component.view.RatesView;
import org.baddev.currency.ui.config.SessionInitDestroyListener;
import org.baddev.currency.ui.security.VaadinSessionSecurityContextHolderStrategy;
import org.baddev.currency.ui.security.event.LoginEvent;
import org.baddev.currency.ui.security.event.LogoutEvent;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.service.UserPreferencesService;
import org.baddev.currency.ui.util.FormatUtils;
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
import static org.baddev.currency.ui.util.AppSettingsUtils.applyUserPreferences;
import static org.baddev.currency.ui.util.NotificationUtils.notifySuccess;
import static org.baddev.currency.ui.util.NotificationUtils.notifyTray;
import static org.baddev.currency.ui.util.VaadinSessionUtils.*;

@Theme("valo-default")
@Widgetset("org.baddev.currency.ui.MyAppWidgetset")
@PreserveOnRefresh
@SpringUI
@Push(transport = Transport.WEBSOCKET_XHR)
public class CurrencyUI extends UI implements NotificationListener<ExchangeCompletionEvent> {

    private static final Logger log = LoggerFactory.getLogger(CurrencyUI.class);

    @Autowired
    private Notifier notifier;
    @Autowired
    private EventBus bus;
    @Autowired
    private Iso4217CcyService ccyService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserPreferencesService preferencesService;
    @Autowired
    private ExchangeCompletionMailer mailer;
    @Autowired
    private NavigatorFactory navigatorFactory;
    @Autowired
    private MainView mainView;
    @Autowired
    private ErrorView errorView;

    public EventBus getEventBus(){
        return bus;
    }

    public static CurrencyUI get() {
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
        setErrorHandler(new DefaultErrorHandler(){
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
        setNavigator(navigatorFactory.create(this, mainView, errorView));
        if (isLoggedIn()) {
            setTheme(getSessionAttribute(UserPreferences.class).getThemeName());
            getNavigator().navigateTo(RatesView.NAME);
        } else getNavigator().navigateTo(LoginView.NAME);
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
    public static class AppServlet extends SpringVaadinServlet {

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
            SessionInitDestroyListener listener = new SessionInitDestroyListener();
            getService().addSessionInitListener(listener);
            getService().addSessionDestroyListener(listener);
        }
    }

    @WebListener
    public static class AppContextLoaderListener extends ContextLoaderListener {
    }

}
