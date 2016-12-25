package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/24/16.
 */

/**
 * Class for holding data in table EnglandPlayerAttributes
 */
public class EnglandPlayerAttributes {
    private int id;
    private int rating, potential;
    private String foot;

    public EnglandPlayerAttributes(int id, int rating, int potential, String foot) {
        this.id = id;
        this.rating = rating;
        this.potential = potential;
        this.foot = foot;
    }

    public EnglandPlayerAttributes() {
    }

    @Override
    public String toString() {
        return "EnglandPlayerAttributes{" +
                "id=" + id +
                ", rating=" + rating +
                ", potential=" + potential +
                ", foot='" + foot + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getPotential() {
        return potential;
    }

    public void setPotential(int potential) {
        this.potential = potential;
    }

    public String getFoot() {
        return foot;
    }

    public void setFoot(String foot) {
        this.foot = foot;
    }
}
