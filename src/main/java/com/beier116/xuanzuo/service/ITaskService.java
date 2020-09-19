package com.beier116.xuanzuo.service;

import com.beier116.xuanzuo.entity.Task;

import java.util.List;

public interface ITaskService {

    List<Task> findTasksByPage(Integer page, Integer size);

    Boolean insertTask(Task task);

    Boolean editTask(Task task);

    Boolean deleteTaskById(Long id);

    Task findTaskById(Long id);
}
