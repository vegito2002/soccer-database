package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/24/16.
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
}
