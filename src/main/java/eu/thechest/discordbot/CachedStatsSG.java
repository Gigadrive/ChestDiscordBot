package eu.thechest.discordbot;

import java.util.UUID;

/**
 * Created by zeryt on 30.05.2017.
 */
public class CachedStatsSG {
    public String username;
    public UUID uuid;
    public int points;
    public int kills;
    public int deaths;
    public int playedGames;
    public int victories;

    public CachedStatsSG(String username, UUID uuid, int points, int kills, int deaths, int playedGames, int victories){
        this.username = username;
        this.uuid = uuid;
        this.points = points;
        this.kills = kills;
        this.deaths = deaths;
        this.playedGames = playedGames;
        this.victories = victories;
    }
}
