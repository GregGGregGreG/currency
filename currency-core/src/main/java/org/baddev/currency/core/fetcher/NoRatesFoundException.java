package org.baddev.currency.core.fetcher;

public final class NoRatesFoundException extends Exception {
    public NoRatesFoundException(String message) {
        super(message);
    }
}