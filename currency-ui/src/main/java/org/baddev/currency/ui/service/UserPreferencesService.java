package org.baddev.currency.ui.service;

/**
 * Created by IPotapchuk on 9/26/2016.
 */
public interface UserPreferencesService {
    void loadPreferencesIntoSession();
    void updatePreferencesFromSession();
}
