package org.baddev.common.event;

import java.time.Instant;
import java.util.EventObject;
import java.util.UUID;

/**
 * Created by IPotapchuk on 6/17/2016.
 */
public abstract class BaseDataEvent<T> extends EventObject {

    private final UUID uuid;
    private final T eventData;
    private final Instant createdTime;

    protected BaseDataEvent(UUID uuid, Object source, T eventData) {
        super(source);
        this.eventData = eventData;
        this.uuid = uuid;
        createdTime = Instant.now();
    }

    protected BaseDataEvent(Object source, T eventData){
        this(UUID.randomUUID(), source, eventData);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public T getEventData() {
        return eventData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseDataEvent)) return false;

        BaseDataEvent<?> that = (BaseDataEvent<?>) o;

        if (!uuid.equals(that.uuid)) return false;
        if (!eventData.equals(that.eventData)) return false;
        return createdTime.equals(that.createdTime);

    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + eventData.hashCode();
        result = 31 * result + createdTime.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BaseDataEvent{" +
                "uuid=" + uuid +
                ", eventData=" + eventData +
                ", createdTime=" + createdTime +
                "} " + super.toString();
    }
}
