package org.baddev.currency.scheduler;

import org.baddev.currency.core.TaskManager;
import org.baddev.currency.core.api.ExchangeTaskService;
import org.baddev.currency.core.task.AbstractTask;
import org.baddev.currency.core.util.RoleEnum;
import org.baddev.currency.jooq.schema.tables.daos.ExchangeTaskDao;
import org.baddev.currency.jooq.schema.tables.interfaces.IExchangeTask;
import org.baddev.currency.jooq.schema.tables.pojos.ExchangeTask;
import org.baddev.currency.jooq.schema.tables.records.ExchangeTaskRecord;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.baddev.currency.jooq.schema.Tables.EXCHANGE_TASK;

/**
 * Created by IPotapchuk on 9/12/2016.
 */
@Service
public class ExchangeTaskServiceImpl implements ExchangeTaskService {

    @Autowired private ExchangeTaskDao    taskDao;
    @Autowired private TaskManager        taskManager;
//    @Autowired private UserDetailsDao     userDetailsDao;
//    @Autowired private UserPreferencesDao userPreferencesDao;
//    @Autowired private ApplicationContext ctx;

//    @PostConstruct
//    private void scheduleOnStart() {
//        Map<Long, UserPreferences> userPrefsCache = new HashMap<>();
//        Set<UserDetails> userDetailsCache = new HashSet<>();
//        taskDao.findAll().stream().filter(ExchangeTask::getActive).forEach(t -> {
//            NotifiableExchangeTask task = ctx.getBean(NotifiableExchangeTask.class);
//            task.setTaskData(t);
//
//            UserPreferences prefs;
//            if (userPrefsCache.containsKey(t.getUserId())) {
//                prefs = userPrefsCache.get(t.getUserId());
//            } else {
//                prefs = userPreferencesDao.fetchOneByUserId(t.getUserId());
//                userPrefsCache.put(t.getUserId(), prefs);
//            }
//
//            UserDetails details = userDetailsCache.stream()
//                    .filter(ud -> ud.getUserId().equals(t.getUserId()))
//                    .findFirst()
//                    .orElseGet(() -> userDetailsDao.fetchOneByUserId(t.getUserId()));
//            userDetailsCache.add(details);
//
//            if (prefs.getMailNotifications()) {
//                MailExchangeCompletionListener mailer = ctx.getBean(MailExchangeCompletionListener.class);
//                mailer.setEmail(details.getEmail());
//                task.getNotifier().subscribe(mailer);
//            }
//
//            taskManager.schedule(task, new CronTrigger(t.getCron()));
//        });
//    }

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
        Arrays.stream(ids).forEach(taskManager::terminate);
        taskDao.deleteById(ids);
    }

    @Override
    @Transactional
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public void schedule(AbstractTask task, CronTrigger trigger) {
        ExchangeTask exchangeTask = taskDao.fetchOneById(task.getId());
        exchangeTask.setActive(true);
        taskDao.update(exchangeTask);
        taskManager.schedule(task, trigger);
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
    @Transactional(readOnly = true)
    @Secured({RoleEnum.USER, RoleEnum.ADMIN})
    public int getActiveCount() {
        return taskManager.getActiveCount();
    }

}
