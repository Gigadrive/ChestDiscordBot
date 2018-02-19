package eu.thechest.discordbot;

import eu.thechest.chestapi.mysql.MySQLManager;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import static eu.thechest.discordbot.DiscordBot.jda;

/**
 * Created by zeryt on 26.05.2017.
 */
public class BotListener extends ListenerAdapter {
    // FIRED WHEN A MESSAGE IS SENT
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMessage().getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) return; // don't check bot messages

        if(e.getMessage().getContent().startsWith("!")){
            DiscordBot.handleCommand(DiscordBot.parser.parse(e.getMessage().getContent(), e));
        }
    }

    // FIRED WHEN BOT IS READY TO GO
    @Override
    public void onReady(ReadyEvent e){
        System.out.println("Logged in as: " + e.getJDA().getSelfUser().getName());

        e.getJDA().getPresence().setGame(Game.of("on thechest.eu"));
        DiscordBot.h();
    }

    // FIRED WHEN A USER JOINS A GUILD
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e){
        Member m = e.getMember();

        DiscordBot.handleFirstRegisterStep(m.getUser());

        if(DiscordBot.getMainGuild() != null){
            DiscordBot.getMainGuild().getTextChannelById(318035383784964096L).sendMessage("Welcome " + e.getMember().getUser().getAsMention() + " to our Discord server!").queue();
        }
    }

    // FIRED WHEN A USER LEAVES A GUILD
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e){
        Member m = e.getMember();

        DiscordBot.handleFirstRegisterStep(m.getUser());

        if(DiscordBot.getMainGuild() != null){
            DiscordBot.getMainGuild().getTextChannelById(318035383784964096L).sendMessage(e.getMember().getUser().getAsMention() + " just left our Discord server.").queue();

            // unregister user
            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("DELETE FROM `discord_connections` WHERE `discord_user` = ?");
                ps.setString(1,m.getUser().getId());
                ps.executeUpdate();
                ps.close();
            } catch(Exception e1){
                e1.printStackTrace();
            }
        }
    }
}
