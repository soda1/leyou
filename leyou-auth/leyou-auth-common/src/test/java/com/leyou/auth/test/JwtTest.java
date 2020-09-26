package com.leyou.auth.test;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;



public class JwtTest {


    private static final String pubKeyPath = "F:\\Java\\Code\\SpringBoot\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "F:\\Java\\Code\\SpringBoot\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTYwMTAzMDc4N30.Df0eZ3kBjOacBH8XxNbgIdkCO4MkOdBWTeT4puc7uDxhgT4kIViOeHht38yeRU_AbxGciVcllkCnA8QVx9v0ZfXkKrZOFMU3iSkOzww-nlITr6-toTCLPDImSiPGpW85n6-RPfEL8ag7i3O0IYmUzjioDSmMLmD7bO11pFpjbX4";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }

}
