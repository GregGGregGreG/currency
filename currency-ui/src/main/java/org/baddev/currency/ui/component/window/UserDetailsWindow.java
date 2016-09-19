package org.baddev.currency.ui.component.window;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.security.user.UserService;
import org.baddev.currency.ui.component.base.AbstractLookupFormWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class UserDetailsWindow extends AbstractLookupFormWindow<IUserDetails> {

    private final UserService userService;

    @Autowired
    public UserDetailsWindow(UserService userService) {
        super(IUserDetails.class, "User Details");
        this.userService = userService;
    }

    @Override
    protected final void customizeForm(FormLayout form, BeanFieldGroup<IUserDetails> binder, Button submitBtn) {
        TextField fname = binder.buildAndBind("First Name", UserDetails.P_FIRST_NAME, TextField.class);
        TextField lname = binder.buildAndBind("Last Name", UserDetails.P_LAST_NAME, TextField.class);
        TextField email = binder.buildAndBind("Email", UserDetails.P_EMAIL, TextField.class);

        Arrays.asList(fname, lname, email).forEach(f -> {
            f.setWidth(300, Unit.PIXELS);
            f.setRequired(true);
            f.setNullRepresentation("");
            f.setImmediate(true);
        });

        submitBtn.setCaption("Edit");

        form.addComponents(fname, lname, email, submitBtn);
    }

    @Override
    protected void onCommitSuccess(BeanFieldGroup<IUserDetails> binder) {
        userService.update(null, binder.getItemDataSource().getBean());
    }

    @Override
    protected void onCommitError(FieldGroup.CommitException e) {

    }
}
