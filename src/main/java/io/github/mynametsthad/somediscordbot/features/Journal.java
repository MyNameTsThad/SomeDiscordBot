package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.Configs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.application.ApplicationCommandCreateEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageEmbedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmoteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class Journal extends ListenerAdapter {
    public boolean enabled = true;
    public byte aggressiveness = 1;

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals(Configs.journalChannels.get(event.getGuild().getId()))) {
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
        if (event.getChannel().getId().equals(Configs.journalChannels.get(event.getGuild().getId()))) {
            event.getChannel().retrieveMessageById(event.getChannel().getLatestMessageId()).queue(message -> {
                event.getChannel().sendMessage("<@" + message.getAuthor().getId() + "> said: '" + message.getContentRaw() + "'")
                        .queue(success -> event.getChannel().sendMessage("No deleting messages in this channel!").queue());
            });
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
}
