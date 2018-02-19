package eu.thechest.discordbot;

import eu.thechest.chestapi.mysql.MySQLManager;
import eu.thechest.discordbot.cmd.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import sun.misc.Cache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * Created by zeryt on 26.05.2017.
 */
public class DiscordBot {
    public static JDA jda;
    public static HashMap<String,Command> COMMANDS = new HashMap<String,Command>();
    public static final CommandParser parser = new CommandParser();

    public static final ArrayList<CachedStatsSG> CACHED_STATS_SG = new ArrayList<CachedStatsSG>();
    public static final ArrayList<CachedStatsMG> CACHED_STATS_MG = new ArrayList<CachedStatsMG>();
    public static final ArrayList<CachedStatsKPVP> CACHED_STATS_KPVP = new ArrayList<CachedStatsKPVP>();

    public static OnlineStatus status = OnlineStatus.ONLINE;

    public static void main(String[] args){
        try {
            jda = new JDABuilder(AccountType.BOT).addEventListener(new BotListener()).setToken("***********").buildBlocking();
            jda.setAutoReconnect(true);

            MySQLManager.getInstance().load();
        } catch(Exception e){
            e.printStackTrace();
        }

        COMMANDS.put("test",new TestCommand());
        COMMANDS.put("register",new RegisterCommand());
        COMMANDS.put("debug",new DebugCommand());
        COMMANDS.put("soos",new SoosCommand());
        COMMANDS.put("unlink",new UnlinkCommand());
        COMMANDS.put("stats",new StatsCommand());
        COMMANDS.put("say",new SayCommand());
        COMMANDS.put("clear",new ClearChatCommand());

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                MySQLManager.getInstance().unload();
            }
        });

        r();
        f();
    }

    public static boolean isValidInteger(String s){
        try {
            Integer i = Integer.parseInt(s);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    public static void log(LogLevel level, String msg){
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();

        System.out.println("[" + now.getDayOfWeek().name() + " " + hour + ":" + minute + ":" + second + "] [" + level.toString() + "] " + msg);
    }

    // CLEAR CACHES
    private static void f(){
        new Thread(){
            @Override
            public void run() {
                try {
                    CACHED_STATS_SG.clear();
                    CACHED_STATS_MG.clear();

                    log(LogLevel.INFO,"Cleared cache!");

                    Thread.sleep((long)3*60*1000);
                    f();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // UPDATE ONLINE STATUS
    public static void h(){
        new Thread(){
            @Override
            public void run() {
                try {
                    if(jda != null && jda.getPresence() != null && jda.getPresence().getStatus() != null){
                        switch(status){
                            case ONLINE:
                                status = OnlineStatus.IDLE;
                                jda.getPresence().setStatus(status);
                                break;
                            case IDLE:
                                status = OnlineStatus.DO_NOT_DISTURB;
                                jda.getPresence().setStatus(status);
                                break;
                            default:
                                status = OnlineStatus.ONLINE;
                                jda.getPresence().setStatus(status);
                                break;
                        }
                    }

                    Thread.sleep((long)5*1000);
                    h();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static CachedStatsSG getSGStats(String name){
        for(CachedStatsSG sg : CACHED_STATS_SG){
            if(sg.username.equalsIgnoreCase(name)) return sg;
        }

        CachedStatsSG s = null;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT u.uuid,u.username,s.points,s.kills,s.deaths,s.playedGames,s.victories FROM users AS u INNER JOIN sg_stats AS s ON s.uuid = u.uuid WHERE u.username = ?");
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();

            if(rs.first()){
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                name = rs.getString("username");
                int points = rs.getInt("points");
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");
                int playedGames = rs.getInt("playedGames");
                int victories = rs.getInt("victories");

                s = new CachedStatsSG(name,uuid,points,kills,deaths,playedGames,victories);
                CACHED_STATS_SG.add(s);
            }

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }

        return s;
    }

    public static CachedStatsMG getMGStats(String name){
        for(CachedStatsMG sg : CACHED_STATS_MG){
            if(sg.username.equalsIgnoreCase(name)) return sg;
        }

        CachedStatsMG s = null;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT u.uuid,u.username,s.points,s.playedGames,s.victories FROM users AS u INNER JOIN mg_stats AS s ON s.uuid = u.uuid WHERE u.username = ?");
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();

            if(rs.first()){
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                name = rs.getString("username");
                int points = rs.getInt("points");
                int playedGames = rs.getInt("playedGames");
                int victories = rs.getInt("victories");

                s = new CachedStatsMG(name,uuid,points,playedGames,victories);
                CACHED_STATS_MG.add(s);
            }

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }

        return s;
    }

    public static CachedStatsKPVP getKPVPStats(String name){
        for(CachedStatsKPVP sg : CACHED_STATS_KPVP){
            if(sg.username.equalsIgnoreCase(name)) return sg;
        }

        CachedStatsKPVP s = null;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT u.uuid,u.username,s.points,s.kills,s.deaths FROM users AS u INNER JOIN kpvp_stats AS s ON s.uuid = u.uuid WHERE u.username = ?");
            ps.setString(1,name);
            ResultSet rs = ps.executeQuery();

            if(rs.first()){
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                name = rs.getString("username");
                int points = rs.getInt("points");
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");

                s = new CachedStatsKPVP(name,uuid,points,kills,deaths);
                CACHED_STATS_KPVP.add(s);
            }

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }

        return s;
    }

    // UPDATE RANKS AND NAMES
    private static void r(){
        new Thread(){
            @Override
            public void run() {
                try {
                    if(getMainGuild() == null){
                        Thread.sleep((long)60*1000);
                        r();
                        return;
                    }

                    //log(LogLevel.INFO,"=> UPDATING RANKS AND NAMES!");
                    PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT u.uuid,c.discord_user,u.rank,c.currentRank,u.username FROM `discord_connections` AS c INNER JOIN `users` AS u ON u.uuid = c.minecraft_uuid");
                    ResultSet rs = ps.executeQuery();

                    HashMap<String,String> h = new HashMap<String,String>();

                    while(rs.next()){
                        String uuid = rs.getString("uuid");
                        String discordID = rs.getString("discord_user");
                        String rank = rs.getString("rank");
                        String storedRank = rs.getString("currentRank");
                        String username = rs.getString("username");
                        Member m = getMainGuild().getMemberById(discordID);

                        if(storedRank == null || !storedRank.equals(rank)){
                            h.put(uuid,rank);

                            if(m != null){
                                //System.out.println("R: " + rank + " STORED RANK: " + storedRank + " UUID: " + uuid + " DID: " + discordID);
                                //if(m.getRoles().size() > 0) m.getGuild().getController().removeRolesFromMember(m,m.getRoles()).queue(); // clear roles

                                if(rank.equals("ADMIN")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223516957409282L)).queue();
                                } else if(rank.equals("CM")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223492483776513L)).queue();
                                } else if(rank.equals("SR_MOD")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223475828195338L)).queue();
                                } else if(rank.equals("MOD")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223455032836096L)).queue();
                                } else if(rank.equals("BUILD_TEAM")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223433683828737L)).queue();
                                } else if(rank.equals("STAFF")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223406361870338L)).queue();
                                } else if(rank.equals("VIP")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223373428195339L)).queue();
                                } else if(rank.equals("TITAN")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223371406802946L)).queue();
                                } else if(rank.equals("PRO_PLUS")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223345020436481L)).queue();
                                } else if(rank.equals("PRO")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(282223162471612416L)).queue();
                                } else if(rank.equals("USER")){
                                    m.getGuild().getController().modifyMemberRoles(m,m.getGuild().getRoleById(317720969856942090L)).queue();
                                }
                            }
                        }

                        if(!m.isOwner()) m.getGuild().getController().setNickname(m,username).queue();
                    }

                    MySQLManager.getInstance().closeResources(rs,ps);

                    if(h.size() > 0){
                        for(String uuid : h.keySet()){
                            String rank = h.get(uuid);

                            ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `discord_connections` SET `currentRank` = ? WHERE `minecraft_uuid` = ?");
                            ps.setString(1,rank);
                            ps.setString(2,uuid);
                            ps.executeUpdate();
                            ps.close();
                        }
                    }

                    Thread.sleep((long)60*1000);
                    r();
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static Guild getMainGuild(){
        return jda.getGuildById(282223052215812097L);
    }

    public static void handleCommand(CommandParser.CommandContainer cmd){
        if(COMMANDS.containsKey(cmd.invoke)){
            Command c = COMMANDS.get(cmd.invoke);
            boolean safe = c.called(cmd.args,cmd.e);

            if(safe){
                c.action(cmd.args,cmd.e);
                c.executed(safe,cmd.e);
            } else {
                c.executed(safe,cmd.e);
            }
        }
    }

    public static String getRankFromUUID(String uuid){
        String rank = null;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `uuid` = ?");
            ps.setString(1,uuid);

            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                rank = rs.getString("rank");
            }

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }

        return rank;
    }

    public static boolean isRegistered(User u){
        boolean b = false;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `discord_connections` WHERE `discord_user` = ?");
            ps.setString(1,u.getId());
            ResultSet rs = ps.executeQuery();

            b = rs.first();

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }

        return b;
    }

    public static void handleFirstRegisterStep(User u){
        handleFirstRegisterStep(u,false);
    }

    public static void handleFirstRegisterStep(User u, boolean demandedRegister){
        if(!isRegistered(u)){
            String discordID = u.getId();
            int code = 0;

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `discord_authCodes` WHERE `discord_user` = ?");
                ps.setString(1,discordID);
                ResultSet rs = ps.executeQuery();

                if(rs.first()){
                    code = rs.getInt("code");
                } else {
                    int min = 1000000;
                    int max = 9999999;
                    code = new Random().nextInt((max - min) + 1) + min;

                    PreparedStatement insert = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `discord_authCodes` (`discord_user`,`code`) VALUES(?,?);");
                    insert.setString(1,discordID);
                    insert.setInt(2,code);
                    insert.execute();
                    insert.close();
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            if(code > 0){
                PrivateChannel p = u.openPrivateChannel().complete();
                p.sendMessage("Welcome to the TheChest.eu Discord Server!").queue();
                p.sendMessage("Please enter **/discord " + code + "** on our Minecraft server (thechest.eu) to get your rank!").queue();
                p.sendMessage("Message this bot with **!register** again if you lose this information.").queue();
            }
        } else {
            if(demandedRegister){
                PrivateChannel p = u.openPrivateChannel().complete();
                p.sendMessage("You are already registered on our discord server.").queue();
                p.sendMessage("Use **!unlink** to remove your registration.").queue();
            }
        }
    }
}
