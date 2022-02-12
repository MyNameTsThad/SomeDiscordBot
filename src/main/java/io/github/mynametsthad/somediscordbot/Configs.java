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
    File prefixesPath = new File(System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
    File journalChannelsPath = new File(System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
    File sudoersRankIDsPath = new File(System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
    File memberWarnsPath = new File(System.getProperty("user.home") + "/somediscordbot/MemberWarns-current.json");
    public Map<String, Boolean> journalStatus = new HashMap<>();
    public Map<String, Boolean> socialCreditStatus = new HashMap<>();
    public Map<String, Integer> socialCredits = new HashMap<>();

    public Map<String, String> prefixes = new HashMap<>();
    public Map<String, String> journalChannels = new HashMap<>();
    public Map<String, String> sudoersRankIDs = new HashMap<>();
    public Map<String, Map<String, Integer>> memberWarns = new HashMap<>();
    File journalStatusPath = new File(System.getProperty("user.home") + "/somediscordbot/JournalStatus-current.json");
    File socialCreditStatusPath = new File(System.getProperty("user.home") + "/somediscordbot/SocialCreditStatus-current.json");
    File socialCreditsPath = new File(System.getProperty("user.home") + "/somediscordbot/SocialCredits-current.json");

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

    public void saveToFile(int what) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String json = null;
        FileOutputStream file = null;
        File path = null;
        String name = null;
        switch (what) {
            case 1 -> {
                json = gson.toJson(prefixes);
                file = FileUtils.openOutputStream(prefixesPath);
                path = prefixesPath;
                name = "Prefixes";
            }
            case 2 -> {
                json = gson.toJson(journalChannels);
                file = FileUtils.openOutputStream(journalChannelsPath);
                path = journalChannelsPath;
                name = "JournalChannels";
            }
            case 3 -> {
                json = gson.toJson(sudoersRankIDs);
                file = FileUtils.openOutputStream(sudoersRankIDsPath);
                path = sudoersRankIDsPath;
                name = "SudoersRankIDs";
            }
            case 4 -> {
                json = gson.toJson(memberWarns);
                file = FileUtils.openOutputStream(memberWarnsPath);
                path = memberWarnsPath;
                name = "MemberWarns";
            }
            case 5 -> {
                json = gson.toJson(journalStatus);
                file = FileUtils.openOutputStream(journalStatusPath);
                path = journalStatusPath;
                name = "JournalStatus";
            }
            case 6 -> {
                json = gson.toJson(socialCreditStatus);
                file = FileUtils.openOutputStream(socialCreditStatusPath);
                path = socialCreditStatusPath;
                name = "SocialCreditStatus";
            }
            case 7 -> {
                json = gson.toJson(socialCredits);
                file = FileUtils.openOutputStream(socialCreditsPath);
                path = socialCreditsPath;
                name = "SocialCredits";
            }
        }

        try {
            assert file != null;
            file.write(json.getBytes());
            System.out.println("Saved " + name + " to: " + path.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert file != null;
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFromFile() throws IOException {
        try {
            String json1 = FileUtils.readFileToString(prefixesPath);
            prefixes = new Gson().fromJson(json1, new TypeToken<Map<String, String>>() {
            }.getType());
            System.out.println("Loaded Prefixes from: " + System.getProperty("user.home") + "/somediscordbot/Prefixes-current.json");
        }catch (IOException e){
            System.out.println("Prefixes File not found; Creating empty file.");
            saveToFile(1);
            System.out.println("Successfully Created Prefixes file.");
        }

        try {
            String json2 = FileUtils.readFileToString(journalChannelsPath);
            journalChannels = new Gson().fromJson(json2, new TypeToken<Map<String, String>>() {
            }.getType());
            System.out.println("Loaded JournalChannels from: " + System.getProperty("user.home") + "/somediscordbot/JournalChannels-current.json");
        }catch (IOException e){
            System.out.println("JournalChannels File not found; Creating empty file.");
            saveToFile(2);
            System.out.println("Successfully Created JournalChannels file.");
        }

        try {
            String json3 = FileUtils.readFileToString(sudoersRankIDsPath);
            sudoersRankIDs = new Gson().fromJson(json3, new TypeToken<Map<String, String>>() {
            }.getType());
            System.out.println("Loaded SudoersRankIDs from: " + System.getProperty("user.home") + "/somediscordbot/SudoersRankIDs-current.json");
        }catch (IOException e){
            System.out.println("SudoersRankIDs File not found; Creating empty file.");
            saveToFile(3);
            System.out.println("Successfully Created SudoersRankIDs file.");
        }

        try {
            String json4 = FileUtils.readFileToString(memberWarnsPath);
            memberWarns = new Gson().fromJson(json4, new TypeToken<Map<String, Map<String, Integer>>>() {
            }.getType());
            System.out.println("Loaded MemberWarns from: " + System.getProperty("user.home") + "/somediscordbot/MemberWarns-current.json");
        } catch (IOException e) {
            System.out.println("MemberWarns File not found; Creating empty file.");
            saveToFile(4);
            System.out.println("Successfully Created MemberWarns file.");
        }

        try {
            String json5 = FileUtils.readFileToString(journalStatusPath);
            journalStatus = new Gson().fromJson(json5, new TypeToken<Map<String, Map<String, Boolean>>>() {
            }.getType());
            System.out.println("Loaded JournalStatus from: " + System.getProperty("user.home") + "/somediscordbot/JournalStatus-current.json");
        } catch (IOException e) {
            System.out.println("JournalStatus File not found; Creating empty file.");
            saveToFile(5);
            System.out.println("Successfully Created JournalStatus file.");
        }

        try {
            String json6 = FileUtils.readFileToString(socialCreditStatusPath);
            socialCreditStatus = new Gson().fromJson(json6, new TypeToken<Map<String, Map<String, Boolean>>>() {
            }.getType());
            System.out.println("Loaded SocialCreditStatus from: " + System.getProperty("user.home") + "/somediscordbot/SocialCreditStatus-current.json");
        } catch (IOException e) {
            System.out.println("SocialCreditStatus File not found; Creating empty file.");
            saveToFile(6);
            System.out.println("Successfully Created SocialCreditStatus file.");
        }

        try {
            String json7 = FileUtils.readFileToString(socialCreditsPath);
            socialCredits = new Gson().fromJson(json7, new TypeToken<Map<String, Map<String, Integer>>>() {
            }.getType());
            System.out.println("Loaded SocialCredits from: " + System.getProperty("user.home") + "/somediscordbot/SocialCredits-current.json");
        } catch (IOException e) {
            System.out.println("SocialCredits File not found; Creating empty file.");
            saveToFile(7);
            System.out.println("Successfully Created SocialCredits file.");
        }
    }
}
