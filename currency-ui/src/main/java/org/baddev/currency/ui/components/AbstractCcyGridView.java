package org.baddev.currency.ui.components;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.ui.MyUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by IPotapchuk on 4/5/2016.
 */
public abstract class AbstractCcyGridView<T> extends VerticalLayout implements View {

    protected static final Logger log = LoggerFactory.getLogger(AbstractCcyGridView.class);

    protected Grid grid;

    @Resource(name = "Iso4217Service")
    protected Iso4217CcyService iso4217Service;

    @PostConstruct
    public abstract void init();

    protected void setup(Class<T> type, Collection<T> items, String... excludeProps) {
        setSizeFull();
        setup(type, excludeProps);
        refresh(items, null, null);
        addComponent(contentRoot());
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
        topBar.setWidth(100.0f, Unit.PERCENTAGE);
        topBar.setSpacing(true);
        customizeTopBar(topBar);
        return topBar;
    }

    private MenuBar menuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setWidth(100.0f, Unit.PERCENTAGE);
        menuBar.addStyleName("small");
        customizeMenuBar(menuBar);
        return menuBar;
    }

    private VerticalLayout gridWithBar() {
        VerticalLayout gridWithBar = new VerticalLayout();
        gridWithBar.addComponent(topBar());
        gridWithBar.addComponent(grid);
        gridWithBar.setMargin(true);
        gridWithBar.setSpacing(true);
        gridWithBar.setSizeFull();
        gridWithBar.setExpandRatio(grid, 1.0f);
        return gridWithBar;
    }

    private VerticalLayout contentRoot() {
        VerticalLayout content = new VerticalLayout();
        content.addComponent(menuBar());
        VerticalLayout gridWithBar = gridWithBar();
        content.addComponent(gridWithBar);
        content.setExpandRatio(gridWithBar, 1.0f);
        content.setSizeFull();
        return content;
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

    protected abstract void customizeMenuBar(MenuBar menuBar);

    /**
     * @param data       data to be stored
     * @param sortPropId if null then there is no sort applied
     * @param direction  if null and {@code sortPropId} not null, then asc
     */
    protected void refresh(Collection<T> data, String sortPropId, SortDirection direction) {
        container().removeAllItems();
        container().addAll(data);
        grid.clearSortOrder();
        if (sortPropId != null) {
            if (direction != null)
                grid.sort(sortPropId, direction);
            else
                grid.sort(sortPropId, SortDirection.ASCENDING);
        }
        Notification.show("Fetched " + data.size() + " records", Notification.Type.TRAY_NOTIFICATION);
    }

    protected void filter(String text) {
        container().removeAllContainerFilters();
        if (!text.isEmpty()) {
            List<Container.Filter> filters = new ArrayList<>();
            container().getContainerPropertyIds()
                    .forEach(p -> filters.add(new SimpleStringFilter(p, text, true, false)));
            container().addContainerFilter(new Or(filters.stream().toArray(SimpleStringFilter[]::new)));
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

    public static void toggleEnabled(boolean enabled, Component... components) {
        Arrays.stream(components).forEach(c -> c.setEnabled(enabled));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
