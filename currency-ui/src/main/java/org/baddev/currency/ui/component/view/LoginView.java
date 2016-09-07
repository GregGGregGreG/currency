package org.baddev.currency.ui.component.view;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.ui.component.base.AbstractFormView;
import org.baddev.currency.ui.security.entity.LoginData;
import org.baddev.currency.ui.security.event.LoginEvent;

import java.util.Arrays;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = LoginView.NAME)
public class LoginView extends AbstractFormView implements Button.ClickListener {

    public static final String NAME = "login";

    private TextField userName = new TextField("Username");
    private PasswordField password = new PasswordField("Password");
    private Button loginBtn = new Button("Login");

    @Override
    protected void customizeForm(FormLayout formLayout) {
        Arrays.asList(userName, password).forEach(f -> {
            f.setWidth(300, Unit.PIXELS);
            f.setRequired(true);
            f.setNullRepresentation("");
        });
        loginBtn.addClickListener(this);
        formLayout.setSpacing(true);
        formLayout.setMargin(new MarginInfo(true, true, true, false));
        formLayout.setSizeUndefined();
        formLayout.addComponents(userName, password, loginBtn);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        facade().postEvent(new LoginEvent(this, new LoginData(userName.getValue(), password.getValue())));
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Sign Up", FontAwesome.USER_PLUS, selectedItem -> navigateTo(SignUpView.NAME));
    }
}
