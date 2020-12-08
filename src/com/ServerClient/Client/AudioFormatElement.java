package com.ServerClient.Client;

import javax.sound.sampled.AudioFormat;
import java.nio.charset.StandardCharsets;

public class AudioFormatElement extends ChainElement {
    public AudioFormatElement(byte dataType)
    {
        super(dataType);
    }

    @Override
    public AudioFormat handleConcreteRequest(MessageTemplate messageTemplate) {
        String audioFormatString = new String(messageTemplate.getBytes(), StandardCharsets.UTF_8);
        String[] split = audioFormatString.split(" ");

        AudioFormat audioFormat = null;

        switch (split[0])
        {
            case "PCM_SIGNED":
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, Float.parseFloat(split[1]),
                        Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]),
                        Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]));
                break;
            case "ALAW":
                audioFormat = new AudioFormat(AudioFormat.Encoding.ALAW, Float.parseFloat(split[1]),
                        Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]),
                        Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]));
                break;
            case "PCM_FLOAT":
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, Float.parseFloat(split[1]),
                        Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]),
                        Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]));
                break;
            case "PCM_UNSIGNED":
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, Float.parseFloat(split[1]),
                        Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]),
                        Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]));
                break;
            case "ULAW":
                audioFormat = new AudioFormat(AudioFormat.Encoding.ULAW, Float.parseFloat(split[1]),
                        Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]),
                        Float.parseFloat(split[5]), Boolean.parseBoolean(split[6]));
                break;
            default:
                System.out.println("Unknown AudioFormat");
                break;
        }

        return audioFormat;
    }
}
