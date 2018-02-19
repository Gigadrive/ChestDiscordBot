package eu.thechest.discordbot.cmd;

import eu.thechest.discordbot.Command;
import eu.thechest.discordbot.DiscordBot;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by zeryt on 04.06.2017.
 */
public class ClearChatCommand implements Command {
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    public void action(String[] args, MessageReceivedEvent event) {
        if(!event.isFromType(ChannelType.TEXT)) return;
        if(!DiscordBot.getMainGuild().getMemberById(event.getAuthor().getId()).getRoles().contains(DiscordBot.getMainGuild().getRoleById(282223516957409282L))) return;

        if(args.length == 1){
            if(DiscordBot.isValidInteger(args[0])){
                int amount = Integer.parseInt(args[0]);

                if(amount <= 200 && amount > 0){
                    int i = amount+1;
                    ArrayList<String> m = new ArrayList<String>();

                    if(event.getTextChannel().getHistory().size() < amount){
                        event.getTextChannel().getHistory().retrievePast(amount-event.getTextChannel().getHistory().size());
                    }

                    ArrayList<Message> h = new ArrayList<Message>();
                    h.addAll(event.getTextChannel().getHistory().getRetrievedHistory());
                    Collections.reverse(h);

                    /*while(event.getTextChannel().hasLatestMessage() && i != 0 && h.size() > 0 && h.size() > ){
                        //m.add(event.getTextChannel().getLatestMessageId());
                        //event.getTextChannel().deleteMessageById(event.getTextChannel().getLatestMessageId()).complete();

                        i--;
                    }*/

                    for(Message message : h){
                        if(i == 0) break;
                        m.add(message.getId());
                        i--;
                    }

                    if(m.size() > 0){
                        event.getTextChannel().deleteMessagesByIds(m).queue();
                    }
                } else {
                    event.getTextChannel().sendMessage("**Please enter a number between 1 and 200!**").queue();
                }
            } else {
                event.getTextChannel().sendMessage("**Please enter a valid number!**").queue();
            }
        } else {
            event.getTextChannel().sendMessage("**USAGE:** !clear <amount of messages>").queue();
        }
    }

    public void executed(boolean success, MessageReceivedEvent event) {
        return;
    }
}
