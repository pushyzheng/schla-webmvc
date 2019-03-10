package com.jobless.demo.service.impl;

import com.jobless.demo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Pushy
 * @since 2019/3/7 12:25
 */
@Service
public class UserServiceImpl implements UserService {

    public String getName() {
        return "Pushy";
    }

}
