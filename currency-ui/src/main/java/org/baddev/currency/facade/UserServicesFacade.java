package org.baddev.currency.facade;

import com.google.common.eventbus.EventBus;
import org.baddev.currency.core.event.NotificationEvent;
import org.baddev.currency.core.exception.NoRatesFoundException;
import org.baddev.currency.core.listener.NotificationListener;
import org.baddev.currency.core.notifier.Notifier;
import org.baddev.currency.exchanger.ExchangerService;
import org.baddev.currency.fetcher.ExchangeRateFetchingService;
import org.baddev.currency.fetcher.ExtendedExchangeRateDao;
import org.baddev.currency.fetcher.impl.nbu.NBU;
import org.baddev.currency.fetcher.other.Iso4217CcyService;
import org.baddev.currency.jooq.schema.tables.daos.UserDao;
import org.baddev.currency.jooq.schema.tables.daos.UserDetailsDao;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeOperation;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeRate;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.UserDetails;
import org.baddev.currency.scheduler.ScheduledExchangeManager;
import org.baddev.currency.security.SecurityUtils;
import org.baddev.currency.security.service.SecurityService;
import org.joda.time.LocalDate;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static org.baddev.currency.jooq.schema.Tables.*;

/**
 * Created by IPotapchuk on 7/7/2016.
 */
@Service
public class UserServicesFacade {

    @Autowired
    private DSLContext context;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserDetailsDao userDetailsDao;
    @NBU
    private ExchangeRateFetchingService<ExchangeRate> fetchingService;
    @Autowired
    private ExchangerService<ExchangeOperation, ExchangeRate> exchangerService;
    @Autowired
    private Iso4217CcyService iso4217CcyService;
    @Autowired
    private ExtendedExchangeRateDao rateDao;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private EventBus bus;
    @Autowired
    private Notifier notifier;
    @Autowired
    private ScheduledExchangeManager scheduler;

    @Transactional(readOnly = true)
    public List<ExchangeTask> findExchangeTasks() {
        return context.select(EXCHANGE_TASK.fields())
                .from(EXCHANGE_TASK)
                .leftOuterJoin(USER_EXCHANGE_TASK).on(EXCHANGE_TASK.ID.eq(USER_EXCHANGE_TASK.EXCHANGE_TASK_ID))
                .where(USER_EXCHANGE_TASK.USER_ID.eq(currentUserId()))
                .fetchInto(ExchangeTask.class);
    }

    @Transactional(readOnly = true)
    public List<ExchangeOperation> findExchangeOperations() {
        return context.select(EXCHANGE_OPERATION.fields())
                .from(EXCHANGE_OPERATION)
                .leftOuterJoin(USER_EXCHANGE_OPERATION).on(EXCHANGE_OPERATION.ID.eq(USER_EXCHANGE_OPERATION.EXCHANGE_OPERATION_ID))
                .where(USER_EXCHANGE_OPERATION.USER_ID.eq(currentUserId()))
                .fetchInto(ExchangeOperation.class);
    }

    public UserDetails findUserDetails() {
        return userDetailsDao.findById(currentUserId());
    }

    public Collection<ExchangeRate> fetchCurrentRates() throws NoRatesFoundException {
        return fetchingService.fetchCurrent();
    }

    public Collection<ExchangeRate> fetchRatesByDate(LocalDate date) throws NoRatesFoundException {
        return fetchingService.fetchByDate(date);
    }

    public ExchangeRate fetchRatesByCurrencyAndDate(Currency currency, LocalDate date) throws NoRatesFoundException {
        return fetchingService.fetchByCurrencyAndDate(currency, date);
    }

    public ExchangeOperation performExchange(ExchangeOperation operation, Collection<ExchangeRate> rates) {
        return exchangerService.exchange(operation, rates);
    }

    public List<String> findCcyParamValues(Iso4217CcyService.Parameter target, Iso4217CcyService.Parameter keyParam, String keyParamVal) {
        return iso4217CcyService.findCcyParamValues(target, keyParam, keyParamVal);
    }

    public List<String> findCcyNamesByCode(String ccyCode) {
        return iso4217CcyService.findCcyNamesByCode(ccyCode);
    }

    public List<String> findCcyCountriesByCode(String ccyCode) {
        return iso4217CcyService.findCcyCountriesByCode(ccyCode);
    }

    public Collection<ExchangeRate> findLastRates() {
        return rateDao.findLastRates();
    }

    public List<ExchangeRate> findAllRates() {
        return rateDao.findAll();
    }

    public void authenticate(String principal, String credentials) {
        securityService.authenticate(principal, credentials);
    }

    public void signUp(String username, String password, UserDetails details, String... roleNames) {
        securityService.signUp(username, password, details, roleNames);
    }

    public void busRegister(Object object) {
        bus.register(object);
    }

    public void busUnregister(Object object) {
        bus.unregister(object);
    }

    public void postEvent(Object event) {
        bus.post(event);
    }

    public <T extends NotificationEvent> void doNotify(T event) {
        notifier.doNotify(event);
    }

    public boolean notifierSubscribe(NotificationListener listener) {
        return notifier.subscribe(listener);
    }

    public boolean notifierUnsubscribe(NotificationListener listener) {
        return notifier.unsubscribe(listener);
    }

    public Collection<NotificationListener> getNotifierSubscribers() {
        return notifier.getSubscribers();
    }

    public Long scheduleTask(ExchangeTask taskData) {
        return scheduler.schedule(taskData);
    }

    public void executeTask(ExchangeTask taskData) {
        scheduler.execute(taskData);
    }

    public boolean cancelTask(Long id, boolean remove) {
        return scheduler.cancel(id, remove);
    }

    public void cancelAllTasks(boolean remove) {
        scheduler.cancelAll(remove);
    }

    public int getActiveTasksCount() {
        return scheduler.getActiveCount();
    }

    public Collection<ExchangeTask> getExchangeTasks() {
        return scheduler.getExchangeTasks();
    }

    public Map<Long, ScheduledFuture> getJobsMap() {
        return scheduler.getJobsMap();
    }

    public void rescheduleTask(ExchangeTask reschedulingData) {
        scheduler.reschedule(reschedulingData);
    }

    private Long currentUserId() {
        return userDao.fetchOneByUsername(SecurityUtils.loggedInUserName()).getId();
    }

}
