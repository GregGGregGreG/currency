package org.baddev.currency.ui.component.view;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.dto.SignUpDTO;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.util.EventBus;
import org.baddev.currency.ui.util.Navigator;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = SignUpView.NAME)
public class SignUpView extends AbstractFormView<SignUpDTO> {

    public static final String NAME = "signup";

    public SignUpView() {
        super(new SignUpDTO(), SignUpDTO.class);
    }

    @Override
    protected void customizeForm(final FormLayout formLayout, final BeanFieldGroup<SignUpDTO> binder, final Button submitBtn) {
        setPanelCaption("Sign Up");

        TextField userName = binder.buildAndBind("Username", "username", TextField.class);
        userName.setIcon(FontAwesome.USER);
        TextField email = binder.buildAndBind("Email", "email", TextField.class);
        email.setIcon(FontAwesome.AT);
        TextField firstName = binder.buildAndBind("First Name", "firstName", TextField.class);
        firstName.setIcon(FontAwesome.INFO);
        TextField lastName = binder.buildAndBind("Last Name", "lastName", TextField.class);
        lastName.setIcon(FontAwesome.INFO);
        PasswordField password = binder.buildAndBind("Password", "password", PasswordField.class);
        password.setIcon(FontAwesome.LOCK);
        PasswordField passwordCheck = binder.buildAndBind("Password Confirmation", "confirmPassword", PasswordField.class);
        passwordCheck.setIcon(FontAwesome.LOCK);

        Arrays.asList(userName, firstName, lastName, password, passwordCheck, email).forEach(f -> {
            f.setWidth(300, Unit.PIXELS);
            f.setRequired(true);
            f.setNullRepresentation("");
            f.setImmediate(true);
        });

        passwordCheck.addValidator(value -> {
            if (!Objects.equals(value, password.getValue())) {
                throw new Validator.InvalidValueException("passwords must match");
            }
        });

        binder.addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                try {
                    password.validate();
                } catch (Validator.InvalidValueException e) {
                    throw new FieldGroup.CommitException(e.getMessage(), e);
                }
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                EventBus.post(new SignUpEvent(this, formBean, binder));
            }
        });

        submitBtn.setCaption("Sign Up");
        submitBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        submitBtn.addClickListener(event -> {
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                password.clear();
                passwordCheck.clear();
                throw new WrappedUIException(e);
            }
        });

        formLayout.addComponents(userName, email, firstName, lastName, password, passwordCheck, submitBtn);
    }

    @Override
    public void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Login", FontAwesome.SIGN_IN, selectedItem -> Navigator.navigate(LoginView.NAME));
    }
}
