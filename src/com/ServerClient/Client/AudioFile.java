package com.ServerClient.Client;

import javax.sound.sampled.AudioFileFormat;
import java.nio.charset.StandardCharsets;

public class AudioFile extends ChainElement{
    public AudioFile(byte dataType)
    {
        super(dataType);
    }

    @Override
    public AudioFileFormat.Type handleConcreteRequest(MessageTemplate messageTemplate) {

        String audioFileFormatString = new String(messageTemplate.getBytes(), StandardCharsets.UTF_8);
        AudioFileFormat.Type targetFileType = null;

        if (audioFileFormatString.contains("AIFC")) {
            targetFileType = AudioFileFormat.Type.AIFC;
        } else if (audioFileFormatString.contains("AIFF")) {
            targetFileType = AudioFileFormat.Type.AIFF;
        } else if (audioFileFormatString.contains("AU")) {
            targetFileType = AudioFileFormat.Type.AU;
        } else if (audioFileFormatString.contains("SND")) {
            targetFileType = AudioFileFormat.Type.SND;
        } else if (audioFileFormatString.contains("WAVE")) {
            targetFileType = AudioFileFormat.Type.WAVE;
        } else {
            System.out.println("Unknown AudioFileFormat type");
        }

        return targetFileType;
    }
}
