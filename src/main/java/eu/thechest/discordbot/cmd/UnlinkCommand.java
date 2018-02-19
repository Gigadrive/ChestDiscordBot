package eu.thechest.discordbot.cmd;

import eu.thechest.chestapi.mysql.MySQLManager;
import eu.thechest.discordbot.Command;
import eu.thechest.discordbot.DiscordBot;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by zeryt on 27.05.2017.
 */
public class UnlinkCommand implements Command {
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    public void action(String[] args, MessageReceivedEvent event) {
        if(!event.isFromType(ChannelType.PRIVATE)) return;
        User u = event.getAuthor();
        Member m = DiscordBot.getMainGuild().getMemberById(u.getId());
        String discordID = u.getId();

        if(m == null) return;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `discord_connections` WHERE `discord_user` = ?");
            ps.setString(1,discordID);
            ResultSet rs = ps.executeQuery();

            if(rs.first()){
                if(m.getRoles().size() > 0) m.getGuild().getController().removeRolesFromMember(m,m.getRoles()).queue();

                ps = MySQLManager.getInstance().getConnection().prepareStatement("DELETE FROM `discord_connections` WHERE `discord_user` = ?");
                ps.setString(1,discordID);
                ps.executeUpdate();
                ps.close();

                PrivateChannel p = u.openPrivateChannel().complete();
                p.sendMessage("Your account has been unlinked.").queue();
                p.sendMessage("Type **!register** to link it again.").queue();
            } else {
                DiscordBot.handleFirstRegisterStep(u,true);

                MySQLManager.getInstance().closeResources(rs,ps);
                return;
            }
        } catch(Exception e){
            PrivateChannel p = u.openPrivateChannel().complete();
            p.sendMessage("An error occured.").queue();

            e.printStackTrace();
        }
    }

    public void executed(boolean success, MessageReceivedEvent event) {
        return;
    }
}
