package org.baddev.currency.ui.component.window;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.security.user.UserService;
import org.baddev.currency.ui.component.base.AbstractFormWindow;
import org.baddev.currency.ui.util.NotificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Created by IPotapchuk on 9/19/2016.
 */
@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public final class UserDetailsWindow extends AbstractFormWindow.EditModeWindow<IUserDetails> {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsWindow.class);
    private final UserService userService;

    @Autowired
    public UserDetailsWindow(UserService userService) {
        super("User Details", IUserDetails.class);
        this.userService = userService;
    }

    @Override
    protected final void customizeForm(FormLayout form, BeanFieldGroup<IUserDetails> binder) {
        TextField fname = binder.buildAndBind("First Name", UserDetails.P_FIRST_NAME, TextField.class);
        TextField lname = binder.buildAndBind("Last Name", UserDetails.P_LAST_NAME, TextField.class);
        TextField email = binder.buildAndBind("Email", UserDetails.P_EMAIL, TextField.class);

        Arrays.asList(fname, lname, email).forEach(f -> {
            f.setWidth(300, Unit.PIXELS);
            f.setRequired(true);
            f.setNullRepresentation("");
            f.setImmediate(true);
        });

        form.addComponents(fname, lname, email);
    }

    @Override
    protected void onCommitSuccess(BeanFieldGroup<IUserDetails> binder) {
        userService.update(null, binder.getItemDataSource().getBean());
        NotificationUtils.notifySuccess("User Details Changes", "Changes successfully saved");
    }

    @Override
    protected void onCommitError(FieldGroup.CommitException e) {
        log.warn("Error changing User Details", e);
        NotificationUtils.notifyWarn("User Details Changes Error", "Check the fields and try again");
    }
}
