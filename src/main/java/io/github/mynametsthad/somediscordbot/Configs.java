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
    private File prefixesPath = new File(System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
    private File journalChannelsPath = new File(System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
    private File sudoersRankIDsPath = new File(System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
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
        new Thread("saveToFile") {
            @Override
            public void run() {
                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json1 = gson.toJson(prefixesPath);
                    String json2 = gson.toJson(journalChannelsPath);
                    String json3 = gson.toJson(sudoersRankIDsPath);

                    FileOutputStream file1 = FileUtils.openOutputStream(prefixesPath);
                    FileOutputStream file2 = FileUtils.openOutputStream(journalChannelsPath);
                    FileOutputStream file3 = FileUtils.openOutputStream(sudoersRankIDsPath);
                    try {
                        file1.write(json1.getBytes());
                        SomeDiscordBot.instance.logger.info("Saved Prefixes to: " + System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
                        file2.write(json2.getBytes());
                        SomeDiscordBot.instance.logger.info("Saved JournalChannels to: " + System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
                        file3.write(json3.getBytes());
                        SomeDiscordBot.instance.logger.info("Saved SudoersRankIDs to: " + System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void readFromFile() throws IOException {
        try {
            String json1 = FileUtils.readFileToString(prefixesPath);
            String json2 = FileUtils.readFileToString(journalChannelsPath);
            String json3 = FileUtils.readFileToString(sudoersRankIDsPath);

            prefixes = new Gson().fromJson(json1, new TypeToken<Map<String, String>>() {
            }.getType());
            SomeDiscordBot.instance.logger.info("Loaded Prefixes from: " + System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
            journalChannels = new Gson().fromJson(json2, new TypeToken<Map<String, String>>() {
            }.getType());
            SomeDiscordBot.instance.logger.info("Loaded JournalChannels from: " + System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
            sudoersRankIDs = new Gson().fromJson(json3, new TypeToken<Map<String, String>>() {
            }.getType());
            SomeDiscordBot.instance.logger.info("Loaded SudoersRankIDs from: " + System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
        } catch (IOException e) {
            if (e.getMessage().equals("File '/home/iwant2tryhard/somediscordbot/Prefixes-current.json' does not exist") |
                    e.getMessage().equals("File '/home/iwant2tryhard/somediscordbot/JournalChannels-current.json' does not exist") |
                    e.getMessage().equals("File '/home/iwant2tryhard/somediscordbot/SudoersRankIDs-current.json' does not exist")) {
                SomeDiscordBot.instance.logger.warn("Storage Files not found; Creating empty files.");
                saveToFile();
                SomeDiscordBot.instance.logger.warn("Successfully Created Storage files.");
            }
        }
    }
}
