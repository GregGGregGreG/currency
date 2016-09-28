package org.baddev.currency.ui.service;

import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinService;
import com.vaadin.spring.annotation.VaadinSessionScope;
import org.baddev.currency.jooq.schema.tables.daos.UserPreferencesDao;
import org.baddev.currency.jooq.schema.tables.pojos.UserPreferences;
import org.baddev.currency.mail.ExchangeCompletionMailer;
import org.baddev.currency.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static org.baddev.currency.ui.util.VaadinSessionUtils.getSessionAttribute;
import static org.baddev.currency.ui.util.VaadinSessionUtils.setSessionAttribute;

/**
 * Created by IPotapchuk on 9/26/2016.
 */
@Service
@VaadinSessionScope
public class UserPreferencesServiceImpl implements UserPreferencesService, SessionDestroyListener {

    private static final Logger log = LoggerFactory.getLogger(UserPreferencesServiceImpl.class);
    
    @Autowired
    private UserPreferencesDao preferencesDao;
    @Autowired
    private ExchangeCompletionMailer mailer;

    @PostConstruct
    private void init() {
        VaadinService.getCurrent().addSessionDestroyListener(this);
    }

    @Override
    @Transactional(readOnly = true)
    public void loadPreferencesIntoSession() {
        UserPreferences preferences = preferencesDao.fetchOneByUserId(SecurityUtils.getIdentityUserPrincipal().getId());
        setSessionAttribute(UserPreferences.class, preferences);
    }

    @Override
    @Transactional
    public void updatePreferencesFromSession() {
        UserPreferences preferences = getSessionAttribute(UserPreferences.class);
        preferencesDao.update(preferences);
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        updatePreferencesFromSession();
        log.info("User preferences saved");
    }
}
