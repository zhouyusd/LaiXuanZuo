package com.beier116.xuanzuo.controller;


import com.beier116.xuanzuo.common.RestResponse;
import com.beier116.xuanzuo.entity.Task;
import com.beier116.xuanzuo.scheduling.CronTaskRegistrar;
import com.beier116.xuanzuo.scheduling.SchedulingRunnable;
import com.beier116.xuanzuo.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskService taskService;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @ResponseBody
    @GetMapping("{id}")
    public RestResponse<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.findTaskById(id);
        System.out.println(task);
        return RestResponse.ok("/tasks/" + id, task);
    }

    @GetMapping
    public String taskList(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            Model model) {
        List<Task> taskList = taskService.findTasksByPage(page - 1, size);
//        log.info(taskList.toString());
//        taskList.get(1).
        model.addAttribute("tasks", taskList);
        return "tasks";
    }

    @ResponseBody
    @PostMapping
    public RestResponse<Task> insertTask(@RequestBody Task task) {
        boolean success = taskService.insertTask(task);
        if (!success)
            return RestResponse.failed("添加任务失败", null, "/tasks");
        if (task.getStatus()) {
            SchedulingRunnable schedulingRunnable =
                    createSchedulingRunnable(task);
            cronTaskRegistrar.addCronTask(schedulingRunnable, task.getCronExpression());
            log.info("task size：{}", cronTaskRegistrar.getScheduledTasks().size());
        }
        return RestResponse.ok("/tasks", null);
    }

    @ResponseBody
    @PutMapping
    public RestResponse<Task> editTask(@RequestBody Task task) {
        Task oldTask = taskService.findTaskById(task.getId());
        boolean oldStatus = oldTask.getStatus();
        log.info("task == oldTask：{}", task.equals(oldTask));
        SchedulingRunnable oldSchedulingRunnable = createSchedulingRunnable(oldTask);
        boolean success = taskService.editTask(task);
        log.info("task == oldTask：{}", task.equals(oldTask));
        if (!success)
            return RestResponse.failed("修改任务失败", null, "/tasks");
        if (oldStatus) {
            cronTaskRegistrar.removeCronTask(oldSchedulingRunnable);
            log.info("task size：{}", cronTaskRegistrar.getScheduledTasks().size());
        }
        task.setBeanName(oldTask.getBeanName());
        task.setMethodName(oldTask.getMethodName());
        task.setCronExpression(oldTask.getCronExpression());
        if (task.getStatus()) {
            SchedulingRunnable schedulingRunnable = createSchedulingRunnable(task);
            cronTaskRegistrar.addCronTask(schedulingRunnable, task.getCronExpression());
            log.info("task size：{}", cronTaskRegistrar.getScheduledTasks().size());
        }
        return RestResponse.ok("/tasks", null);
    }

    @ResponseBody
    @DeleteMapping
    public RestResponse<Task> deleteTask(@RequestParam Long id) {
        Task oldTask = taskService.findTaskById(id);
        boolean oldStatus = oldTask.getStatus();
        SchedulingRunnable oldSchedulingRunnable = createSchedulingRunnable(oldTask);
        boolean success = taskService.deleteTaskById(id);
        if (!success)
            return RestResponse.failed("删除任务失败", null, "/tasks");
        if (oldStatus) {
            cronTaskRegistrar.removeCronTask(oldSchedulingRunnable);
            log.info("task size：{}", cronTaskRegistrar.getScheduledTasks().size());
        }
        return RestResponse.ok("/tasks", null);
    }

    @ResponseBody
    @PutMapping("/toggle")
    @Transactional
    public RestResponse<Task> toggleTask(@RequestParam Long id) {
        Task oldTask = taskService.findTaskById(id);
        if (oldTask.getStatus()) {
            oldTask.setStatus(false);
            boolean success = taskService.editTask(oldTask);
            if (!success)
                return RestResponse.failed("关闭任务失败", null, "/tasks/toggle");
            SchedulingRunnable schedulingRunnable = createSchedulingRunnable(oldTask);
            cronTaskRegistrar.removeCronTask(schedulingRunnable);
            log.info("task size：{}", cronTaskRegistrar.getScheduledTasks().size());
            return RestResponse.ok("/tasks/toggle", null);
        } else {
            oldTask.setStatus(true);
            boolean success = taskService.editTask(oldTask);
            if (!success)
                return RestResponse.failed("开启任务失败", null, "/tasks/toggle");
            SchedulingRunnable schedulingRunnable = createSchedulingRunnable(oldTask);
            cronTaskRegistrar.addCronTask(schedulingRunnable, oldTask.getCronExpression());
            log.info("task size：{}", cronTaskRegistrar.getScheduledTasks().size());
            return RestResponse.ok("/tasks/toggle", null);
        }
    }

    private static SchedulingRunnable createSchedulingRunnable(Task task) {
        return new SchedulingRunnable(
                task.getBeanName(),
                task.getMethodName(),
                task.getWechatSessionID() + "@" + task.getPosition());
    }
}
