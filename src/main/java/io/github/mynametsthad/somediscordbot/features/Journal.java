package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmoteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Journal extends ListenerAdapter {
    public boolean enabled = true;
    public byte aggressiveness = 1;

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))) {
            if (!event.getAuthor().isBot()) {
                String authorID = event.getAuthor().getId();
                event.getMessage().delete().queue(delete -> event.getChannel().sendMessage("<@" + authorID + ">, you are not allowed to send Messages in this channel.").queue());
            }
        }
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        if (event.getChannel().getId().equals(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))) {
            event.getChannel().sendMessage("No deleting messages in this channel!").queue();
        }
    }

    @Override
    public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
    }

    @Override
    public void onMessageEmbed(@Nonnull MessageEmbedEvent event) {
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
    }

    @Override
    public void onMessageReactionRemoveAll(@Nonnull MessageReactionRemoveAllEvent event) {
    }

    @Override
    public void onMessageReactionRemoveEmote(@Nonnull MessageReactionRemoveEmoteEvent event) {
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        //System.out.println("added roles to member " + event.getMember().getId() + " on guild " + event.getGuild().getId());
        StringBuilder message = new StringBuilder("<@" + event.getMember().getId() + "> got added the following roles:\n");
        for (Role added : event.getRoles()) {
            message.append("`").append(added.getName()).append("` (").append(added.getId()).append("), ");
        }
        message.setLength(message.length() - 2);
        Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))).sendMessage(message.toString()).queue();
        if (SomeDiscordBot.instance.overrideRoleAddProtection) return;
        for (Role added : event.getRoles()) {
            String roleId = SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId());
            if (added.getId().equals(roleId)) {
                event.getGuild().removeRoleFromMember(event.getMember(), added).queue(success -> {
                    Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId())))
                            .sendMessage("You cannot add the <@&" + roleId + "> role by yourself. Please contact a person with the role to add it for you.").queue();
                    SomeDiscordBot.instance.overrideRoleAddProtection = false;
                });
            }
        }

        //prevent any roles from being added to the bot
        if (event.getMember().getId().equals(SomeDiscordBot.instance.getSelfUser().getId())) {
            //loop through all added roles
            for (Role added : event.getRoles()) {
                //remove the role from the bot
                event.getGuild().removeRoleFromMember(event.getMember(), added).queue();
            }
        }
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        StringBuilder message = new StringBuilder("<@" + event.getMember().getId() + "> got removed the following roles:\n");
        for (Role added : event.getRoles()) {
            message.append("`").append(added.getName()).append("` (").append(added.getId()).append("), ");
        }
        message.setLength(message.length() - 2);
        Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))).sendMessage(message.toString()).queue();

        //prevent any roles from being removed from the bot
        if (event.getMember().getId().equals(SomeDiscordBot.instance.getSelfUser().getId())) {
            //loop through all removed roles
            for (Role removed : event.getRoles()) {
                //add the role back to the bot
                event.getGuild().addRoleToMember(event.getMember(), removed).queue();
            }
        }
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
//        if (event.getMember().getUser().getName().equals("Some Discord Bot") && event.getMember().getUser().getDiscriminator().contains("4709")){
//            String oldNick = event.getNewNickname();
//            event.getGuild().modifyNickname(event.getMember(), "Some Discord Bot").queue(success -> {
//                Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))).sendMessage("Someone tried to change My Nickname! The nickname attempted to be applied is `" + oldNick + "`.").queue();
//            });
//        }
        //prevent the bot from its nickname being changed
        if (event.getMember().getId().equals(SomeDiscordBot.instance.getSelfUser().getId())) {
            String newNick = event.getNewNickname();
            event.getGuild().modifyNickname(event.getMember(), "Some Discord Bot").queue(success -> {
                Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))).sendMessage("Someone tried to change My Nickname! The nickname attempted to be applied is `" + newNick + "`.").queue();
            });
        }
    }
}
