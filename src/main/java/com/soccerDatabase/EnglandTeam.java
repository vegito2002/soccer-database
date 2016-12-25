package com.soccerDatabase;

import java.util.List;

/**
 * Created by vegito2002 on 12/23/16.
 */

/**
 * Class for holding data in table EnglandTeam
 */
public class EnglandTeam {
    private int id;
    private String teamName, teamCode;
    private String city;


    public EnglandTeam(int id, String teamName, String teamCode, String city) {
        this.id = id;
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.city = city;
    }

    @Override
    public String toString() {
        return "EnglandTeam{" +
                "id=" + id +
                ", teamName='" + teamName + '\'' +
                ", teamCode='" + teamCode + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
