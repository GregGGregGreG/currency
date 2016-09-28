package org.baddev.currency.ui.component.view.base;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import static org.baddev.currency.ui.CurrencyUI.currencyUI;

/**
 * Created by IPotapchuk on 5/16/2016.
 */
public abstract class AbstractCcyView extends VerticalLayout implements View, InitializingBean,
        DisposableBean, BeanFactoryAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        VerticalLayout rootLayout = contentRoot();
        addComponent(rootLayout);
        setExpandRatio(rootLayout, 1.0f);
        postInit(rootLayout);
        setSizeFull();
        log.debug("View created {}, {}", getClass().getName(), hashCode());
    }

    private VerticalLayout contentRoot() {
        VerticalLayout root = new VerticalLayout();
        init(root);
        root.setSizeFull();
        return root;
    }

    protected void init(VerticalLayout rootLayout) {
    }

    protected void postInit(VerticalLayout rootLayout) {
    }

    public void customizeMenuBar(MenuBar menuBar) {
    }

    protected static void navigateTo(String viewName) {
        currencyUI().getNavigator().navigateTo(viewName);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    @Override
    public void destroy() {
        log.debug("View destroyed {}", getClass().getName(), hashCode());
    }

}
