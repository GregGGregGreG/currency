package org.baddev.currency.ui.component.view.base;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.provider.MatchMode;
import eu.maxschuster.vaadin.autocompletetextfield.shared.ScrollBehavior;
import org.baddev.currency.ui.component.base.VerticalSpacedLayout;
import org.baddev.currency.ui.converter.DateToLocalDateConverter;
import org.baddev.currency.ui.converter.DateToLocalDateTimeConverter;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 4/5/2016.
 */
public abstract class AbstractCcyGridView<T> extends AbstractCcyView {

    protected Grid grid = new Grid();
    private Grid.HeaderRow filterRow;

    protected enum FilterKind {
        TEXT, DATE, DATETIME, SELECT, CHECKBOX
    }

    protected enum DateTimeResolution {
        HOUR, MINUTE, SECOND
    }

    public final class FilterConfig {
        private String propId;
        private FilterKind kind;
        private Collection<String> selectOptions = new ArrayList<>();
        private boolean rangeDateOrDateTime;
        private boolean exactDateOrDateTime;
        private boolean textAutocomplete;
        private DateTimeResolution resolution;

        public FilterConfig setPropId(String propId) {
            this.propId = propId;
            return this;
        }

        public FilterConfig setKind(FilterKind kind) {
            this.kind = kind;
            return this;
        }

        public FilterConfig setSelectOptions(Collection<String> selectOptions) {
            this.selectOptions = selectOptions;
            return this;
        }

        public FilterConfig setRangeDateOrDateTime(boolean rangeDateOrDateTime) {
            this.rangeDateOrDateTime = rangeDateOrDateTime;
            return this;
        }

        public FilterConfig setExactDateOrDateTime(boolean exactDateOrDateTime) {
            this.exactDateOrDateTime = exactDateOrDateTime;
            return this;
        }

        public FilterConfig setResolution(DateTimeResolution resolution) {
            this.resolution = resolution;
            return this;
        }

        public FilterConfig setTextAutocomplete(boolean textAutocomplete) {
            this.textAutocomplete = textAutocomplete;
            return this;
        }
    }

    @Override
    protected final void init(VerticalSpacedLayout rootLayout) {
        rootLayout.addComponent(gridBar());
        filterRow = grid.appendHeaderRow();
        rootLayout.addComponent(grid);
        rootLayout.setExpandRatio(grid, 1.0f);
    }

    protected void setup(Class<T> type, Collection<? extends T> items, Object... excludePropIds) {
        setup(type, excludePropIds);
        refresh(items, null, null);
        grid.getColumns().forEach(c -> c.setHidable(true));
    }

    private void setup(Class<T> type, Object... excludeProps) {
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        BeanItemContainer<T> container = new BeanItemContainer<>(type);
        GeneratedPropertyContainer wrapperContainer = new GeneratedPropertyContainer(container);
        grid.setContainerDataSource(wrapperContainer);
        setupGeneratedProperties(wrapperContainer);
        grid.setImmediate(true);
        if (excludeProps.length != 0)
            for (Object prop : excludeProps) wrapperContainer.removeContainerProperty(prop);
    }

    protected void setupGeneratedProperties(GeneratedPropertyContainer container) {
    }

    protected void addRowFilter(FilterConfig config) {
        if (!containerWrapper().getContainerPropertyIds().contains(config.propId)) {
            throw new IllegalArgumentException("Given propId not found: " + config.propId);
        }

        Grid.HeaderCell cell = filterRow.getCell(config.propId);
        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setSizeFull();
        cell.setComponent(wrapper);
        AbstractComponent filterComponent = null;

        switch (config.kind) {
            case TEXT: {
                TextField filterField;
                if (config.textAutocomplete && !config.selectOptions.isEmpty()) {
                    filterField = new AutocompleteTextField()
                            .withCache(true)
                            .withDelay(100)
                            .withMinChars(3)
                            .withScrollBehavior(ScrollBehavior.NONE)
                            .withSuggestionLimit(10)
                            .withSuggestionProvider(new CollectionSuggestionProvider(config.selectOptions, MatchMode.CONTAINS, true))
                            .withTextChangeListener(event -> {
                                if (event.getText().isEmpty())
                                    removeContainerPropertyFilters(containerWrapper(), config.propId);
                            }).withValueChangeListener(event -> {
                                removeContainerPropertyFilters(containerWrapper(), config.propId);
                                if (event.getProperty().getValue() != null)
                                    containerWrapper().addContainerFilter(new SimpleStringFilter(
                                            config.propId, String.valueOf(event.getProperty().getValue()), true, false));
                            });
                } else {
                    filterField = new TextField();
                    filterField.addTextChangeListener(change -> {
                        removeContainerPropertyFilters(containerWrapper(), config.propId);
                        if (!change.getText().isEmpty())
                            containerWrapper().addContainerFilter(new SimpleStringFilter(
                                    config.propId, change.getText(), true, false));
                    });
                }
                filterField.setTextChangeTimeout(300);
                filterField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.TIMEOUT);
                filterComponent = filterField;
                break;
            }
            case DATE: {
                if (config.exactDateOrDateTime) {
                    DateField df = new DateField();
                    df.setConverter(new DateToLocalDateConverter());
                    df.setResolution(Resolution.DAY);
                    df.addValueChangeListener(event -> {
                        removeContainerPropertyFilters(containerWrapper(), config.propId);
                        if (event.getProperty().getValue() != null)
                            containerWrapper().addContainerFilter(new Compare.Equal(
                                    config.propId, LocalDate.fromDateFields((Date) event.getProperty().getValue())));
                    });
                    filterComponent = df;
                }
                break;
            }
            case DATETIME: {
                if (config.exactDateOrDateTime) {
                    DateField df = new DateField();
                    df.setConverter(new DateToLocalDateTimeConverter());
                    switch (config.resolution) {
                        case HOUR:
                            df.setResolution(Resolution.HOUR);
                            break;
                        case MINUTE:
                            df.setResolution(Resolution.MINUTE);
                            break;
                        case SECOND:
                            df.setResolution(Resolution.SECOND);
                            break;
                        default:
                            df.setResolution(Resolution.MINUTE);
                            break;
                    }
                    df.addValueChangeListener(event -> {
                        removeContainerPropertyFilters(containerWrapper(), config.propId);
                        containerWrapper().addContainerFilter(new Compare.Equal(
                                config.propId, LocalDateTime.fromDateFields((Date) event.getProperty().getValue())));
                    });
                    filterComponent = df;
                }
                break;
            }
            case SELECT: {
                NativeSelect select = new NativeSelect();
                select.setNewItemsAllowed(false);
                select.addItems(new TreeSet<>(config.selectOptions));
                select.addValueChangeListener(event -> {
                    removeContainerPropertyFilters(containerWrapper(), config.propId);
                    if (event.getProperty().getValue() != null)
                        containerWrapper().addContainerFilter(new SimpleStringFilter(
                                config.propId, String.valueOf(event.getProperty().getValue()), false, false));
                });
                filterComponent = select;
                break;
            }
            case CHECKBOX: {
                CheckBox cb = new CheckBox();
                cb.setValue(null);
                cb.addValueChangeListener(event -> {
                    removeContainerPropertyFilters(containerWrapper(), config.propId);
                    if (event.getProperty().getValue() != null)
                        containerWrapper().addContainerFilter(new Compare.Equal(config.propId, event.getProperty().getValue()));
                });
                filterComponent = cb;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid filter kind: " + config.kind);
        }
        filterComponent.setImmediate(true);
        filterComponent.setStyleName("small");
        filterComponent.setWidth("100%");
        wrapper.addComponent(filterComponent);
    }

    private void removeRowFilter(String propId) {
        container().removeContainerFilters(propId);
        Grid.HeaderCell cell = filterRow.getCell(propId);
        if (cell != null) cell.setText("");
    }

    private static void removeContainerPropertyFilters(GeneratedPropertyContainer container, String propId) {
        List<Container.Filter> pFilters = container.getContainerFilters()
                .stream()
                .filter(f -> f.appliesToProperty(propId))
                .collect(Collectors.toList());
        for (Iterator<Container.Filter> it = pFilters.iterator(); it.hasNext(); ) {
            container.removeContainerFilter(it.next());
        }
    }

    private HorizontalLayout gridBar() {
        HorizontalLayout gridBar = new HorizontalLayout();
        gridBar.setSizeUndefined();
        gridBar.setWidth(100.0f, Unit.PERCENTAGE);
        gridBar.setSpacing(true);
        customizeGridBar(gridBar);
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

    protected abstract void customizeGridBar(HorizontalLayout topBar);

    protected void refresh(Collection<? extends T> data, Object sortPropId) {
        refresh(data, sortPropId, SortDirection.ASCENDING);
    }

    /**
     * Refreshes and sorts an underlying datasource container.
     * Also manages the row selection after refresh is performed.
     *
     * @param data       data to be displayed
     * @param sortPropId if null then there is no sort applied
     * @param direction  if null and {@code sortPropId} not null, then asc
     */
    protected void refresh(Collection<? extends T> data, Object sortPropId, SortDirection direction) {
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
        selectRowAfterRefresh(sizeBefore, container().getItemIds().size(), selectedIdx);
    }

    private void selectRowAfterRefresh(int sizeBefore, int sizeAfter, int selectedIdx){
        if (sizeAfter == 0 || selectedIdx == -1) return;
        if (sizeAfter == 1 || sizeAfter > sizeBefore || (selectedIdx == 0 && sizeAfter > 0)) {
            grid.select(container().getIdByIndex(0));
        } else if (sizeAfter == sizeBefore) {
            grid.select(container().getIdByIndex(selectedIdx));
        } else if (sizeAfter < sizeBefore) {
            grid.select(container().getIdByIndex(selectedIdx - 1));
        }
    }

    private void clearFilterComponents() {
        container().getContainerPropertyIds().forEach(this::removeRowFilter);
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
    protected void addGeneratedButton(String propertyId, String value, ClickableRenderer.RendererClickListener listener) {
        addGeneratedButton(propertyId, r -> value, listener);
    }

    /**
     * Adds button with dynamic string value to grid
     *
     * @param propertyId
     * @param r          function which decides which value to render
     * @param listener
     */
    protected void addGeneratedButton(String propertyId, Function<Object, String> r, ClickableRenderer.RendererClickListener listener) {
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

    protected void addGeneratedStringProperty(String propertyId, boolean html, Function<Object, String> r) {
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

    protected <E> void addGeneratedProperty(String propId, PropertyValueGenerator<E> generator) {
        containerWrapper().addGeneratedProperty(propId, generator);
    }

}
