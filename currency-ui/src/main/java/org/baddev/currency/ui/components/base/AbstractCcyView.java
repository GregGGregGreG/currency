package org.baddev.currency.ui.components.base;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.ui.MyUI;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Created by IPotapchuk on 5/16/2016.
 */
public abstract class AbstractCcyView extends VerticalLayout implements View {

    @Resource(name = "Iso4217Service")
    protected Iso4217CcyService iso4217Service;

    @PostConstruct
    public void init(){
        setSizeFull();
        addComponent(contentRoot());
    }

    protected VerticalLayout contentRoot(){
        VerticalLayout content = new VerticalLayout();
        content.addComponent(menuBar());
        content.setSizeFull();
        return content;
    }

    protected MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setWidth(100.0f, Unit.PERCENTAGE);
        menuBar.addStyleName("small");
        customizeMenuBar(menuBar);
        return menuBar;
    }

    protected abstract void customizeMenuBar(MenuBar menuBar);

    protected final void navigateTo(String viewName) {
        MyUI.current().getNavigator().navigateTo(viewName);
    }

    public static void attachComponents(AbstractOrderedLayout l, Component... cs) {
        Arrays.stream(cs).forEach(c -> {
            if (l.getComponentIndex(c) == -1)
                l.addComponent(c);
        });
    }

    public static void toggleVisibility(boolean visible, Component... components) {
        Arrays.stream(components).forEach(c -> c.setVisible(visible));
    }

    public static void toggleEnabled(boolean enabled, Component... components) {
        Arrays.stream(components).forEach(c -> c.setEnabled(enabled));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}
