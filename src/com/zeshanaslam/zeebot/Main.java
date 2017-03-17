package com.zeshanaslam.zeebot;

import com.darkprograms.speech.microphone.microphone.MicrophoneAnalyzer;
import com.darkprograms.speech.microphone.recognizer.GSpeechDuplex;
import javaFlacEncoder.FLACFileWriter;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;

/**
 * ZeeBot is an assistant that accepts voice commands, questions and more. March 03, 2017.
 *
 * @author Zeshan Aslam
 */
public class Main {

    static BrainUtil brainUtil;

    public static void main(String[] args) {
        brainUtil = new BrainUtil();
        brainUtil.config.load();

        ScriptsManager scriptsManager = new ScriptsManager();

        scriptsManager.load();

        // Start tracking
        System.out.println("Say '" + brainUtil.config.get("Trigger") + "'.");
        MicrophoneAnalyzer mic = new MicrophoneAnalyzer(FLACFileWriter.FLAC);
        GSpeechDuplex duplex = new GSpeechDuplex(brainUtil.config.get("Key"));

        duplex.addResponseListener(googleResponse -> {
            if (!googleResponse.isFinalResponse()) return;

            String input = brainUtil.replace(googleResponse.getResponse().trim());

            System.out.println(input);
            if (!brainUtil.isListening) {
                if (input.equals(brainUtil.config.get("Trigger").toLowerCase())) {
                    brainUtil.isListening = true;
                    brainUtil.say("Hey, how can I help?");
                }
            } else {
                switch (input) {
                    case "reload":
                        brainUtil.config.load();
                        scriptsManager.load();
                        break;
                    case "quit":
                    case "exit":
                        System.exit(0);
                        break;

                    case "cancel":
                    case "never mind":
                        break;

                    // Hook into scripting
                    default:
                        // Compile later
                        if (scriptsManager.contains(input)) {
                            ScriptObject scriptObject = scriptsManager.getObject(input);
                            try {
                                Bindings bindings = scriptsManager.engine.getBindings(ScriptContext.ENGINE_SCOPE);
                                bindings.put("Brain", Main.brainUtil);

                                scriptObject.script.eval(bindings);
                            } catch (ScriptException e) {
                                e.printStackTrace();
                            }
                        } else {
                            brainUtil.say("Sorry, I don't understand.");
                        }
                        ScriptObject scriptObject = null;

                        for (String key : scriptsManager.getKeys()) {
                            if (key.contains(" + ")) {
                                String[] data = key.split(" + ");

                            } else {

                            }
                        }
                        break;
                }

                brainUtil.isListening = false;
            }

        });

        mic.open();
        try {
            // Recognition done here.
            duplex.recognize(mic.getTargetDataLine(), mic.getAudioFormat());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
