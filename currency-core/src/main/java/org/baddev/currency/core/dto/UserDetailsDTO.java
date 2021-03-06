package org.baddev.currency.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.baddev.currency.jooq.schema.tables.interfaces.IUserDetails;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class UserDetailsDTO implements IUserDetails {
    private Long userId;
    @Size(min = 2, max = 50)
    @Pattern(regexp = "[A-Z][a-zA-Z]*",
            message = "must start with uppercase letter, numbers are forbidden")
    private String firstName;
    @Size(min = 2, max = 50)
    @Pattern(regexp = "[a-zA-z]+([ '-][a-zA-Z]+)*")
    private String lastName;
    @Email
    private String email;

    @Override
    public void from(IUserDetails from) {
        setUserId(from.getUserId());
        setEmail(from.getEmail());
        setFirstName(from.getFirstName());
        setLastName(from.getLastName());
    }

    @Override
    public <E extends IUserDetails> E into(E into) {
        into.from(this);
        return into;
    }
}
