package org.baddev.currency.ui.component.view;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.security.dto.SignUpDTO;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = SignUpView.NAME)
public class SignUpView extends AbstractFormView<SignUpDTO> {

    public static final String NAME = "signup";
    private final EventBus bus;

    @Autowired
    public SignUpView(EventBus bus) {
        super(new SignUpDTO(), SignUpDTO.class);
        this.bus = bus;
    }

    @Override
    protected void customizeForm(final FormLayout formLayout, final BeanFieldGroup<SignUpDTO> binder, final Button submitBtn) {
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
                bus.post(new SignUpEvent(this, formBean, binder));
            }
        });

        submitBtn.setCaption("Sign Up");
        submitBtn.addClickListener(event -> {
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                password.clear();
                passwordCheck.clear();
                NotificationUtils.notifyWarn("Sign Up Submit Error",
                        "Some fields contain errors. Check them and try again");
            }
        });

        formLayout.addComponents(userName, email, firstName, lastName, password, passwordCheck, submitBtn);
    }

    @Override
    public void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Login", FontAwesome.SIGN_IN, selectedItem -> navigateTo(LoginView.NAME));
    }
}
