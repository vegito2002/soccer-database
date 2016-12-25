package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/24/16.
 */

/**
 * A class to hold an additional column than EnglandPlayerAttributes, which is the counter id, so that I can find the latest
 * Player_Attributes tuple for a certain playerId
 */
public class AnyPlayerAttributes extends EnglandPlayerAttributes {
    private int counter;

    @Override
    public String toString() {
        return super.toString() + " | AnyPlayerAttributes{" +
                "counter=" + counter +
                '}';
    }

    public AnyPlayerAttributes(int id, int rating, int potential, String foot, int counter) {
        super(id, rating, potential, foot);
        this.counter = counter;
    }

    public AnyPlayerAttributes(int counter) {
        this.counter = counter;
    }

    public int getCounter() {

        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
