package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/24/16.
 */

/**
 * Class for holding data in table EnglandReferee
 */
public class EnglandReferee {
    private String name;
    private String fullName;
    private String birthday;
    private String birthCity;
    private int birthdayNumber;

    public EnglandReferee(String name, String fullName, String birthday, String birthCity, int birthdayNumber) {
        this.name = name;
        this.fullName = fullName;
        this.birthday = birthday;
        this.birthCity = birthCity;
        this.birthdayNumber = birthdayNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    @Override
    public String toString() {
        return "EnglandReferee{" +
                "name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", birthCity='" + birthCity + '\'' +
                ", birthdayNumber=" + birthdayNumber +
                '}';
    }
}
