package org.baddev.currency.ui.component.base;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public abstract class AbstractFormView extends AbstractCcyView {

    @Override
    protected VerticalLayout contentRoot() {
        VerticalLayout root = super.contentRoot();
        FormLayout form = new FormLayout();
        customizeForm(form);
        root.addComponent(form);
        root.setExpandRatio(form, 1.0f);
        root.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        return root;
    }

    protected abstract void customizeForm(FormLayout formLayout);

}
