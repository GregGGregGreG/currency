package org.baddev.currency.ui.component.view.user;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.dto.SignInDTO;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.event.binder.SafeProcessableBinderEvent;
import org.baddev.currency.ui.event.binder.SignInEvent;
import org.baddev.currency.ui.util.Navigator;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.baddev.currency.ui.util.FieldGroupUtils.fConf;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = SignInView.NAME)
public class SignInView extends AbstractFormView<SignInDTO> {

    private static final long serialVersionUID = 2149935732094845683L;

    public static final String NAME = "signin";

    public SignInView() {
        super(SignInDTO.class, new SignInDTO());
        setPanelCaption("Sign In");
        setFormChangedCheckEnabled(false);
    }

    @Override
    protected void customizeForm(final FormLayout formLayout, final BeanFieldGroup<SignInDTO> binder, final Button submitBtn) {
        List<? extends Field> fields = map(false,
                fConf("username", "Username", FontAwesome.USER),
                fConf("password", "Password", FontAwesome.LOCK, PasswordField.class)
        );
        fields.forEach(f -> f.setRequired(true));
        configureFieldsWithDefaults(fields);

        submitBtn.setCaption("Sign In");
        submitBtn.setIcon(FontAwesome.SIGN_IN);

        Button forgot = new Button("Forgot Password?", event -> {
            Navigator.navigate(ResetPasswordStep1View.NAME);
        });

        forgot.setStyleName(ValoTheme.BUTTON_LINK);
        forgot.addStyleName(ValoTheme.BUTTON_SMALL);

        HorizontalLayout wrapper = new HorizontalLayout(submitBtn, forgot);
        wrapper.setComponentAlignment(forgot, Alignment.MIDDLE_RIGHT);

        formLayout.addComponents(wrapper);
    }

    @Override
    protected Class<? extends SafeProcessableBinderEvent<SignInDTO>> getEventType() {
        return SignInEvent.class;
    }

    @Override
    protected void commitFailed(FieldGroup binder, Exception e) {
        binder.clear();
    }

    @Override
    public Collection<MenuBar.MenuItem> customizeMenuBar(MenuBar menuBar) {
        return Collections.singletonList(menuBar.addItem("Sign Up", FontAwesome.USER_PLUS, selectedItem -> Navigator.navigate(SignUpView.NAME)));
    }

    @Override
    public String getNameCaption() {
        return "Sign In";
    }
}
