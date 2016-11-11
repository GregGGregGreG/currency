package org.baddev.currency.ui.listener;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.baddev.currency.core.api.UserService;
import org.baddev.currency.ui.component.view.ResetPasswordStep2View;
import org.baddev.currency.ui.util.Navigator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
@SpringComponent
public class AppViewChangeListener implements ViewChangeListener {

    @Autowired
    private Logger log;
    @Autowired
    private UserService userService;

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        if (!(event.getNewView() instanceof ResetPasswordStep2View)) {
            return true;
        }

        ResetPasswordStep2View view = (ResetPasswordStep2View) event.getNewView();

        if (event.getParameters() == null || !event.getParameters().startsWith("?token")) {
            log.warn("Password reset failed: invalid parameters set");
            return error();
        }

        String params = event.getParameters().substring(1, event.getParameters().length());
        List<NameValuePair> parsedParameters = URLEncodedUtils.parse(params, Charset.defaultCharset());

        if (parsedParameters.size() != 1) {
            log.warn("Invalid parameters set");
            return error();
        }

        String rawToken = parsedParameters.get(0).getValue();

        if (!userService.isPasswordResetTokenValid(rawToken)) {
            log.warn("Password reset failed: invalid parameter value: {}", rawToken);
            return error();
        }

        view.setToken(rawToken);
        return true;
    }

    private static boolean error() {
        Navigator.navigate("error");
        return false;
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
    }
}
