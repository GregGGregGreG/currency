package org.baddev.currency.ui.util;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import eu.maxschuster.vaadin.autocompletetextfield.AutocompleteTextField;
import eu.maxschuster.vaadin.autocompletetextfield.provider.CollectionSuggestionProvider;
import eu.maxschuster.vaadin.autocompletetextfield.provider.MatchMode;
import eu.maxschuster.vaadin.autocompletetextfield.shared.ScrollBehavior;
import org.baddev.currency.ui.converter.DateToLocalDateConverter;
import org.baddev.currency.ui.converter.DateToLocalDateTimeConverter;
import org.baddev.currency.ui.model.grid.FilterConfig;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 11/24/2016.
 */
public final class GridUtils {

    public static Component createFilterComponent(Container.Filterable container, FilterConfig config) {
        if (!container.getContainerPropertyIds().contains(config.getPropId())) {
            throw new IllegalArgumentException("Given propId not found: " + config.getPropId());
        }

        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setSizeFull();
        AbstractComponent filterComponent = null;

        switch (config.getKind()) {
            case TEXT: {
                TextField filterField;
                if (config.isTextAutocomplete() && !config.getSelectOptions().isEmpty()) {
                    filterField = new AutocompleteTextField()
                            .withCache(true)
                            .withDelay(100)
                            .withMinChars(2)
                            .withScrollBehavior(ScrollBehavior.NONE)
                            .withSuggestionLimit(10)
                            .withSuggestionProvider(new CollectionSuggestionProvider(config.getSelectOptions(), MatchMode.BEGINS, true))
                            .withTextChangeListener(event -> {
                                if (event.getText().isEmpty())
                                    removeContainerPropertyFilters(container, config.getPropId());
                            }).withValueChangeListener(event -> {
                                removeContainerPropertyFilters(container, config.getPropId());
                                if (event.getProperty().getValue() != null)
                                    container.addContainerFilter(new SimpleStringFilter(
                                            config.getPropId(), String.valueOf(event.getProperty().getValue()), true, false));
                            });
                } else {
                    filterField = new TextField();
                    filterField.addTextChangeListener(change -> {
                        removeContainerPropertyFilters(container, config.getPropId());
                        if (!change.getText().isEmpty())
                            container.addContainerFilter(new SimpleStringFilter(
                                    config.getPropId(), change.getText(), true, false));
                    });
                }
                filterField.setTextChangeTimeout(300);
                filterField.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.TIMEOUT);
                filterComponent = filterField;
                break;
            }
            case DATE: {
                if (config.isExactDateOrDateTime()) {
                    PopupDateField df = new PopupDateField();
                    df.setConverter(new DateToLocalDateConverter());
                    df.setResolution(Resolution.DAY);
                    df.addValueChangeListener(event -> {
                        removeContainerPropertyFilters(container, config.getPropId());
                        if (event.getProperty().getValue() != null)
                            container.addContainerFilter(new Compare.Equal(
                                    config.getPropId(), LocalDate.fromDateFields((Date) event.getProperty().getValue())));
                    });
                    filterComponent = df;
                }
                break;
            }
            case DATETIME: {
                if (config.isExactDateOrDateTime()) {
                    PopupDateField df = new PopupDateField();
                    df.setConverter(new DateToLocalDateTimeConverter());
                    switch (config.getResolution()) {
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
                        removeContainerPropertyFilters(container, config.getPropId());
                        if (event.getProperty().getValue() != null)
                            container.addContainerFilter(new Compare.Equal(
                                    config.getPropId(), LocalDateTime.fromDateFields((Date) event.getProperty().getValue())));
                    });
                    filterComponent = df;
                }
                break;
            }
            case SELECT: {
                NativeSelect select = new NativeSelect();
                select.setNewItemsAllowed(false);
                select.addItems(new TreeSet<>(config.getSelectOptions()));
                select.addValueChangeListener(event -> {
                    removeContainerPropertyFilters(container, config.getPropId());
                    if (event.getProperty().getValue() != null)
                        container.addContainerFilter(new SimpleStringFilter(
                                config.getPropId(), String.valueOf(event.getProperty().getValue()), false, false));
                });
                filterComponent = select;
                break;
            }
            case CHECKBOX: {
                CheckBox cb = new CheckBox();
                cb.setValue(null);
                cb.addValueChangeListener(event -> {
                    removeContainerPropertyFilters(container, config.getPropId());
                    if (event.getProperty().getValue() != null)
                        container.addContainerFilter(new Compare.Equal(config.getPropId(), event.getProperty().getValue()));
                });
                filterComponent = cb;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid filter kind: " + config.getKind());
        }
        filterComponent.setImmediate(true);
        filterComponent.setStyleName("small");
        filterComponent.setWidth("100%");
        wrapper.addComponent(filterComponent);
        return wrapper;
    }

    public static void removeContainerPropertyFilters(Container.Filterable container, String propId) {
        List<Container.Filter> pFilters = container.getContainerFilters()
                .stream()
                .filter(f -> f.appliesToProperty(propId))
                .collect(Collectors.toList());
        for (Iterator<Container.Filter> it = pFilters.iterator(); it.hasNext(); ) {
            container.removeContainerFilter(it.next());
        }
    }

}
