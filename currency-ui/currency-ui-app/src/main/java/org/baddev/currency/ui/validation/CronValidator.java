package org.baddev.currency.ui.validation;

import com.vaadin.data.validator.AbstractStringValidator;
import org.springframework.scheduling.support.CronSequenceGenerator;

/**
 * Created by IPOTAPCHUK on 6/10/2016.
 */
public class CronValidator extends AbstractStringValidator {

    public CronValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    protected boolean isValidValue(String value) {
        try {
            new CronSequenceGenerator(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
