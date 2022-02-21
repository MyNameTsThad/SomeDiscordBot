package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
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
                        //command to add a role to author
                        //[prefix]rolebruteforce @role
                        if (args.length > 2) {
                            if (args[2].startsWith("<@&")) {
                                String roleId = args[2].substring(3, args[2].length() - 1);
                                //check if role exists
                                if (event.getGuild().getRoleById(roleId) != null) {
                                    //add role to author
                                    try {
                                        event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(roleId))).queue();
                                    } catch (HierarchyException e) {
                                        event.getMessage().reply("I can't add that role to you, I don't have the permissions to do that.").queue();
                                    }
                                }
                                //if role doesn't exist, reply with error
                                else {
                                    event.getMessage().reply("Role with ID " + roleId + " doesn't exist.").queue();
                                }
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
                    } else if (args[1].equalsIgnoreCase("sudoers")) {
                        //command to add/remove sudoers
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("add")) {
                                if (args.length > 3) {
                                    //dm the user with a confirmation prompt
                                    //send a message directly to the user
                                    //if the user confirms, add to sudoers
                                    //if the user denies, do nothing
                                    String userID = args[3].substring(2, args[3].length() - 1);
                                    event.getAuthor().openPrivateChannel().queue(privateChannel ->
                                            privateChannel.sendMessage("Are you sure you want to add " + Objects.requireNonNull(event.getGuild().getMemberById(userID)).getAsMention() + " to the sudoers list?\n" + event.getAuthor().getAsMention()).queue(message -> {
                                                //if the author reacts with the correct emoji, remove from sudoers
                                                message.addReaction("✅").queue();
                                                message.addReaction("❌").queue();
                                                //check if the author reacted with the correct emoji
                                                message.getReactions().forEach(reaction ->
                                                        reaction.retrieveUsers().queue(users -> {
                                                            if (users.contains(event.getAuthor())) {
                                                                if (reaction.getReactionEmote().getName().equalsIgnoreCase("✅")) {
                                                                    //if user exists, add to sudoers
                                                                    event.getGuild().addRoleToMember(Objects.requireNonNull(event.getGuild().getMemberById(userID)),
                                                                            Objects.requireNonNull(event.getGuild().getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId())))).queue();
                                                                    //dm the member that they were added to sudoers
                                                                    Objects.requireNonNull(event.getGuild().getMemberById(userID)).getUser().openPrivateChannel().queue(privateChannel1 ->
                                                                            privateChannel1.sendMessage("You have been added to the sudoers list on '" + event.getGuild().getName() + "'.").queue());
                                                                }
                                                            }
                                                        }));
                                            }));
                                }
                            } else if (args[2].equalsIgnoreCase("remove")) {
                                if (args.length > 3) {
                                    //dm the user with a confirmation prompt
                                    //send a message directly to the user
                                    //if the user confirms, remove from sudoers
                                    //if the user denies, do nothing
                                    String userID = args[3].substring(2, args[3].length() - 1);
                                    event.getAuthor().openPrivateChannel().queue(privateChannel ->
                                            privateChannel.sendMessage("Are you sure you want to remove " + Objects.requireNonNull(event.getGuild().getMemberById(userID)).getAsMention() + " from the sudoers list?\n" + event.getAuthor().getAsMention()).queue(message -> {
                                                //if the author reacts with the correct emoji, remove from sudoers
                                                message.addReaction("✅").queue();
                                                message.addReaction("❌").queue();
                                                //check if the author reacted with the correct emoji
                                                message.getReactions().forEach(reaction ->
                                                        reaction.retrieveUsers().queue(users -> {
                                                            if (users.contains(event.getAuthor())) {
                                                                if (reaction.getReactionEmote().getName().equalsIgnoreCase("✅")) {

                                                                    //if user exists, remove from sudoers
                                                                    event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getGuild().getMemberById(userID)),
                                                                            Objects.requireNonNull(event.getGuild().getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId())))).queue();
                                                                    //dm the member that they were removed from sudoers
                                                                    Objects.requireNonNull(event.getGuild().getMemberById(userID)).getUser().openPrivateChannel().queue(privateChannel1 ->
                                                                            privateChannel1.sendMessage("You have been removed from the sudoers list on '" + event.getGuild().getName() + "'.").queue());
                                                                }
                                                            }
                                                        }));
                                            }));
                                }
                            }
                        }
                    } else {
                        event.getMessage().reply("Invalid subcommand. Refer to `" + SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "help` for Commands and Subcommands.").queue();
                    }
                } else {
                    event.getMessage().reply("No Subcommand detected! Provide a subcommand!").queue();
                }
            } else if (args[0].equalsIgnoreCase("sdb|init") | args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "init")) {
                if (!event.getGuild().getRolesByName("sudoers", false).isEmpty()) {
                    if (!event.getGuild().getRolesByName("sudoers", false).get(0).getPermissions().contains(Permission.ADMINISTRATOR)) {
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
            else if (args[0].equalsIgnoreCase(SomeDiscordBot.instance.configs.prefixes.get(event.getGuild().getId()) + "rules")) {
                if (args.length < 2) {
                    event.getMessage().reply("""
                            Usage: `[prefix] rules <add/remove/view> <rule>`
                            Example: `[prefix] rules add "No spamming"`""").queue();
                }
                //add rule
                else if (args[1].equalsIgnoreCase("add") && isSudoersRole) {
                    if (args.length < 3) {
                        event.getMessage().reply("""
                                Usage: `[prefix] rules add <rule>`
                                Example: `[prefix] rules add "No spamming"`""").queue();
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
                        event.getMessage().reply("""
                                Usage: `[prefix] rules remove <rule number>`
                                Example: `[prefix] rules remove 1`""").queue();

                    }
                    //remove rule from server rules
                    else {
                        try {
                            SomeDiscordBot.instance.configs.serverRules.get(event.getGuild().getId()).remove(Integer.parseInt(args[2]) - 1);
                            //save to file
                            try {
                                SomeDiscordBot.instance.configs.saveToFile(8);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (NumberFormatException | IndexOutOfBoundsException e) {
                            event.getMessage().reply("""
                                    Usage: `[prefix] rules remove <rule number>`
                                    Example: `[prefix] rules remove 1`""").queue();
                        }
                    }
                }
                //view server rules
                else if (args[1].equalsIgnoreCase("view")) {
                    StringBuilder rules = new StringBuilder();
                    for (int i = 0; i < SomeDiscordBot.instance.configs.serverRules.get(event.getGuild().getId()).size(); i++) {
                        rules.append(i + 1).append(". ").append(SomeDiscordBot.instance.configs.serverRules.get(event.getGuild().getId()).get(i)).append("\n");
                    }
                    event.getMessage().reply("Rules:\n" + rules).queue();
                }
                //if they are not sudoers and they try to add/remove rules, tell them they are not sudoers
                else if (!isSudoersRole && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                    event.getMessage().reply("You do not have the <@&" + SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId()) + "> role. Please contact them instead.").queue();
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
