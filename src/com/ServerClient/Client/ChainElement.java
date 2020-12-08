package com.ServerClient.Client;

public abstract class ChainElement {
    protected byte typeOfData;
    protected ChainElement nextElement;

    public ChainElement(byte typeOfData) {
        this.typeOfData = typeOfData;
    }

    public void addNextElement(ChainElement nextElement) {
        this.nextElement = nextElement;
    }

    public Object handleReqest(MessageTemplate messageTemplate) {
        if (checkDataType(messageTemplate.getPreambula()))
            return handleConcreteRequest(messageTemplate);
        else {
            return nextElement.handleReqest(messageTemplate);
        }
    }

    public abstract Object handleConcreteRequest(MessageTemplate messageTemplate);

    public boolean checkDataType(byte typeOfData)
    {
        return this.typeOfData == typeOfData;
    }
}
