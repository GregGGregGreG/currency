package org.baddev.currency.ui.util;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import lombok.NonNull;

/**
 * Created by IPotapchuk on 11/23/2016.
 */
public final class ButtonFactory {

    public enum Mode {
        CREATE, REMOVE, EDIT, DEFAULT
    }

    public static Button create(@NonNull Mode mode, boolean dialogue){
        Button button = new Button();
        switch (mode){
            case CREATE:{
                button.setCaption("Create");
                button.setIcon(FontAwesome.PLUS_CIRCLE);
                break;
            }
            case REMOVE:{
                button.setCaption("Remove");
                button.setIcon(FontAwesome.MINUS_CIRCLE);
                button.setStyleName(ValoTheme.BUTTON_DANGER);
                break;
            }
            case EDIT:{
                button.setCaption("Edit");
                button.setIcon(FontAwesome.EDIT);
                break;
            }
            default:{
                button.setCaption("Submit");
                button.setIcon(FontAwesome.CHECK);
                button.setStyleName(ValoTheme.BUTTON_PRIMARY);
                break;
            }
        }
        if(dialogue){
            button.setCaption(button.getCaption().concat("..."));
        } else if(mode != Mode.REMOVE) {
            button.setStyleName(ValoTheme.BUTTON_PRIMARY);
        }
        button.setImmediate(true);
        return button;
    }

    public static Button createDialogButton(@NonNull Mode mode){
        return create(mode, true);
    }

    public static Button createFormButton(@NonNull Mode mode){
        return create(mode, false);
    }

    public static Button createSubmitBtn(){
        return create(Mode.DEFAULT, false);
    }

}
