package org.baddev.currency.ui.component.view.error;

import com.vaadin.spring.annotation.SpringComponent;
import org.baddev.currency.ui.core.component.view.AbstractErrorView;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by IPotapchuk on 9/20/2016.
 */
@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class AccessDeniedView extends AbstractErrorView {

    private static final long serialVersionUID = 8268092077665984858L;

    public AccessDeniedView() {
        setErrorMsg("Sorry, but <b>you don't have permissions</b> to access the requested view <b>%s</b>");
    }

    @Override
    public String getNameCaption() {
        return "Access Denied";
    }
}
