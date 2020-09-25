package com.leyou.sms.listen;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;


    @Autowired
    private SmsProperties prop;

    private final static Logger LOGGER = LoggerFactory.getLogger(SmsListener.class);


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "leyou.sms.queue", durable = "true"),
            exchange = @Exchange(name = "leyou.sms.exchange",type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}
    ))
    public void sentSms(Map<String, String> msg) {
        if (msg == null || msg.size() <= 0) {
            return;
        }

        String phone = msg.get("phone");
        String code = msg.get("code");
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            return;
        }
        try {
            SendSmsResponse sendSmsResponse = this.smsUtils.sendSms(phone, code, prop.getSignName(), prop.getVerifyCodeTemplate());
        } catch (ClientException e) {
            LOGGER.error("短信发送失败{}" + e, phone);
        }
//        System.out.println("hello");
    }
}
