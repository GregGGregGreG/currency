package org.baddev.currency.ui.core.util;

import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
public final class Navigator {

    private Navigator() {}

    public static void navigate(String viewName) {
        UI.getCurrent().getNavigator().navigateTo(viewName);
    }

    public static View getCurrentView(){
        return UI.getCurrent().getNavigator().getCurrentView();
    }

}
