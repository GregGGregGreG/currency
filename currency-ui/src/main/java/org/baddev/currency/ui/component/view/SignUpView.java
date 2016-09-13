package org.baddev.currency.ui.component.view;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.baddev.currency.ui.component.base.AbstractFormView;
import org.baddev.currency.ui.security.dto.SignUpData;
import org.baddev.currency.ui.security.event.SignUpEvent;

import java.util.Arrays;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
@SpringView(name = SignUpView.NAME)
public class SignUpView extends AbstractFormView implements Button.ClickListener {

    public static final String NAME = "signup";

    private TextField userName = new TextField("Username");
    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private PasswordField password = new PasswordField("Password");
    private PasswordField passwordCheck = new PasswordField("Password Confirmation");
    private Button signupBtn = new Button("Sign Up");

    @Override
    protected void customizeForm(FormLayout formLayout) {
        Arrays.asList(userName, firstName, lastName, password, passwordCheck).forEach(f -> {
            f.setWidth(300, Unit.PIXELS);
            f.setRequired(true);
            f.setNullRepresentation("");
        });
        signupBtn.addClickListener(this);
        formLayout.setSpacing(true);
        formLayout.setMargin(new MarginInfo(true, true, true, false));
        formLayout.setSizeUndefined();
        formLayout.addComponents(userName, firstName, lastName, password, passwordCheck, signupBtn);
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        bus.post(new SignUpEvent(this,
                new SignUpData(userName.getValue(), password.getValue(), firstName.getValue(), lastName.getValue())));
    }

    @Override
    protected void customizeMenuBar(MenuBar menuBar) {
        menuBar.addItem("Login", FontAwesome.SIGN_IN, selectedItem -> navigateTo(LoginView.NAME));
    }
}
