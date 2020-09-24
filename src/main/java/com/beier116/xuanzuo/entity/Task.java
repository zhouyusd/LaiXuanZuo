package com.beier116.xuanzuo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "lxz_task")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Task implements Serializable {

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
    private String message;
}
