package com.beier116.xuanzuo.service;

import com.beier116.xuanzuo.entity.User;

public interface IUserService {

    User findUserById(Long id);

    Boolean insertUser(User user);
}
