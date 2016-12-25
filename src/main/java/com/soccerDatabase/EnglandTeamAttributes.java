package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/23/16.
 */

/**
 * Class for holding data in table EnglandTeamAttributes
 */
public class EnglandTeamAttributes {
    private int id;
    private String buildUpPlaySpeedClass, buildUpPlayDribblingClass, buildUpPlayPassingClass, buildUpPlayPositioningClass;
    private String chanceCreationPassingClass, chanceCreationCrossingClass, chanceCreationShootingClass, chanceCreationPositioningClass;
    private String defencePressureClass,defenceAggressionClass, defenceTeamWidthClass, defenceDefenderLineClass;

    @Override
    public String toString() {
        return "EnglandTeamAttributes{" +
                "id=" + id +
                ", buildUpPlaySpeedClass='" + buildUpPlaySpeedClass + '\'' +
                ", buildUpPlayDribblingClass='" + buildUpPlayDribblingClass + '\'' +
                ", buildUpPlayPassingClass='" + buildUpPlayPassingClass + '\'' +
                ", buildUpPlayPositioningClass='" + buildUpPlayPositioningClass + '\'' +
                ", chanceCreationPassingClass='" + chanceCreationPassingClass + '\'' +
                ", chanceCreationCrossingClass='" + chanceCreationCrossingClass + '\'' +
                ", chanceCreationShootingClass='" + chanceCreationShootingClass + '\'' +
                ", chanceCreationPositioningClass='" + chanceCreationPositioningClass + '\'' +
                ", defencePressureClass='" + defencePressureClass + '\'' +
                ", defenceAggressionClass='" + defenceAggressionClass + '\'' +
                ", defenceTeamWidthClass='" + defenceTeamWidthClass + '\'' +
                ", defenceDefenderLineClass='" + defenceDefenderLineClass + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuildUpPlaySpeedClass() {
        return buildUpPlaySpeedClass;
    }

    public void setBuildUpPlaySpeedClass(String buildUpPlaySpeedClass) {
        this.buildUpPlaySpeedClass = buildUpPlaySpeedClass;
    }

    public String getBuildUpPlayDribblingClass() {
        return buildUpPlayDribblingClass;
    }

    public void setBuildUpPlayDribblingClass(String buildUpPlayDribblingClass) {
        this.buildUpPlayDribblingClass = buildUpPlayDribblingClass;
    }

    public String getBuildUpPlayPassingClass() {
        return buildUpPlayPassingClass;
    }

    public void setBuildUpPlayPassingClass(String buildUpPlayPassingClass) {
        this.buildUpPlayPassingClass = buildUpPlayPassingClass;
    }

    public String getBuildUpPlayPositioningClass() {
        return buildUpPlayPositioningClass;
    }

    public void setBuildUpPlayPositioningClass(String buildUpPlayPositioningClass) {
        this.buildUpPlayPositioningClass = buildUpPlayPositioningClass;
    }

    public String getChanceCreationPassingClass() {
        return chanceCreationPassingClass;
    }

    public void setChanceCreationPassingClass(String chanceCreationPassingClass) {
        this.chanceCreationPassingClass = chanceCreationPassingClass;
    }

    public String getChanceCreationCrossingClass() {
        return chanceCreationCrossingClass;
    }

    public void setChanceCreationCrossingClass(String chanceCreationCrossingClass) {
        this.chanceCreationCrossingClass = chanceCreationCrossingClass;
    }

    public String getChanceCreationShootingClass() {
        return chanceCreationShootingClass;
    }

    public void setChanceCreationShootingClass(String chanceCreationShootingClass) {
        this.chanceCreationShootingClass = chanceCreationShootingClass;
    }

    public String getChanceCreationPositioningClass() {
        return chanceCreationPositioningClass;
    }

    public void setChanceCreationPositioningClass(String chanceCreationPositioningClass) {
        this.chanceCreationPositioningClass = chanceCreationPositioningClass;
    }

    public String getDefencePressureClass() {
        return defencePressureClass;
    }

    public void setDefencePressureClass(String defencePressureClass) {
        this.defencePressureClass = defencePressureClass;
    }

    public String getDefenceAggressionClass() {
        return defenceAggressionClass;
    }

    public void setDefenceAggressionClass(String defenceAggressionClass) {
        this.defenceAggressionClass = defenceAggressionClass;
    }

    public String getDefenceTeamWidthClass() {
        return defenceTeamWidthClass;
    }

    public void setDefenceTeamWidthClass(String defenceTeamWidthClass) {
        this.defenceTeamWidthClass = defenceTeamWidthClass;
    }

    public String getDefenceDefenderLineClass() {
        return defenceDefenderLineClass;
    }

    public void setDefenceDefenderLineClass(String defenceDefenderLineClass) {
        this.defenceDefenderLineClass = defenceDefenderLineClass;
    }

    public EnglandTeamAttributes(int id, String buildUpPlaySpeedClass, String buildUpPlayDribblingClass, String buildUpPlayPassingClass, String buildUpPlayPositioningClass, String chanceCreationPassingClass, String chanceCreationCrossingClass, String chanceCreationShootingClass, String chanceCreationPositioningClass, String defencePressureClass, String defenceAggressionClass, String defenceTeamWidthClass, String defenceDefenderLineClass) {

        this.id = id;
        this.buildUpPlaySpeedClass = buildUpPlaySpeedClass;
        this.buildUpPlayDribblingClass = buildUpPlayDribblingClass;
        this.buildUpPlayPassingClass = buildUpPlayPassingClass;
        this.buildUpPlayPositioningClass = buildUpPlayPositioningClass;
        this.chanceCreationPassingClass = chanceCreationPassingClass;
        this.chanceCreationCrossingClass = chanceCreationCrossingClass;
        this.chanceCreationShootingClass = chanceCreationShootingClass;
        this.chanceCreationPositioningClass = chanceCreationPositioningClass;
        this.defencePressureClass = defencePressureClass;
        this.defenceAggressionClass = defenceAggressionClass;
        this.defenceTeamWidthClass = defenceTeamWidthClass;
        this.defenceDefenderLineClass = defenceDefenderLineClass;
    }
}
