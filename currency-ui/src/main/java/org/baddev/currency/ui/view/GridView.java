package org.baddev.currency.ui.view;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.ui.MyUI;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * Created by IPotapchuk on 4/5/2016.
 */
public abstract class GridView<T> extends VerticalLayout implements View {

    protected Grid grid;

    @PostConstruct
    public abstract void init();

    protected void setup(Class<T> type, Collection<T> items, String... excludeProps) {
        setSizeFull();
        setupGrid(type, excludeProps).addAll(items);
        addComponent(gridWithTopBar());
    }

    private BeanItemContainer<T> setupGrid(Class<T> type, String... excludeProps) {
        grid = new Grid();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        BeanItemContainer<T> container = new BeanItemContainer<>(type);
        GeneratedPropertyContainer wrapperContainer = new GeneratedPropertyContainer(container);
        grid.setContainerDataSource(wrapperContainer);

        if (excludeProps.length != 0)
            for (String prop : excludeProps)
                wrapperContainer.removeContainerProperty(prop);
        return container;
    }

    private HorizontalLayout topBar() {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setSpacing(true);
        topBar.setWidth("100%");
        customizeTopBar(topBar);
        return topBar;
    }

    @SuppressWarnings("unchecked")
    private BeanItemContainer<T> container() {
        return ((BeanItemContainer<T>) ((GeneratedPropertyContainer) grid.getContainerDataSource())
                .getWrappedContainer());
    }

    protected abstract void customizeTopBar(HorizontalLayout topBar);

    private VerticalLayout gridWithTopBar() {
        VerticalLayout gridWithBar = new VerticalLayout();
        gridWithBar.addComponent(topBar());
        gridWithBar.addComponent(grid);
        gridWithBar.setMargin(true);
        gridWithBar.setSpacing(true);
        gridWithBar.setSizeFull();
        gridWithBar.setExpandRatio(grid, 1);
        return gridWithBar;
    }

    protected void refresh(Collection<T> data) {
        container().removeAllItems();
        container().addAll(data);
        grid.clearSortOrder();
    }

    protected void navigateTo(String viewName) {
        MyUI.current().getNavigator().navigateTo(viewName);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
