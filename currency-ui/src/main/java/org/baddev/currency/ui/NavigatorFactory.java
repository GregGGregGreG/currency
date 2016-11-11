package org.baddev.currency.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import org.baddev.currency.ui.component.view.AccessDeniedView;
import org.baddev.currency.ui.component.view.base.AbstractErrorView;
import org.baddev.currency.ui.listener.AppViewChangeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
@SpringComponent
public class NavigatorFactory {

    private final SpringViewProvider viewProvider;
    private final AbstractErrorView errorView;
    private final AppViewChangeListener viewChangeListener;

    @Autowired
    public NavigatorFactory(SpringViewProvider viewProvider, AbstractErrorView errorView, AppViewChangeListener viewChangeListener) {
        Assert.notNull(viewProvider, "viewProvider can't be null");
        Assert.notNull(errorView, "errorView can't be null");
        Assert.notNull(viewChangeListener, "viewChangeListener can't be null");
        this.viewProvider = viewProvider;
        this.errorView = errorView;
        this.viewChangeListener = viewChangeListener;
    }

    public Navigator create(UI ui, ViewDisplay display){
        Navigator navigator = new Navigator(ui, display);
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
        navigator.addViewChangeListener(viewChangeListener);
        return navigator;
    }
}
