package com.ServerClient.Client;

public class MessageTemplate {
    private byte preambula;
    private byte[] bytes;

    public MessageTemplate(byte[] bytes)
    {
        preambula = bytes[0];
        this.bytes = new byte[bytes.length - 1];

        for(int i = 1; i < bytes.length - 1; i++)
            this.bytes[i - 1] = bytes[i];
    }

    public byte getPreambula() {
        return preambula;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
