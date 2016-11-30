package org.baddev.currency.ui.core.component.window.form;

import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import lombok.NonNull;
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.component.window.Showable;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Created by IPotapchuk on 11/15/2016.
 */
class SimpleFormWindow extends Window implements Showable {

    private static final long serialVersionUID = -5462809945311539068L;

    private final AbstractFormView content;

    SimpleFormWindow(@NonNull AbstractFormView formView) {
        this.content = formView;
        setupWindow();
    }

    private void setupWindow(){
        setModal(true);
        setResizable(false);
        setCaptionAsHtml(true);
        center();
    }

    private static VerticalLayout expand(AbstractFormView view){
        VerticalLayout layout = new VerticalLayout(view);
        layout.setExpandRatio(view, 1f);
        return layout;
    }

    @Override
    public void close() {
        if (content.isFormModified()) {
            ConfirmDialog.show(UI.getCurrent(),
                    "Changes Discard Confirmation",
                    "Do you really want to discard the changes you made?",
                    "Ok",
                    "Cancel",
                    dialog -> {
                        if (dialog.isConfirmed()) {
                            super.close();
                        }
                    });
        } else super.close();
    }

    @Override
    public Window show(String caption) {
        setCaption(caption);
        setContent(expand(content));
        UI.getCurrent().addWindow(this);
        return this;
    }
}
