package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import net.dv8tion.jda.api.entities.Message;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Journal extends ListenerAdapter {
    public byte aggressiveness = 1;

    private boolean roleLockOverride = false;
    private boolean deleteLockOverride = false;

    private List<Message> last50MessagesInJournalChannel = new ArrayList<>(50);

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) != null && SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId())) {
            if (event.getChannel().getId().equals(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))) {
                if (!event.getAuthor().isBot()) {
                    String authorID = event.getAuthor().getId();
                    deleteLockOverride = true;
                    event.getMessage().delete().queue(delete -> {
                        event.getChannel().sendMessage("<@" + authorID + ">, you are not allowed to send Messages in this channel.").queue();
                    });
                }else{
                    //get last 50 messages in journal channel
                    last50MessagesInJournalChannel = event.getChannel().getHistory().retrievePast(50).complete();
                }
            }

            //increase the social credit of the author if the message says that they love and glory to the CCP
            if ((event.getMessage().getContentRaw().toLowerCase().contains("love") && event.getMessage().getContentRaw().toLowerCase().contains("ccp"))
                    || (event.getMessage().getContentRaw().toLowerCase().contains("glory") && event.getMessage().getContentRaw().toLowerCase().contains("ccp"))
                    || event.getMessage().getContentRaw().toLowerCase().contains("glory to the ccp")) {
                //if the author is not a bot
                if (!event.getAuthor().isBot()) {
                    //increase the social credit of the author
                    SomeDiscordBot.instance.configs.socialCredits.get(event.getGuild().getId()).putIfAbsent(event.getAuthor().getId(), 1000);
                    SomeDiscordBot.instance.configs.socialCredits.get(event.getGuild().getId()).put(event.getAuthor().getId(), SomeDiscordBot.instance.configs.socialCredits.get(event.getGuild().getId()).get(event.getAuthor().getId()) + 500);
                    try {
                        SomeDiscordBot.instance.configs.saveToFile(7);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //send a copypasta message to the channel that the author has been given 50 social credit
                    event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> [ 中华人民共和国寄语] Great work, Celebrity! Your social credit score has increased by [500] Integers. Xi Jinping would like to meet you personally at Zhongnanhai to encourage your good work. I am sure you notice that you have gained lot of dislike recently. Do not worry. We will send re-education vans to make sure your figure is in good graces. Keep up the good work! [ 中华人民共和国寄语]").queue();
                }
            }

            //decrease the social credit of the author if the message says that they hate and shame to the CCP or that Taiwan is a country
            if ((event.getMessage().getContentRaw().toLowerCase().contains("hate") && event.getMessage().getContentRaw().toLowerCase().contains("CCP"))
                    || (event.getMessage().getContentRaw().toLowerCase().contains("shame") && event.getMessage().getContentRaw().toLowerCase().contains("CCP"))
                    || event.getMessage().getContentRaw().toLowerCase().contains("taiwan") || event.getMessage().getContentRaw().toLowerCase().contains("taiwanese")
                    || event.getMessage().getContentRaw().toLowerCase().contains("taiwanese people")
                    || event.getMessage().getContentRaw().toLowerCase().contains("taiwanese people are good people")
                    || event.getMessage().getContentRaw().toLowerCase().contains("taiwan is a country")
                    || event.getMessage().getContentRaw().toLowerCase().contains("hate the ccp")) {
                //if the author is not a bot
                if (!event.getAuthor().isBot()) {
                    //decrease the social credit of the author
                    SomeDiscordBot.instance.configs.socialCredits.get(event.getGuild().getId()).putIfAbsent(event.getAuthor().getId(), 1000);
                    SomeDiscordBot.instance.configs.socialCredits.get(event.getGuild().getId()).put(event.getAuthor().getId(), SomeDiscordBot.instance.configs.socialCredits.get(event.getGuild().getId()).get(event.getAuthor().getId()) - 250);
                    try {
                        SomeDiscordBot.instance.configs.saveToFile(7);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //send a copypasta message to the channel that the author has lost 250 social credit
                    event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> VERY BAD! 250 social credits have been deducted 低等公民 Please refrain from mentioning events that never happened that could discredit the great 人民共产党 People’s Communist Party again or we will be forced to 饿了就睡觉 send party agents to escort you to a re-education van [人民行刑车].").queue();
                }
            }
        } else if (SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) == null) {
            SomeDiscordBot.instance.configs.journalStatus = new HashMap<>();
            if (SomeDiscordBot.instance.configs.journalStatus.putIfAbsent(event.getGuild().getId(), true) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        if (event.getChannel().getId().equals(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId())) && SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) && !deleteLockOverride) {
            //compare the cached 50 messages list and the current message list to see what messages have been deleted
            List<Message> currentMessages = event.getChannel().getHistory().retrievePast(50).complete();
            List<Message> cachedMessages = last50MessagesInJournalChannel;
            for (int i = 0; i < currentMessages.size(); i++) {
                if (!currentMessages.get(i).equals(cachedMessages.get(i))) {
                    //if the message has been deleted, restore the message and mention the original author
                    int finalI = i;
                    event.getChannel().sendMessage(cachedMessages.get(i)).queue(message -> message.editMessage(cachedMessages.get(finalI).getContentRaw() + " (originally sent by " + cachedMessages.get(finalI).getAuthor().getAsMention() + ")").queue());
                    //break the loop
                    break;
                }
            }
            //update the cached message list
            last50MessagesInJournalChannel = event.getChannel().getHistory().retrievePast(50).complete();
        }else if (deleteLockOverride) {
            deleteLockOverride = false;
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
        if (SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) != null && SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId())) {
            StringBuilder message = new StringBuilder("<@" + event.getMember().getId() + "> got added the following roles:\n");
            for (Role added : event.getRoles()) {
                message.append("`").append(added.getName()).append("` (").append(added.getId()).append("), ");
            }
            message.setLength(message.length() - 2);
            Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))).sendMessage(message.toString()).queue();
        } else if (SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) == null) {
            SomeDiscordBot.instance.configs.journalStatus = new HashMap<>();
            if (SomeDiscordBot.instance.configs.journalStatus.putIfAbsent(event.getGuild().getId(), true) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //System.out.println("added roles to member " + event.getMember().getId() + " on guild " + event.getGuild().getId());
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
        if (event.getMember().getId().equals(SomeDiscordBot.instance.getSelfUser().getId()) && !roleLockOverride) {
            //loop through all added roles
            for (Role added : event.getRoles()) {
                //remove the role from the bot
                event.getGuild().removeRoleFromMember(event.getMember(), added).queue();
                roleLockOverride = true;
            }
        }else{
            roleLockOverride = false;
        }
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        if (SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) != null && SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId())) {
            StringBuilder message = new StringBuilder("<@" + event.getMember().getId() + "> got removed the following roles:\n");
            for (Role added : event.getRoles()) {
                message.append("`").append(added.getName()).append("` (").append(added.getId()).append("), ");
            }
            message.setLength(message.length() - 2);
            Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))).sendMessage(message.toString()).queue();
        } else if (SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) == null) {
            SomeDiscordBot.instance.configs.journalStatus = new HashMap<>();
            if (SomeDiscordBot.instance.configs.journalStatus.putIfAbsent(event.getGuild().getId(), true) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //prevent any roles from being removed from the bot
        if (event.getMember().getId().equals(SomeDiscordBot.instance.getSelfUser().getId()) && !roleLockOverride) {
            //loop through all removed roles
            for (Role removed : event.getRoles()) {
                //add the role back to the bot
                event.getGuild().addRoleToMember(event.getMember(), removed).queue();
                roleLockOverride = true;
            }
        } else {
            roleLockOverride = false;
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
