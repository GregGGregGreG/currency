package org.baddev.currency.scheduler.exchange.service.impl;

import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.ExchangeTaskDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.jooq.schema.tables.records.ExchangeTaskRecord;
import org.baddev.currency.scheduler.exchange.ExchangeTaskScheduler;
import org.baddev.currency.scheduler.exchange.service.ExchangeTaskService;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_TASK;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
@Service
public class ExchangeTaskServiceImpl implements ExchangeTaskService {

    private final ExchangeTaskScheduler scheduler;
    private final ExchangeTaskDao taskDao;

    @Autowired
    public ExchangeTaskServiceImpl(ExchangeTaskDao taskDao, ExchangeTaskScheduler scheduler) {
        this.taskDao = taskDao;
        this.scheduler = scheduler;
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Collection<ExchangeTask> findForUser(Long key) {
        return taskDao.fetchByUserId(key);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN})
    public Collection<ExchangeTask> findAll() {
        return taskDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Collection<ExchangeTask> findById(Long... ids) {
        return taskDao.fetchById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Optional<IExchangeTask> findOneById(Long id) {
        return Optional.ofNullable(taskDao.fetchOneById(id));
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void save(IExchangeTask exchangeTask) {
        taskDao.insert(exchangeTask.into(new ExchangeTask()));
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public IExchangeTask saveReturning(IExchangeTask exchangeTask) {
        return DSL.using(taskDao.configuration())
                .insertInto(EXCHANGE_TASK)
                .set(exchangeTask.into(new ExchangeTaskRecord()))
                .returning()
                .fetchOne()
                .into(ExchangeTask.class);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void update(IExchangeTask exchangeTask) {
        taskDao.update(exchangeTask.into(new ExchangeTask()));
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void delete(Long... ids) {
        taskDao.deleteById(ids);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public Long schedule(IExchangeTask taskData) {
        return scheduler.schedule(taskData);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void execute(IExchangeTask taskData) {
        scheduler.execute(taskData);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void cancel(Long id, boolean remove) {
        scheduler.cancel(id, remove);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN})
    public void cancelAll(boolean remove) {
        scheduler.cancelAll(remove);
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN})
    public int getActiveCount() {
        return scheduler.getActiveCount();
    }

    @Override
    @Transactional(readOnly = true)
    @Secured({RoleEnum.ADMIN, RoleEnum.USER})
    public int getActiveCountByUser(Long key) {
        return taskDao.fetchByUserId(key).stream().filter(ExchangeTask::getActive).collect(Collectors.toList()).size();
    }
}
