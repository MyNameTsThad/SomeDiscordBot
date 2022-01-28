package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.Configs;
import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Moderation {
    private final Configs configs;

    public Moderation() {
        configs = SomeDiscordBot.instance.configs;
    }

    public void warn(Guild guild, Member member, String reason) {
        System.out.println("attempting to warn member " + member.getEffectiveName() + " (id: " + member.getId() + ") in guild " + guild.getName() + " (id: " + guild.getId() + ") for " + reason);
        if (configs.memberWarns.get(guild.getId()).get(member.getId()) == null) {
            configs.memberWarns.get(guild.getId()).put(member.getId(), 1);
        } else {
            configs.memberWarns.get(guild.getId()).replace(member.getId(), configs.memberWarns.get(guild.getId()).get(member.getId()) + 1);
        }
        Objects.requireNonNull(guild.getTextChannelById(configs.journalChannels.get(guild.getId()))).sendMessage("User <@" + member.getId() + "> has been warned " + (reason.isEmpty() ? "" : "for '" + reason + "'") + ". They now have " + configs.memberWarns.get(guild.getId()).get(member.getId()) + "warns.").queue();
        if (configs.memberWarns.get(guild.getId()).get(member.getId()) % 3 == 0) {
            guild.timeoutFor(member, configs.memberWarns.get(guild.getId()).get(member.getId()), TimeUnit.MINUTES).queue(success ->
                    Objects.requireNonNull(guild.getTextChannelById(configs.journalChannels.get(guild.getId()))).sendMessage("User <@" + member.getId() + "> has been warned " +
                            configs.memberWarns.get(guild.getId()).get(member.getId()) + " times. Timing them out for " + configs.memberWarns.get(guild.getId()).get(member.getId()) +
                            "minutes.").queue());
        }
    }
}
