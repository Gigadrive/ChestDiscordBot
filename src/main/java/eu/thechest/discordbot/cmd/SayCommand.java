package eu.thechest.discordbot.cmd;

import eu.thechest.discordbot.Command;
import eu.thechest.discordbot.DiscordBot;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by zeryt on 30.05.2017.
 */
public class SayCommand implements Command {
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    public void action(String[] args, MessageReceivedEvent event) {
        if(!event.isFromType(ChannelType.TEXT)) return;
        if(!DiscordBot.getMainGuild().getMemberById(event.getAuthor().getId()).getRoles().contains(DiscordBot.getMainGuild().getRoleById(282223516957409282L))) return;

        String s = "";
        for(String ss : args) s = s + ss + " ";

        if(args.length > 0){
            event.getTextChannel().sendMessage(s).queue();
            event.getMessage().delete().queue();
        }
    }

    public void executed(boolean success, MessageReceivedEvent event) {
        return;
    }
}
