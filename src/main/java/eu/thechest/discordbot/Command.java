package eu.thechest.discordbot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by zeryt on 26.05.2017.
 */
public interface Command {
    public boolean called(String[] args, MessageReceivedEvent event);
    public void action(String[] args, MessageReceivedEvent event);
    public void executed(boolean success, MessageReceivedEvent event);
}
