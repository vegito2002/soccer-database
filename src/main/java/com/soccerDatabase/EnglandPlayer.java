package com.soccerDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by vegito2002 on 12/23/16.
 */
public class EnglandPlayer {
    private int id;
    private String name;
    private double height, weight;
    private String birthday;
    private int birthdayNumber;

    @Override
    public String toString() {
        return "EnglandPlayer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", birthday='" + birthday + '\'' +
                ", birthdayNumber=" + birthdayNumber +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getBirthdayNumber() {
        return birthdayNumber;
    }

    public void setBirthdayNumber(int birthdayNumber) {
        this.birthdayNumber = birthdayNumber;
    }

    public EnglandPlayer(int id, String name, double height, double weight, String birthday, int birthdayNumber) {

        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.birthday = birthday;
        this.birthdayNumber = birthdayNumber;
    }

    public void calculateDateNumber() {
        String[] dateWords = getBirthday().split(" ");
        LocalDate dateLocal = LocalDate.parse(dateWords[0], DateTimeFormatter.ISO_LOCAL_DATE);
        int dateInt = 86400 * (int) dateLocal.toEpochDay();
        setBirthdayNumber(dateInt);
    }
}
