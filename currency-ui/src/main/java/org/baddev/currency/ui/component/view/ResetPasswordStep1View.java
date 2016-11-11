package org.baddev.currency.ui.component.view;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import org.baddev.currency.core.dto.ResetPasswordRequestDTO;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.security.event.ResetPwdStep1Event;
import org.baddev.currency.ui.util.EventBus;
import org.baddev.currency.ui.util.Navigator;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
@SpringView(name = ResetPasswordStep1View.NAME)
public class ResetPasswordStep1View extends AbstractFormView<ResetPasswordRequestDTO>{

    public static final String NAME = "reqreset";

    public ResetPasswordStep1View() {
        super(new ResetPasswordRequestDTO(), ResetPasswordRequestDTO.class);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<ResetPasswordRequestDTO> binder, Button submitBtn) {
        setPanelCaption("Reset Password Step 1");
        TextField emailField = binder.buildAndBind("Email", "email", TextField.class);
        emailField.setIcon(FontAwesome.AT);

        emailField.setWidth(300, Unit.PIXELS);
        emailField.setRequired(true);
        emailField.setNullRepresentation("");
        emailField.setImmediate(true);

        submitBtn.setCaption("Reset");

        formLayout.addComponents(emailField, submitBtn);
    }

    @Override
    protected void submitBtnClicked(Button.ClickEvent clickEvent, BeanFieldGroup<ResetPasswordRequestDTO> binder) {
        try {
            binder.commit();
        } catch (FieldGroup.CommitException e) {
            throw new WrappedUIException(e);
        }
    }

    @Override
    public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
    }

    @Override
    public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        EventBus.post(new ResetPwdStep1Event(this, formBean, commitEvent.getFieldBinder()));
    }

    @Override
    public Collection<MenuBar.MenuItem> customizeMenuBar(MenuBar menuBar) {
        return Arrays.asList(
                menuBar.addItem("Sign In", FontAwesome.SIGN_IN, selectedItem -> Navigator.navigate(SignInView.NAME)),
                menuBar.addItem("Sign Up", FontAwesome.USER_PLUS, selectedItem -> Navigator.navigate(SignUpView.NAME)));
    }

    @Override
    public String getNameCaption() {
        return "Reset Password Step 1";
    }
}
