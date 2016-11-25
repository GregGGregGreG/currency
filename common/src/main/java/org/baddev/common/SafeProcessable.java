package org.baddev.common;

import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 11/16/2016.
 */
public interface SafeProcessable<T extends SafeProcessable> {

    void process(Consumer<T> selfConsumer) throws RuntimeException;

    default void safeProcess(Consumer<T> selfConsumer){
        try {
            process(selfConsumer);
        } catch (RuntimeException e) {
        }
    }
}
