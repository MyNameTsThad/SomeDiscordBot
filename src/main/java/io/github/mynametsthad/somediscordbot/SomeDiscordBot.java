package io.github.mynametsthad.somediscordbot;

import io.github.mynametsthad.somediscordbot.features.Commands;
import io.github.mynametsthad.somediscordbot.features.Journal;
import io.github.mynametsthad.somediscordbot.features.Moderation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SomeDiscordBot {
    public static final String NAME = "Some Discord Bot";
    public static final String PACKAGENAME = "io.github.mynametsthad.somediscordbot";
    public static final String VERSION = "1.4.0-2";
    public static final int VERSION_ID = 80;
    public static final String TOKEN = ""; //token here

    public static final boolean devMode = false;

    public static SomeDiscordBot instance;
    public Logger logger = LoggerFactory.getLogger(SomeDiscordBot.class);
    public JDA jda;
    public Configs configs;
    public Moderation moderation;
    public Journal journal;
    public boolean overrideSudoersRoleProtection = false;

    public SomeDiscordBot() throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);
        jdaBuilder.setActivity(devMode ? Activity.playing("DEVMODE: sdb|ver (" + VERSION + ")") : Activity.listening("sdb|ver (" + VERSION + ")"));
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        jdaBuilder.setChunkingFilter(ChunkingFilter.ALL); // enable member chunking for all guilds
        jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL); // ignored if chunking enabled
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        instance = this;
        configs = new Configs();
        journal = new Journal();
        moderation = new Moderation();
        jdaBuilder.addEventListeners(
                new Commands(),
                journal);

        jda = jdaBuilder.build();
        startLoop();
    }

    public static void main(String[] args) throws LoginException {
        new SomeDiscordBot();
    }

    public User getSelfUser() {
        return jda.getSelfUser();
    }

    public void startLoop(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                //loop through the journal.timeoutMap and check if the time has passed, if so, remove the entry
                for(String serverID : journal.timeoutMap.keySet()){
                    if (configs.journalStatus.get(serverID)) {
                        for (String userID : journal.timeoutMap.get(serverID).keySet()) {
                            if(journal.timeoutMap.get(serverID).get(userID) <= currentTime){
                                journal.timeoutMap.get(serverID).remove(userID);
                                Objects.requireNonNull(jda.getTextChannelById(configs.journalChannels.get(serverID)))
                                        .sendMessage(Utils.formatBold(Objects.requireNonNull(jda.getUserById(userID)).getAsMention()) +
                                                "'s timeout has been lifted.").queue();
                            }
                        }
                    }
                }
            }
        }, 500, 500);
    }
}
