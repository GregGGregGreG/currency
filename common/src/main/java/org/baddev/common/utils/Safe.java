package org.baddev.common.utils;

import org.baddev.common.CommonErrorHandler;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

    public static <E, S, T extends BiConsumer<E, Exception>,
            V extends Consumer<S>> void tryWithActionsAndRethrow(Caller c,
                                                                 Supplier<T> failActionSupplier,
                                                                 Supplier<E> failActionArgSupplier,
                                                                 Supplier<V> successActionSupplier,
                                                                 Supplier<S> successActionArgSupplier) {
        tryWithActionsAndRethrow(c,
                failActionSupplier.get(),
                failActionArgSupplier.get(),
                successActionSupplier.get(),
                successActionArgSupplier.get());
    }

    public static <E, S> void tryWithActionsAndRethrow(Caller c,
                                                       BiConsumer<E, Exception> failAction,
                                                       E failActionArg,
                                                       Consumer<S> successAction,
                                                       S successActionArg) {
        try {
            c.call();
            successAction.accept(successActionArg);
        } catch (Exception e) {
            failAction.accept(failActionArg, e);
            throw e;
        }
    }

    public static <E, S, T extends BiConsumer<E, Exception>,
            V extends Consumer<S>> void tryWithActions(Caller c,
                                                       Supplier<T> failActionSupplier,
                                                       Supplier<E> failActionArgSupplier,
                                                       Supplier<V> successActionSupplier,
                                                       Supplier<S> successActionArgSupplier) {
        try {
            tryWithActionsAndRethrow(c,
                    failActionSupplier,
                    failActionArgSupplier,
                    successActionSupplier,
                    successActionArgSupplier);
        } catch (Exception e) {
        }
    }

    public static <E, S> void tryWithActions(Caller c,
                                             BiConsumer<E, Exception> failAction,
                                             E failActionArg,
                                             Consumer<S> successAction,
                                             S successActionArg) {
        try {
            tryWithActionsAndRethrow(c, failAction, failActionArg, successAction, successActionArg);
        } catch (Exception e) {
        }
    }

    public static void tryWithFailAndRethrow(Caller c, Consumer<Exception> failAction) {
        try {
            c.call();
        } catch (Exception e) {
            failAction.accept(e);
            throw e;
        }
    }

    public static <T extends BiConsumer<E, Exception>, E> void tryWithFailAndRethrow(Caller c, Supplier<T> failActionSupplier, Supplier<E> failActionArgSupplier) {
        try {
            c.call();
        } catch (Exception e) {
            failActionSupplier.get().accept(failActionArgSupplier.get(), e);
            throw e;
        }
    }

    public static void tryWithFail(Caller c, Consumer<Exception> failAction) {
        try {
            tryWithFailAndRethrow(c, failAction);
        } catch (Exception e) {
        }
    }

    public static <T extends BiConsumer<E, Exception>, E> void tryWithFail(Caller c, Supplier<T> failActionSupplier, Supplier<E> failActionArgSupplier) {
        try {
            tryWithFailAndRethrow(c, failActionSupplier, failActionArgSupplier);
        } catch (Exception e) {
        }
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
