package org.baddev.currency.ui.component.view.user;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.core.dto.SignUpDTO;
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;
import org.baddev.currency.ui.core.util.Navigator;
import org.baddev.currency.ui.event.binder.SignUpEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.baddev.currency.ui.core.util.FieldGroupUtils.fConf;
import static org.baddev.currency.ui.core.util.NotificationUtils.notifySuccess;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = SignUpView.NAME)
public class SignUpView extends AbstractFormView<SignUpDTO> {

    private static final long serialVersionUID = -141726028201569275L;

    public static final String NAME = "signup";

    public SignUpView() {
        super(SignUpDTO.class, new SignUpDTO());
        setPanelCaption("Sign Up");
        setFormChangedCheckEnabled(false);
    }

    @Override
    protected void customizeForm(final FormLayout formLayout, final BeanFieldGroup<SignUpDTO> binder, final Button submitBtn) {
        List<? extends Field> fields = map(false, fConf("username", "Username", FontAwesome.USER),
                fConf("email", "Email", FontAwesome.AT),
                fConf("firstName", "First Name", FontAwesome.INFO),
                fConf("lastName", "Last Name", FontAwesome.INFO),
                fConf("password", "Password", FontAwesome.LOCK, PasswordField.class),
                fConf("confirmPassword", "Password Confirmation", FontAwesome.LOCK,
                        PasswordField.class, value -> {
                            if (!Objects.equals(value, binder.getField("password").getValue())) {
                                throw new Validator.InvalidValueException("passwords must match");
                            }
                        }));
        fields.forEach(f -> f.setRequired(true));
        configureFieldsWithDefaults(fields);
        submitBtn.setCaption("Sign Up");
        submitBtn.setIcon(FontAwesome.USER_PLUS);
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<SignUpDTO>> getEventType() {
        return SignUpEvent.class;
    }

    @Override
    protected void commitSuccess(SignUpDTO formBean) {
        notifySuccess("Account Creation",
                String.format("Account \"%s\" successfully created. Please, use your credentials to sign in",
                        formBean.getUsername()));
    }

    @Override
    protected void commitFailed(FieldGroup binder, Exception e) {
        binder.getField("password").clear();
        binder.getField("confirmPassword").clear();
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
