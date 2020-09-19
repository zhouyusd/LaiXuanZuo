package com.beier116.xuanzuo.service.impl;

import com.beier116.xuanzuo.dao.UserDao;
import com.beier116.xuanzuo.entity.User;
import com.beier116.xuanzuo.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User findUserById(Long id) {
        return userDao.getOne(id);
    }

    @Override
    public Boolean insertUser(User user) {
        try {
            userDao.save(user);
            log.info("添加用户成功");
            return true;
        } catch (Exception e) {
            log.error("添加用户失败，用户名：{}，原因：{}", user.getUsername(), e.getMessage());
            return false;
        }
    }
}
