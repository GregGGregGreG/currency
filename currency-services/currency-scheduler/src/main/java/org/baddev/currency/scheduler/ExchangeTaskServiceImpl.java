package org.baddev.currency.scheduler;

import lombok.RequiredArgsConstructor;
import org.baddev.common.schedulling.ScheduledTaskManager;
import org.baddev.common.schedulling.task.AbstractTask;
import org.baddev.currency.core.api.ExchangeTaskService;
import org.baddev.currency.core.exception.ServiceException;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.ExchangeTaskDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.jooq.schema.tables.records.ExchangeTaskRecord;
import org.jooq.impl.DSL;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_TASK;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
@Service
@RequiredArgsConstructor
public class ExchangeTaskServiceImpl implements ExchangeTaskService {

    private final ExchangeTaskDao      taskDao;
    private final ScheduledTaskManager taskManager;

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
    public void create(IExchangeTask exchangeTask) {
        taskDao.insert(exchangeTask.into(new ExchangeTask()));
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public IExchangeTask createReturning(IExchangeTask exchangeTask) {
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
        Arrays.stream(ids).forEach(taskManager::terminate);
        taskDao.deleteById(ids);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void schedule(AbstractTask task, String cron) {
        ExchangeTask exchangeTask = taskDao.fetchOneById(task.getId());
        exchangeTask.setActive(true);
        taskDao.update(exchangeTask);
        try {
            taskManager.schedule(task, cron);
        } catch (Exception e) {
            throw new ServiceException("Failed to schedule task", e);
        }
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void execute(AbstractTask task) {
        taskManager.execute(task);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void terminate(Long taskId) {
        ExchangeTask task = taskDao.fetchOneById(taskId);
        task.setActive(false);
        taskDao.update(task);
        taskManager.terminate(taskId);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.ADMIN})
    public void terminateAll() {
        Collection<ExchangeTask> tasks = taskDao.findAll();
        tasks.forEach(t -> t.setActive(false));
        taskDao.update(tasks);
        taskManager.terminateAll();
    }

    @Override
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public int getActiveCount() {
        return taskManager.getActiveCount();
    }

    @Override
    public boolean isScheduled(Long id) {
        return taskManager.isScheduled(id);
    }

    @Override
    public Collection<AbstractTask> getScheduledTasks() {
        return taskManager.getScheduledTasks();
    }

    @Override
    public Optional<ScheduledFuture> getFuture(Long taskId) {
        return taskManager.getFuture(taskId);
    }

}
