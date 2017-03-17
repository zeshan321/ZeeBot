package com.zeshanaslam.zeebot;

import javax.script.ScriptException;

/**
 * ZeeBot is an assistant that accepts voice commands, questions and more. March 03, 2017.
 *
 * @author Zeshan Aslam
 */
public class Main {

    static BrainUtil brainUtil;

    public static void main(String[] args) {
        // Initialize utils
        brainUtil = new BrainUtil();
        ScriptsManager scriptsManager = new ScriptsManager();

        scriptsManager.clear();
        scriptsManager.load();

        // initialize Voce
        voce.SpeechInterface.init("./lib", true, true, "./lib/gram", "brain");

        // Start tracking
        System.out.println("Say 'Hey Zee'.");
        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }

            while (voce.SpeechInterface.getRecognizerQueueSize() > 0) {
                String input = voce.SpeechInterface.popRecognizedString();

                System.out.println(input);
                if (!brainUtil.isListening) {
                    if (input.equals("hey zee") || input.equals("hey z")) {
                        brainUtil.isListening = true;
                        voce.SpeechInterface.synthesize("Hey, how can I help?");
                    }
                } else {
                    switch (input) {
                        case "reload":
                            voce.SpeechInterface.stopSynthesizing();
                            voce.SpeechInterface.destroy();
                            scriptsManager.clear();
                            break;
                        case "quit":
                        case "exit":
                            voce.SpeechInterface.destroy();
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
                                    scriptObject.script.eval();
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
            }
        }
    }
}
