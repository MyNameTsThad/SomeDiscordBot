package io.github.mynametsthad.somediscordbot;

import io.github.mynametsthad.somediscordbot.features.Commands;
import io.github.mynametsthad.somediscordbot.features.Journal;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class SomeDiscordBot {
    public static final String NAME = "Some Discord Bot";
    public static final String SHORTNAME = "SomeDiscordBot";
    public static final String VERSION = "0.2.0";
    public static final int VERSION_ID = 2;
    public static final String TOKEN = "<TOKEN>";

    public static SomeDiscordBot instance;
    public Logger logger = LoggerFactory.getLogger(SomeDiscordBot.class);
    public JDA jda;
    public Configs configs;
    public boolean overrideRoleAddProtection = false;

    public SomeDiscordBot() throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);
        jdaBuilder.setActivity(Activity.listening("sdb|help"));
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        jdaBuilder.addEventListeners(
                new Commands(),
                new Journal());

        jda = jdaBuilder.build();
        instance = this;
        configs = new Configs();
    }

    public static void main(String[] args) throws LoginException {
        new SomeDiscordBot();
    }
}
