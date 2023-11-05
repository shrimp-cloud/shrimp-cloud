package com.wkclz.spring.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-21 上午12:41
 */
public class IpHelper {
    private static final Logger logger = LoggerFactory.getLogger(IpHelper.class);

    public static String getUpstreamIp(HttpServletRequest req){
        String remoteAddr = req.getRemoteAddr();
        return remoteAddr;
    }

    public static String getOriginIp(HttpServletRequest req) {
        String ipAddress = req.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage(), e);
                }
                ipAddress = inet == null ? null : inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) {
            // "***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    public static String getServerIp() {
        List<NetworkInfterfaceParam> ipList = new ArrayList<>();
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
                    NetworkInfterfaceParam param = new NetworkInfterfaceParam();
                    param.setName(nextInterface.getName());
                    param.setHostAddress(inetAddress.getHostAddress());
                    ipList.add(param);
                }
            }
        }
        List<NetworkInfterfaceParam> usefullInterface = ipList.stream().filter(ip -> {
            String name = ip.getName();
            if ("lo".equals(name)) {
                return false;
            }
            if (name.contains("docker")) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(usefullInterface)) {
            return usefullInterface.get(0).getHostAddress();
        }
        return ipList.get(0).getHostAddress();
    }

    public static List<Map<String, Object>> getServerIps() {
        List<Map<String, Object>> ips = new ArrayList<>();
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
        try {
            while (interfaces.hasMoreElements()) {
                //得到单个接口
                NetworkInterface nextInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = nextInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    //得到单个IP
                    InetAddress inetAddress = inetAddresses.nextElement();

                    Map<String, Object> ip = new HashMap<>();
                    ips.add(ip);
                    ip.put("name", nextInterface.getName());
                    ip.put("displayName", nextInterface.getDisplayName());
                    ip.put("hostAddress", inetAddress.getHostAddress());
                    ip.put("address", inetAddress.getAddress());
                    ip.put("canonicalHostName", inetAddress.getCanonicalHostName());
                    ip.put("hostName", inetAddress.getHostName());
                    ip.put("reachable", inetAddress.isReachable(100));
                    ip.put("loopbackAddress", inetAddress.isLoopbackAddress());
                    ip.put("linkLocalAddress", inetAddress.isLinkLocalAddress());
                    ip.put("anyLocalAddress", inetAddress.isAnyLocalAddress());
                    ip.put("str", inetAddress.toString());
                    ip.put("mcGlobal", inetAddress.isMCGlobal());
                    ip.put("mcLinkLocal", inetAddress.isMCLinkLocal());
                    ip.put("mcNodeLocal", inetAddress. isMCNodeLocal());
                    ip.put("mcSiteLocal", inetAddress.isMCSiteLocal());
                    ip.put("mcOrgLocal", inetAddress.isMCOrgLocal());
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ips;
    }

    private static class NetworkInfterfaceParam {
        private String name;
        private String hostAddress;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getHostAddress() {
            return hostAddress;
        }

        public void setHostAddress(String hostAddress) {
            this.hostAddress = hostAddress;
        }
    }

}
