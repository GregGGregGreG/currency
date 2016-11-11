package org.baddev.currency.ui.util;

import com.vaadin.navigator.View;

import static org.baddev.currency.ui.CurrencyUI.get;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
public final class Navigator {

    private Navigator() {}

    public static void navigate(String viewName) {
        get().getNavigator().navigateTo(viewName);
    }

    public static View getCurrentView(){
        return get().getNavigator().getCurrentView();
    }

}
