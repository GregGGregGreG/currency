package org.baddev.currency.ui.component.view;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.security.dto.SignUpDTO;
import org.baddev.currency.ui.component.base.AbstractFormView;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.util.NotificationUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = SignUpView.NAME)
public class SignUpView extends AbstractFormView<SignUpDTO> {

    public static final String NAME = "signup";

    public SignUpView() {
        super(SignUpDTO.class, new SignUpDTO());
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<SignUpDTO> binder) {
        TextField userName = binder.buildAndBind("Username", "username", TextField.class);
        TextField email = binder.buildAndBind("Email", "email", TextField.class);
        TextField firstName = binder.buildAndBind("First Name", "firstName", TextField.class);
        TextField lastName = binder.buildAndBind("Last Name", "lastName", TextField.class);
        PasswordField password = binder.buildAndBind("Password", "password", PasswordField.class);
        PasswordField passwordCheck = binder.buildAndBind("Password Confirmation", "confirmPassword", PasswordField.class);

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
                bus.post(new SignUpEvent(this, formBean));
            }
        });

        Button signupBtn = new Button("Sign Up", event -> {
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                NotificationUtils.notifyWarn("Sign Up Submit Error",
                        "Some fields contain errors. Check them and try again");
            }
        });
        formLayout.setSpacing(true);
        formLayout.setMargin(new MarginInfo(true, true, true, false));
        formLayout.setSizeUndefined();
        formLayout.addComponents(userName, email, firstName, lastName, password, passwordCheck, signupBtn);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Login", FontAwesome.SIGN_IN, selectedItem -> navigateTo(LoginView.NAME));
    }
}
