package org.baddev.currency.ui.component.view;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.ui.component.base.AbstractFormView;
import org.baddev.currency.ui.security.dto.LoginData;
import org.baddev.currency.ui.security.event.LoginEvent;
import org.baddev.currency.ui.util.NotificationUtils;

import java.util.Arrays;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = LoginView.NAME)
public class LoginView extends AbstractFormView<LoginData> {

    public static final String NAME = "login";

    public LoginView() {
        super(LoginData.class, new LoginData());
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<LoginData> binder) {
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
                bus.post(new LoginEvent(this, formBean));
            }
        });

        Button loginBtn = new Button("Login", event -> {
            try {
                binder.commit();
            } catch (FieldGroup.CommitException e) {
                NotificationUtils.notifyWarn("Login Submit Error",
                        "Some fields contain errors. Check them and try again");
            }
        });

        formLayout.setSpacing(true);
        formLayout.setMargin(new MarginInfo(true, true, true, false));
        formLayout.setSizeUndefined();
        formLayout.addComponents(userName, password, loginBtn);
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Sign Up", FontAwesome.USER_PLUS, selectedItem -> navigateTo(SignUpView.NAME));
    }
}
