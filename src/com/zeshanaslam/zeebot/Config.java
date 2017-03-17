package com.zeshanaslam.zeebot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Config {

    private HashMap<String, String> settings = new HashMap<>();

    public void load() {
        settings.clear();

        try {
            try (BufferedReader br = new BufferedReader(new FileReader("./lib/config.zbs"))) {
                for (String line; (line = br.readLine()) != null; ) {
                    if (line.startsWith("#") || !line.contains(" = ")) continue;
                    String[] separate = line.split(" = ", 2);

                    String key = separate[0];
                    String value = separate[1].replace(" ", "");
                    settings.put(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean contains(String key) {
        return settings.containsKey(key);
    }

    public String get(String key) {
        return settings.get(key);
    }
}
