package com.gagechan.ntserver;


import org.apache.commons.cli.*;

import com.gagechan.ntserver.socket.DefaultChannelInitializer;
import com.gagechan.ntserver.socket.ProxyChannelInitializer;
import com.gagechan.ntserver.socket.Server;

/**
 * @author GageChan
 */
public class ServerStarter {

    private final static int DEFAULT_AGENT_PORT = 8999;
    private final static int DEFAULT_PROXY_PORT = 9000;

    public static void main(String[] args) throws Exception {
        /* 解析命令行 **/
        Options options = new Options();
        options.addOption("h", false, "the usage");
        options.addOption("ap",true, "the agent port, default value is 8999");
        options.addOption("pp",true, "the proxy port, default value is 9000");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("ntraversal server", options);
            return;
        }

        /* start agent server **/
        String ap = cmd.getOptionValue("ap", String.valueOf(DEFAULT_AGENT_PORT));
        Server agentServer = new Server();
        agentServer.start(new DefaultChannelInitializer(), Integer.parseInt(ap));

        /* start proxy server **/
        String pp = cmd.getOptionValue("pp", String.valueOf(DEFAULT_PROXY_PORT));
        Server proxyServer = new Server();
        proxyServer.start(new ProxyChannelInitializer(), Integer.parseInt(pp));

    }

}
