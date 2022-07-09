package com.wkclz.spring.helper;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * rest 请求模板
 */
public class RestTemplateHelper {

    private static SimpleClientHttpRequestFactory REQUEST_FACTORY = null;

    public static RestTemplate getRestTemplate(){
        SimpleClientHttpRequestFactory requestFactory = gettHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }


    private static SimpleClientHttpRequestFactory gettHttpRequestFactory(){
        if (REQUEST_FACTORY != null){
            return REQUEST_FACTORY;
        }
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000);
        requestFactory.setReadTimeout(1000);
        REQUEST_FACTORY = requestFactory;
        return requestFactory;
    }

}
