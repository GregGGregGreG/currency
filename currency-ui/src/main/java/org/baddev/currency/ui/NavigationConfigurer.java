package org.baddev.currency.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import lombok.RequiredArgsConstructor;
import org.baddev.currency.ui.component.view.error.AccessDeniedView;
import org.baddev.currency.ui.component.view.error.ErrorView;
import org.baddev.currency.ui.listener.AppViewChangeListener;

import java.io.Serializable;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
@SpringComponent
@UIScope
@RequiredArgsConstructor
public class NavigationConfigurer implements Serializable {

    private static final long serialVersionUID = -4392990222872924283L;

    private final SpringViewProvider    viewProvider;
    private final SpringNavigator       navigator;
    private final AppViewChangeListener viewChangeListener;

    public Navigator configure(){
        viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
        navigator.setErrorView(ErrorView.class);
        navigator.addViewChangeListener(viewChangeListener);
        return navigator;
    }
}
