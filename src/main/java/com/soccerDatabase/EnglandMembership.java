package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/24/16.
 */

/**
 * Class for holding data of table EnglandMembershp
 */
public class EnglandMembership {
    private int teamId;
    private int playerId;

    public EnglandMembership(int teamId, int playerId) {
        this.teamId = teamId;
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "EnglandMembership{" +
                "teamId=" + teamId +
                ", playerId=" + playerId +
                '}';
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
