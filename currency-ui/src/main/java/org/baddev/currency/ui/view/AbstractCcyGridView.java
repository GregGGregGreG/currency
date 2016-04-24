package org.baddev.currency.ui.view;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.ui.MyUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * Created by IPotapchuk on 4/5/2016.
 */
public abstract class AbstractCcyGridView<T> extends VerticalLayout implements View {

    private static final Logger log = LoggerFactory.getLogger(AbstractCcyGridView.class);

    protected Grid grid;

    @Resource(name = "Iso4217Service")
    private Iso4217CcyService iso4217Service;

    public Iso4217CcyService iso4217Service() {
        return iso4217Service;
    }

    @PostConstruct
    public abstract void init();

    protected void setup(Class<T> type, Collection<T> items, String... excludeProps) {
        setSizeFull();
        setup(type, excludeProps);
        refresh(items);
        addComponent(gridWithTopBar());
    }

    private void setup(Class<T> type, String... excludeProps) {
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
    }

    private HorizontalLayout topBar() {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setSizeUndefined();
        topBar.setWidth("100%");
        topBar.setSpacing(true);
        customizeTopBar(topBar);
        return topBar;
    }

    protected Object getSelectedRow() {
        return ((com.vaadin.ui.Grid.SingleSelectionModel) grid.getSelectionModel()).getSelectedRow();
    }

    @SuppressWarnings("unchecked")
    protected BeanItemContainer<T> container() {
        return ((BeanItemContainer<T>) ((GeneratedPropertyContainer) grid.getContainerDataSource())
                .getWrappedContainer());
    }

    protected GeneratedPropertyContainer containerWrapper() {
        return (GeneratedPropertyContainer) grid.getContainerDataSource();
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
        Notification.show("Fetched " + data.size() + " records", Notification.Type.TRAY_NOTIFICATION);
//        grid.sort(sortPropertyId);
    }

    protected void filter(String text) {
        container().removeAllContainerFilters();
        if (!text.isEmpty()) {
            List<Container.Filter> filters = new ArrayList<>();
            container().getContainerPropertyIds()
                    .forEach(p -> filters.add(new SimpleStringFilter(p, text, true, false)));
            container().addContainerFilter(new Or(filters.toArray(new Container.Filter[filters.size()])));
        }
    }

    protected void navigateTo(String viewName) {
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

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
