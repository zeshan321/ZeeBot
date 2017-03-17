package com.zeshanaslam.zeebot;

import com.darkprograms.speech.microphone.synthesiser.Synthesiser;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BrainUtil {

    public Config config;
    public boolean isListening = false;

    public BrainUtil() {
        this.config = new Config();
    }

    public void say(String message) {
        String language = "en-us";
        Synthesiser synthesiser = new Synthesiser(language);
        try {
            InputStream inputStream = synthesiser.getMP3Data(message);
            new AudioPlayer(new BufferedInputStream(inputStream)).play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListening(boolean listening) {
        isListening = listening;
    }

    public String replace(String math) {
        System.out.println("t: " + math);
        math = math.replace("square root", "sqrt");
        math = math.replace("sine", "sin");
        math = math.replace("cosine", "cos");
        math = math.replace("tangent", "tan");
        math = math.replace("minus", "-");
        math = math.replace("subtract", "-");
        math = math.replace("negative", "-");
        math = math.replace("plus", "+");
        math = math.replace("positive", "+");
        math = math.replace("divided", "/");
        math = math.replace("divide", "/");
        math = math.replace("times", "*");
        math = math.replace("time", "*");
        math = math.replace("multiply", "*");
        math = math.replace("decimal", "\\.");
        math = math.replace("dot", "\\.");
        math = math.replace("period", "\\.");
        math = math.replace("percent", "%");

        NumberWordConverter converter = new NumberWordConverter();
        String converted = converter.replaceNumbers(math);
        if (!converted.equals("000")) {
            math = converted;
        }
        return math;
    }

    public double getLevel(AudioFormat af, byte[] chunk) throws IOException {
        PCMSigned8Bit converter = new PCMSigned8Bit(af);
        if (chunk.length != converter.getRequiredChunkByteSize())
            return -1;

        AudioInputStream ais = converter.convert(chunk);
        ais.read(chunk, 0, chunk.length);

        long lSum = 0;
        for (int i = 0; i < chunk.length; i++)
            lSum = lSum + chunk[i];

        double dAvg = lSum / chunk.length;
        double sumMeanSquare = 0d;

        for (int j = 0; j < chunk.length; j++)

            sumMeanSquare = sumMeanSquare + Math.pow(chunk[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / chunk.length;

        return (Math.pow(averageMeanSquare, 0.5d));
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
