package com.zeshanaslam.zeebot;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

public class AudioPlayer {

    private BufferedInputStream inputStream;

    public AudioPlayer(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void play() {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(inputStream);
            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(decodedFormat, in);
            rawPlay(decodedFormat, audioInputStream);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void rawPlay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            // Start
            line.start();
            int nBytesRead = 0, nBytesWritten = 0;
            while (nBytesRead != -1) {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1) {
                    nBytesWritten = line.write(data, 0, nBytesRead);
                }

            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }

    }

    private synchronized SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceDataLine.open(audioFormat);
        return sourceDataLine;
    }
}
