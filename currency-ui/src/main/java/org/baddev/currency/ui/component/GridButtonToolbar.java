package org.baddev.currency.ui.component;

import com.vaadin.event.SelectionEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.ui.util.NotificationUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
public class GridButtonToolbar extends HorizontalLayout implements SelectionEvent.SelectionListener {

    private static final String DEF_BTN_STYLE = "small";

    private Map<Consumer, Button> buttonActionMap = new HashMap<>();
    private Set selected = new HashSet();
    private CssLayout buttonLayout = new CssLayout();

    public GridButtonToolbar(Grid grid) {
        init();
        grid.addSelectionListener(this);
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
        buttonActionMap.values().forEach(b -> b.setEnabled(!event.getSelected().isEmpty()));
        selected = event.getSelected();
    }

    public GridButtonToolbar addButton(String caption, Consumer<Set> action) {
        Button btn = new Button(caption);
        btn.setEnabled(!selected.isEmpty());
        buttonActionMap.put(action, btn);
        btn.addClickListener((Button.ClickListener) event -> {
            try {
                action.accept(selected);
            } catch (Exception e){
                NotificationUtils.notifyFailure("Unexpected Error", e.getMessage());
            }
        });
        btn.addStyleName(DEF_BTN_STYLE);
        buttonLayout.addComponent(btn);
        return this;
    }

}
