package org.baddev.currency.ui.component.toolbar;

import com.google.common.collect.ImmutableSet;
import com.vaadin.event.SelectionEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.ui.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
public class GridButtonToolbar extends HorizontalLayout implements SelectionEvent.SelectionListener {

    private static final Logger log = LoggerFactory.getLogger(GridButtonToolbar.class);

    private static final String DEF_BTN_STYLE = "small";

    private Map<Consumer, Button> actionBtnMap = new HashMap<>();
    private Map<Function<Set, String>, Button> captionRenderedBtnMap = new HashMap<>();
    private Set selected =  ImmutableSet.of();
    private CssLayout buttonLayout = new CssLayout();
    private boolean hideNoSelect;

    public <T> GridButtonToolbar(Grid grid) {
        init();
        grid.addSelectionListener(this);
    }

    public GridButtonToolbar(Grid grid, boolean hideNoSelect) {
        this(grid);
        this.hideNoSelect = hideNoSelect;
        if (hideNoSelect) setVisible(!selected.isEmpty());
    }

    private void init() {
        setSpacing(true);
        setImmediate(true);
        buttonLayout.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        addComponent(buttonLayout);
        setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
    }

    @Override
    public void select(SelectionEvent event) {
        selected = ImmutableSet.copyOf(event.getSelected());
        actionBtnMap.values().forEach(b -> {
            b.setEnabled(!selected.isEmpty());
            if (hideNoSelect) setVisible(!selected.isEmpty());
        });
        captionRenderedBtnMap.entrySet().forEach(entry -> entry.getValue().setCaption(entry.getKey().apply(selected)));
    }

    public GridButtonToolbar withButton(String caption, Consumer<Set> action) {
        return withButton(selected -> caption, action);
    }

    public GridButtonToolbar withButton(Function<Set, String> captionRendered, Consumer<Set> action) {
        return withButtonStyled(captionRendered, action);
    }

    public GridButtonToolbar withButtonStyled(Function<Set, String> captionRendered,
                                              Consumer<Set> action,
                                              String... customStyles) {
        Button btn = new Button(captionRendered.apply(selected));
        btn.setEnabled(!selected.isEmpty());

        actionBtnMap.put(action, btn);
        captionRenderedBtnMap.put(captionRendered, btn);

        btn.addClickListener(event -> {
            action.accept(selected);
            event.getButton().setCaption(captionRendered.apply(selected));
        });

        btn.addStyleName(DEF_BTN_STYLE);
        btn.addStyleName(FormatUtils.joinByComma(Arrays.asList(customStyles)));

        buttonLayout.addComponent(btn);
        return this;
    }

    public GridButtonToolbar withButtonStyled(String caption, Consumer<Set> action, String... customStyles) {
        return withButtonStyled(selected -> caption, action, customStyles);
    }

}
