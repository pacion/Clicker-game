package com.example.pio.upgrade.perclick;

public class MyCursor extends UpgradePerClick implements Clicker {

    public MyCursor() {
        setPrice(15);
        setAmount(0);
        setCoinsPerClick(1);
    }

    @Override
    public boolean isAvailableToBuy(double coins) {
        return coins >= getPrice();
    }

    @Override
    public double buyCursor() {
        setCoinsPerClick(getCoinsPerClick() + 1);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice(getPrice() * 2);
        return toReturn;
    }
}
