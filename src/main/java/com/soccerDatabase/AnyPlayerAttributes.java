package com.soccerDatabase;

/**
 * Created by vegito2002 on 12/24/16.
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
