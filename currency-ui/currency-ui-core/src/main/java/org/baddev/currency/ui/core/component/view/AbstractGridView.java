package org.baddev.currency.ui.core.component.view;

import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.baddev.currency.ui.core.component.VerticalSpacedLayout;
import org.baddev.currency.ui.core.model.grid.FilterConfig;
import org.baddev.currency.ui.core.util.GridUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by IPotapchuk on 4/5/2016.
 */
public abstract class AbstractGridView<T> extends AbstractView {

    private final Grid grid = new Grid();
    private Grid.HeaderRow filterRow = grid.appendHeaderRow();

    public AbstractGridView(Class<T> clazz, Object... excludePropIds) {
        setup(clazz, excludePropIds);
    }

    @Override
    protected final void init(VerticalSpacedLayout rootLayout) {
        rootLayout.addComponent(grid);
        rootLayout.setExpandRatio(grid, 1.0f);
    }

    @Override
    protected final void postInit(VerticalSpacedLayout rootLayout) {
        setupGeneratedProperties(containerWrapper());
        setup(grid);
        if (grid.getSortOrder().isEmpty()) {
            refresh();
        } else {
            refresh(grid.getSortOrder().get(0).getPropertyId(), grid.getSortOrder().get(0).getDirection());
        }
        HorizontalLayout gridBar = gridBar();
        customizeGridBar(gridBar);
        rootLayout.addComponent(gridBar, 0);
    }

    public void setHidableColumns(boolean value) {
        grid.getColumns().forEach(c -> c.setHidable(value));
    }

    protected void setSortOrder(Object propId, SortDirection direction) {
        grid.setSortOrder(Collections.singletonList(new SortOrder(propId, direction)));
    }

    protected abstract Collection<? extends T> getItems();

    protected abstract void setup(Grid grid);

    private void setup(Class<T> type, Object... excludeProps) {
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        BeanItemContainer<T> container = new BeanItemContainer<>(type);
        GeneratedPropertyContainer wrapperContainer = new GeneratedPropertyContainer(container);
        grid.setContainerDataSource(wrapperContainer);
        grid.setImmediate(true);
        if (excludeProps.length != 0)
            for (Object prop : excludeProps) wrapperContainer.removeContainerProperty(prop);
    }

    protected void setupGeneratedProperties(GeneratedPropertyContainer container) {
    }

    protected final void addRowFilter(FilterConfig config) {
        filterRow.getCell(config.getPropId()).setComponent(GridUtils.createFilterComponent(containerWrapper(), config));
    }

    protected final void removeRowFilter(Object propId) {
        containerWrapper().getContainerFilters().stream().filter(f -> f.appliesToProperty(propId)).forEach(containerWrapper()::removeContainerFilter);
        Optional.ofNullable(filterRow.getCell(propId)).ifPresent(cell -> cell.setText(""));
    }

    private void clearFilterComponents() {
        containerWrapper().getContainerPropertyIds().forEach(this::removeRowFilter);
    }

    private HorizontalLayout gridBar() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSizeUndefined();
        gridBar.setWidth(100.0f, Unit.PERCENTAGE);
        gridBar.setSpacing(true);
        gridBar.setImmediate(true);
        return gridBar;
    }

    @SuppressWarnings("unchecked")
    protected BeanItemContainer<T> container() {
        return ((BeanItemContainer<T>) ((GeneratedPropertyContainer) grid.getContainerDataSource())
                .getWrappedContainer());
    }

    private GeneratedPropertyContainer containerWrapper() {
        return (GeneratedPropertyContainer) grid.getContainerDataSource();
    }

    protected abstract void customizeGridBar(HorizontalLayout gridBar);

    public void refresh() {
        SortOrder sortOrder = grid.getSortOrder().get(0);
        refresh(getItems(), sortOrder == null ? null : sortOrder.getPropertyId(),
                sortOrder == null ? null : sortOrder.getDirection());
    }

    public void refresh(Object sortPropId) {
        refresh(getItems(), sortPropId, null);
    }

    public void refresh(Object sortPropId, SortDirection direction) {
        refresh(getItems(), sortPropId, direction);
    }

    protected void refresh(Collection<? extends T> data, Object sortPropId) {
        refresh(data, sortPropId, null);
    }

    protected void refresh(Collection<? extends T> data, Object sortPropId, SortDirection direction) {
        refresh(data, sortPropId, direction, true);
    }

    /**
     * Refreshes and sorts an underlying datasource container.
     * Also manages the row selection after refresh is performed.
     *
     * @param data            data to be displayed
     * @param sortPropId      if null then there is no sort applied
     * @param direction       if null and {@code sortPropId} not null, then asc
     * @param handleSelection whether to calculate new selection index based on data changes after refresh
     */
    protected void refresh(Collection<? extends T> data, Object sortPropId, SortDirection direction, boolean handleSelection) {
        int sizeBefore = container().getItemIds().size();
        int selectedIdx = container().indexOfId(grid.getSelectedRow());
        container().removeAllItems();
        container().addAll(data);
        grid.clearSortOrder();
        if (sortPropId != null) {
            if (direction != null)
                grid.sort(sortPropId, direction);
            else
                grid.sort(sortPropId, SortDirection.ASCENDING);
        }
        clearFilterComponents();
        postRefresh(data);
        if (handleSelection) selectRowAfterRefresh(sizeBefore, container().getItemIds().size(), selectedIdx);
    }

    private void selectRowAfterRefresh(int sizeBefore, int sizeAfter, int selectedIdx) {
        if (sizeAfter == 0 || selectedIdx == -1) return;
        if (sizeAfter == 1 || sizeAfter > sizeBefore || (selectedIdx == 0 && sizeAfter > 0) || selectedIdx >= sizeAfter) {
            grid.select(container().getIdByIndex(0));
        } else if (sizeAfter == sizeBefore) {
            grid.select(container().getIdByIndex(selectedIdx));
        } else if (sizeAfter < sizeBefore) {
            grid.select(container().getIdByIndex(selectedIdx - 1));
        }
    }

    protected void postRefresh(Collection<? extends T> data) {
    }

    /**
     * Adds button with static string value to grid
     *
     * @param propertyId
     * @param value
     * @param listener
     */
    protected final void addGeneratedButton(String propertyId, String value, ClickableRenderer.RendererClickListener listener) {
        addGeneratedButton(propertyId, r -> value, listener);
    }

    /**
     * Adds button with dynamic string value to grid
     *
     * @param propertyId
     * @param r          function which decides which value to render
     * @param listener
     */
    protected final void addGeneratedButton(String propertyId, Function<Object, String> r, ClickableRenderer.RendererClickListener listener) {
        containerWrapper().addGeneratedProperty(propertyId, new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return r.apply(itemId);
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        setupGenButton(propertyId, listener);
    }

    private void setupGenButton(String propId, ClickableRenderer.RendererClickListener listener) {
        grid.getColumn(propId).setRenderer(new ButtonRenderer(listener));
    }

    protected final void addGeneratedStringProperty(String propertyId, boolean html, Function<Object, String> r) {
        containerWrapper().addGeneratedProperty(propertyId, new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return r.apply(itemId);
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        if (html) grid.getColumn(propertyId).setRenderer(new HtmlRenderer());
    }

    protected final <E> void addGeneratedProperty(String propId, PropertyValueGenerator<E> generator) {
        containerWrapper().addGeneratedProperty(propId, generator);
    }

    public Grid getGrid() {
        return grid;
    }
}
