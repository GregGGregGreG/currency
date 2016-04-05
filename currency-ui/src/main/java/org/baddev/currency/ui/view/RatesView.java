package org.baddev.currency.ui.view;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.baddev.currency.core.fetcher.entity.BaseExchangeRate;
import org.baddev.currency.ui.MyUI;

import javax.annotation.PostConstruct;

/**
 * Created by IPotapchuk on 4/5/2016.
 */
@SpringView(name = RatesView.NAME)
public class RatesView extends VerticalLayout implements View {

    public static final String NAME = "rates";
    private Grid rates = new Grid();

    @PostConstruct
    public void init() {
        setSizeFull();
        setupRatesGrid();
        addComponent(rates);
    }

    private void setupRatesGrid(){
        rates.setSizeFull();
        rates.setSelectionMode(Grid.SelectionMode.SINGLE);
        BeanItemContainer<BaseExchangeRate> container = new BeanItemContainer<>(BaseExchangeRate.class);
        GeneratedPropertyContainer wrapperContainer = new GeneratedPropertyContainer(container);
        rates.setContainerDataSource(wrapperContainer);
        wrapperContainer.removeContainerProperty("id");
        rates.setColumnOrder("exchangeDate", "baseLiterCode", "literCode", "rate");
        container.addAll(MyUI.current().getFetcher().fetchCurrent());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
