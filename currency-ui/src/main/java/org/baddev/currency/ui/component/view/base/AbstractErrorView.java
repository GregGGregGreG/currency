package org.baddev.currency.ui.component.view.base;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import lombok.AccessLevel;
import lombok.Setter;
import org.baddev.currency.ui.component.base.VerticalSpacedLayout;

import java.util.IllegalFormatException;

public abstract class AbstractErrorView extends AbstractView {

    private Label msgLabel;
    @Setter(AccessLevel.PROTECTED)
    private String errorMsg = "";

    @Override
    protected void init(VerticalSpacedLayout rootLayout) {
        msgLabel = new Label();
        rootLayout.addComponent(msgLabel);
        msgLabel.addStyleName(ValoTheme.LABEL_FAILURE);
        msgLabel.setContentMode(ContentMode.HTML);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        super.enter(event);
        try {
            msgLabel.setValue(String.format(errorMsg, event.getViewName()));
        } catch (IllegalFormatException e) {
            msgLabel.setValue(errorMsg);
        }
    }
}