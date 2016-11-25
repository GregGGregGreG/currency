package org.baddev.currency.ui.listener.eventbus;

import com.google.common.eventbus.Subscribe;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.baddev.common.mail.ApplicationMailer;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.core.security.utils.SecurityUtils;
import org.baddev.currency.jooq.schema.tables.interfaces.IUser;
import org.baddev.currency.ui.CurrencyUI;
import org.baddev.currency.ui.component.view.feature.RatesView;
import org.baddev.currency.ui.component.view.user.ResetPasswordStep2View;
import org.baddev.currency.ui.component.view.user.SignInView;
import org.baddev.currency.ui.event.LogoutEvent;
import org.baddev.currency.ui.event.binder.*;
import org.baddev.currency.ui.util.Navigator;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

import java.io.Serializable;
import java.util.UUID;

import static org.baddev.currency.ui.util.VaadinSessionUtils.getSession;

/**
 * Created by IPotapchuk on 11/18/2016.
 */
@SpringComponent
@UIScope
@RequiredArgsConstructor
public class UserOperationsListener implements Serializable {

    private static final long serialVersionUID = -4977724952711813147L;

    private final UserService                  userService;
    private final MessageDigestPasswordEncoder encoder;
    private final ApplicationMailer            mailer;

    @Subscribe
    private void signIn(SignInEvent event) {
        event.process(signInEvent -> {
            userService.authenticate(signInEvent.getEventData());
            VaadinService.reinitializeSession(VaadinService.getCurrentRequest());
            CurrencyUI.get().initPreferences();
        });
        Navigator.navigate(RatesView.NAME);
    }

    @Subscribe
    private void signUp(SignUpEvent event) {
        event.process(signUpEvent -> userService.signUp(signUpEvent.getEventData(), RoleEnum.USER));
        Navigator.navigate(SignInView.NAME);
    }

    @Subscribe
    private void logout(LogoutEvent event) {
        getSession().close();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        Page.getCurrent().reload();
    }

    @Subscribe
    private void requestPasswordReset(ResetPwdStep1Event event) {
        event.process(resetPwd1Event -> {
            String email = resetPwd1Event.getEventData().getEmail();
            String rawToken = UUID.randomUUID().toString();
            userService.createPasswordResetToken(email, encoder.encodePassword(rawToken, null), 60);
            String baseUrl = Page.getCurrent().getLocation().getScheme() + ":" + Page.getCurrent().getLocation().getSchemeSpecificPart();
            String url = baseUrl + "#!" + ResetPasswordStep2View.NAME + "/" + "?token=" + rawToken;
            mailer.sendMail(email, "Password Reset", "To reset your password click the link below: \n" +
                    url);
            resetPwd1Event.getBinder().clear();
        });
    }

    @Subscribe
    private void passwordReset(ResetPwdStep2Event event) {
        event.process(resetPwd2Event -> {
            userService.resetPassword(resetPwd2Event.getEventData());
            resetPwd2Event.getBinder().clear();
        });
        Navigator.navigate(SignInView.NAME);
    }

    @Subscribe
    private void passwordChange(PasswordChangeEvent event) {
        event.process(pwdChangeEvent -> {
            userService.changePassword(pwdChangeEvent.getEventData());
            pwdChangeEvent.getBinder().clear();
        });
    }

    @Subscribe
    private void accountDetailsChange(AccountDetailsChangeEvent event) {
        event.process(detailsChangeEvent -> {
            userService.update(null, detailsChangeEvent.getEventData());
            SecurityUtils.setUserDetails(detailsChangeEvent.getEventData());
        });
    }

    @Subscribe
    private void userRestrictionsChange(RestrictionsChangeEvent event) {
        event.process(binderEvent -> {
            IUser user = userService.findOneUserByUserName(binderEvent.getEventData().getUserName())
                    .map(u -> {
                        u.setEnabled(binderEvent.getEventData().isAccountEnabled());
                        u.setAccNonLocked(binderEvent.getEventData().isAccountNotLocked());
                        u.setAccNonExpired(binderEvent.getEventData().isAccountNotExpired());
                        u.setCredNonExpired(binderEvent.getEventData().isCredentialsNotExpired());
                        return u;
                    }).orElseThrow(() -> new ServiceException("User not found: " + binderEvent.getEventData().getUserName()));
            userService.update(user, null);
        });
    }

}
