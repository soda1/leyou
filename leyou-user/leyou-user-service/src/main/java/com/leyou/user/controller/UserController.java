package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 校验用户注册数据
     * @param param
     * @param type 1:用户名 2：手机号
     * @return
     */
    @GetMapping("/check/{param}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("param") String param, @PathVariable("type") Integer type) {
        Boolean bool = userService.checkUserData(param, type);

        if (bool == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(bool);
    }


    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Boolean> sendVerifyCode( String phone) {
        Boolean bool = this.userService.sendVerifyCode(phone);
        if (bool == null || !bool) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Boolean> userRegister(@Valid User user, @RequestParam("code") String code) {

        Boolean bool = this.userService.userRegister(user, code);

        if (bool == null || !bool) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * 查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username")String username,
            @RequestParam("password") String password
    ) {
        User user = this.userService.queryuser(username, password);

        if (user == null) {
            return  ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(user);
    }
}
