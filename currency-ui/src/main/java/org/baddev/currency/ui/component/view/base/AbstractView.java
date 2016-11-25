package org.baddev.currency.ui.component.view.base;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import lombok.NonNull;
import lombok.Setter;
import org.baddev.currency.ui.component.base.VerticalSpacedLayout;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by IPotapchuk on 5/16/2016.
 */
public abstract class AbstractView extends VerticalLayout implements View, InitializingBean, DisposableBean {

    @Setter(onMethod = @__({@Autowired, @Required}), onParam = @__({@NonNull}))
    protected Logger log;

    @Override
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

    @Override
    public void destroy() {
        log.debug("View destroyed, {}", hashCode());
    }

}
