package org.baddev.currency.ui.component.toolbar;

import com.vaadin.event.SelectionEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.ui.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 9/23/2016.
 */
public class GridButtonToolbar extends HorizontalLayout implements SelectionEvent.SelectionListener {

    private static final Logger log = LoggerFactory.getLogger(GridButtonToolbar.class);

    private static final String DEF_BTN_STYLE = "small";

    private Map<Consumer, Button> actionButtonMap = new HashMap<>();
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
        actionButtonMap.values().forEach(b -> b.setEnabled(!event.getSelected().isEmpty()));
        selected = event.getSelected();
    }

    public GridButtonToolbar addButton(String caption, Consumer<Set> action) {
        return addButton(caption, action, new String[0]);
    }

    public GridButtonToolbar addButton(String caption, Consumer<Set> action, String...customStyles) {
        Button btn = new Button(caption);
        btn.setEnabled(!selected.isEmpty());
        actionButtonMap.put(action, btn);
        btn.addClickListener((Button.ClickListener) event -> {
                action.accept(selected);
        });
        btn.addStyleName(DEF_BTN_STYLE);
        btn.addStyleName(FormatUtils.joinByComma(Arrays.asList(customStyles)));
        buttonLayout.addComponent(btn);
        return this;
    }

}
