package org.baddev.currency.ui.config;

import com.vaadin.server.*;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IPotapchuk on 10/5/2016.
 */
public class SessionInitDestroyListener implements SessionInitListener, SessionDestroyListener {

    private static final Logger log = LoggerFactory.getLogger(SessionInitDestroyListener.class);

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        log.debug("Session destroyed, {}", event.getSession());
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        log.debug("Session initialized, {}", event.getSession());
        event.getSession().addBootstrapListener(new BootstrapListener() {

            @Override
            public void modifyBootstrapPage(final BootstrapPageResponse response) {
                final Element head = response.getDocument().head();
                head.appendElement("meta")
                        .attr("name", "viewport")
                        .attr("content",
                                "width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no");
                head.appendElement("meta")
                        .attr("name", "apple-mobile-web-app-capable")
                        .attr("content", "yes");
                head.appendElement("meta")
                        .attr("name", "apple-mobile-web-app-status-bar-style")
                        .attr("content", "black-translucent");
//                String contextPath = response.getRequest().getContextPath();
//                head.appendElement("link")
//                        .attr("rel", "apple-touch-icon")
//                        .attr("href",
//                                contextPath
//                                        + "/VAADIN/themes/dashboard/img/app-icon.png");
            }

            @Override
            public void modifyBootstrapFragment(BootstrapFragmentResponse response) {

            }
        });
    }
}
