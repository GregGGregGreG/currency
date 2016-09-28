package org.baddev.currency.ui.component.view;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.security.dto.LoginDTO;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.security.event.LoginEvent;
import org.baddev.currency.ui.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = LoginView.NAME)
public class LoginView extends AbstractFormView<LoginDTO> {

    public static final String NAME = "login";

    private final EventBus bus;

    @Autowired
    public LoginView(EventBus bus) {
        super(new LoginDTO(), LoginDTO.class);
        this.bus = bus;
    }

    @Override
    protected void customizeForm(final FormLayout formLayout, final BeanFieldGroup<LoginDTO> binder, final Button submitBtn) {
        TextField userName = binder.buildAndBind("Username", "username", TextField.class);
        PasswordField password = binder.buildAndBind("Password", "password", PasswordField.class);

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
                bus.post(new LoginEvent(this, formBean, binder));
            }
        });

        submitBtn.setCaption("Login");
        submitBtn.addClickListener(event -> {
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                binder.clear();
                NotificationUtils.notifyWarn("Login Submit Error",
                        "Some fields contain errors. Check them and try again");
            }
        });

        formLayout.addComponents(userName, password, submitBtn);
    }

    @Override
    public void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Sign Up", FontAwesome.USER_PLUS, selectedItem -> navigateTo(SignUpView.NAME));
    }
}
