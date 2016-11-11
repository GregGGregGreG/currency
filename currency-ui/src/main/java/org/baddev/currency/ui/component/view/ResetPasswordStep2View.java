package org.baddev.currency.ui.component.view;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.PasswordField;
import org.baddev.currency.core.dto.ResetPasswordDTO;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.security.event.ResetPwdStep2Event;
import org.baddev.currency.ui.util.EventBus;
import org.baddev.currency.ui.util.Navigator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
@SpringView(name = ResetPasswordStep2View.NAME)
public class ResetPasswordStep2View extends AbstractFormView<ResetPasswordDTO> {

    public static final String NAME = "reset";

    private String token;
    private PasswordField passwordConfirmField;

    public ResetPasswordStep2View() {
        super(new ResetPasswordDTO(), ResetPasswordDTO.class);
    }

    public void setToken(String token) {
        this.token = token;
        formBean.setToken(token);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<ResetPasswordDTO> binder, Button submitBtn) {
        setPanelCaption("Reset Password Step 2");

        PasswordField passwordField = binder.buildAndBind("Password", "password", PasswordField.class);
        passwordConfirmField = binder.buildAndBind("Confirm Password", "passwordConfirm", PasswordField.class);

        Arrays.asList(passwordConfirmField, passwordField).forEach(f -> {
            f.setIcon(FontAwesome.LOCK);
            f.setWidth(300, Unit.PIXELS);
            f.setRequired(true);
            f.setNullRepresentation("");
            f.setImmediate(true);
        });

        passwordConfirmField.addValidator(value -> {
            if (!Objects.equals(value, passwordField.getValue())) {
                throw new Validator.InvalidValueException("passwords must match");
            }
        });

        submitBtn.setCaption("Update");

        formLayout.addComponents(passwordField, passwordConfirmField, submitBtn);
    }

    @Override
    protected void submitBtnClicked(Button.ClickEvent clickEvent, BeanFieldGroup<ResetPasswordDTO> binder) {
        try {
            binder.commit();
        } catch (FieldGroup.CommitException e) {
            throw new WrappedUIException(e);
        }
    }

    @Override
    public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        if(token == null){
            commitEvent.getFieldBinder().clear();
            throw new FieldGroup.CommitException("Password change is not allowed");
        }
        passwordConfirmField.validate();
    }

    @Override
    public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        EventBus.post(new ResetPwdStep2Event(this, formBean, commitEvent.getFieldBinder()));
    }

    @Override
    public Collection<MenuBar.MenuItem> customizeMenuBar(MenuBar menuBar) {
        return Arrays.asList(
                menuBar.addItem("Sign In", FontAwesome.SIGN_IN, selectedItem -> Navigator.navigate(SignInView.NAME)),
                menuBar.addItem("Sign Up", FontAwesome.USER_PLUS, selectedItem -> Navigator.navigate(SignUpView.NAME)));
    }

    @Override
    public String getNameCaption() {
        return "Reset Password Step 2";
    }

}
