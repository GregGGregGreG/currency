package org.baddev.currency.ui.core;

import com.google.common.eventbus.EventBus;

/**
 * Created by monkey on 29.11.2016.
 */
public interface EventBusAwareUI {
    EventBus getEventBus();
}
