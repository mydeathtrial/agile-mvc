package com.agile.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * @author 佟盟 on 2017/2/23
 */
public class ServletUtil {
    /**
     * 获取http请求的真实IP地址
     *
     * @param request 请求对象
     * @return 返回IP地址
     */
    public static String getCustomerIPAddr(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return localhostFormat(ip);
    }

    /**
     * 本地地址格式化
     *
     * @param ip IP地址
     * @return 格式化后的IP地址
     */
    public static String localhostFormat(String ip) {
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
                ip = "未知IP地址";
            }
        }
        return ip;
    }

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     */
    private static String getLinuxLocalIp() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 获取本地Host名称
     */
    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断操作系统是否是Windows
     */
    private static boolean isWindowsOS() {
        boolean isWindowsOS = false;
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            isWindowsOS = true;
        }
        return isWindowsOS;
    }

    /**
     * 获取本地IP地址
     */
    public static String getLocalIP() {
        if (isWindowsOS()) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                return "未知";
            }
        } else {
            return getLinuxLocalIp();
        }
    }

    /**
     * 处理body参数
     *
     * @param request 请求request
     */
    public static String getBody(HttpServletRequest request) {

        try {
            BufferedReader br = request.getReader();

            String temp;
            StringBuilder jsonStr = new StringBuilder();
            while ((temp = br.readLine()) != null) {
                jsonStr.append(temp);
            }
            if (jsonStr.length() > 0) {
                return jsonStr.toString();
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    /**
     * 获取当前request中的请求地址
     *
     * @return url
     */
    public static String getCurrentUrl(HttpServletRequest request) {
        if (request != null) {
            return request.getRequestURL().toString();
        }
        return null;
    }

    /**
     * 请求中获取header或cookies中的信息
     *
     * @param request 请求 请求
     * @param key     索引
     * @return 信息
     */
    public static String getInfo(HttpServletRequest request, String key) {
        String token = request.getHeader(key);
        if (StringUtil.isBlank(token)) {
            try {
                Cookie cook = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(key)).findFirst().get();
                token = cook.getValue();
            } catch (Exception e) {
                token = null;
            }
        }
        return token;
    }
}
