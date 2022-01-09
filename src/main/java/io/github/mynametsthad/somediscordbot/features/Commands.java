package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.Configs;
import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Commands extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.isFromGuild()) {
            String[] args = event.getMessage().getContentRaw().split(" ");

            List<String> roleIds = new ArrayList<>();
            Objects.requireNonNull(event.getMember()).getRoles().forEach(role -> roleIds.add(role.getId()));
            boolean isSudoersRole = roleIds.contains(Configs.sudoersRankIDs.get(event.getGuild().getId()));

            if (args[0].equalsIgnoreCase(Configs.prefixes.get(event.getGuild().getId()) + "ver")) {
                event.getMessage().reply(
                        SomeDiscordBot.NAME + " version " + SomeDiscordBot.VERSION +
                                "\n" + "(" + SomeDiscordBot.SHORTNAME + ":" + SomeDiscordBot.VERSION_ID + ")" +
                                "\n" + "Bot made by <@600496278857842698>." +
                                "\n\n" + "Use `" + Configs.prefixes.get(event.getGuild().getId()) + "help` for Commands List.").queue();
            } else if (args[0].equalsIgnoreCase(Configs.prefixes.get(event.getGuild().getId()) + "botctl") && isSudoersRole) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("prefix")) {
                        if (args.length > 2) {
                            Configs.prefixes.replace(event.getGuild().getId(), args[2].toLowerCase(Locale.ROOT) + "|");
                            event.getMessage().reply("This guild's bot prefix is now set to `" + Configs.prefixes.get(event.getGuild().getId()) + "`").queue();
                        } else {
                            event.getMessage().reply("The prefix for this guild is: `" + Configs.prefixes.get(event.getGuild().getId()) + "`").queue();
                        }
                    } else if (args[1].equalsIgnoreCase("journal")) {
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("setchannel")) {
                                if (args.length > 3) {
                                    String channelID = args[3].substring(2, args[3].length() - 1);
                                    Configs.journalChannels.replace(event.getGuild().getId(), channelID);
                                    System.out.println("channelID: " + channelID);
                                    event.getMessage().reply("Channel <#" + channelID + "> is now the journal channel.").queue();
                                } else {
                                    event.getMessage().reply("No channel detected! Provide a channel!").queue();
                                }
                            } else {
                                event.getMessage().reply("Invalid subcommand. Refer to `" + Configs.prefixes.get(event.getGuild().getId()) + "help` for Commands and Subcommands.").queue();
                            }
                        } else {
                            event.getMessage().reply("No Subcommand detected! Provide a subcommand!").queue();
                        }
                    } else {
                        event.getMessage().reply("Invalid subcommand. Refer to `" + Configs.prefixes.get(event.getGuild().getId()) + "help` for Commands and Subcommands.").queue();
                    }
                } else {
                    event.getMessage().reply("No Subcommand detected! Provide a subcommand!").queue();
                }
            } else if (args[0].equalsIgnoreCase("sdb|init") | args[0].equalsIgnoreCase(Configs.prefixes.get(event.getGuild().getId()) + "init")) {
                if (event.getGuild().getRolesByName("sudoers", false).isEmpty() | !Configs.prefixes.containsKey(event.getGuild().getId())) {
                    event.getMessage().reply(
                                    """
                                            Initializing guild...
                                            [ ] Creating `sudoers` role...
                                            [ ] Adding `sudoers` role to requested user...
                                            [ ] Adding server-specific configurations...""")
                            .queue(response -> {
                                if (event.getGuild().getRolesByName("sudoers", false).isEmpty()) {
                                    event.getGuild().createRole()
                                            .setName("sudoers")
                                            .setColor(Color.RED)
                                            .queue(role -> {
                                                response.editMessage("""
                                                        Initializing guild...
                                                        [:white_check_mark:] Created `sudoers` role
                                                        [ ] Adding `sudoers` role to requested user...
                                                        [ ] Adding server-specific configurations...""").queue();
                                                event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(role.getId())))
                                                        .queue(nextstep1 -> response.editMessage("""
                                                                Initializing guild...
                                                                [:white_check_mark:] Created `sudoers` role
                                                                [:white_check_mark:] Added `sudoers` role to requested user
                                                                [ ] Adding server-specific configurations...""").queue(nextstep2 -> {
                                                            if (!Configs.prefixes.containsKey(event.getGuild().getId())) {
                                                                Configs.prefixes.put(event.getGuild().getId(), "sdb|");
                                                                Configs.journalChannels.put(event.getGuild().getId(), "");
                                                            }
                                                            response.editMessage("""
                                                                    Guild initialized.
                                                                    [:white_check_mark:] Created `sudoers` role
                                                                    [:white_check_mark:] Added `sudoers` role to requested user
                                                                    [:white_check_mark:] Added server-specific configurations""").queue();
                                                        }));
                                                Configs.sudoersRankIDs.put(event.getGuild().getId(), role.getId());
                                            });
                                } else {
                                    response.editMessage("""
                                            Initializing guild...
                                            [:white_check_mark:] Created `sudoers` role
                                            [ ] Adding `sudoers` role to requested user...
                                            [ ] Adding server-specific configurations...""").queue();
                                }
                            });
                } else {
                    event.getMessage().reply("""
                            Failed to initialize guild.
                            [:x:] Failed to create `sudoers` role - Role already exists! Please delete the role and try again.
                            [ ] Adding `sudoers` role to requested user...
                            [ ] Adding server-specific configurations...""").queue();
                }
            }

            //non dependent command
            else if (args[0].equalsIgnoreCase("sdb|prefix") | args[0].equalsIgnoreCase(Configs.prefixes.get(event.getGuild().getId()) + "prefix")) {
                event.getMessage().reply("The prefix for this guild is: `" + Configs.prefixes.get(event.getGuild().getId()) + "`").queue();
            }
        }
    }

    /*@Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equals("ver")){
            event.reply(SomeDiscordBot.NAME + " version " + SomeDiscordBot.VERSION +
                    "\n" + "(" + SomeDiscordBot.SHORTNAME + ":" + SomeDiscordBot.VERSION_ID + ")")
                    .setEphemeral(true).queue();
        }
    }*/
}
