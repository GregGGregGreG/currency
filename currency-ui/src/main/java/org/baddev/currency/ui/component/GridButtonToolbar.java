package org.baddev.currency.ui.component;

import com.vaadin.event.SelectionEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
public class GridButtonToolbar extends HorizontalLayout implements SelectionEvent.SelectionListener {

    private Map<Consumer, Button> buttonActionMap = new HashMap<>();
    private Set selected = new HashSet();
    public static final String DEF_BTN_STYLE = "tiny";

    public GridButtonToolbar(Grid grid) {
        init();
        grid.addSelectionListener(this);
    }

    private void init() {
        setSpacing(true);
        setImmediate(true);
    }

    @Override
    public void select(SelectionEvent event) {
        buttonActionMap.values().forEach(b -> b.setEnabled(!event.getSelected().isEmpty()));
        selected = event.getSelected();
    }

    public GridButtonToolbar createButton(String caption, Consumer<Set> action) {
        Button btn = new Button(caption);
        btn.setEnabled(!selected.isEmpty());
        buttonActionMap.put(action, btn);
        btn.addClickListener((Button.ClickListener) event -> {
            action.accept(selected);
        });
        btn.addStyleName(DEF_BTN_STYLE);
        addComponent(btn);
        setComponentAlignment(btn, Alignment.MIDDLE_RIGHT);
        return this;
    }

}
