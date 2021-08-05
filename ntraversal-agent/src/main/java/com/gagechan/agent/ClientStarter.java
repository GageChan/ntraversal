package com.gagechan.agent;

import com.gagechan.agent.scoket.Client;
import com.gagechan.agent.scoket.DefaultChannelInitializer;

/**
 * @author GageChan
 */
public class ClientStarter {

    public static void main(String[] args) throws Exception {
        AppConfig.init();
        Client client = new Client();
        client.connect(new DefaultChannelInitializer(), AppConfig.serverHost(), AppConfig.serverPort());
    }

}
