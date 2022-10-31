package com.softj.itple.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.*;

@Slf4j
public class RestUtil {
    //Http Get요청
    @SuppressWarnings("unchecked")
    public static <T> Map<String,Object> getCall(String url, Map<String,Object> headers, Class<T> resType) throws IOException {
        HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
        Map<String, Object> responseBody = new HashMap<>();

        try{
            HttpGet request = new HttpGet(url);
            if(headers != null && !headers.isEmpty()) {
                Set<String> keys = headers.keySet();
                for(String key:keys) {
                    if(!headers.get(key).equals(""))
                        request.setHeader(key, (String)headers.get(key));
                }
            }

            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                try {
                    responseBody.put("result",new ObjectMapper().readValue(body, resType));
                }catch (Exception e) {
                    if(resType.equals(Map.class)) {
                        responseBody.put("result",new ObjectMapper().readValue(body, List.class));
                    }else {
                        responseBody.put("result",new ObjectMapper().readValue(body, Map.class));
                    }
                }
            } else {
                responseBody.put("status","fail");
            }
        }catch (Exception e){
            responseBody.put("status","fail");
            e.printStackTrace();
        }
        return responseBody;
    }

    //Http Delete요청
    @SuppressWarnings("unchecked")
    public static <T> Map<String,Object> deleteCall(String url, Map<String,Object> headers, Class<T> resType) throws IOException {
        HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
        Map<String, Object> responseBody = new HashMap<>();

        try{
            HttpDelete request = new HttpDelete(url);
            if(headers != null && !headers.isEmpty()) {
                Set<String> keys = headers.keySet();
                for(String key:keys) {
                    if(!headers.get(key).equals(""))
                        request.setHeader(key, (String)headers.get(key));
                }
            }

            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                try {
                    responseBody.put("result",new ObjectMapper().readValue(body, resType));
                }catch (Exception e) {
                    if(resType.equals(Map.class)) {
                        responseBody.put("result",new ObjectMapper().readValue(body, List.class));
                    }else {
                        responseBody.put("result",new ObjectMapper().readValue(body, Map.class));
                    }
                }
            } else if(response.getStatusLine().getStatusCode() == 204){
                responseBody.put("status","success");
            } else {
                responseBody.put("status","fail");
            }
        }catch (Exception e){
            responseBody.put("status","fail");
            e.printStackTrace();
        }
        return responseBody;
    }

    //Http Get 이외 요청 폼데이터
    @SuppressWarnings("unchecked")
    public static <T> Map<String,Object> restCall(String url, RequestMethod type, Map<String,Object> headers, Map<String,Object> bodys, Class<T> resType) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10*1000)
                .setConnectTimeout(10*1000)
                .setConnectionRequestTimeout(10*1000)
                .build();

        HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
        Map<String, Object> responBody = new HashMap<>();

        try{
            HttpEntityEnclosingRequestBase request = null;
            switch(type) {
                case POST:
                    request = new HttpPost(url);
                    break;
                case PUT:
                    request = new HttpPut(url);
                    break;
            }
            request.setConfig(requestConfig);

            if(headers != null && !headers.isEmpty()) {
                Set<String> keys = headers.keySet();
                for(String key:keys) {
                    if(!headers.get(key).equals(""))
                        request.addHeader(key,String.valueOf(headers.get(key)));
                }
            }

            ArrayList<NameValuePair> entity = new ArrayList<>();
            if (bodys != null && !bodys.isEmpty()) {
                Set<String> keys = bodys.keySet();
                for (String key:keys) {
                    if(!bodys.get(key).equals(""))
                        entity.add(new BasicNameValuePair(key, String.valueOf(bodys.get(key))));
                }
            }

            request.setEntity(new UrlEncodedFormEntity(entity, "UTF-8"));

            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                log.debug("RestUtil restCall response body: {}",body);
                responBody.put("result",new ObjectMapper().readValue(body, resType));
            } else {
                responBody.put("status","fail");
            }
        }catch (Exception e){
        	responBody.put("status","fail");
            e.printStackTrace();
        }
        return responBody;
    }

    //Http Get 이외 요청 Json
    @SuppressWarnings("unchecked")
    public static <T> Map<String,Object> restCallJson(String url, RequestMethod type, Map<String,Object> headers, Map<String,Object> bodys, Class<T> resType) throws IOException {
        HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
        Map<String, Object> responBody = new HashMap<>();

        try{
            HttpEntityEnclosingRequestBase request = null;
            switch(type) {
                case POST:
                    request = new HttpPost(url);
                    break;
                case PUT:
                    request = new HttpPut(url);
                    break;
            }

            request.addHeader("Content-Type","application/json");
            if(!MapUtils.isEmpty(headers)) {
                Set<String> keys = headers.keySet();
                for(String key:keys) {
                    if(!headers.get(key).equals(""))
                        request.addHeader(key,(String)headers.get(key));
                }
            }

            if(!MapUtils.isEmpty(bodys)) {
                ObjectMapper objectMapper = new ObjectMapper();
                StringEntity entity = new StringEntity(objectMapper.writeValueAsString(bodys), "UTF-8");
                request.setEntity(entity);
            }

            HttpResponse response = client.execute(request);
            log.debug("RestUtil response: {}", response);
            if (response.getStatusLine().getStatusCode() == 200) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String body = handler.handleResponse(response);
                responBody.put("result",new ObjectMapper().readValue(body, resType));
            } else {
                responBody.put("status","fail");
            }
        }catch (Exception e){
            e.printStackTrace();
            responBody.put("status","fail");
        }
        return responBody;
    }

    public static String mapToQueryStr(Map<String,String> map){
        String str = "?";
        for(String k:map.keySet()){
            str += k+"="+map.get(k)+"&";
        }
        return str;
    }
}