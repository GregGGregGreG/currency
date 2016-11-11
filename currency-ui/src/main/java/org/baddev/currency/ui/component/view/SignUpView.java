package org.baddev.currency.ui.component.view;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.core.dto.SignUpDTO;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.security.event.SignUpEvent;
import org.baddev.currency.ui.util.EventBus;
import org.baddev.currency.ui.util.Navigator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = SignUpView.NAME)
public class SignUpView extends AbstractFormView<SignUpDTO> {

    private static final long serialVersionUID = -141726028201569275L;

    public static final String NAME = "signup";

    private PasswordField password;
    private PasswordField passwordCheck;

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
        password = binder.buildAndBind("Password", "password", PasswordField.class);
        password.setIcon(FontAwesome.LOCK);
        passwordCheck = binder.buildAndBind("Password Confirmation", "confirmPassword", PasswordField.class);
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

        submitBtn.setCaption("Sign Up");
        submitBtn.setIcon(FontAwesome.USER_PLUS);

        formLayout.addComponents(userName, email, firstName, lastName, password, passwordCheck, submitBtn);
    }

    @Override
    protected void submitBtnClicked(Button.ClickEvent clickEvent, BeanFieldGroup<SignUpDTO> binder) {
        try {
            binder.commit();
        } catch (FieldGroup.CommitException e) {
            password.clear();
            passwordCheck.clear();
            throw new WrappedUIException(e);
        }
    }

    @Override
    public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        password.validate();
    }

    @Override
    public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        EventBus.post(new SignUpEvent(this, formBean, commitEvent.getFieldBinder()));
    }

    @Override
    public Collection<MenuBar.MenuItem> customizeMenuBar(MenuBar menuBar) {
        return Collections.singletonList(menuBar.addItem("Sign In", FontAwesome.SIGN_IN, selectedItem -> Navigator.navigate(SignInView.NAME)));
    }

    @Override
    public String getNameCaption() {
        return "Sign Up";
    }
}
