package org.baddev.currency.security;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IPotapchuk on 6/30/2016.
 */
public class UserDetails {

    private String firstName;
    private String lastName;
    private List<String> permissions;

    public UserDetails(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        permissions = new ArrayList<>();
        permissions.add(RoleEnum.USER);
    }

    public UserDetails(String firstName, String lastName, List<String> permissions) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.permissions = permissions;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
