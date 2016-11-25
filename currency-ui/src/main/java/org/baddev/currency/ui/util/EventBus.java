package org.baddev.currency.ui.util;

import org.baddev.common.event.BaseDataEvent;
import org.baddev.currency.ui.CurrencyUI;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
public final class EventBus {

    public static void register(Object obj) {
        CurrencyUI.get().getEventBus().register(obj);
    }

    public static void unregister(Object obj) {
        CurrencyUI.get().getEventBus().unregister(obj);
    }

    public static <T extends BaseDataEvent> void post(T event) {
        CurrencyUI.get().getEventBus().post(event);
    }

    public String getId() {
        return CurrencyUI.get().getEventBus().identifier();
    }

}
