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
 * Created by zeryt on 26.05.2017.
 */
public class RegisterCommand implements Command {
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    public void action(String[] args, MessageReceivedEvent event) {
        User u = event.getAuthor();
        String discordID = event.getAuthor().getId();
        Member m = DiscordBot.getMainGuild().getMemberById(discordID);

        if(event.isFromType(ChannelType.PRIVATE)){
            if(DiscordBot.jda.getGuildById(282223052215812097L).getMemberById(event.getAuthor().getId()) == null){
                PrivateChannel p = m.getUser().openPrivateChannel().complete();
                p.sendMessage("Please join our discord server before linking your account.").queue();
                return;
            }

            if(!DiscordBot.isRegistered(event.getAuthor())){

                if(DiscordBot.jda.getGuildById(282223052215812097L).getMemberById(discordID) == null){
                    PrivateChannel p = m.getUser().openPrivateChannel().complete();
                    p.sendMessage("Please join our discord server before linking your account.").queue();
                    return;
                }

                try {
                    PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `discord_authCodes` WHERE `discord_user` = ?");
                    ps.setString(1,discordID);
                    ResultSet rs = ps.executeQuery();

                    if(rs.first()){
                        int code = rs.getInt("code");
                        String uuid = rs.getString("minecraftUUID");

                        MySQLManager.getInstance().closeResources(rs,ps);

                        if(uuid != null && !uuid.isEmpty()){
                            try {
                                ps = MySQLManager.getInstance().getConnection().prepareStatement("DELETE FROM `discord_authCodes` WHERE `discord_user` = ? AND `minecraftUUID` = ?");
                                ps.setString(1, event.getAuthor().getId());
                                ps.setString(2,uuid);
                                ps.executeUpdate();
                                ps.close();

                                ps = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `discord_connections` (`discord_user`,`minecraft_uuid`,`currentRank`) VALUES(?,?,?);");
                                ps.setString(1,discordID);
                                ps.setString(2,uuid);
                                ps.setString(3,null);
                                ps.executeUpdate();
                                ps.close();

                                PrivateChannel p = m.getUser().openPrivateChannel().complete();
                                p.sendMessage("Your account has been linked successfully.").queue();
                                p.sendMessage("You should receive your rank within the next 10 minutes.").queue();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            DiscordBot.handleFirstRegisterStep(event.getAuthor(),true);
                        }
                    } else {
                        DiscordBot.handleFirstRegisterStep(event.getAuthor(),true);
                        MySQLManager.getInstance().closeResources(rs,ps);
                        return;
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    PrivateChannel p = m.getUser().openPrivateChannel().complete();
                    p.sendMessage("An error occured.").queue();
                }
            } else {
                PrivateChannel p = m.getUser().openPrivateChannel().complete();
                p.sendMessage("You are already registered.").queue();
            }
        }
    }

    public void executed(boolean success, MessageReceivedEvent event) {
        return;
    }
}
