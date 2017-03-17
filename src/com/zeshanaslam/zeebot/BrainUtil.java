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
        math = math.replace("x", "*");
        math = math.replace("decimal", "\\.");
        math = math.replace("dot", "\\.");
        math = math.replace("period", "\\.");
        math = math.replace("percent", "%");

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

    public double evaluateMath(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) return Double.NaN;
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else return Double.NaN;
                } else {
                    return Double.NaN;
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public boolean isNaN(double number) {
        return Double.isNaN(number);
    }

    public void print(String output) {
        System.out.println(output);
    }
}
