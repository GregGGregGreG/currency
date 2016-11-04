package org.baddev.currency.ui.component.view;

import com.vaadin.spring.annotation.SpringComponent;
import org.baddev.currency.ui.component.view.base.AbstractErrorView;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by IPotapchuk on 9/20/2016.
 */
@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class AccessDeniedView extends AbstractErrorView {

    public AccessDeniedView() {
        super("Sorry, but <b>you don't have permissions</b> to access the requested view <b>%s</b>");
    }

    @Override
    public String getNameCaption() {
        return "Access Denied";
    }
}
