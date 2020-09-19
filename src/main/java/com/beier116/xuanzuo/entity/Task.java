package com.beier116.xuanzuo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "lxz_task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wechatSessionID;
    private String beanName;
    private String methodName;
    private String position;
    private String cronExpression;
    private Boolean status;
    private String remarks;
    private Boolean isDeleted;
    private Date createTime;
    private Date updateTime;
}
