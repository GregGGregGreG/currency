package org.baddev.currency.ui.component.toolbar;

import com.vaadin.addon.tableexport.DefaultTableHolder;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.component.window.form.FormWindow;
import org.baddev.currency.ui.util.ButtonFactory;
import org.baddev.currency.ui.util.FormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private Set selected = new HashSet();
    private CssLayout buttonLayout = new CssLayout();
    private Grid grid;
    private boolean hideNoSelect;

    private Set<Button> customBtns = new LinkedHashSet<>();
    private Button excelExportBtn;

    public GridButtonToolbar(Grid grid) {
        this.grid = grid;
        init();
        grid.addSelectionListener(this);
    }

    public GridButtonToolbar(Grid grid, boolean hideNoSelect) {
        this(grid);
        this.hideNoSelect = hideNoSelect;
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
        selected = event.getSelected();
        actionBtnMap.values().forEach(b -> {
            b.setEnabled(!selected.isEmpty());
            if (hideNoSelect) b.setVisible(!selected.isEmpty());
        });
        captionRenderedBtnMap.entrySet().forEach(entry -> entry.getValue().setCaption(entry.getKey().apply(selected)));
    }

    public GridButtonToolbar withActionBtn(String caption, Consumer<Set> action, String... customStyles) {
        return withActionBtn(selected -> caption, null, action, customStyles);
    }

    public GridButtonToolbar withActionBtn(String caption, Resource icon, Consumer<Set> action, String... customStyles) {
        return withActionBtn(selected -> caption, icon, action, customStyles);
    }

    public GridButtonToolbar withActionBtn(Function<Set, String> captionRendered, Consumer<Set> action, String... customStyles) {
        return withActionBtn(captionRendered, null, action, customStyles);
    }

    public GridButtonToolbar withActionBtn(Function<Set, String> captionRendered, Resource icon, Consumer<Set> action, String... customStyles) {
        Button btn = new Button(captionRendered.apply(selected));
        if (icon != null) btn.setIcon(icon);
        setupActionBtn(btn, action, captionRendered, customStyles);
        buttonLayout.addComponent(btn);
        return this;
    }

    public GridButtonToolbar withCustomBtn(Button btn, String... customStyles) {
        setupBtnStyles(btn, customStyles);
        customBtns.add(btn);
        buttonLayout.addComponent(btn);
        return this;
    }

    public GridButtonToolbar withFactoryBtn(ButtonFactory.Mode mode, Button.ClickListener listener) {
        Button button = ButtonFactory.createDialogButton(mode);
        button.addClickListener(listener);
        setupBtnStyles(button);
        customBtns.add(button);
        buttonLayout.addComponent(button);
        return this;
    }

    public GridButtonToolbar withActionFactoryBtn(ButtonFactory.Mode mode, Consumer<Set> action) {
        Button button = ButtonFactory.createDialogButton(mode);
        setupActionBtn(button, action, selected -> button.getCaption());
        buttonLayout.addComponent(button);
        return this;
    }

    public GridButtonToolbar withExportBtn(String reportTitle, String... exportBtnStyles) {
        if (excelExportBtn != null) return this;
        excelExportBtn = new Button("Export", FontAwesome.FILE_EXCEL_O);
        setupBtnStyles(excelExportBtn, exportBtnStyles);
        excelExportBtn.addClickListener(event -> {
            ExcelExport excelExport = new ExcelExport(new DefaultTableHolder(grid), reportTitle);
            excelExport.excludeCollapsedColumns();
            excelExport.setReportTitle(reportTitle);
            excelExport.setExportFileName(reportTitle+"-report-"+
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+".xls");
            excelExport.export();
        });
        buttonLayout.addComponent(excelExportBtn, 0);
        return this;
    }

    public GridButtonToolbar withExportBtn(AbstractFormView view, String... exportBtnStyles) {
        if (excelExportBtn != null) return this;
        excelExportBtn = new Button("Export", FontAwesome.FILE_EXCEL_O);
        setupBtnStyles(excelExportBtn, exportBtnStyles);
        excelExportBtn.addClickListener(event -> {
            FormWindow.show(view);
        });
        buttonLayout.addComponent(excelExportBtn, 0);
        return this;
    }

    public boolean isExportEnabled() {
        return excelExportBtn != null;
    }

    private static void setupBtnStyles(Button button, String... customStyles) {
        button.addStyleName(DEF_BTN_STYLE);
        if (customStyles != null && customStyles.length > 0) {
            button.addStyleName(FormatUtils.joinByComma(Arrays.asList(customStyles)));
        }
    }

    private void setupActionBtn(Button btn, Consumer<Set> action, Function<Set, String> captionRendered, String... customStyles) {
        setupBtnStyles(btn, customStyles);
        btn.setEnabled(!selected.isEmpty());
        if (hideNoSelect) btn.setVisible(!selected.isEmpty());
        actionBtnMap.put(action, btn);
        captionRenderedBtnMap.put(captionRendered, btn);
        btn.addClickListener(event -> {
            action.accept(selected);
            event.getButton().setCaption(captionRendered.apply(selected));
        });
    }

}
