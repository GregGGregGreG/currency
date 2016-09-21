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
public class ErrorView extends AbstractErrorView {

    @Autowired
    public ErrorView(SettingsWindow settingsWindow, EventBus bus) {
        super(settingsWindow, bus, "Requested view with name <b>%s</b> was not found");
    }

}
