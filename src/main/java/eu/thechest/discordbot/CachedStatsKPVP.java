package eu.thechest.discordbot;

import java.util.UUID;

/**
 * Created by zeryt on 06.06.2017.
 */
public class CachedStatsKPVP {
    public String username;
    public UUID uuid;
    public int points;
    public int kills;
    public int deaths;

    public CachedStatsKPVP(String username, UUID uuid, int points, int kills, int deaths){
        this.username = username;
        this.uuid = uuid;
        this.points = points;
        this.kills = kills;
        this.deaths = deaths;
    }
}
