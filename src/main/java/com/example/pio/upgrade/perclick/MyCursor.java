package com.example.pio.upgrade.perclick;

public class MyCursor extends UpgradePerClick implements Clicker {

    public MyCursor() {
        setPrice(10);
        setAmount(0);
        setCoinsPerClick(1D);
    }

    @Override
    public boolean isAvailableToBuy(double coins) {
        return coins >= getPrice();
    }

    @Override
    public double buyCursor() {
        setCoinsPerClick(getCoinsPerClick() + 0.1);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice((int)(getPrice() + ((getPrice() * 1.15)/2)));
        return toReturn;
    }
}
