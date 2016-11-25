package org.baddev.common.event;

import org.baddev.common.SafeProcessable;
import org.baddev.common.utils.Safe;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by IPotapchuk on 11/16/2016.
 */
public abstract class SafeProcessableDataEvent<T, E, SELF extends SafeProcessableDataEvent> extends BaseDataEvent<T> implements SafeProcessable<SELF> {

    private final BiConsumer<E, Exception> failAction;
    private final Consumer<T> successAction;

    protected SafeProcessableDataEvent(UUID uuid,
                                       Object source,
                                       T eventData,
                                       BiConsumer<E, Exception> failAction,
                                       Consumer<T> successAction) {
        super(uuid, source, eventData);
        this.failAction = failAction;
        this.successAction = successAction;
    }

    protected SafeProcessableDataEvent(Object source,
                                       T eventData,
                                       BiConsumer<E, Exception> failAction,
                                       Consumer<T> successAction) {
        this(UUID.randomUUID(), source, eventData, failAction, successAction);
    }

    protected abstract E getFailActionArg();

    @Override
    public void process(Consumer<SELF> selfConsumer) throws RuntimeException {
        Function<SELF, Safe.Caller> f = arg -> () -> selfConsumer.accept(arg);
        Safe.tryWithActionsAndRethrow(f.apply((SELF) this),
                failAction,
                getFailActionArg(),
                successAction,
                getEventData());
    }

}
