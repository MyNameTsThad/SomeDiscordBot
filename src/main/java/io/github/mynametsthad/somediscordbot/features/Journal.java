package io.github.mynametsthad.somediscordbot.features;

import io.github.mynametsthad.somediscordbot.SomeDiscordBot;
import net.dv8tion.jda.api.entities.Guild;
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
import java.util.Map;
import java.util.Objects;

public class Journal extends ListenerAdapter {
    public byte aggressiveness = 1;

    private boolean roleLockOverride = false;
    private boolean deleteLockOverride = false;

    private List<Message> last50MessagesInJournalChannel = new ArrayList<>(50);

    public Map<String, String[]> addConfirmationMap = new HashMap<>();
    public Map<String, String[]> removeConfirmationMap = new HashMap<>();

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.isFromGuild() && SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) != null && SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId())) {
            if (event.getChannel().getId().equals(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId()))) {
                if (!event.getAuthor().isBot()) {
                    String authorID = event.getAuthor().getId();
                    deleteLockOverride = true;
                    event.getMessage().delete().queue(delete -> {
                        event.getChannel().sendMessage("<@" + authorID + ">, you are not allowed to send Messages in this channel.").queue();
                    });
                } else {
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
        } else if (event.isFromGuild() && SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) == null) {
            SomeDiscordBot.instance.configs.journalStatus = new HashMap<>();
            if (SomeDiscordBot.instance.configs.journalStatus.putIfAbsent(event.getGuild().getId(), true) == null) {
                try {
                    SomeDiscordBot.instance.configs.saveToFile(5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //troll
        if (event.getGuild().getId().equals("915071717901238304")) {
            if (event.getAuthor().getId().equals("829231018191749120")) {
                //if message contains more than 2 emotes
                if (event.getMessage().getEmotes().size() >= 1) {
                    //react the message with "cringe" emojis
                    event.getMessage().addReaction("\uD83C\uDDEA").queue();
                    event.getMessage().addReaction("\uD83C\uDDF2").queue();
                    event.getMessage().addReaction("\uD83C\uDDF4").queue();
                    event.getMessage().addReaction("\uD83C\uDDEF").queue();
                    event.getMessage().addReaction("\uD83C\uDDEE").queue();
                    event.getMessage().addReaction("\uD83C\uDDF8").queue();
                    event.getMessage().addReaction("\uD83E\uDD21").queue();
                    event.getMessage().addReaction("\uD83D\uDCF8").queue();
                }
                System.out.println("cringe");
            }
        }

        //lol idk what to put here
        if (event.isFromGuild() && !event.getAuthor().isBot()) {
            String message = event.getMessage().getContentRaw();
            if (message.toLowerCase().contains("america")
                    | message.toLowerCase().contains("united states")
                    | message.toLowerCase().contains("อเมริกา") | message.toLowerCase().contains("อเมริกัน")) {
                event.getMessage().reply("""
                        I'm a regular John from city Kansas. I love burgers, soda and my native country very much,
                        but I do not understand our government. Everyone says America is a great country, and I look
                        around and see who else is a great China. China has a very strong government and economy.
                        Chinese resident is a great man. And the greatest leader Xi. Thick hair, strong grip, jade rod!
                        We would have such a leader instead of sleeping in negotiations, rare hair, soft pickle, bad
                        memory old Beadon. Punch!""").queue();
            } else if (message.contains("biden")) {
                event.getMessage().reply("""
                        Joe Biden’s America
                                                 
                        LIBRAL SCOOL BE LIKE:
                                                 
                        9:00: GAY LESON!!
                                                 
                        9:45: how to be be GAYY!!
                                                 
                        10:30: TRANS LERNINNG!!
                                                 
                        11:15: GAY RECESS!!
                                                 
                        11:45: CROSDRESING HOUR!!
                                                 
                        12:45: GAY LESON!!!
                                                 
                        1:30: TRANGENER LUNCH!!
                                                 
                        2:15: BLM PERIOD!!!
                                                 
                        3:00: COMUNIS T HISTORY!!
                                                 
                        3:30: TAKE NON BINAR BUS HOME!!
                                                 
                        THIS IS WHAT THE LEFT WANT!""").queue();
            } else if (message.toLowerCase().contains("bitches") | message.toLowerCase().contains("กะหรี่")) {
                event.getMessage().reply("""
                        No bitches?
                        ```
                        ⣞⢽⢪⢣⢣⢣⢫⡺⡵⣝⡮⣗⢷⢽⢽⢽⣮⡷⡽⣜⣜⢮⢺⣜⢷⢽⢝⡽⣝
                        ⠸⡸⠜⠕⠕⠁⢁⢇⢏⢽⢺⣪⡳⡝⣎⣏⢯⢞⡿⣟⣷⣳⢯⡷⣽⢽⢯⣳⣫⠇
                        ⠀⠀⢀⢀⢄⢬⢪⡪⡎⣆⡈⠚⠜⠕⠇⠗⠝⢕⢯⢫⣞⣯⣿⣻⡽⣏⢗⣗⠏⠀\s
                         ⠀⠪⡪⡪⣪⢪⢺⢸⢢⢓⢆⢤⢀⠀⠀⠀⠀⠈⢊⢞⡾⣿⡯⣏⢮⠷⠁⠀⠀ ⠀
                         ⠀⠀⠈⠊⠆⡃⠕⢕⢇⢇⢇⢇⢇⢏⢎⢎⢆⢄⠀⢑⣽⣿⢝⠲⠉⠀⠀⠀⠀ ⠀⠀
                          ⠀⠀⠀⡿⠂⠠⠀⡇⢇⠕⢈⣀⠀⠁⠡⠣⡣⡫⣂⣿⠯⢪⠰⠂⠀⠀⠀⠀ ⠀⠀⠀
                            ⠀⡦⡙⡂⢀⢤⢣⠣⡈⣾⡃⠠⠄⠀⡄⢱⣌⣶⢏⢊⠂⠀⠀⠀⠀⠀⠀ ⠀⠀⠀⠀
                              ⢝⡲⣜⡮⡏⢎⢌⢂⠙⠢⠐⢀⢘⢵⣽⣿⡿⠁⠁⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⠀⠀
                              ⠨⣺⡺⡕⡕⡱⡑⡆⡕⡅⡕⡜⡼⢽⡻⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⠀⠀
                              ⣼⣳⣫⣾⣵⣗⡵⡱⡡⢣⢑⢕⢜⢕⡝⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⠀
                            ⣴⣿⣾⣿⣿⣿⡿⡽⡑⢌⠪⡢⡣⣣⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⠀
                            ⡟⡾⣿⢿⢿⢵⣽⣾⣼⣘⢸⢸⣞⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⠀
                            ⠀⠁⠇⠡⠩⡫⢿⣝⡻⡮⣒⢽⠋⠀⠀⠀⠀⠀
                        ```""").queue();
            } else if (message.toLowerCase().contains("source") | message.toLowerCase().contains("proof")) {
                event.getMessage().reply("""
                                Do you have a source on that?
                                                        
                                Source?
                                                        
                                A source. I need a source.
                                                        
                                Sorry, I mean I need a source that explicitly states your argument. This is just tangential to the discussion.
                                                        
                                No, you can't make inferences and observations from the sources you've gathered. Any additional comments from you MUST be a subset of the information from the sources you've gathered.
                                                        
                                You can't make normative statements from empirical evidence.
                                                        
                                Do you have a degree in that field?
                                                        
                                A college degree? In that field?
                                                        
                                Then your arguments are invalid.
                                                        
                                No, it doesn't matter how close those data points are correlated. Correlation does not equal causation.
                                                        
                                Correlation does not equal causation.
                                                        
                                CORRELATION. DOES. NOT. EQUAL. CAUSATION.
                                                        
                                You still haven't provided me a valid source yet.
                                                        
                                Nope, still haven't.
                                                        
                                I just looked through all 308 pages of your user history, figures I'm debating a glormpf supporter. A moron.""")
                        .queue();
            } else if (message.toLowerCase().contains("ww3")) {
                event.getMessage().reply("""
                                Russia vs Ukraine is just an Attack on Titan (Shingeki no Kyojin) allegory. Ukraine has been
                                pushed back (some may caged (like a bird?)) by the Russian (Titans). Not only does Russia have
                                soldiers (normal titans), they have armored tanks (the armored titan) and nukes (the colossal
                                titan(pre-episode 55, Midnight Sun (9.9/10 on IMDB) this will be important later) Beroltolt
                                 Hoober). Bertie is Russian. Now here's the scary thing. There are Russians within the walls
                                (Ukraine border) that pledge their allegiance to none other than Russia (just like the "Eldians"
                                that "came" from Marley). And of course the one leading the charge is Putin, or should I say
                                Zeke, son of monkey, Yeager. And just like the monkey himself, Putin sneaks into territories,
                                converts people to Russian, and leaves. Horrifying I know. But what if I told you it gets worse?
                                What if I told you the tanks along the border are actually the wall. Or more specifically the colossal titans within the
                                wall. Putin will talk and talk and scream (like monkey) but all he wants is
                                to youthenize (to make young) the Ukrainians. Luckily Eren, other son of monkey, Yeager wants
                                the Ukraine to stay old. So Donald Trump (Eren) decided to get close with Putin and have him
                                come to Mar-a-Largo (Paths). Putin accepted expecting to be able to use some of Trump's eternal
                                youth, but Trump had a trump card and an ulterior motive. Of course before Trump acted on his
                                plan, he gathered all Ukrainese people in paths and told them how great he was. Ultimately,
                                Trump wanted Russia to attack Ukraine so he used his power to put the walls in motion
                                (Rumbling?) by having the tanks move into Ukraine (Rumbling). The world looked in disbelief as
                                the tanks began to move, but there was still a hero to save the day; a hope Ukraine; a man that
                                has been around since the dawn of time- Joeseph R Biden. Joeseph R Biden is the one man
                                capable of defeating Trump once his plan was in motion. Joeseph R Biden, who is Armin Artlet
                                (the colossal titan)""")
                        .queue(message1 -> {
                            message1.reply("""
                                    (post-episode 55, Midnight Sun (9.9/10 on IMDB) I said this would be important later)Armin Artlet),
                                    used his nuclear power to stop the Russian troops from rumbling to victory. This was only possible
                                    because the Russian nukes (Bernie) were inside the American nukes (Armin). But more
                                    importantly, Mikasa Akerman (staring Kamala Harris) went in before the rumbling started to cut Putin down to
                                    size. So keeping with the theme of Attack on Titan, War was stopped by Joeseph
                                    R Biden by defeating the Russians, which helped Putin's plan to make Ukraine young again
                                    (MUYA). War will never again plague the people of Eastern Europe and all will be forever young.""").queue();
                        });
            } else if (message.toLowerCase().contains("among us") | message.toLowerCase().contains("amogus") | message.toLowerCase().contains("sus")) {
                event.getMessage().reply("""
                        AMONG US Funny Moments! How to Free Robux and VBUCKS in SQUID GAME
                        FORTNITE UPDATE! (NOT CLICKBAIT) MUKBANG ROBLOX GAMEPLAY TUTORIAL (GONE
                        WRONG) Finger Family Learn Your ABCs at 3AM! Fortnite Impostor Potion! MrBeast
                        free toys halal gameplay nae nae download حدث خطأ في الساعة 3 صباحًا
                        حدث خطأ في الساعة 3 صباحًاحدث خطأ في الساعة 3 صباحًا Super Idol的笑容都没你的甜八月正午的阳光都没
                        你耀眼热爱 105 °C的你滴滴清纯的蒸馏水 amongla download Meme Compilation (POLICE
                        CALLED) (GONE WRONG) (GONE SEXUAL) (NOT CLICKBAIT) Minecraft Series Lets Play
                        Videos Number 481 - Poop Funny Hilarious Minecraft Roblox Fails for Fortnite - How to
                        install halal minecraft cheats hacks 2021 still works (STILL WORKS 2018) Impostor
                        Gameplay (Among Us) Zamn
                        """).queue();
            } else if (message.toLowerCase().contains("nft")) {
                event.getMessage().reply("""
                        Dude I own this NFT. Do you really think that you can get away with theft when you’re
                        showing what you stole from me directly to my face? My lawyer will make an easy job of this
                        case. Prepare to say goodbye to your luscious life and start preparing for the streets. I will ruin you.
                        """).queue();
            } else if (message.toLowerCase().contains("1984")) {
                event.getMessage().reply("""
                        > LiTeRaLlY nInEtEeN eIgHtY-fOuR
                                                
                        * George Orwell, 1948
                        """).queue();
            } else if (message.toLowerCase().contains("ukraine") | message.toLowerCase().contains("ยูเครน")) {
                event.getMessage().reply("**SLAVA UKRAINI! :flag_ua::flag_ua::flag_ua:**").queue();
            } else if (message.toLowerCase().contains("russia") | message.toLowerCase().contains("รัสเซีย")) {
                event.getMessage().reply("imagine having a currency worth less than bobux :dollar: :dollar: :dollar: :money_mouth: :flag_ru: " +
                        ":face_vomiting: :nauseated_face: :nauseated_face: :nauseated_face: :face_vomiting: :face_vomiting: :face_vomiting: :face_vomiting: " +
                        ":face_vomiting: :face_vomiting: :face_vomiting: :face_vomiting: :face_vomiting: :face_vomiting:").queue();
            } else if (message.toLowerCase().contains("concern")) {
                event.getMessage().reply("""
                        ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠿⠛⠛⠛⠛⠿⣿⣿⣿⣿⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⢿⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣿⣿⣿⠋⠈⠀⠀⠀⠀⠐⠺⣖⢄⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣿⣿⡇⡼⠀⠀⠀⠀⠈⠻⣅⣨⠇⠈⠀⠰⣀⣀⣀⡀⠀⢸⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣿⣿⡅⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢿⠀⠈⠓⠚⢸⣿⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠐⠉⠀⠀⠙⠉⠀⠠⡶⣸⠁⠀⣠⣿⣿⣿⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣿⣿⣿⣿⡇⠀⠀⠀⠀⠠⣄⣉⣙⡉⠓⢀⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿
                        ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣤⣀⣀⠀⣀⣠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
                        """).queue();
            } else if (message.toLowerCase().contains("sex") | message.toLowerCase().contains("fuck") |
                    message.toLowerCase().contains("vagina") | message.toLowerCase().contains("dick") |
                    message.toLowerCase().contains("เย็้ด") | message.toLowerCase().contains("หี") |
                    message.toLowerCase().contains("ควย")) {
                event.getMessage().reply("""
                        No sex before marriage
                        """).queue();
            }else if (message.toLowerCase().contains("69420")) {
                event.getMessage().reply("""
                        🤢🤢🤮🤢🤮🤮🤮🤮🤢🤮🤮🤮🤮🤮😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩
                        😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩😩🤢🤮🤢🤮🤢🤮🤢
                        """).queue();
            }else if (message.toLowerCase().contains("fucktion") | message.toLowerCase().contains("thad") | message.toLowerCase().contains("choyrum")) {
                event.getMessage().reply(":regional_indicator_e::regional_indicator_m::regional_indicator_o::regional_indicator_j::regional_indicator_i::regional_indicator_s:" +
                        ":clown::camera_with_flash:").queue();
            }
        }
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
    }

    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        if (event.isFromGuild() && event.getChannel().getId().equals(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId())) && SomeDiscordBot.instance.configs.journalStatus.get(event.getGuild().getId()) && !deleteLockOverride) {
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
        } else if (event.isFromGuild() && deleteLockOverride) {
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
        if (Objects.requireNonNull(event.getUser()).isBot()) return;
        if (!event.isFromGuild()) {
            if (addConfirmationMap.containsKey(event.getUser().getId())) {
                if (addConfirmationMap.get(event.getUser().getId())[0].equals(event.getMessageId())) {
                    event.retrieveMessage().queue(message ->
                            message.getReactions().forEach(reaction -> {
                                //check if the author reacted with the correct emoji
                                reaction.retrieveUsers().queue(users -> {
                                    if (users.contains(event.getUser())) {
                                        if (reaction.getReactionEmote().getName().equalsIgnoreCase("✅")) {
                                            String userID = addConfirmationMap.get(event.getUser().getId())[2];
                                            Guild guild = Objects.requireNonNull(SomeDiscordBot.instance.jda.getGuildById(addConfirmationMap.get(event.getUser().getId())[1]));
                                            //if user exists, add to sudoers
                                            SomeDiscordBot.instance.overrideSudoersRoleProtection = true;
                                            guild.addRoleToMember(Objects.requireNonNull(guild.getMemberById(userID)),
                                                    Objects.requireNonNull(guild.getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(guild.getId())))).queue();
                                            //dm the member that they were added to sudoers
                                            message.getChannel().sendMessage(Objects.requireNonNull(guild.getMemberById(userID)).getAsMention() + " has been added to the sudoers list on '" + guild.getName() + "'.").queue();
                                            Objects.requireNonNull(guild.getMemberById(userID)).getUser().openPrivateChannel().queue(privateChannel1 ->
                                                    privateChannel1.sendMessage("You have been added to the sudoers list on '" + guild.getName() + "'.").queue());
                                            addConfirmationMap.remove(event.getUser().getId(), new String[]{event.getMessageId(), guild.getId()});
                                        }
                                    }
                                });
                            }));
                } else if (removeConfirmationMap.containsKey(event.getUser().getId())) {
                    if (removeConfirmationMap.get(event.getUser().getId())[0].equals(event.getMessageId())) {
                        event.retrieveMessage().queue(message ->
                                message.getReactions().forEach(reaction -> {
                                    //check if the author reacted with the correct emoji
                                    reaction.retrieveUsers().queue(users -> {
                                        if (users.contains(event.getUser())) {
                                            if (reaction.getReactionEmote().getName().equalsIgnoreCase("✅")) {
                                                String userID = removeConfirmationMap.get(event.getUser().getId())[2];
                                                Guild guild = Objects.requireNonNull(SomeDiscordBot.instance.jda.getGuildById(removeConfirmationMap.get(event.getUser().getId())[1]));
                                                //if user exists, remove from sudoers
                                                SomeDiscordBot.instance.overrideSudoersRoleProtection = true;
                                                guild.removeRoleFromMember(Objects.requireNonNull(guild.getMemberById(userID)),
                                                        Objects.requireNonNull(guild.getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(guild.getId())))).queue();
                                                //dm the member that they were removed from sudoers
                                                message.getChannel().sendMessage(Objects.requireNonNull(guild.getMemberById(userID)).getAsMention() + " has been removed from the sudoers list on '" + guild.getName() + "'.").queue();
                                                Objects.requireNonNull(guild.getMemberById(userID)).getUser().openPrivateChannel().queue(privateChannel1 ->
                                                        privateChannel1.sendMessage("You have been removed from the sudoers list on '" + guild.getName() + "'.").queue());
                                                removeConfirmationMap.remove(event.getUser().getId(), new String[]{event.getMessageId(), guild.getId()});
                                            }
                                        }
                                    });
                                }));
                    }
                }
            } else if (removeConfirmationMap.containsKey(event.getUser().getId())) {
                if (removeConfirmationMap.get(event.getUser().getId())[0].equals(event.getMessageId())) {
                    event.retrieveMessage().queue(message ->
                            message.getReactions().forEach(reaction -> {
                                //check if the author reacted with the correct emoji
                                reaction.retrieveUsers().queue(users -> {
                                    if (users.contains(event.getUser())) {
                                        if (reaction.getReactionEmote().getName().equalsIgnoreCase("✅")) {
                                            String userID = removeConfirmationMap.get(event.getUser().getId())[2];
                                            Guild guild = Objects.requireNonNull(SomeDiscordBot.instance.jda.getGuildById(removeConfirmationMap.get(event.getUser().getId())[1]));
                                            //if user exists, remove from sudoers
                                            SomeDiscordBot.instance.overrideSudoersRoleProtection = true;
                                            guild.removeRoleFromMember(Objects.requireNonNull(guild.getMemberById(userID)),
                                                    Objects.requireNonNull(guild.getRoleById(SomeDiscordBot.instance.configs.sudoersRankIDs.get(guild.getId())))).queue();
                                            //dm the member that they were removed from sudoers
                                            message.getChannel().sendMessage(Objects.requireNonNull(guild.getMemberById(userID)).getAsMention() + " has been removed from the sudoers list on '" + guild.getName() + "'.").queue();
                                            Objects.requireNonNull(guild.getMemberById(userID)).getUser().openPrivateChannel().queue(privateChannel1 ->
                                                    privateChannel1.sendMessage("You have been removed from the sudoers list on '" + guild.getName() + "'.").queue());
                                            removeConfirmationMap.remove(event.getUser().getId(), new String[]{event.getMessageId(), guild.getId()});
                                        }
                                    }
                                });
                            }));
                }
            }
        }
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
        if (!SomeDiscordBot.instance.overrideSudoersRoleProtection) {
            for (Role added : event.getRoles()) {
                String roleId = SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId());
                if (added.getId().equals(roleId)) {
                    SomeDiscordBot.instance.overrideSudoersRoleProtection = true;
                    event.getGuild().removeRoleFromMember(event.getMember(), added).queue(success -> {
                        Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId())))
                                .sendMessage("You cannot add the <@&" + roleId + "> role by yourself. Please contact a person with the role to add it for you.").queue();
                    });
                }
            }
        } else {
            SomeDiscordBot.instance.overrideSudoersRoleProtection = false;
        }

        //prevent any roles from being added to the bot
        if (event.getMember().getId().equals(SomeDiscordBot.instance.getSelfUser().getId()) && !roleLockOverride) {
            //loop through all added roles
            for (Role added : event.getRoles()) {
                //remove the role from the bot
                event.getGuild().removeRoleFromMember(event.getMember(), added).queue();
                roleLockOverride = true;
            }
        } else {
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

        if (!SomeDiscordBot.instance.overrideSudoersRoleProtection) {
            for (Role removed : event.getRoles()) {
                String roleId = SomeDiscordBot.instance.configs.sudoersRankIDs.get(event.getGuild().getId());
                if (removed.getId().equals(roleId)) {
                    SomeDiscordBot.instance.overrideSudoersRoleProtection = true;
                    event.getGuild().addRoleToMember(event.getMember(), removed).queue(success -> {
                        Objects.requireNonNull(event.getGuild().getTextChannelById(SomeDiscordBot.instance.configs.journalChannels.get(event.getGuild().getId())))
                                .sendMessage("You cannot remove the <@&" + roleId + "> role by using Discord alone.").queue();
                    });
                }
            }
        } else {
            SomeDiscordBot.instance.overrideSudoersRoleProtection = false;
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
