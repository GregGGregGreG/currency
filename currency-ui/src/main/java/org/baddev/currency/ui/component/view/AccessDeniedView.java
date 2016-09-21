package org.baddev.currency.ui.component.view;

import com.google.common.eventbus.EventBus;
import com.vaadin.spring.annotation.SpringComponent;
import org.baddev.currency.ui.component.base.AbstractErrorView;
import org.baddev.currency.ui.component.window.SettingsWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by IPotapchuk on 9/20/2016.
 */
@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class AccessDeniedView extends AbstractErrorView {

    @Autowired
    public AccessDeniedView(SettingsWindow settingsWindow, EventBus bus) {
        super(settingsWindow, bus, "Sorry, but <b>you don't have permissions</b> to access the requested view <b>%s</b>");
    }

}
