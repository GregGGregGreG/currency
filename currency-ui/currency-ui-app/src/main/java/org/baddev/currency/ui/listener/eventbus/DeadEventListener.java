package org.baddev.currency.ui.listener.eventbus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.io.Serializable;

/**
 * Created by IPotapchuk on 11/18/2016.
 */
@SpringComponent
@UIScope
@RequiredArgsConstructor
public class DeadEventListener implements Serializable{

    private static final long serialVersionUID = -619269944424593146L;

    private final Logger log;

    @Subscribe
    private void deadEvent(DeadEvent event) {
        log.error("No subscribers registered for event {}", event.getEvent().getClass());
    }

}
