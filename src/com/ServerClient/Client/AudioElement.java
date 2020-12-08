package com.ServerClient.Client;

public class AudioElement extends ChainElement {
    public AudioElement(byte dataType)
    {
        super(dataType);
    }

    @Override
    public byte[] handleConcreteRequest(MessageTemplate messageTemplate)
    {
        return messageTemplate.getBytes();
    }
}
