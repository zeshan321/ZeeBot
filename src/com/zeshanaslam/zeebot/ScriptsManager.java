package com.zeshanaslam.zeebot;

import javax.script.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class ScriptsManager {

    public ScriptEngine engine;
    public Compilable compilableEngine;
    private HashMap<String, ScriptObject> scriptData = new HashMap<>();

    public void load() {
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
        this.compilableEngine = (Compilable) engine;
        scriptData.clear();

        int loaded = 0;
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("./lib/scripts/scripts.zbs"))) {
                for (String line; (line = br.readLine()) != null; ) {
                    if (line.startsWith("#") || !line.contains(" = ")) continue;

                    String[] separate = line.split(" = ", 2);
                    String data = separate[0];
                    String dir = separate[1].replace(" ", "");
                    File file = new File("./lib/scripts/" + File.separator + dir);

                    if (!file.exists()) {
                        System.out.println("[Zee] Error! File not found: " + data + " = " + dir);
                        continue;
                    }

                    FileReader reader = new FileReader(file);
                    BufferedReader textReader = new BufferedReader(reader);

                    String line1;
                    String script = "";
                    while ((line1 = textReader.readLine()) != null) {

                        if (line1.contains("//")) {
                            continue;
                        }

                        script += line1;
                    }

                    script = String.join("\n", script).replace("\n", "").replace("\t", "");

                    try {
                        scriptData.put(data, new ScriptObject(data, dir, compilableEngine.compile(script)));
                        loaded = loaded + 1;
                    } catch (ScriptException e) {
                        System.out.println("[Zee] Error! Unable to compile: " + data + " = " + dir);
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[Zee] Loaded " + loaded + " scripts.");
    }

    public void clear() {
        scriptData.clear();
    }

    public boolean contains(String key) {
        return scriptData.containsKey(key);
    }

    public ScriptObject getObject(String key) {
        return scriptData.get(key);
    }

    public Set<String> getKeys() {
        return scriptData.keySet();
    }
}
