package org.baddev.currency.ui.component.view;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.dto.SignInDTO;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.exception.WrappedUIException;
import org.baddev.currency.ui.security.event.SignInEvent;
import org.baddev.currency.ui.util.EventBus;
import org.baddev.currency.ui.util.Navigator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = SignInView.NAME)
public class SignInView extends AbstractFormView<SignInDTO> {

    public static final String NAME = "signin";

    public SignInView() {
        super(new SignInDTO(), SignInDTO.class);
    }

    @Override
    protected void customizeForm(final FormLayout formLayout, final BeanFieldGroup<SignInDTO> binder, final Button submitBtn) {
        setPanelCaption("Sign In");
        TextField userName = binder.buildAndBind("Username", "username", TextField.class);
        userName.setIcon(FontAwesome.USER);
        PasswordField password = binder.buildAndBind("Password", "password", PasswordField.class);
        password.setIcon(FontAwesome.LOCK);

        Arrays.asList(userName, password).forEach(f -> {
            f.setWidth(300, Unit.PIXELS);
            f.setRequired(true);
            f.setNullRepresentation("");
            f.setImmediate(true);
        });

        binder.addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                EventBus.post(new SignInEvent(this, formBean, binder));
            }
        });

        submitBtn.setCaption("Sign In");
        submitBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        submitBtn.addClickListener(event -> {
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                binder.clear();
                throw new WrappedUIException(e);
            }
        });
        formLayout.addComponents(userName, password, submitBtn);
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
