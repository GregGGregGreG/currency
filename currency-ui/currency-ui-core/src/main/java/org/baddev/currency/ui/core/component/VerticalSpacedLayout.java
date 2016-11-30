package org.baddev.currency.ui.core.component;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
public class VerticalSpacedLayout extends VerticalLayout {

    public VerticalSpacedLayout() {
        setup();
    }

    public VerticalSpacedLayout(Component... children) {
        super(children);
        setup();
    }

    private void setup(){
        setMargin(true);
        setSpacing(true);
    }
}
