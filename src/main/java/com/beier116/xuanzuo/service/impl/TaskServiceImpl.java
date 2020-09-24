package com.beier116.xuanzuo.service.impl;

import com.beier116.xuanzuo.dao.TaskDao;
import com.beier116.xuanzuo.entity.Task;
import com.beier116.xuanzuo.service.ITaskService;
import com.beier116.xuanzuo.util.MyBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private TaskDao taskDao;

    @Override
    public List<Task> findTasksByPage(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskDao.findAll(pageable).getContent();
    }

    @Override
    @Transactional
    public Boolean insertTask(Task task) {
        if (task.getId() == null) {
            Task t =
                    taskDao.findTaskByWechatSessionIDEqualsAndMethodNameEquals(
                            task.getWechatSessionID(), task.getMethodName());
            if (t != null) {
                log.info("任务已存在，不要重复添加");
                return false;
            }
            task.setCreateTime(new Date());
            task.setUpdateTime(new Date());
            task.setIsDeleted(false);
        }
        try {
            if (task.getId() != null) {
                if (taskDao.getOne(task.getId()) == null) {
                    return false;
                }
            }
            taskDao.saveAndFlush(task);
            log.info("添加任务成功");
            return true;
        } catch (Exception e) {
            log.error("添加任务失败，原因：{}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean editTask(Task task) {
        try {
            Task t = taskDao.getOne(task.getId());
            if (t != null) {
                MyBeanUtils.copyPropertiesIgnoreNull(task, t);
                t.setUpdateTime(new Date());
                System.out.println(t);
                taskDao.saveAndFlush(t);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteTaskById(Long id) {
        try {
            taskDao.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Task findTaskById(Long id) {
        return taskDao.getOne(id);
    }
}
