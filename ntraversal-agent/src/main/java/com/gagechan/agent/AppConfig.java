package com.gagechan.agent;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author GageChan
 */
public class AppConfig {

    private final static Logger log = LoggerFactory.getLogger(AppConfig.class);

    private final static String DEFAULT_SERVER_HOST = "127.0.0.1";

    private final static int DEFAULT_SERVER_PORT = 8999;

    private final static int DEFAULT_PROXY_PORT = 3306;

    private static String serverHost;

    private static int serverPort;

    private static int proxyPort;

    public static void init() {
        JSONObject jsonObj = loadConfig();
        log.info("load config: {}", jsonObj.toStringPretty());
        Integer inProxyPort = jsonObj.getInt("proxy_port");
        proxyPort = Objects.isNull(inProxyPort) ? DEFAULT_PROXY_PORT : inProxyPort;

        String inServerHost = jsonObj.getStr("server_host");
        serverHost = Objects.isNull(inServerHost) ? DEFAULT_SERVER_HOST : inServerHost;

        Integer inServerPort = jsonObj.getInt("server_port");
        serverPort = Objects.isNull(inProxyPort) ? DEFAULT_SERVER_PORT : inServerPort;
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

    private static JSONObject loadConfig() {
        String jsonStr = "{}";
        try (InputStream in = new FileInputStream("config/local_proxy.json")) {
            jsonStr = IoUtil.read(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("the local_proxy.json not find in ./config/");
            return JSONUtil.parseObj(jsonStr);
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONUtil.parseObj(jsonStr);
        } catch (Throwable throwable) {
            log.error("the local_proxy.json is a illegal json. for example :{\n" +
                    "  \"server_host\": \"127.0.0.1\",\n" +
                    "  \"server_port\": 8999,\n" +
                    "  \"proxy_port\": 6379\n" +
                    "}\n");
            System.exit(-2);
        }
        return jsonObject;
    }

}
