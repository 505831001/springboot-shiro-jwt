package com.liuweiwei.shiro.jwt.controller;

import com.liuweiwei.shiro.jwt.mapper.UserMapper;
import com.liuweiwei.shiro.jwt.model.ResultMap;
import com.liuweiwei.shiro.jwt.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA
 *
 * @author liuweiwei 505831001@qq.com
 * @since 2018-04-06
 */
@RestController
public class LoginController {
    /**
     * slf4j 骚人日志记录必备
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final UserMapper userMapper;
    private final ResultMap resultMap;

    @Autowired
    public LoginController(UserMapper userMapper, ResultMap resultMap) {
        this.userMapper = userMapper;
        this.resultMap = resultMap;
    }

    @PostMapping("/login")
    public ResultMap login(@RequestParam("username") String username,
                           @RequestParam("password") String password) {
        LOGGER.info("The user login parameters is -> username: {}, password: " + username, password);
        String realPassword = userMapper.getPassword(username);
        if (realPassword == null) {
            return resultMap.fail().code(401).message("User name error");
        } else if (!realPassword.equals(password)) {
            return resultMap.fail().code(401).message("Password error");
        } else {
            LOGGER.info("User token is -> " + JWTUtil.createToken(username));
            return resultMap.success().code(200).message(JWTUtil.createToken(username));
        }
    }

    @RequestMapping(path = "/unauthorized/{message}")
    public ResultMap unauthorized(@PathVariable String message) throws UnsupportedEncodingException {
        return resultMap.success().code(401).message(message);
    }
}
