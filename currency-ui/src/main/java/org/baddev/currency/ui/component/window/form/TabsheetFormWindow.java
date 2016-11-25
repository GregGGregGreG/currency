package org.baddev.currency.ui.component.window.form;

import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.component.view.base.AbstractView;
import org.baddev.currency.ui.component.window.Showable;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by IPotapchuk on 11/14/2016.
 */
class TabsheetFormWindow extends Window implements Showable {

    private static final long serialVersionUID = -4633358948661230722L;

    private TabSheet tabSheet = new TabSheet();

    TabsheetFormWindow() {
        setWidth(600f, Unit.PIXELS);
        setModal(true);
        setResizable(false);
        setCaptionAsHtml(true);
        configureTabsheet();
        setContent(tabSheet);
        center();
    }

    TabsheetFormWindow(AbstractView... views) {
        this();
        addTabs(views);
    }

    @Override
    public void close() {
        Iterator<Component> it = tabSheet.iterator();

        Component modified = null;

        while (it.hasNext()) {
            Component next = it.next();
            if (next instanceof AbstractFormView) {
                if (((AbstractFormView) next).isFormModified()) {
                    modified = next;
                    break;
                }
            }
        }

        if (modified != null) {
            Component finalModified = modified;
            ConfirmDialog.show(UI.getCurrent(),
                    "Changes Discard Confirmation",
                    "Do you really want to discard the changes you made?",
                    "Ok",
                    "Cancel",
                    dialog -> {
                        if (dialog.isConfirmed()) {
                            super.close();
                        } else tabSheet.setSelectedTab(finalModified);
                    });
        } else super.close();
    }

    private void configureTabsheet() {
        tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
    }

    void addTabs(AbstractView... views) {
        Arrays.stream(views).forEach(this::addTab);
    }

    void addTab(AbstractView view) {
        tabSheet.addTab(view, view.getNameCaption());
    }

    @Override
    public Window show(String caption) {
        setCaption(caption);
        UI.getCurrent().addWindow(this);
        return this;
    }

}
