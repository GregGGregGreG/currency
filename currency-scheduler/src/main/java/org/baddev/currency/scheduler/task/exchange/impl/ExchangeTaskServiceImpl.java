package org.baddev.currency.scheduler.task.exchange.impl;

import org.baddev.currency.core.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.ExchangeTaskDao;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.jooq.schema.tables.records.ExchangeTaskRecord;
import org.baddev.currency.scheduler.task.exchange.ExchangeTaskService;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_TASK;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
@Service
public class ExchangeTaskServiceImpl implements ExchangeTaskService<ExchangeTask> {

    private ExchangeTaskDao taskDao;

    @Autowired
    public ExchangeTaskServiceImpl(ExchangeTaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void save(ExchangeTask exchangeTask) {
        taskDao.insert(exchangeTask);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public ExchangeTask saveReturning(ExchangeTask exchangeTask) {
        return DSL.using(taskDao.configuration())
                .insertInto(EXCHANGE_TASK)
                .set(exchangeTask.into(new ExchangeTaskRecord()))
                .returning()
                .fetchOne()
                .into(ExchangeTask.class);
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
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void update(ExchangeTask exchangeTask) {
        taskDao.update(exchangeTask);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void deleteById(Long... ids) {
        taskDao.deleteById(ids);
    }
}
