package org.baddev.currency.exchange.entity;

import org.baddev.currency.Identity;
import org.baddev.currency.exchange.exception.CurrencyNotFoundException;
import org.baddev.currency.fetcher.entity.ExchangeRate;
import org.joda.time.LocalDate;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by IPotapchuk on 3/14/2016.
 */
@XmlRootElement(name = "exchange")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeOperation implements Identity<Long> {

    @XmlAttribute
    private Long id;
    @XmlElement
    private String amountLiterCode;
    private double amount;
    private String exchangedAmountLiterCode;
    private double exchangedAmount;
    private LocalDate date;

    public ExchangeOperation() {
    }

    private ExchangeOperation(Builder builder) {
        setId(builder.id);
        setAmountLiterCode(builder.amountCurrencyLiterCode);
        setAmount(builder.amount);
        setExchangedAmountLiterCode(builder.exchangedAmountCurrencyLiterCode);
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

    public String getAmountLiterCode() {
        return amountLiterCode;
    }

    public void setAmountLiterCode(String amountLiterCode) {
        this.amountLiterCode = amountLiterCode;
    }

    public String getExchangedAmountLiterCode() {
        return exchangedAmountLiterCode;
    }

    public void setExchangedAmountLiterCode(String exchangedAmountLiterCode) {
        this.exchangedAmountLiterCode = exchangedAmountLiterCode;
    }

    public double getExchangedAmount() {
        return exchangedAmount;
    }

    public void setExchangedAmount(double exchangedAmount) {
        this.exchangedAmount = exchangedAmount;
    }

    public void exchange(List<ExchangeRate> rates) throws CurrencyNotFoundException {
        ExchangeRate exc = null;
        for (ExchangeRate ex : rates) {
            if (ex.getLiterCode().equals(this.amountLiterCode)) {
                exc = ex;
                break;
            }
        }
        if (exc == null) {
            throw new CurrencyNotFoundException("Currency " + amountLiterCode + " not found.");
        }
        exchangedAmount = exc.getRate() * amount;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExchangeOperation{");
        sb.append("id=").append(id);
        sb.append(", amountLiterCode='").append(amountLiterCode).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", exchangedAmountLiterCode='").append(exchangedAmountLiterCode).append('\'');
        sb.append(", exchangedAmount=").append(exchangedAmount);
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }

    public static final class Builder {
        private Long id;
        private String amountCurrencyLiterCode;
        private double amount;
        private String exchangedAmountCurrencyLiterCode;
        private double exchangedAmount;
        private LocalDate date;

        private Builder() {
        }

        public Builder id(Long val) {
            id = val;
            return this;
        }

        public Builder from(String val) {
            amountCurrencyLiterCode = val;
            return this;
        }

        public Builder amount(double val) {
            amount = val;
            return this;
        }

        public Builder to(String val) {
            exchangedAmountCurrencyLiterCode = val;
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
