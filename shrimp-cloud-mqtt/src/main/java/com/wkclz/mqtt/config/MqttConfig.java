package com.wkclz.mqtt.config;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "shrimp.cloud.mqtt")
// @ConditionalOnProperty(prefix = "shrimp.cloud.mqtt", value = {"client-id-prefix"})
public class MqttConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    // 自定义
    private String username;
    private String password;

    // 公共
    private String endPoint;
    private String clientIdPrefix;

    // 阿里云
    private String instanceId;
    private String accessKey;
    private String secretKey;

    @Bean
    public MqttClient mqttClient() {
        if (StringUtils.isBlank(endPoint)) {
            logger.warn("mqtt: endpoint is empty!");
            return null;
        }
        MqttClient mqttClient = null;

        String clientIdPrefix = getClientIdPrefix();
        if (StringUtils.isBlank(clientIdPrefix)) {
            clientIdPrefix = "server";
        }
        String clientId = clientIdPrefix + "@" + getServerIp();
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(getEndPoint(), clientId, persistence);
            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName(getUsername());
            connOpts.setPassword(getPassword().toCharArray());
            // 保留会话
            connOpts.setCleanSession(true);
            // 建立连接
            logger.info("Connecting to broker: " + getEndPoint());
            mqttClient.connect(connOpts);
            logger.info("Connected");
        } catch (MqttException me) {
            logger.error("reason " + me.getReasonCode());
            logger.error("msg " + me.getMessage());
            logger.error("loc " + me.getLocalizedMessage());
            logger.error("cause " + me.getCause());
            logger.error("excep " + me);
            throw new RuntimeException(me.getMessage());
        }
        return mqttClient;
    }




    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getClientIdPrefix() {
        return clientIdPrefix;
    }

    public void setClientIdPrefix(String clientIdPrefix) {
        this.clientIdPrefix = clientIdPrefix;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }


    private static String getServerIp() {
        Set<String> ipList = new HashSet<>();
        //得到所有接口
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
        }
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            //得到单个接口
            NetworkInterface nextInterface = interfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = nextInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                //得到单个IP
                InetAddress inetAddress = inetAddresses.nextElement();
                //确定要是 ipv4的地址
                if (inetAddress instanceof Inet4Address) {
                    String ip = inetAddress.getHostAddress();
                    if (!"127.0.0.1".equals(ip)){
                        ipList.add(ip);
                    }
                }
            }
        }
        return ipList.toArray()[0].toString();
    }


}