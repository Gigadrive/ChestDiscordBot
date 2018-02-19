package eu.thechest.discordbot;

import java.util.UUID;

/**
 * Created by zeryt on 30.05.2017.
 */
public class CachedStatsMG {
    public String username;
    public UUID uuid;
    public int points;
    public int playedGames;
    public int victories;

    public CachedStatsMG(String username, UUID uuid, int points, int playedGames, int victories){
        this.username = username;
        this.uuid = uuid;
        this.points = points;
        this.playedGames = playedGames;
        this.victories = victories;
    }
}
