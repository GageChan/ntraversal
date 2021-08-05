package com.gagechan.common.protocol;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author GageChan
 */
public class Packet implements Serializable {

    private byte command;

    private byte[] data;

    private String remoteProxyChannelId;

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public String getRemoteProxyChannelId() {
        return remoteProxyChannelId;
    }

    public void setRemoteProxyChannelId(String remoteProxyChannelId) {
        this.remoteProxyChannelId = remoteProxyChannelId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public static Packet build(byte command, byte[] data) {
        Packet packet = new Packet();
        packet.setCommand(command);
        packet.setData(data);
        return packet;
    }

    public static Packet build(String remoteProxyChannelId, byte command, byte[] data) {
        Packet packet = new Packet();
        packet.setRemoteProxyChannelId(remoteProxyChannelId);
        packet.setCommand(command);
        packet.setData(data);
        return packet;
    }

    public static byte[] emptyData() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "Msg{" +
                "command=" + command +
                ", data='" + Arrays.toString(data) + '\'' +
                '}';
    }
}
