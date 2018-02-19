package eu.thechest.discordbot.cmd;

import eu.thechest.discordbot.Command;
import eu.thechest.discordbot.DiscordBot;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by zeryt on 27.05.2017.
 */
public class SoosCommand implements Command {
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    public void action(String[] args, MessageReceivedEvent event) {
        if(!event.isFromType(ChannelType.TEXT)) return;
        event.getMessage().getTextChannel().sendMessage(DiscordBot.getMainGuild().getEmoteById(315241586055446528l).getAsMention()).queue();
    }

    public void executed(boolean success, MessageReceivedEvent event) {
        return;
    }
}
