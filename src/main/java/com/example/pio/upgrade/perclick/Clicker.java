package com.example.pio.upgrade.perclick;

public interface Clicker {
    public boolean isAvailableToBuy(double coins);

    public double buyCursor();

    public void resetValues();

}
