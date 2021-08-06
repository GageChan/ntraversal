package com.github.gagechan.common.protocol;

/**
 * @author GageChan
 */
public enum Command {

    START_AGENT_PROXY(0), HEARTBEAT(1), FORWARD(2);
    private byte code;

    Command(byte code) {
        this.code = code;
    }

    Command(int i) {
        this.code = (byte) i;
    }

    public byte code() {
        return code;
    }

}
