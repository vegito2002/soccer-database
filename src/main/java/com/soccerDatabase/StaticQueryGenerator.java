package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/24/16.
 */
public class StaticQueryGenerator {

    public final static String SQL_CREATETABLE_MATCHRECORDS ="CREATE TABLE IF NOT EXISTS MatchRecords ( "
            + " dateString TEXT,  "
            + " homeTeam TEXT,  "
            + " awayTeam TEXT,  "
            + " fullTimeResult TEXT,  "
            + " halfTimeResult TEXT,  "
            + " referee TEXT, "
            + " dateNumber INTEGER,  "
            + " fthg INTEGER,  "
            + " ftag INTEGER,  "
            + " hthg INTEGER,  "
            + " htag INTEGER, "
            + " homeShot INTEGER,  "
            + " awayShot INTEGER,  "
            + " hst INTEGER,  "
            + " ast INTEGER,  "
            + " hf INTEGER,  "
            + " af INTEGER, "
            + " hc INTEGER,  "
            + " ac INTEGER,  "
            + " hy INTEGER,  "
            + " ay INTEGER,  "
            + " hr INTEGER,  "
            + " ar INTEGER, "
            + " PRIMARY KEY(dateNumber, homeTeam, awayTeam)); ";
    public final static String SQL_CREATETABLE_UPDATELOG = " CREATE TABLE IF NOT EXISTS updatelog "
            + " (sourcename TEXT NOT NULL, "
            + " updatecount INTEGER NOT NULL, "
            + " PRIMARY KEY(sourcename)); ";
    public final static String SQL_CREATETABLE_ENGLANDTEAM = "CREATE TABLE IF NOT EXISTS EnglandTeam "
            + " (id INTEGER NOT NULL, "
            + " teamName TEXT, "
            + " teamCode TEXT, "
            + " city TEXT, "
            + " PRIMARY KEY(id));";
    public final static String SQL_CREATETABLE_ENGLAND_TEAM_ATTRIBUTES = " CREATE TABLE IF NOT EXISTS EnglandTeamAttributes "
            + " (id INTEGER NOT NULL,  "
            + " buildUpPlaySpeedClass TEXT, "
            + " buildUpPlayDribblingClass TEXT, "
            + " buildUpPlayPassingClass TEXT, "
            + " buildUpPlayPositioningClass TEXT, "
            + " chanceCreationPassingClass TEXT, "
            + " chanceCreationCrossingClass TEXT, "
            + " chanceCreationShootingClass TEXT, "
            + " chanceCreationPositioningClass TEXT, "
            + " defencePressureClass TEXT, "
            + " defenceAggressionClass TEXT, "
            + " defenceTeamWidthClass TEXT, "
            + " defenceDefenderLineClass TEXT, "
            + " PRIMARY KEY(id)); ";
    public final static String SQL_CREATETABLE_ENGLAND_MEMBERSHIP = " CREATE TABLE IF NOT EXISTS EnglandMembership "
            + " (teamId INTEGER NOT NULL, "
            + " playerId INTEGER NOT NULL, "
            + " PRIMARY KEY(teamId, playerId)); ";
    public final static String SQL_CREATETABLE_ENGLAND_PLAYER = " CREATE TABLE IF NOT EXISTS EnglandPlayer "
            + " (id INTEGER NOT NULL, "
            + " name TEXT, "
            + " height REAL, "
            + " weight REAL, "
            + " birthday TEXT, "
            + " birthdayNumber INTEGER, "
            + " PRIMARY KEY(id)); ";
}