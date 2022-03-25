package io.github.mynametsthad.somediscordbot.features;

//import Jwiki.Jwiki;

import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import io.github.mynametsthad.somediscordbot.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.fastily.jwiki.core.Wiki;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Commands extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.isFromGuild()) {
            //init check
            if (SomeDiscordBot.instance.configs.prefixes == null)
                SomeDiscordBot.instance.configs.prefixes = new HashMap<>();
            if (SomeDiscordBot.instance.configs.prefixes.putIfAbsent(event.getGuild().getId(), "sdb|") == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.journalChannels == null)
                SomeDiscordBot.instance.configs.journalChannels = new HashMap<>();
            if (SomeDiscordBot.instance.configs.journalChannels.putIfAbsent(event.getGuild().getId(), "") == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.sudoersRankIDs == null)
                SomeDiscordBot.instance.configs.sudoersRankIDs = new HashMap<>();
            if (SomeDiscordBot.instance.configs.sudoersRankIDs.putIfAbsent(event.getGuild().getId(), "") == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.memberWarns == null)
                SomeDiscordBot.instance.configs.memberWarns = new HashMap<>();
            if (SomeDiscordBot.instance.configs.memberWarns.putIfAbsent(event.getGuild().getId(), new HashMap<>()) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(4);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.journalStatus == null)
                SomeDiscordBot.instance.configs.journalStatus = new HashMap<>();
            if (SomeDiscordBot.instance.configs.journalStatus.putIfAbsent(event.getGuild().getId(), true) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.socialCreditStatus == null)
                SomeDiscordBot.instance.configs.socialCreditStatus = new HashMap<>();
            if (SomeDiscordBot.instance.configs.socialCreditStatus.putIfAbsent(event.getGuild().getId(), true) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(6);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.socialCredits == null)
                SomeDiscordBot.instance.configs.socialCredits = new HashMap<>();
            if (SomeDiscordBot.instance.configs.socialCredits.putIfAbsent(event.getGuild().getId(), new HashMap<>()) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(7);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.serverRules == null)
                SomeDiscordBot.instance.configs.serverRules = new HashMap<>();
            if (SomeDiscordBot.instance.configs.serverRules.putIfAbsent(event.getGuild().getId(), new ArrayList<>()) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(8);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //main stuff
            String[] args = event.getMessage().getContentRaw().split(" ");

            List<String> roleIds = new ArrayList<>();
            Objects.requireNonNull(event.getMember()).getRoles().forEach(role -> roleIds.add(role.getId()));
            boolean isSudoersRole = event.isFromGuild() && roleIds.contains(SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId()));

            if (args[0].equalsIgnoreCase("sdb|ver") | (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "ver"))) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(SomeDiscordBot.NAME + " version " + SomeDiscordBot.VERSION)
                        .setThumbnail("https://cdn.discordapp.com/avatars/923870625876029440/3eb5ad84e9278bc8c7d6be382e22db5c.png?size=256")
                        .setColor(Color.GREEN)
                        .setDescription("INTERNAL ID: " + Utils.formatCode(SomeDiscordBot.PACKAGENAME + ":" + SomeDiscordBot.VERSION_ID))
                        .setFooter("Bot made by IWant2TryHard#1702.", "https://cdn.discordapp.com/avatars/600496278857842698/fbfb49e5052d9050d503cfa42506f5e9.webp?size=160");
                event.getMessage().replyEmbeds(embed.build()).queue();
            } else if (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "botctl") && isSudoersRole) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("prefix")) {
                        if (args.length > 2) {
                            SomeDiscordBot.instance.configs.prefixes.replace(event.getGuild().getId(), args[2].toLowerCase(Locale.ROOT) + "|");
                            try {
                                SomeDiscordBot.instance.configs.saveToFile(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("Success! :white_check_mark:")
                                    .setColor(Color.GREEN)
                                    .setDescription("This guild's bot prefix is now set to " + Utils.formatCode(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId())));
                            event.getMessage().replyEmbeds(embed.build()).queue();
                        }
                    } else if (args[1].equalsIgnoreCase("rolebruteforce")) {
                        //command to add a role to author
                        //[prefix]rolebruteforce @role
                        if (args.length > 2) {
                            if (args[2].startsWith("<@&")) {
                                String roleId = args[2].substring(3, args[2].length() - 1);
                                //check if role exists
                                if (event.getGuild().getRoleById(roleId) != null) {
                                    //add role to author
                                    try {
                                        event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(roleId))).queue(success -> {
                                            EmbedBuilder embed = new EmbedBuilder();
                                            embed.setTitle("Success! :white_check_mark:")
                                                    .setColor(Color.GREEN)
                                                    .setDescription("Role " + Objects.requireNonNull(event.getGuild().getRoleById(roleId)).getAsMention() + " has been added to " + event.getMember().getAsMention());
                                            event.getMessage().replyEmbeds(embed.build()).queue();
                                        });
                                    } catch (HierarchyException e) {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Error! :x:")
                                                .setColor(Color.RED)
                                                .setDescription("I can't add that role to you, I don't have the permissions to do that.")
                                                .appendDescription("\n\n Click for Exception Message: \n" + Utils.formatSpoiler(Utils.formatBlockCode(e.getMessage())));
                                        event.getMessage().replyEmbeds(embed.build()).queue();
                                    }
                                }
                                //if role doesn't exist, reply with error
                                else {
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setTitle("Error! :x:")
                                            .setColor(Color.RED)
                                            .setDescription("Role doesn't exist. Just use the role selection menu, rather than typing the role ID.");
                                    event.getMessage().replyEmbeds(embed.build()).queue();
                                }
                            } else {
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("Error! :x:")
                                        .setColor(Color.RED)
                                        .setDescription("Invalid input. Just use the role selection menu.");
                                event.getMessage().replyEmbeds(embed.build()).queue();
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("journal")) {
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("setchannel")) {
                                if (args.length > 3) {
                                    String channelID = args[3].substring(2, args[3].length() - 1);
                                    SomeDiscordBot.instance.configs.journalChannels.replace(event.getGuild().getId(), channelID);
                                    try {
                                        SomeDiscordBot.instance.configs.saveToFile(2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setTitle("Success! :white_check_mark:")
                                            .setColor(Color.GREEN)
                                            .setDescription("Channel " + Utils.formatMentionChannel(channelID) + " is now the Journal Channel.");
                                    event.getMessage().replyEmbeds(embed.build()).queue();
                                } else {
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setTitle("Error! :x:")
                                            .setColor(Color.RED)
                                            .setDescription("Provide a channel to set as the Journal Channel!");
                                    event.getMessage().replyEmbeds(embed.build()).queue();
                                }
                                //command to enable/disable journalling
                            } else if (args[2].equalsIgnoreCase("status")) {
                                //enable journalling
                                if (args.length > 3) {
                                    if (args[3].equalsIgnoreCase("enable")) {
                                        SomeDiscordBot.instance.configs.journalStatus.replace(event.getGuild().getId(), true);
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Success! :white_check_mark:")
                                                .setColor(Color.GREEN)
                                                .setDescription("Enabled journaling!");
                                        event.getMessage().replyEmbeds(embed.build()).queue();
                                    } else if (args[3].equalsIgnoreCase("disable")) {
                                        SomeDiscordBot.instance.configs.journalStatus.replace(event.getGuild().getId(), false);
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle("Success! :white_check_mark:")
                                                .setColor(Color.GREEN)
                                                .setDescription("Disabled journaling!");
                                        event.getMessage().replyEmbeds(embed.build()).queue();
                                    } else {
                                        EmbedBuilder embed = new EmbedBuilder();
                                        String prefix = SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId());
                                        embed.setTitle("Error! :x:")
                                                .setColor(Color.RED)
                                                .setDescription("Invalid command! Use " + Utils.formatCode(prefix + "botctl journal status enable") + " or " + Utils.formatCode(prefix + "botctl journal status disable") + ".");
                                        event.getMessage().replyEmbeds(embed.build()).queue();
                                    }
                                }
                            } else {
                                String prefix = SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId());
                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("Error! :x:")
                                        .setColor(Color.RED)
                                        .setDescription("Invalid subcommand. Refer to " + Utils.formatCode(prefix + "help") + " for Commands and Subcommands.");
                                event.getMessage().replyEmbeds(embed.build()).queue();
                            }
                        } else {
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle("Error! :x:")
                                    .setColor(Color.RED)
                                    .setDescription("No Subcommand found! Provide a subcommand!");
                            event.getMessage().replyEmbeds(embed.build()).queue();
                        }
                    } else if (args[1].equalsIgnoreCase("sudoers")) {
                        //command to add/remove sudoers
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("add")) {
                                if (args.length > 3) {
                                    //dm the user with a confirmation prompt
                                    //send a message directly to the user
                                    //if the user confirms, add to sudoers
                                    //if the user denies, do nothing
                                    String userID = args[3].substring(3, args[3].length() - 1);
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setTitle("Question")
                                            .setColor(Color.ORANGE)
                                            .setDescription("Are you sure you want to add " + Objects.requireNonNull(event.getGuild().getMemberById(userID)).getAsMention() + " to the sudoers list on '" + event.getGuild().getName() + "'?");
                                    event.getAuthor().openPrivateChannel().queue(privateChannel ->
                                            privateChannel.sendMessageEmbeds(embed.build()).queue(message -> {
                                                //if the author reacts with the correct emoji, remove from sudoers
                                                message.addReaction("✅").queue();
                                                message.addReaction("❌").queue();
                                                SomeDiscordBot.instance.journal.addConfirmationMap.put(privateChannel.getUser().getId(), new String[]{message.getId(), event.getGuild().getId(), userID});
                                            }));
                                }
                            } else if (args[2].equalsIgnoreCase("remove")) {
                                if (args.length > 3) {
                                    String userID = args[3].substring(3, args[3].length() - 1);
                                    EmbedBuilder embed = new EmbedBuilder();
                                    embed.setTitle("Question")
                                            .setColor(Color.ORANGE)
                                            .setDescription("Are you sure you want to remove " + Objects.requireNonNull(event.getGuild().getMemberById(userID)).getAsMention() + " from the sudoers list on '" + event.getGuild().getName() + "'?");
                                    event.getAuthor().openPrivateChannel().queue(privateChannel ->
                                            privateChannel.sendMessageEmbeds(embed.build()).queue(message -> {
                                                message.addReaction("✅").queue();
                                                message.addReaction("❌").queue();
                                                SomeDiscordBot.instance.journal.removeConfirmationMap.put(privateChannel.getUser().getId(), new String[]{message.getId(), event.getGuild().getId(), userID});
                                            }));
                                }
                            }
                        }
                    } else {
                        String prefix = SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId());
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Error! :x:")
                                .setColor(Color.RED)
                                .setDescription("Invalid subcommand. Refer to " + Utils.formatCode(prefix + "help") + " for Commands and Subcommands.");
                        event.getMessage().replyEmbeds(embed.build()).queue();
                    }
                } else {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Error! :x:")
                            .setColor(Color.RED)
                            .setDescription("No Subcommand found! Provide a subcommand!");
                    event.getMessage().replyEmbeds(embed.build()).queue();
                }
            } else if (args[0].equalsIgnoreCase("sdb|init") | args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "init")) {
                if (!event.getGuild().getRolesByName("sudoers", false).isEmpty()) {
                    if (!event.getGuild().getRolesByName("sudoers", false).get(0).getPermissions().contains(Permission.ADMINISTRATOR)) {
                        event.getGuild().getRolesByName("sudoers", false).get(0).delete().queue();
                    }
                }
                EmbedBuilder eb_0_0_0 = new EmbedBuilder(), eb_1_0_0 = new EmbedBuilder(), eb_1_1_0 = new EmbedBuilder(), eb_1_1_1 = new EmbedBuilder(), eb_x_0_0 = new EmbedBuilder();
                eb_0_0_0.setTitle("Server Setup for Server '" + event.getGuild().getName() + "'").setColor(Color.orange).setThumbnail(event.getGuild().getIconUrl())
                        .setDescription("Initializing guild - In Progress...")
                        .addField(new MessageEmbed.Field("Creating `sudoers` role...", ":black_large_square:", false))
                        .addField(new MessageEmbed.Field("Add `sudoers` role to requested user", ":black_large_square:", false))
                        .addField(new MessageEmbed.Field("Add server-specific configurations", ":black_large_square:", false));
                eb_1_0_0.setTitle("Server Setup for Server '" + event.getGuild().getName() + "'").setColor(Color.orange).setThumbnail(event.getGuild().getIconUrl())
                        .setDescription("Initializing guild - In Progress...")
                        .addField(new MessageEmbed.Field("Creating `sudoers` role...", ":white_check_mark:", false))
                        .addField(new MessageEmbed.Field("Add `sudoers` role to requested user", ":black_large_square:", false))
                        .addField(new MessageEmbed.Field("Add server-specific configurations", ":black_large_square:", false));
                eb_1_1_0.setTitle("Server Setup for Server '" + event.getGuild().getName() + "'").setColor(Color.orange).setThumbnail(event.getGuild().getIconUrl())
                        .setDescription("Initializing guild - In Progress...")
                        .addField(new MessageEmbed.Field("Creating `sudoers` role...", ":white_check_mark:", false))
                        .addField(new MessageEmbed.Field("Add `sudoers` role to requested user", ":white_check_mark:", false))
                        .addField(new MessageEmbed.Field("Add server-specific configurations", ":black_large_square:", false));
                eb_1_1_1.setTitle("Server Setup for Server '" + event.getGuild().getName() + "'").setColor(Color.green).setThumbnail(event.getGuild().getIconUrl())
                        .setDescription("Initialized guild.")
                        .addField(new MessageEmbed.Field("Creating `sudoers` role...", ":white_check_mark:", false))
                        .addField(new MessageEmbed.Field("Add `sudoers` role to requested user", ":white_check_mark:", false))
                        .addField(new MessageEmbed.Field("Add server-specific configurations", ":white_check_mark:", false));
                eb_x_0_0.setTitle("Server Setup for Server '" + event.getGuild().getName() + "'").setColor(Color.red).setThumbnail(event.getGuild().getIconUrl())
                        .setDescription("Initialize guild failed.")
                        .addField(new MessageEmbed.Field("Creating `sudoers` role...", ":x:", false))
                        .addField(new MessageEmbed.Field("Add `sudoers` role to requested user", ":black_large_square:", false))
                        .addField(new MessageEmbed.Field("Add server-specific configurations", ":black_large_square:", false));
                if (event.getGuild().getRolesByName("sudoers", false).isEmpty() | !SomeDiscordBot.instance.configs.sudoersRankIDs.containsKey(event.getGuild().getId())) {
                    event.getMessage().replyEmbeds(eb_0_0_0.build())
                            .queue(response -> {
                                if (event.getGuild().getRolesByName("sudoers", false).isEmpty()) {
                                    event.getGuild().createRole()
                                            .setName("sudoers")
                                            .setColor(Color.RED)
                                            .setPermissions(Permission.ADMINISTRATOR)
                                            .queue(role -> {
                                                response.editMessageEmbeds(eb_1_0_0.build()).queue();
                                                SomeDiscordBot.instance.overrideSudoersRoleProtection = true;
                                                event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(role.getId())))
                                                        .queue(nextstep1 -> response.editMessageEmbeds(eb_1_1_0.build()).queue(nextstep2 -> {
                                                            if (!SomeDiscordBot.instance.configs.prefixes.containsKey(event.getGuild().getId())) {
                                                                SomeDiscordBot.instance.configs.prefixes.put(event.getGuild().getId(), "sdb|");
                                                                SomeDiscordBot.instance.configs.journalChannels.put(event.getGuild().getId(), "");
                                                                SomeDiscordBot.instance.configs.memberWarns.put(event.getGuild().getId(), new HashMap<>());
                                                                try {
                                                                    SomeDiscordBot.instance.configs.saveToFile(1);
                                                                    SomeDiscordBot.instance.configs.saveToFile(2);
                                                                    SomeDiscordBot.instance.configs.saveToFile(4);
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                            response.editMessageEmbeds(eb_1_1_1.build()).queue();
                                                        }));
                                                SomeDiscordBot.instance.configs.sudoersRankIDs.put(event.getGuild().getId(), role.getId());
                                                try {
                                                    SomeDiscordBot.instance.configs.saveToFile(3);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                } else {
                                    response.editMessageEmbeds(eb_1_0_0.build())
                                            .queue();
                                    event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId()))))
                                            .queue(nextstep -> response.editMessageEmbeds(eb_1_1_0.build()).queue(nextstep2 -> {
                                                if (!SomeDiscordBot.instance.configs.prefixes.containsKey(event.getGuild().getId())) {
                                                    SomeDiscordBot.instance.configs.prefixes.put(event.getGuild().getId(), "sdb|");
                                                    SomeDiscordBot.instance.configs.journalChannels.put(event.getGuild().getId(), "");
                                                    SomeDiscordBot.instance.configs.journalStatus.put(event.getGuild().getId(), true);
                                                    SomeDiscordBot.instance.configs.socialCreditStatus.put(event.getGuild().getId(), true);
                                                    SomeDiscordBot.instance.configs.socialCredits.put(event.getGuild().getId(), new HashMap<>());
                                                    SomeDiscordBot.instance.configs.serverRules.put(event.getGuild().getId(), new ArrayList<>());
                                                    try {
                                                        SomeDiscordBot.instance.configs.saveToFile(1);
                                                        SomeDiscordBot.instance.configs.saveToFile(2);
                                                        SomeDiscordBot.instance.configs.saveToFile(3);
                                                        SomeDiscordBot.instance.configs.saveToFile(4);
                                                        SomeDiscordBot.instance.configs.saveToFile(5);
                                                        SomeDiscordBot.instance.configs.saveToFile(6);
                                                        SomeDiscordBot.instance.configs.saveToFile(7);
                                                        SomeDiscordBot.instance.configs.saveToFile(8);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                response.editMessageEmbeds(eb_1_1_1.build()).queue();
                                            }));
                                }
                            });
                } else {
                    event.getMessage().replyEmbeds(eb_x_0_0.build())
                            .queue(response ->
                                    event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId()))))
                                            .queue(nextstep -> response.editMessageEmbeds(eb_1_1_0.build()).queue(nextstep2 -> {
                                                if (!SomeDiscordBot.instance.configs.prefixes.containsKey(event.getGuild().getId())) {
                                                    SomeDiscordBot.instance.configs.prefixes.put(event.getGuild().getId(), "sdb|");
                                                    SomeDiscordBot.instance.configs.journalChannels.put(event.getGuild().getId(), "");
                                                    SomeDiscordBot.instance.configs.journalStatus.put(event.getGuild().getId(), true);
                                                    SomeDiscordBot.instance.configs.socialCreditStatus.put(event.getGuild().getId(), true);
                                                    SomeDiscordBot.instance.configs.socialCredits.put(event.getGuild().getId(), new HashMap<>());
                                                    SomeDiscordBot.instance.configs.serverRules.put(event.getGuild().getId(), new ArrayList<>());
                                                    try {
                                                        SomeDiscordBot.instance.configs.saveToFile(1);
                                                        SomeDiscordBot.instance.configs.saveToFile(2);
                                                        SomeDiscordBot.instance.configs.saveToFile(3);
                                                        SomeDiscordBot.instance.configs.saveToFile(4);
                                                        SomeDiscordBot.instance.configs.saveToFile(5);
                                                        SomeDiscordBot.instance.configs.saveToFile(6);
                                                        SomeDiscordBot.instance.configs.saveToFile(7);
                                                        SomeDiscordBot.instance.configs.saveToFile(8);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                response.editMessageEmbeds(eb_1_1_1.build()).queue();
                                            })));
                }
            }
            //command to warn member for a specified reason
            else if (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "warn") && isSudoersRole) {
                if (args.length < 3) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Info")
                            .setColor(Color.cyan)
                            .setDescription("**Usage:** " + Utils.formatCode(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "warn <user> <reason>"));
                    event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                } else {
                    Member member = event.getGuild().getMemberById(args[1].replace("<@!", "").replace(">", ""));
                    if (member != null) {
                        StringBuilder reason = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            //append reason
                            reason.append(args[i]).append(" ");
                        }
                        //call warn method
                        try {
                            SomeDiscordBot.instance.moderation.warn(event.getGuild(), member, event.getMember(), reason.toString().trim());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //command to set and view server rules
            else if (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "rules")) {
                if (args.length < 2) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Info")
                            .setColor(Color.cyan)
                            .setDescription("**Usage:** " + Utils.formatCode(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "rules <add/remove/view> <rule>"));
                    event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                }
                //add rule
                else if (args[1].equalsIgnoreCase("add") && isSudoersRole) {
                    if (args.length < 3) {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Info")
                                .setColor(Color.cyan)
                                .setDescription("**Usage:** " + Utils.formatCode(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "rules add <rule>"));
                        event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                    }
                    //add rule to server rules
                    else {
                        StringBuilder rule = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            //append rule
                            rule.append(args[i]).append(" ");
                        }
                        //add rule to server rules
                        SomeDiscordBot.instance.configs.serverRules.get(event.getGuild().getId()).add(rule.toString().trim());
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Success! :white_check_mark:")
                                .setColor(Color.green)
                                .setDescription("Added rule: " + Utils.formatCode(rule.toString().trim()));
                        event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                        //save to file
                        try {
                            SomeDiscordBot.instance.configs.saveToFile(8);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //remove rule by index
                else if (args[1].equalsIgnoreCase("remove") && isSudoersRole) {
                    if (args.length < 3) {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Info")
                                .setColor(Color.cyan)
                                .setDescription("**Usage:** " + Utils.formatCode(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "rules remove <rule>"));
                        event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                    }
                    //remove rule from server rules
                    else {
                        try {
                            String rule = SomeDiscordBot.instance.configs.serverRules.get(event.getGuild().getId()).get(Integer.parseInt(args[2]) - 1);
                            SomeDiscordBot.instance.configs.serverRules.get(event.getGuild().getId()).remove(Integer.parseInt(args[2]) - 1);
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle("Success! :white_check_mark:")
                                    .setColor(Color.green)
                                    .setDescription("Removed rule: " + Utils.formatCode(rule));
                            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                            //save to file
                            try {
                                SomeDiscordBot.instance.configs.saveToFile(8);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (NumberFormatException | IndexOutOfBoundsException e) {
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle("Error! :x:")
                                    .setColor(Color.green)
                                    .setDescription("You need to specify a valid rule number!")
                                    .appendDescription("\n\n Click for Exception Message: \n" + Utils.formatSpoiler(Utils.formatBlockCode(e.getMessage())));
                            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                        }
                    }
                }
                //view server rules
                else if (args[1].equalsIgnoreCase("view")) {
                    StringBuilder rules = new StringBuilder();
                    for (int i = 0; i < SomeDiscordBot.instance.configs.serverRules.get(event.getGuild().getId()).size(); i++) {
                        rules.append(i + 1).append(". ").append(SomeDiscordBot.instance.configs.serverRules.get(event.getGuild().getId()).get(i)).append("\n");
                    }
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Rules for server '" + event.getGuild().getName() + "':")
                            .setColor(Color.cyan)
                            .setImage(event.getGuild().getIconUrl())
                            .setDescription(rules.toString());
                    event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                }
                //if they are not sudoers, and they try to add/remove rules, tell them they are not sudoers
                else if (!isSudoersRole && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Error! :x:")
                            .setColor(Color.red)
                            .setDescription("You do not have the <@&\" + SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId()) + \"> role. Please contact them instead.");
                    event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                }
            }
            //status command
            else if (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "status")) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Modules Status:")
                        .setColor(Color.cyan)
                        .setDescription("Journaling: " + (SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) ? ":green_circle:" : ":red_circle:") + "\n"
                                + "Social credit: " + (SomeDiscordBot.instance.configs.socialCreditStatus.get(event.getGuild().getId()) ? ":green_circle:" : ":red_circle:"));
                event.getMessage().replyEmbeds(embedBuilder.build()).queue();
            }

            //timeout status command
            else if (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "timeouts")) {
                Map<String, Long> timeouts = SomeDiscordBot.instance.journal.timeoutMap.get(event.getGuild().getId());
                StringBuilder sb = new StringBuilder();
                if (timeouts != null && !timeouts.isEmpty()) {
                    for (Map.Entry<String, Long> entry : timeouts.entrySet()) {
                        sb
                                .append("\n ")
                                .append(Objects.requireNonNull(event.getGuild().getMemberById(entry.getKey())).getAsMention())
                                .append(": ")
                                .append(Utils.formatTime(entry.getValue()))
                                .append(" left");
                    }
                    event.getMessage().reply(sb.toString()).queue();
                } else {
                    sb.append("\n    No current ongoing timeouts!");
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Timeout status for server '" + event.getGuild().getName() + "':")
                        .setColor(Color.cyan)
                        .setDescription(sb.toString());
                event.getMessage().replyEmbeds(embedBuilder.build()).queue();
            }

            //non dependent command
            else if (args[0].equalsIgnoreCase("sdb|prefix") | (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "prefix"))) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Prefix for '" + event.getGuild().getName() + "':")
                        .setColor(Color.cyan)
                        .setDescription("The prefix for this guild is: " + Utils.formatCode(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId())));
                event.getMessage().replyEmbeds(embedBuilder.build()).queue();
            }

            //non dependent command
            else if (args[0].equalsIgnoreCase("sdb|getconfig") | (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "getconfig"))) {
                if (event.getAuthor().getId().equals("600496278857842698")) {
                    event.getMessage().reply("Current Configuration Files (command can only be invoked by <@600496278857842698>):")
                            .addFile(SomeDiscordBot.instance.configs.getPrefixesPath())
                            .addFile(SomeDiscordBot.instance.configs.getJournalChannelsPath())
                            .addFile(SomeDiscordBot.instance.configs.getSudoersRankIDsPath())
                            .addFile(SomeDiscordBot.instance.configs.getMemberWarnsPath())
                            .addFile(SomeDiscordBot.instance.configs.getJournalStatusPath())
                            .addFile(SomeDiscordBot.instance.configs.getSocialCreditStatusPath())
                            .addFile(SomeDiscordBot.instance.configs.getSocialCreditsPath())
                            .addFile(SomeDiscordBot.instance.configs.getServerRulesPath())
                            .queue();
                }
            } else if (args[0].equalsIgnoreCase("sdb|wiki") | (event.isFromGuild() && args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "wiki"))) {
                if (args.length > 1) {
                    Wiki wiki = new Wiki.Builder().build();
                    StringBuilder query = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        query.append(args[i]).append("_");
                    }
                    if (wiki.exists(query.toString())) {
                        event.getMessage().reply(wiki.getTextExtract(query.toString())).queue();
                    } else {
                        event.getMessage().reply("The page you requested does not exist.").queue();
                    }
                }
            }
        }
    }
}
