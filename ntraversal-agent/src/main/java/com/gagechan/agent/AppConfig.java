package com.gagechan.agent;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * @author GageChan
 */
public class AppConfig {

    private static String serverHost;

    private static int serverPort;

    private static int proxyPort;

    public static void init() {
        JSONObject jsonObj = loadFromResource();
        proxyPort = jsonObj.getInt("proxy_port");
        serverHost = jsonObj.getStr("server_host");
        serverPort = jsonObj.getInt("server_port");
    }

    public static int proxyPort() {
        return proxyPort;
    }

    public static String serverHost() {
        return serverHost;
    }

    public static int serverPort() {
        return serverPort;
    }

    private static JSONObject loadFromResource() {
        String jsonStr = ResourceUtil.readUtf8Str("proxy.json");
        return JSONUtil.parseObj(jsonStr);
    }

}
