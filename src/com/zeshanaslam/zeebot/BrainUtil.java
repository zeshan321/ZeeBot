package com.zeshanaslam.zeebot;

public class BrainUtil {

    public boolean isListening = false;

    public void say(String message) {
        voce.SpeechInterface.synthesize(message);
    }

    public void stopSay() {
        voce.SpeechInterface.stopSynthesizing();
    }

    public void setListening(boolean listening) {
        isListening = listening;
    }
}
