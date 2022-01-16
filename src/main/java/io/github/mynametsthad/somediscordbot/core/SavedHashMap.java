package io.github.mynametsthad.somediscordbot.core;

import io.github.mynametsthad.somediscordbot.SomeDiscordBot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SavedHashMap<K, V> extends HashMap<K, V> {
    @Override
    public V put(K key, V value) {
        V returnValue = super.put(key, value);
        new Thread("saveToFile") {
            @Override
            public void run() {
                try {
                    SomeDiscordBot.instance.configs.saveToFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return returnValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        new Thread("saveToFile") {
            @Override
            public void run() {
                try {
                    SomeDiscordBot.instance.configs.saveToFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
