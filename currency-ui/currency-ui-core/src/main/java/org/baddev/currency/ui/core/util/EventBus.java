package org.baddev.currency.ui.core.util;

import com.vaadin.ui.UI;
import org.baddev.currency.ui.core.EventBusAwareUI;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
public final class EventBus {

    public static void register(Object obj) {
        ((EventBusAwareUI)UI.getCurrent()).getEventBus().register(obj);
    }

    public static void unregister(Object obj) {
        ((EventBusAwareUI)UI.getCurrent()).getEventBus().unregister(obj);
    }

    public static void post(Object event) {
        ((EventBusAwareUI)UI.getCurrent()).getEventBus().post(event);
    }

    public String getId() {
        return ((EventBusAwareUI)UI.getCurrent()).getEventBus().identifier();
    }

}
