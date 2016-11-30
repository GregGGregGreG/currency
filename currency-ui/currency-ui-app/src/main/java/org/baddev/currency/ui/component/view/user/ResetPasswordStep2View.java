package org.baddev.currency.ui.component.view.user;

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
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;
import org.baddev.currency.ui.core.util.Navigator;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.baddev.currency.ui.event.binder.ResetPwdStep2Event;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static org.baddev.currency.ui.core.util.FieldGroupUtils.fConf;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
@SpringView(name = ResetPasswordStep2View.NAME)
public class ResetPasswordStep2View extends AbstractFormView<ResetPasswordDTO> {

    public static final String NAME = "reset";

    private String token;

    public ResetPasswordStep2View() {
        super(ResetPasswordDTO.class, new ResetPasswordDTO());
        setPanelCaption("Reset Password Step 2");
        setFormChangedCheckEnabled(false);
    }

    public void setToken(String token) {
        this.token = token;
        formBean.setToken(token);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<ResetPasswordDTO> binder, Button submitBtn) {
        map(fConf("password", "Password", FontAwesome.LOCK, PasswordField.class),
                fConf("passwordConfirm", "Confirm Password", FontAwesome.LOCK,
                        PasswordField.class, value -> {
                            if (!Objects.equals(value, binder.getField("password").getValue())) {
                                throw new Validator.InvalidValueException("passwords must match");
                            }
                        }));
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<ResetPasswordDTO>> getEventType() {
        return ResetPwdStep2Event.class;
    }

    @Override
    public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        if (token == null) {
            commitEvent.getFieldBinder().clear();
            throw new FieldGroup.CommitException("Password change is not allowed");
        }
    }

    @Override
    protected void commitSuccess(ResetPasswordDTO formBean) {
        NotificationUtils.notifySuccess("Password Reset", "Password updated. Please, use your new credentials to login");
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
