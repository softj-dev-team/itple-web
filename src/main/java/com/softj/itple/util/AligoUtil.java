package com.softj.itple.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AligoUtil {
    private static final String BASE_URL = "https://kakaoapi.aligo.in";
    private static final String SMS_BASE_URL = "https://apis.aligo.in";

    @Value("${aligo.api-key}")
    private String apiKey;
    @Value("${aligo.sender-key}")
    private String senderKey;
    @Value("${aligo.sender}")
    private String sender;
    @Value("${aligo.user-id}")
    private String userId;

    public String getToken() {
        String token = "";
        Map<String,Object> formData = new HashMap<>();
        formData.put("apikey", apiKey);
        formData.put("userid", userId);
        try {
            Map<String, Object> response = RestUtil.restCall(BASE_URL + "/akv10/token/create/30/s", RequestMethod.POST, null, formData, Map.class);
            token = String.valueOf(((Map)response.get("result")).get("token"));
        }catch (IOException e){
            e.printStackTrace();
        }
        log.debug("token {}", token);
        return token;
    }

    public void send(String receiver,String message, String templateCode,String subject,String button) {
        Map<String,Object> formData = new HashMap<>();
        formData.put("apikey", apiKey);
        formData.put("userid", userId);
        formData.put("token", this.getToken());
        formData.put("senderkey", senderKey);
        formData.put("tpl_code", templateCode);
        formData.put("sender", sender);
        formData.put("receiver_1", receiver);
        formData.put("subject_1", subject);
        formData.put("message_1", message);
        formData.put("failover", "Y");
        formData.put("fsubject_1", subject);
        formData.put("fmessage_1", message);
        if(!StringUtils.isEmpty(button)){
            formData.put("button_1", button);
        }
        try {
            Map<String,Object> response = RestUtil.restCall(BASE_URL+"/akv10/alimtalk/send", RequestMethod.POST, null, formData, Map.class);
            log.debug("Aligo Res: {}", response);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void smsSsend(String receiver,String message) {
        Map<String,Object> formData = new HashMap<>();
        formData.put("key", apiKey);
        formData.put("user_id", userId);
        formData.put("sender", sender);
        formData.put("receiver", receiver);
        formData.put("msg", message);
        try {
            Map<String,Object> response = RestUtil.restCall(SMS_BASE_URL+"/send/", RequestMethod.POST, null, formData, Map.class);
            log.debug("Aligo Res: {}", response);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Map remain() {
        Map<String,Object> response= null;
        Map<String,Object> formData = new HashMap<>();
        formData.put("apikey", apiKey);
        formData.put("userid", userId);
        formData.put("token", this.getToken());
        try {
            response = RestUtil.restCall(BASE_URL+"/akv10/heartinfo/", RequestMethod.POST, null, formData, Map.class);
            log.debug(" {}", response);
        }catch (IOException e){
            e.printStackTrace();
        }
        return response;
    }
}
