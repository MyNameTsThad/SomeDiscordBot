package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Commands extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot() && event.isFromGuild()) {
            //init check
            if (SomeDiscordBot.instance.configs.prefixes == null) SomeDiscordBot.instance.configs.prefixes = new HashMap<>();
            if (SomeDiscordBot.instance.configs.prefixes.putIfAbsent(event.getGuild().getId(), "sdb|") == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.journalChannels == null) SomeDiscordBot.instance.configs.journalChannels = new HashMap<>();
            if (SomeDiscordBot.instance.configs.journalChannels.putIfAbsent(event.getGuild().getId(), "") == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (SomeDiscordBot.instance.configs.sudoersRankIDs == null) SomeDiscordBot.instance.configs.sudoersRankIDs = new HashMap<>();
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

            //main stuff
            String[] args = event.getMessage().getContentRaw().split(" ");

            List<String> roleIds = new ArrayList<>();
            Objects.requireNonNull(event.getMember()).getRoles().forEach(role -> roleIds.add(role.getId()));
            boolean isSudoersRole = roleIds.contains(SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId()));

            if (args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "ver")) {
                event.getMessage().reply(
                        SomeDiscordBot.NAME + " version " + SomeDiscordBot.VERSION +
                                "\n" + "(" + SomeDiscordBot.SHORTNAME + ":" + SomeDiscordBot.VERSION_ID + ")" +
                                "\n" + "Bot made by <@600496278857842698>." +
                                "\n\n" + "Use `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "help` for Commands List.").queue();
            } else if (args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "botctl") && isSudoersRole) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("prefix")) {
                        if (args.length > 2) {
                            SomeDiscordBot.instance.configs.prefixes.replace(event.getGuild().getId(), args[2].toLowerCase(Locale.ROOT) + "|");
                            try {
                                SomeDiscordBot.instance.configs.saveToFile(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            event.getMessage().reply("This guild's bot prefix is now set to `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "`").queue();
                        } else {
                            event.getMessage().reply("The prefix for this guild is: `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "`").queue();
                        }
                    } else if (args[1].equalsIgnoreCase("rolebruteforce")) {
                        if (args.length > 2) {
                            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(args[2]))).queue();
                            event.getMessage().reply("Added `" + Objects.requireNonNull(event.getGuild().getRoleById(args[2])).getName() + "` role to <@" + event.getMember().getId() + ">").queue();
                        } else {
                            event.getMessage().reply("No role ID found!").queue();
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
                                    event.getMessage().reply("Channel <#" + channelID + "> is now the journal channel.").queue();
                                } else {
                                    event.getMessage().reply("No channel detected! Provide a channel!").queue();
                                }
                                //command to enable/disable journalling
                            } else if (args[2].equalsIgnoreCase("status")) {
                                //enable journalling
                                if (args.length > 3) {
                                    if (args[3].equalsIgnoreCase("enable")) {
                                        SomeDiscordBot.instance.configs.journalStatus.replace(event.getGuild().getId(), true);
                                    } else if (args[3].equalsIgnoreCase("disable")) {
                                        SomeDiscordBot.instance.configs.journalStatus.replace(event.getGuild().getId(), false);
                                    } else {
                                        event.getMessage().reply("Invalid argument! Use `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "botctl journal status enable` or `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "botctl journal status disable`").queue();
                                    }
                                }
                            } else {
                                event.getMessage().reply("Invalid subcommand. Refer to `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "help` for Commands and Subcommands.").queue();
                            }
                        } else {
                            event.getMessage().reply("No Subcommand detected! Provide a subcommand!").queue();
                        }
                    } else {
                        event.getMessage().reply("Invalid subcommand. Refer to `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "help` for Commands and Subcommands.").queue();
                    }
                } else {
                    event.getMessage().reply("No Subcommand detected! Provide a subcommand!").queue();
                }
            } else if (args[0].equalsIgnoreCase("sdb|init") | args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "init")) {
                if (!event.getGuild().getRolesByName("sudoers", false).isEmpty()) {
                    if (!event.getGuild().getRolesByName("sudoers", false).get(0).getPermissions().contains(Permission.ADMINISTRATOR)){
                        event.getGuild().getRolesByName("sudoers", false).get(0).delete().queue();
                    }
                }
                if (event.getGuild().getRolesByName("sudoers", false).isEmpty() | !SomeDiscordBot.instance.configs.sudoersRankIDs.containsKey(event.getGuild().getId())) {
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
                                            .setPermissions(Permission.ADMINISTRATOR)
                                            .queue(role -> {
                                                response.editMessage("""
                                                        Initializing guild...
                                                        [:white_check_mark:] Created `sudoers` role
                                                        [ ] Adding `sudoers` role to requested user...
                                                        [ ] Adding server-specific configurations...""").queue();
                                                SomeDiscordBot.instance.overrideRoleAddProtection = true;
                                                event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(role.getId())))
                                                        .queue(nextstep1 -> response.editMessage("""
                                                                Initializing guild...
                                                                [:white_check_mark:] Created `sudoers` role
                                                                [:white_check_mark:] Added `sudoers` role to requested user
                                                                [ ] Adding server-specific configurations...""").queue(nextstep2 -> {
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
                                                            response.editMessage("""
                                                                    Guild initialized.
                                                                    [:white_check_mark:] Created `sudoers` role
                                                                    [:white_check_mark:] Added `sudoers` role to requested user
                                                                    [:white_check_mark:] Added server-specific configurations""").queue();
                                                        }));
                                                SomeDiscordBot.instance.configs.sudoersRankIDs.put(event.getGuild().getId(), role.getId());
                                                try {
                                                    SomeDiscordBot.instance.configs.saveToFile(3);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            });
                                } else {
                                    response.editMessage("""
                                            Initializing guild...
                                            [:white_check_mark:] Created `sudoers` role
                                            [ ] Adding `sudoers` role to requested user...
                                            [ ] Adding server-specific configurations...""")
                                            .queue();
                                    event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId()))))
                                            .queue(nextstep -> response.editMessage("""
                                                                Initializing guild...
                                                                [:white_check_mark:] Created `sudoers` role
                                                                [:white_check_mark:] Added `sudoers` role to requested user
                                                                [ ] Adding server-specific configurations...""").queue(nextstep2 -> {
                                                if (!SomeDiscordBot.instance.configs.prefixes.containsKey(event.getGuild().getId())) {
                                                    SomeDiscordBot.instance.configs.prefixes.put(event.getGuild().getId(), "sdb|");
                                                    SomeDiscordBot.instance.configs.journalChannels.put(event.getGuild().getId(), "");
                                                    SomeDiscordBot.instance.configs.journalStatus.put(event.getGuild().getId(), true);
                                                    SomeDiscordBot.instance.configs.socialCreditStatus.put(event.getGuild().getId(), true);
                                                    SomeDiscordBot.instance.configs.socialCredits.put(event.getGuild().getId(), new HashMap<>());
                                                    try {
                                                        SomeDiscordBot.instance.configs.saveToFile(1);
                                                        SomeDiscordBot.instance.configs.saveToFile(2);
                                                        SomeDiscordBot.instance.configs.saveToFile(3);
                                                        SomeDiscordBot.instance.configs.saveToFile(4);
                                                        SomeDiscordBot.instance.configs.saveToFile(5);
                                                        SomeDiscordBot.instance.configs.saveToFile(6);
                                                        SomeDiscordBot.instance.configs.saveToFile(7);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                response.editMessage("""
                                                                    Guild initialized.
                                                                    [:white_check_mark:] Created `sudoers` role
                                                                    [:white_check_mark:] Added `sudoers` role to requested user
                                                                    [:white_check_mark:] Added server-specific configurations""").queue();
                                            }));
                                }
                            });
                } else {
                    event.getMessage().reply("""
                            Initializing guild...
                            [:x:] Failed to create `sudoers` role - Role already exists! Please delete the role and try again.
                            [ ] Adding `sudoers` role to requested user...
                            [ ] Adding server-specific configurations...""")
                            .queue(response ->
                                    event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId()))))
                                    .queue(nextstep -> response.editMessage("""
                                                            Initializing guild...
                                                            [:white_check_mark:] Created `sudoers` role
                                                            [:white_check_mark:] Added `sudoers` role to requested user
                                                            [ ] Adding server-specific configurations...""").queue(nextstep2 -> {
                                        if (!SomeDiscordBot.instance.configs.prefixes.containsKey(event.getGuild().getId())) {
                                            SomeDiscordBot.instance.configs.prefixes.put(event.getGuild().getId(), "sdb|");
                                            SomeDiscordBot.instance.configs.journalChannels.put(event.getGuild().getId(), "");
                                            SomeDiscordBot.instance.configs.journalStatus.put(event.getGuild().getId(), true);
                                            SomeDiscordBot.instance.configs.socialCreditStatus.put(event.getGuild().getId(), true);
                                            SomeDiscordBot.instance.configs.socialCredits.put(event.getGuild().getId(), new HashMap<>());
                                            try {
                                                SomeDiscordBot.instance.configs.saveToFile(1);
                                                SomeDiscordBot.instance.configs.saveToFile(2);
                                                SomeDiscordBot.instance.configs.saveToFile(3);
                                                SomeDiscordBot.instance.configs.saveToFile(4);
                                                SomeDiscordBot.instance.configs.saveToFile(5);
                                                SomeDiscordBot.instance.configs.saveToFile(6);
                                                SomeDiscordBot.instance.configs.saveToFile(7);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        response.editMessage("""
                                                Guild initialized.
                                                [:white_check_mark:] Created `sudoers` role
                                                [:white_check_mark:] Added `sudoers` role to requested user
                                                [:white_check_mark:] Added server-specific configurations""").queue();
                                    })));
                }
            }
            //command to warn member for a specified reason
            else if (args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "warn") && isSudoersRole) {
                if (args.length < 3) {
                    event.getMessage().reply("""
                            Usage: `[prefix] warn <user> <reason>`
                            Example: `[prefix] warn @user#1234 "This is a warning"`""").queue();
                } else {
                    Member member = event.getGuild().getMemberById(args[1].replace("<@", "").replace(">", ""));
                    if (member != null) {
                        StringBuilder reason = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            //append reason
                            reason.append(args[i]).append(" ");
                        }
                        //call warn method
                        SomeDiscordBot.instance.moderation.warn(event.getGuild(), member, event.getMember(), reason.toString());
                    }
                }
            }
            //status command
            else if (args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "status")) {
                event.getMessage().reply("Modules status: \n"
                        + "    Journaling: " + (SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) ? ":green_circle:" : ":red_circle:") + "\n"
                        + "    Social credit: " + (SomeDiscordBot.instance.configs.socialCreditStatus.get(event.getGuild().getId()) ? ":green_circle:" : ":red_circle:")).queue();
            }

            //non dependent command
            else if (args[0].equalsIgnoreCase("sdb|prefix") | args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "prefix")) {
                event.getMessage().reply("The prefix for this guild is: `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "`").queue();
            }
        }
    }
}
