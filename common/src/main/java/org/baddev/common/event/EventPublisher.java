package org.baddev.common.event;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public interface EventPublisher extends Serializable {
    void publish(BaseDataEvent event);
    boolean subscribe(GenericEventListener listener);
    boolean unsubscribe(GenericEventListener listener);
    Collection<GenericEventListener> getSubscribers();
}
