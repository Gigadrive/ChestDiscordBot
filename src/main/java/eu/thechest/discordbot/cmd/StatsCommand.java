package eu.thechest.discordbot.cmd;

import eu.thechest.discordbot.*;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by zeryt on 30.05.2017.
 */
public class StatsCommand implements Command {
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    public void action(String[] args, MessageReceivedEvent event) {
        if(!event.isFromType(ChannelType.TEXT)) return;

        if(args.length == 2){
            String name = args[0];
            String mode = args[1];

            if(mode.equalsIgnoreCase("SG")){
                CachedStatsSG stats = DiscordBot.getSGStats(name);

                if(stats != null){
                    event.getTextChannel().sendMessage(
                            event.getAuthor().getAsMention() + " Survival Games Statistics for " + stats.username + "\n" +
                                    "**Points:** " + stats.points + "\n" +
                                    "**Kills:** " + stats.kills +  "\n" +
                                    "**Deaths:** " + stats.deaths + "\n" +
                                    "**Played games:** " + stats.playedGames +  "\n" +
                                    "**Victories:** " + stats.victories
                    ).queue();
                } else {
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " **That user has never played Survival Games.**").queue();
                }
            } else if(mode.equalsIgnoreCase("MG")){
                CachedStatsMG stats = DiscordBot.getMGStats(name);

                if(stats != null){
                    event.getTextChannel().sendMessage(
                            event.getAuthor().getAsMention() + " Musical Guess Statistics for " + stats.username + "\n" +
                                    "**Points:** " + stats.points + "\n" +
                                    "**Played games:** " + stats.playedGames +  "\n" +
                                    "**Victories:** " + stats.victories
                    ).queue();
                } else {
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " **That user has never played Musical Guess.**").queue();
                }
            } else if(mode.equalsIgnoreCase("KPVP")){
                CachedStatsKPVP stats = DiscordBot.getKPVPStats(name);

                if(stats != null){
                    event.getTextChannel().sendMessage(
                            event.getAuthor().getAsMention() + " KitPvP Statistics for " + stats.username + "\n" +
                                    "**Points:** " + stats.points + "\n" +
                                    "**Kills:** " + stats.kills +  "\n" +
                                    "**Deaths:** " + stats.deaths
                    ).queue();
                } else {
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " **That user has never played Musical Guess.**").queue();
                }
            } else if(mode.equalsIgnoreCase("BG")){

            } else if(mode.equalsIgnoreCase("SOCCER")){

            } else if(mode.equalsIgnoreCase("GG")){

            } else if(mode.equalsIgnoreCase("SGD")){

            } else {
                event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " **Invalid gamemode! Valid gamemodes are: ** SG, MG, KPVP, BG, SOCCER, GG, SGD").queue();;
            }
        } else {
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " **Usage:** !stats <Username> <Gamemode> ").queue();;
        }
    }

    public void executed(boolean success, MessageReceivedEvent event) {
        return;
    }
}
