package com.soccerDatabase;

import java.util.List;

/**
 * Created by vegito2002 on 12/22/16.
 */
public class EnglandMatch {
    private String dateString;
    private int dateNumber;

    public int getDateNumber() {
        return dateNumber;
    }

    public void setDateNumber(int dateNumber) {
        this.dateNumber = dateNumber;
    }

    private String homeTeam, awayTeam;
    private String fullTimeResult, halfTimeResult, referee;

    private int fthg, ftag;
    private int htag, hthg;
    private int homeShot, awayShot, hst, ast, hf, af, hc, ac, hy, ay, hr, ar;


    public void expandTeamNames(List<EnglandTeam> teams) {
        System.out.printf("Expanding team names for team %s and team %s%n", getHomeTeam(), getAwayTeam());

        setHomeTeam(findFullTeamName(this.homeTeam,teams));
        setAwayTeam(findFullTeamName(this.awayTeam, teams));

        System.out.printf("Expanded team names are %s and %s%n", getHomeTeam(), getAwayTeam());
    }

    private String findFullTeamName(String teamName, List<EnglandTeam> teams) {
        String[] nameWords = teamName.split(" ");

        String result;

        boolean isFound = true;

        for (EnglandTeam eachTeam : teams ) {
            for (String word : nameWords) {
                isFound = isFound && (eachTeam.getTeamName().toUpperCase().contains(word.toUpperCase()));
            }
            if (isFound) return eachTeam.getTeamName();
            isFound = true;
        }

        return "NotFound";
    }

    @Override
    public String toString() {
        return "EnglandMatch{" +
                "dateString='" + dateString + '\'' +
                ", dateNumber=" + dateNumber +
                ", homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", fullTimeResult='" + fullTimeResult + '\'' +
                ", halfTimeResult='" + halfTimeResult + '\'' +
                ", referee='" + referee + '\'' +
                ", fthg=" + fthg +
                ", ftag=" + ftag +
                ", htag=" + htag +
                ", hthg=" + hthg +
                ", homeShot=" + homeShot +
                ", awayShot=" + awayShot +
                ", hst=" + hst +
                ", ast=" + ast +
                ", hf=" + hf +
                ", af=" + af +
                ", hc=" + hc +
                ", ac=" + ac +
                ", hy=" + hy +
                ", ay=" + ay +
                ", hr=" + hr +
                ", ar=" + ar +
                '}';
    }

    public EnglandMatch(String dateString, int dateNumber, String homeTeam, String awayTeam, String fullTimeResult, String halfTimeResult, String referee, int fthg, int ftag, int hthg, int htag, int homeShot, int awayShot, int hst, int ast, int hf, int af, int hc, int ac, int hy, int ay, int hr, int ar) {
        this.dateString = dateString;
        this.dateNumber = dateNumber;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.fullTimeResult = fullTimeResult;
        this.halfTimeResult = halfTimeResult;
        this.referee = referee;
        this.fthg = fthg;
        this.ftag = ftag;
        this.hthg = hthg;
        this.htag = htag;
        this.homeShot = homeShot;
        this.awayShot = awayShot;
        this.hst = hst;
        this.ast = ast;
        this.hf = hf;
        this.af = af;
        this.hc = hc;
        this.ac = ac;
        this.hy = hy;
        this.ay = ay;
        this.hr = hr;
        this.ar = ar;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getFullTimeResult() {
        return fullTimeResult;
    }

    public void setFullTimeResult(String fullTimeResult) {
        this.fullTimeResult = fullTimeResult;
    }

    public String getHalfTimeResult() {
        return halfTimeResult;
    }

    public void setHalfTimeResult(String halfTimeResult) {
        this.halfTimeResult = halfTimeResult;
    }

    public String getReferee() {
        return referee;
    }

    public void setReferee(String referee) {
        this.referee = referee;
    }

    public int getFthg() {
        return fthg;
    }

    public void setFthg(int fthg) {
        this.fthg = fthg;
    }

    public int getFtag() {
        return ftag;
    }

    public void setFtag(int ftag) {
        this.ftag = ftag;
    }

    public int getHtag() {
        return htag;
    }

    public void setHtag(int htag) {
        this.htag = htag;
    }

    public int getHthg() {
        return hthg;
    }

    public void setHthg(int hthg) {
        this.hthg = hthg;
    }

    public int getHomeShot() {
        return homeShot;
    }

    public void setHomeShot(int homeShot) {
        this.homeShot = homeShot;
    }

    public int getAwayShot() {
        return awayShot;
    }

    public void setAwayShot(int awayShot) {
        this.awayShot = awayShot;
    }

    public int getHst() {
        return hst;
    }

    public void setHst(int hst) {
        this.hst = hst;
    }

    public int getAst() {
        return ast;
    }

    public void setAst(int ast) {
        this.ast = ast;
    }

    public int getHf() {
        return hf;
    }

    public void setHf(int hf) {
        this.hf = hf;
    }

    public int getAf() {
        return af;
    }

    public void setAf(int af) {
        this.af = af;
    }

    public int getHc() {
        return hc;
    }

    public void setHc(int hc) {
        this.hc = hc;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getHy() {
        return hy;
    }

    public void setHy(int hy) {
        this.hy = hy;
    }

    public int getAy() {
        return ay;
    }

    public void setAy(int ay) {
        this.ay = ay;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }
}
