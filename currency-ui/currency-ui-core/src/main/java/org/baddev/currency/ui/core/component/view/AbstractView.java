package org.baddev.currency.ui.core.component.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.ui.core.component.VerticalSpacedLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by IPotapchuk on 5/16/2016.
 */
public abstract class AbstractView extends VerticalLayout implements View {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        VerticalSpacedLayout rootLayout = contentRoot();
        addComponent(rootLayout);
        setSizeFull();
        setExpandRatio(rootLayout, 1.0f);
        postInit(rootLayout);
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

    public abstract String getNameCaption();

    public Collection<MenuBar.MenuItem> customizeMenuBar(MenuBar menuBar) {
        return Collections.emptyList();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    @PreDestroy
    public void destroy() {
        log.debug("View destroyed, {}", hashCode());
    }

}
