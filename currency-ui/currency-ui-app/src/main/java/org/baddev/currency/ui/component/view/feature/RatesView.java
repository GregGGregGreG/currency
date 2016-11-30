package org.baddev.currency.ui.component.view.feature;

import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.baddev.currency.core.api.ExchangeRateService;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.fetcher.iso4217.Iso4217CcyService;
import org.baddev.currency.fetcher.iso4217.IsoEntityParam;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeRate;
import org.baddev.currency.ui.converter.DoubleAmountToStringConverter;
import org.baddev.currency.ui.core.component.toolbar.GridButtonToolbar;
import org.baddev.currency.ui.core.component.view.AbstractGridView;
import org.baddev.currency.ui.core.converter.DateToLocalDateConverter;
import org.baddev.currency.ui.core.model.grid.FilterConfig;
import org.baddev.currency.ui.core.model.grid.FilterKind;
import org.baddev.currency.ui.core.util.FormatUtils;
import org.baddev.currency.ui.core.util.Navigator;
import org.baddev.currency.ui.generator.Iso4217PropertyValGen;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate.*;
import static org.baddev.currency.ui.core.util.UIUtils.toggleVisible;

/**
 * Created by IPotapchuk on 4/8/2016.
 */
@SpringView(name = RatesView.NAME)
@RolesAllowed({RoleEnum.ADMIN, RoleEnum.USER})
public class RatesView extends AbstractGridView<IExchangeRate> {

    private static final long serialVersionUID = 6861511559761673896L;

    public  static final String NAME           = "rates";
    private static final String P_GEN_CCY_NAME = IsoEntityParam.CCY_NM.fieldName();

    @Value("${nbu.url_param_val.min_date}")
    private String minDateVal;

    private ComboBox       fetchOptCb = new ComboBox("Fetch:");
    private PopupDateField df         = new PopupDateField("Select Date:");
    private Button         fetchBtn   = new Button("Fetch");

    private final Iso4217CcyService               ccyService;
    private final ExchangeRateService             rateService;
    private final ObjectProvider<ExcelExportView> exportViewProvider;

    private Supplier<Collection<? extends IExchangeRate>> ratesSupplier;

    private interface FetchOption {
        String ALL = "All";
        String CUR_DT = "Current";
        String CUST_DT = "By Date";
        String[] VALUES = {
                ALL,
                CUR_DT,
                CUST_DT
        };
    }

    public RatesView(Iso4217CcyService ccyService, ExchangeRateService rateService, ObjectProvider<ExcelExportView> exportViewProvider) {
        super(IExchangeRate.class, P_ID);
        this.ccyService = ccyService;
        this.rateService = rateService;
        this.exportViewProvider = exportViewProvider;
    }

    @Override
    protected final void setup(Grid grid) {
        grid.setCellDescriptionGenerator(cell -> P_GEN_CCY_NAME.equals(cell.getPropertyId()) ? String.valueOf(cell.getValue()) : "");

        grid.getColumn(P_BASE_CCY).setHeaderCaption("Base Currency Code");
        grid.getColumn(P_CCY).setHeaderCaption("Currency Code");
        grid.getColumn(P_GEN_CCY_NAME).setHeaderCaption("Currency Name");
        grid.getColumn(P_RATE).setConverter(new DoubleAmountToStringConverter());
        grid.getColumn(P_EXCHANGE_DATE).setRenderer(FormatUtils.dateRenderer(false), new DateToLocalDateConverter());
        grid.getColumns().stream()
                .filter(c -> !c.getPropertyId().equals(P_EXCHANGE_DATE))
                .forEach(c -> c.setRenderer(new HtmlRenderer()));
        grid.setColumnOrder(P_EXCHANGE_DATE, P_BASE_CCY, P_CCY, P_GEN_CCY_NAME, P_RATE);

        fetchOptCb.setContainerDataSource(new IndexedContainer(Arrays.asList(FetchOption.VALUES)));
        fetchOptCb.setIcon(FontAwesome.DOWNLOAD);
        fetchOptCb.setNullSelectionAllowed(false);
        fetchOptCb.setTextInputAllowed(false);
        fetchOptCb.addValueChangeListener(event -> {
            String option = (String) event.getProperty().getValue();
            switch (option) {
                case FetchOption.ALL:
                    toggleVisible(false, df, fetchBtn);
                    ratesSupplier = rateService::findAll;
                    setSortOrder(P_EXCHANGE_DATE, SortDirection.DESCENDING);
                    refresh();
                    break;
                case FetchOption.CUR_DT:
                    toggleVisible(false, df, fetchBtn);
                    ratesSupplier = rateService::fetchCurrent;
                    setSortOrder(P_CCY, SortDirection.ASCENDING);
                    refresh();
                    break;
                case FetchOption.CUST_DT:
                    ratesSupplier = () -> rateService.fetchByDate(LocalDate.fromDateFields(df.getValue()));
                    setSortOrder(P_CCY, SortDirection.ASCENDING);
                    fetchBtn.addClickListener(clickEvent -> {
                        if (df.getValue() != null) refresh();
                    });
                    toggleVisible(true, df, fetchBtn);
                    break;
            }
        });
        fetchOptCb.setImmediate(true);
        fetchOptCb.select(FetchOption.CUR_DT);

        setHidableColumns(true);
    }

    @Override
    protected Collection<? extends IExchangeRate> getItems() {
        return ratesSupplier.get();
    }

    @Override
    protected void setupGeneratedProperties(GeneratedPropertyContainer container) {
        container.addGeneratedProperty(P_GEN_CCY_NAME,
                new Iso4217PropertyValGen(IsoEntityParam.CCY_NM, IsoEntityParam.CCY, ccyService));
    }

    @Override
    protected void postRefresh(Collection<? extends IExchangeRate> data) {
        if (fetchOptCb.getValue().equals(FetchOption.ALL))
            addRowFilter(new FilterConfig()
                    .setPropId(P_EXCHANGE_DATE)
                    .setKind(FilterKind.DATE)
                    .setExactDateOrDateTime(true));
        addRowFilter(new FilterConfig()
                .setPropId(P_CCY)
                .setKind(FilterKind.SELECT)
                .setSelectOptions(data.stream().map(IExchangeRate::getCcy).collect(Collectors.toList())));
        addRowFilter(new FilterConfig()
                .setPropId(P_GEN_CCY_NAME)
                .setKind(FilterKind.TEXT)
                .setTextAutocomplete(true)
                .setSelectOptions(data.stream()
                        .map(er -> FormatUtils.joinByComma(ccyService.findCcyNamesByCode(er.getCcy())))
                        .collect(Collectors.toList())));
    }

    @Override
    protected void customizeGridBar(HorizontalLayout gridBar) {
        df.setResolution(Resolution.DAY);
        df.setIcon(FontAwesome.CALENDAR);
        df.setTextFieldEnabled(false);
        LocalDate minDate = LocalDate.parse(minDateVal, DateTimeFormat.forPattern("dd.MM.YYYY"));
        Date today = new Date();
        df.setRangeStart(minDate.toDate());
        df.setRangeEnd(today);
        df.addValidator(new DateRangeValidator("Select date in range ["
                + minDate.toString() + "..." + LocalDate.fromDateFields(today) + "]",
                minDate.toDate(), today, Resolution.DAY));
        df.setRequired(true);
        df.setRequiredError("Date must be selected");
        df.addValueChangeListener(event -> {
            if (df.isValid()) {
                fetchBtn.setEnabled(true);
                fetchBtn.focus();
            } else {
                fetchBtn.setEnabled(false);
            }
        });
        df.setImmediate(true);

        fetchBtn.setEnabled(false);
        fetchBtn.setImmediate(true);

        Label space = new Label();

        GridButtonToolbar toolbar = new GridButtonToolbar(getGrid(), true)
                .withExportBtn(exportViewProvider.getObject(this), ValoTheme.BUTTON_FRIENDLY);

        gridBar.addComponents(fetchOptCb, df, fetchBtn, space, toolbar);
        gridBar.setExpandRatio(space, 1.0f);

        gridBar.setComponentAlignment(fetchOptCb, Alignment.MIDDLE_LEFT);

        gridBar.setComponentAlignment(df, Alignment.MIDDLE_LEFT);
        gridBar.setComponentAlignment(fetchBtn, Alignment.BOTTOM_LEFT);
        gridBar.setComponentAlignment(toolbar, Alignment.BOTTOM_RIGHT);
        gridBar.setImmediate(true);
        toggleVisible(false, df, fetchBtn);
    }

    @Override
    public Collection<MenuBar.MenuItem> customizeMenuBar(MenuBar menuBar) {
        return Arrays.asList(
                menuBar.addItem("Exchanges", FontAwesome.EXCHANGE, selectedItem -> Navigator.navigate(ExchangesView.NAME)),
                menuBar.addItem("Scheduler", FontAwesome.GEARS, selectedItem -> Navigator.navigate(SchedulerView.NAME)));
    }

    @Override
    public String getNameCaption() {
        return "Rates";
    }
}
