package com.ServerClient.Client;

import java.nio.charset.StandardCharsets;

public class TextElement extends ChainElement{

    public TextElement(byte typeOfData) {
        super(typeOfData);
    }

    @Override
    public String handleConcreteRequest(MessageTemplate messageTemplate) {
        return new String(messageTemplate.getBytes(), StandardCharsets.UTF_8);
    }
}
