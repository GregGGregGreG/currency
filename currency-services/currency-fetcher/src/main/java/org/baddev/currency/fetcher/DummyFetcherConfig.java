package org.baddev.currency.fetcher;

import org.baddev.currency.core.meta.Dev;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyEntry;
import org.baddev.currency.fetcher.iso4217.entity.IsoCcyHistEntry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * Created by IPotapchuk on 10/31/2016.
 */
@Dev @Configuration
@ComponentScan("org.baddev.currency.fetcher")
public class DummyFetcherConfig {

    @Bean(name = "IsoCurCcys")
    public List<IsoCcyEntry> isoCurCcyEntryList() {
        return Collections.emptyList();
    }

    @Bean(name = "IsoHistCcys")
    public List<IsoCcyHistEntry> isoCcyHistEntryList() {
        return Collections.emptyList();
    }

}
