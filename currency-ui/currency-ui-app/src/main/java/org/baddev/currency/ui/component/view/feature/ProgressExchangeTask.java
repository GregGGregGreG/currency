package org.baddev.currency.ui.component.view.feature;

import lombok.Getter;
import lombok.Setter;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;

/**
 * Created by IPotapchuk on 11/25/2016.
 */
public class ProgressExchangeTask extends ExchangeTask {

    @Setter
    @Getter
    private double progress;
}
