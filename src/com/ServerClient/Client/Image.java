package com.ServerClient.Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Image extends ChainElement {

    public Image(byte typeOfData) {
        super(typeOfData);
    }

    @Override
    public BufferedImage handleConcreteRequest(MessageTemplate messageTemplate) {
        BufferedImage bufferedImage = null;

        try
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(messageTemplate.getBytes());
            bufferedImage = ImageIO.read(inputStream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return bufferedImage;
    }
}
