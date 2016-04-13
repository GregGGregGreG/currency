package org.baddev.currency.ui.view;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import org.baddev.currency.ui.MyUI;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        grid.setImmediate(true);
        if (excludeProps.length != 0)
            for (String prop : excludeProps)
                wrapperContainer.removeContainerProperty(prop);
        return container;
    }

    private HorizontalLayout topBar() {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setSizeUndefined();
        topBar.setWidth("100%");
        topBar.setSpacing(true);
        customizeTopBar(topBar);
        return topBar;
    }

    @SuppressWarnings("unchecked")
    protected BeanItemContainer<T> container() {
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

    protected void refresh(Collection<T> data, String sortPropertyId) {
        container().removeAllItems();
        container().addAll(data);
        grid.clearSortOrder();
        Notification.show("Updated", Notification.Type.TRAY_NOTIFICATION);
        grid.sort(sortPropertyId, SortDirection.DESCENDING);
    }

    protected void filter(String text){
        container().removeAllContainerFilters();
        if(!text.isEmpty()){
            List<Container.Filter> filters = new ArrayList<>();
            container().getContainerPropertyIds()
                    .forEach(p -> filters.add(new SimpleStringFilter(p, text, true, false)));
            container().addContainerFilter(new Or(filters.toArray(new Container.Filter[filters.size()])));
        }
    }

    protected void navigateTo(String viewName) {
        MyUI.current().getNavigator().navigateTo(viewName);
    }

    public static void attachComponents(AbstractOrderedLayout l, Component...cs) {
        Arrays.stream(cs).forEach(c -> {
            if(l.getComponentIndex(c)==-1)
                l.addComponent(c);
        });
    }

    public static void toggleVisibility(boolean visible, Component...components) {
        Arrays.stream(components).forEach(c -> c.setVisible(visible));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
