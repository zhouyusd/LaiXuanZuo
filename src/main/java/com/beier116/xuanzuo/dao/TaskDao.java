package com.beier116.xuanzuo.dao;

import com.beier116.xuanzuo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDao extends JpaRepository<Task, Long> {

    @Query("from Task t where t.status=:status")
    List<Task> findTasksByStatusEquals(Boolean status);

    @Query("from Task t where t.wechatSessionID=:sessionId and t.methodName=:methodName")
    Task findTaskByWechatSessionIDEqualsAndMethodNameEquals(String sessionId, String methodName);
}
