package org.baddev.currency.ui.component.view.user;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.MenuBar;
import org.baddev.currency.core.dto.ResetPasswordRequestDTO;
import org.baddev.currency.ui.core.component.view.AbstractFormView;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;
import org.baddev.currency.ui.core.util.Navigator;
import org.baddev.currency.ui.core.util.NotificationUtils;
import org.baddev.currency.ui.event.binder.ResetPwdStep1Event;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.baddev.currency.ui.core.util.FieldGroupUtils.fConf;

/**
 * Created by IPotapchuk on 11/8/2016.
 */
@SpringView(name = ResetPasswordStep1View.NAME)
public class ResetPasswordStep1View extends AbstractFormView<ResetPasswordRequestDTO> {

    public static final String NAME = "reqreset";

    public ResetPasswordStep1View() {
        super(ResetPasswordRequestDTO.class, new ResetPasswordRequestDTO());
        setPanelCaption("Reset Password Step 1");
        setFormChangedCheckEnabled(false);
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<ResetPasswordRequestDTO> binder, Button submitBtn) {
        List<? extends Field> fields = map(false, fConf("email", "Email", FontAwesome.AT));
        fields.forEach(f -> f.setRequired(true));
        configureFieldsWithDefaults(fields);
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<ResetPasswordRequestDTO>> getEventType() {
        return ResetPwdStep1Event.class;
    }

    @Override
    protected void commitSuccess(ResetPasswordRequestDTO formBean) {
        NotificationUtils.notifySuccessCloseable("Password Reset",
                "We sent an email to "+formBean.getEmail()+". Please, follow an instructions in the letter");
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
