package eu.thechest.discordbot.cmd;

import eu.thechest.discordbot.Command;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by zeryt on 26.05.2017.
 */
public class TestCommand implements Command {
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    public void action(String[] args, MessageReceivedEvent event) {
        if(!event.isFromType(ChannelType.TEXT)) return;
        event.getTextChannel().sendMessage("allo").queue();
    }

    public void executed(boolean success, MessageReceivedEvent event) {
        return;
    }
}
