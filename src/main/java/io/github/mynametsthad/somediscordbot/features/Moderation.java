package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.Configs;
import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Moderation {
    private final Configs configs;

    public Moderation() {
        configs = SomeDiscordBot.instance.configs;
    }

    public void warn(Guild guild, Member member, Member warner, String reason) throws IOException {
        System.out.println("attempting to warn member " + member.getEffectiveName() + " (id: " + member.getId() + ") in guild " + guild.getName() + " (id: " + guild.getId() + ") for " + reason);
        //increment warns for member in guild
        if (configs.memberWarns.get(guild.getId()).putIfAbsent(member.getId(), 1) != null) {
            configs.memberWarns.get(guild.getId()).put(member.getId(), configs.memberWarns.get(guild.getId()).get(member.getId()) + 1);
        }
        //send message to journal channel
        if (configs.journalChannels.get(guild.getId()) != null) {
            Objects.requireNonNull(guild.getTextChannelById(configs.journalChannels.get(guild.getId()))).sendMessage("**" + member.getAsMention() + "** was warned by " + warner.getAsMention() + " for " + reason).queue();
        }
        //if warns is divisible by 3, timeout member for their current warns number of minutes
        if (configs.memberWarns.get(guild.getId()).get(member.getId()) % 3 == 0) {
            guild.timeoutFor(member, configs.memberWarns.get(guild.getId()).get(member.getId()), TimeUnit.MINUTES).queue();
            //send message to journal channel
            if (configs.journalChannels.get(guild.getId()) != null) {
                Objects.requireNonNull(guild.getTextChannelById(configs.journalChannels.get(guild.getId()))).sendMessage("**" + member.getAsMention() + "** was timed out for " + configs.memberWarns.get(guild.getId()).get(member.getId()) + " minutes due to having" + configs.memberWarns.get(guild.getId()).get(member.getId()) + "warns.").queue();
            }
        }

        //save to file
        configs.saveToFile(4);
    }
}
