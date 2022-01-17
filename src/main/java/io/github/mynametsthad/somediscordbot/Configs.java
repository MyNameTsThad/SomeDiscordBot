package io.github.mynametsthad.somediscordbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Configs {
    public Map<String, String> prefixes = new HashMap<>();
    public Map<String, String> journalChannels = new HashMap<>();
    public Map<String, String> sudoersRankIDs = new HashMap<>();

    public Configs() {
        new Thread("loadFromFile") {
            @Override
            public void run() {
                try {
                    readFromFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void saveToFile() throws IOException {
        File prefixesPath = new File(System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
        File journalChannelsPath = new File(System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
        File sudoersRankIDsPath = new File(System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json1 = gson.toJson(prefixes);
        String json2 = gson.toJson(journalChannels);
        String json3 = gson.toJson(sudoersRankIDs);

        FileOutputStream file1 = FileUtils.openOutputStream(prefixesPath);
        FileOutputStream file2 = FileUtils.openOutputStream(journalChannelsPath);
        FileOutputStream file3 = FileUtils.openOutputStream(sudoersRankIDsPath);
        try {
            file1.write(json1.getBytes());
            System.out.println("Saved Prefixes to: " + System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
            file2.write(json2.getBytes());
            System.out.println("Saved JournalChannels to: " + System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
            file3.write(json3.getBytes());
            System.out.println("Saved SudoersRankIDs to: " + System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file1.flush();
                file2.flush();
                file3.flush();
                file1.close();
                file2.close();
                file3.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFromFile() throws IOException {
        try {
            File prefixesPath = new File(System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
            File journalChannelsPath = new File(System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
            File sudoersRankIDsPath = new File(System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
            String json1 = FileUtils.readFileToString(prefixesPath);
            String json2 = FileUtils.readFileToString(journalChannelsPath);
            String json3 = FileUtils.readFileToString(sudoersRankIDsPath);

            prefixes = new Gson().fromJson(json1, new TypeToken<Map<String, String>>() {
            }.getType());
            System.out.println("Loaded Prefixes from: " + System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
            journalChannels = new Gson().fromJson(json2, new TypeToken<Map<String, String>>() {
            }.getType());
            System.out.println("Loaded JournalChannels from: " + System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
            sudoersRankIDs = new Gson().fromJson(json3, new TypeToken<Map<String, String>>() {
            }.getType());
            System.out.println("Loaded SudoersRankIDs from: " + System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
        } catch (IOException e) {
            System.out.println("Storage Files not found; Creating empty files.");
            saveToFile();
            System.out.println("Successfully Created Storage files.");
        }
    }
}
