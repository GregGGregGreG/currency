package org.baddev.currency.core.util;

import org.baddev.currency.core.CommonErrorHandler;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by IPotapchuk on 10/6/2016.
 */
public final class Safe {

    private Safe() {
    }

    @FunctionalInterface
    public interface Caller {
        void call();
    }

    public static <T, E extends CommonErrorHandler> Optional<T> trySupply(E errorHandler, Supplier<T> context) {
        Optional<T> result = Optional.empty();
        try {
            result = Optional.ofNullable(context.get());
        } catch (Exception e) {
            errorHandler.handle(e);
        }
        return result;
    }

    public static <T> Optional<T> trySupply(Supplier<T> context) {
        return trySupply(new CommonErrorHandler(), context);
    }

    public static <E, T extends Collection<E>, R extends CommonErrorHandler> Stream<E> trySupplyStream(R errorHandler, Supplier<T> context) {
        Stream<E> result = Stream.empty();
        try {
            result = context.get().stream();
        } catch (Exception e) {
           errorHandler.handle(e);
        }
        return result;
    }

    public static <E, T extends Collection<E>> Stream<E> trySupplyStream(Supplier<T> context) {
        return trySupplyStream(new CommonErrorHandler(), context);
    }

    public static void tryCall(Caller context) {
        tryCall(new CommonErrorHandler(), context);
    }

    public static <T extends CommonErrorHandler> void tryCall(T errorHandler, Caller context) {
        try {
            context.call();
        } catch (Exception e) {
            errorHandler.handle(e);
        }
    }

}
