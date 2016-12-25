package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/24/16.
 */
public class EnglandManager {
    private String name;
    private int teamId;
    private String birthday, birthCity;
    private int birthdayNumber;

    @Override
    public String toString() {
        return "EnglandManager{" +
                "name='" + name + '\'' +
                ", teamId=" + teamId +
                ", birthday='" + birthday + '\'' +
                ", birthCity='" + birthCity + '\'' +
                ", birthdayNumber=" + birthdayNumber +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public int getBirthdayNumber() {
        return birthdayNumber;
    }

    public void setBirthdayNumber(int birthdayNumber) {
        this.birthdayNumber = birthdayNumber;
    }

    public EnglandManager(String name, int teamId, String birthday, String birthCity, int birthdayNumber) {

        this.name = name;
        this.teamId = teamId;
        this.birthday = birthday;
        this.birthCity = birthCity;
        this.birthdayNumber = birthdayNumber;
    }
}
