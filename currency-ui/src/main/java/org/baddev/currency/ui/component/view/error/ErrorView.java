package org.baddev.currency.ui.component.view.error;

import com.vaadin.spring.annotation.SpringComponent;
import org.baddev.currency.ui.component.view.base.AbstractErrorView;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by IPotapchuk on 9/20/2016.
 */
@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class ErrorView extends AbstractErrorView {

    private static final long serialVersionUID = 110792557690891243L;

    public ErrorView() {
        setErrorMsg("Requested view with name <b>%s</b> was not found");
    }

    @Override
    public String getNameCaption() {
        return "Error";
    }
}
