package org.baddev.currency.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import org.baddev.currency.ui.component.view.AccessDeniedView;
import org.baddev.currency.ui.component.view.ErrorView;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
@SpringComponent
public class NavigatorFactory {

    @Autowired
    private SpringViewProvider viewProvider;
    @Autowired
    private ErrorView errorView;

    public Navigator create(UI ui, ViewDisplay display){
        Navigator navigator = new Navigator(UI.getCurrent(), display);
        navigator.setErrorProvider(new ViewProvider() {
            @Override
            public String getViewName(String viewAndParameters) {
                return navigator.getState();
            }

            @Override
            public View getView(String viewName) {
                return errorView;
            }
        });
        viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
        navigator.addProvider(viewProvider);
        return navigator;
    }
}
