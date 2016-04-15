package org.baddev.currency.core.exchange.entity;

import org.baddev.currency.core.Identity;
import org.baddev.currency.core.exchange.exception.CurrencyNotFoundException;
import org.baddev.currency.core.fetcher.entity.ExchangeRate;
import org.joda.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@XmlRootElement(name = "exchange")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeOperation implements Identity<Long> {

    @XmlAttribute
    private Long id;
    private String amountCurrencyCode;
    private double amount;
    private String exchangedAmountCurrencyCode;
    private double exchangedAmount;
    private LocalDate date;

    public static final String P_ID = "id";
    public static final String P_AM_CD = "amountCurrencyCode";
    public static final String P_AM = "amount";
    public static final String P_EXC_AM_CD = "exchangedAmountCurrencyCode";
    public static final String P_EXC_AM = "exchangedAmount";
    public static final String P_DATE = "date";

    public ExchangeOperation() {
    }

    private ExchangeOperation(Builder builder) {
        setId(builder.id);
        setAmountCurrencyCode(builder.amountCurrencyCode);
        setAmount(builder.amount);
        setExchangedAmountCurrencyCode(builder.exchangedAmountCurrencyCode);
        setExchangedAmount(builder.exchangedAmount);
        setDate(builder.date);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAmountCurrencyCode() {
        return amountCurrencyCode;
    }

    public void setAmountCurrencyCode(String amountCurrencyCode) {
        this.amountCurrencyCode = amountCurrencyCode;
    }

    public String getExchangedAmountCurrencyCode() {
        return exchangedAmountCurrencyCode;
    }

    public void setExchangedAmountCurrencyCode(String exchangedAmountCurrencyCode) {
        this.exchangedAmountCurrencyCode = exchangedAmountCurrencyCode;
    }

    public double getExchangedAmount() {
        return exchangedAmount;
    }

    public void setExchangedAmount(double exchangedAmount) {
        this.exchangedAmount = exchangedAmount;
    }

    public double exchange(Collection<ExchangeRate> rates) throws CurrencyNotFoundException {
        ExchangeRate exc = null;
        for (ExchangeRate ex : rates) {
            if (ex.getCcy().equals(this.amountCurrencyCode)) {
                exc = ex;
                break;
            }
        }
        if (exc == null) {
            throw new CurrencyNotFoundException("Currency " + amountCurrencyCode + " not found.");
        }
        exchangedAmount = exc.getRate() * amount;
        return exchangedAmount;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExchangeOperation{");
        sb.append("id=").append(id);
        sb.append(", amountCurrencyCode='").append(amountCurrencyCode).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", exchangedAmountCurrencyCode='").append(exchangedAmountCurrencyCode).append('\'');
        sb.append(", exchangedAmount=").append(exchangedAmount);
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private Long id;
        private String amountCurrencyCode;
        private double amount;
        private String exchangedAmountCurrencyCode;
        private double exchangedAmount;
        private LocalDate date;

        private Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder from(String val) {
            amountCurrencyCode = val;
            return this;
        }

        public Builder amount(double val) {
            amount = val;
            return this;
        }

        public Builder to(String val) {
            exchangedAmountCurrencyCode = val;
            return this;
        }

        public Builder exchangedAmount(double val) {
            exchangedAmount = val;
            return this;
        }

        public Builder date(LocalDate val) {
            date = val;
            return this;
        }

        public ExchangeOperation build() {
            return new ExchangeOperation(this);
        }
    }
}
