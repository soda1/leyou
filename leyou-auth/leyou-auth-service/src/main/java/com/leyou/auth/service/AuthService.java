package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {


    @Autowired
    private UserClient userClient;


    @Autowired
    private JwtProperties jwtProperties;

    public String authuser(String username, String password) {
        try {
            //查询用户
            User user = this.userClient.queryUser(username, password);

            if (user == null) {
                return null;
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());

            //生成token
            String token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            if (token == null) {
                return null;
            }

            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


}
