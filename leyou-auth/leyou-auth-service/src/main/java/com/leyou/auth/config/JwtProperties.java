package com.leyou.auth.config;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {

    private String secret;

    private String pubKeyPath;

    private String priKeyPath;

    private Integer expire;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    private String cookieName;

    private Integer cookieMaxAge;

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public Integer getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtProperties.class);


    /**
     * 在构造方法之后执行
     */
    @PostConstruct
    public void init() {
        try {

            File pubkey = new File(this.pubKeyPath);
            File prikey = new File(this.priKeyPath);
            if (pubkey == null || prikey == null) {

                RsaUtils.generateKey(this.pubKeyPath, this.priKeyPath, this.secret);
            }
            this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);

        } catch (Exception e) {
            LOGGER.error("获取钥匙失败{}", e);
            throw new RuntimeException();
        }

    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public String getPriKeyPath() {
        return priKeyPath;
    }

    public void setPriKeyPath(String priKeyPath) {
        this.priKeyPath = priKeyPath;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }
}
