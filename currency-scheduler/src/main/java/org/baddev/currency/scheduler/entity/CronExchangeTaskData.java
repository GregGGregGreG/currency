package org.baddev.currency.scheduler.entity;

import org.baddev.currency.core.MutableIdentity;
import org.joda.time.LocalDateTime;

/**
 * Created by IPOTAPCHUK on 6/9/2016.
 */
public class CronExchangeTaskData implements MutableIdentity<Long> {

    private Long id;
    private LocalDateTime addedDate;
    private String fromCcy;
    private String toCcy;
    private double amount;
    private String cron;

    public static final String P_ID = "id";
    public static final String P_AD_DT = "addedDate";
    public static final String P_AM_CD = "fromCcy";
    public static final String P_EXC_AM_CD = "toCcy";
    public static final String P_AM = "amount";
    public static final String P_CRON = "cron";

    public CronExchangeTaskData() {
    }

    private CronExchangeTaskData(Builder builder) {
        setId(builder.id);
        setAddedDate(builder.addedDate);
        setFromCcy(builder.fromCcy);
        setToCcy(builder.toCcy);
        setAmount(builder.amount);
        setCron(builder.cron);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    public String getFromCcy() {
        return fromCcy;
    }

    public void setFromCcy(String fromCcy) {
        this.fromCcy = fromCcy;
    }

    public String getToCcy() {
        return toCcy;
    }

    public void setToCcy(String toCcy) {
        this.toCcy = toCcy;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getCron() {
        return cron;
    }


    public static final class Builder {
        private Long id;
        private LocalDateTime addedDate;
        private String fromCcy;
        private String toCcy;
        private double amount;
        private String cron;

        private Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder addedDate(LocalDateTime val) {
            addedDate = val;
            return this;
        }

        public Builder fromCcy(String val) {
            fromCcy = val;
            return this;
        }

        public Builder toCcy(String val) {
            toCcy = val;
            return this;
        }

        public Builder amount(double val) {
            amount = val;
            return this;
        }

        public Builder cron(String val) {
            cron = val;
            return this;
        }

        public CronExchangeTaskData build() {
            return new CronExchangeTaskData(this);
        }
    }
}
