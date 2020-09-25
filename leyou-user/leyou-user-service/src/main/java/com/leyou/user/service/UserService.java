package com.leyou.user.service;


import com.leyou.common.utils.CodecUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:code:phone:";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * 校验用户数据
     * @param param
     * @param type 1：用户 2：手机号
     * @return
     */
    public Boolean checkUserData(String param, Integer type) {

        User checked = new User();
        if (type == 1) {
            checked.setUsername(param);
        } else if (type == 2) {
            checked.setPhone(param);
        } else {
            return null;
        }
        return this.userMapper.selectCount(checked) == 0;
    }


    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    public Boolean sendVerifyCode(String phone) {

        String code = NumberUtils.generateCode(6);
        try {
            Map<String, String> msg = new HashMap<>();

            msg.put("code", code);
            msg.put("phone", phone);
            //发送验证码到消息中间件
            this.amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code", msg);
            //存入redis
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            LOGGER.error("短信发送失败. phone:{}, code:{}", phone, code);
            return false;
        }

    }

    /**
     *
     * @param user
     * @return
     */
    public Boolean userRegister(User user, String code) {




        /*
        1）校验短信验证码
        2）生成盐
        3）对密码加密
        4）写入数据库
        5）删除Redis中的验证码
        */
        String trueCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!code.equals(trueCode)) {
            return false;
        }

        String salt = CodecUtils.generateSalt();

        user.setSalt(salt);

        //强制设置指定的参数不能为null
        user.setId(null);

        user.setCreated(new Date());

        //对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        Boolean bool = this.userMapper.insertSelective(user) == 1;

        if (bool) {
            this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
        }

        return bool;




    }

    public User queryuser(String username, String password) {

        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);

        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
            return null;
        }
        return user;

    }
}
