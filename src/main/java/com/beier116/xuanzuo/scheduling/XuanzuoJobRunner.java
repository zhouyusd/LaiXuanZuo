package com.beier116.xuanzuo.scheduling;

import com.beier116.xuanzuo.dao.TaskDao;
import com.beier116.xuanzuo.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class XuanzuoJobRunner implements CommandLineRunner {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Override
    public void run(String... args) {
        List<Task> tasks = taskDao.findTasksByStatusEquals(true);
        if (!CollectionUtils.isEmpty(tasks)) {
            for (Task task : tasks) {
                SchedulingRunnable schedulingRunnable =
                        new SchedulingRunnable(
                                task.getBeanName(),
                                task.getMethodName(),
                                task.getWechatSessionID() + "@" + task.getPosition()
                        );
                cronTaskRegistrar.addCronTask(schedulingRunnable, task.getCronExpression());
            }
            log.info("定时任务加载完毕...");
        }
    }
}
