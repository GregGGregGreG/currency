package org.baddev.currency.ui.component.view.base;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.ui.component.base.VerticalSpacedLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

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
        VerticalSpacedLayout rootLayout = contentRoot();
        addComponent(rootLayout);
        setExpandRatio(rootLayout, 1.0f);
        postInit(rootLayout);
        setSizeFull();
        log.debug("View created, {}", hashCode());
    }

    private VerticalSpacedLayout contentRoot() {
        VerticalSpacedLayout root = new VerticalSpacedLayout();
        init(root);
        root.setSizeFull();
        return root;
    }

    protected void init(VerticalSpacedLayout rootLayout) {
    }

    protected void postInit(VerticalSpacedLayout rootLayout) {
    }

    public void customizeMenuBar(MenuBar menuBar) {
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    @Override
    public void destroy() {
        log.debug("View destroyed, {}", hashCode());
    }

}
