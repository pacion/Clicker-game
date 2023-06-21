package com.example.pio.upgrade.persecond;

public class Ethereum extends UpgradePerSecond implements Cryptocurrency {

    public Ethereum() {
        setPrice(100);
        setAmount(0);
        setCoinsPerSecond(0);
    }

    @Override
    public boolean isAvailableToBuy(double coins) {
        return coins >= getPrice();
    }

    @Override
    public double buyCrypto() {
        setCoinsPerSecond(getCoinsPerSecond() * 1.32 + 0.15);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice((int)(getPrice() + ((getPrice() * 1.15)/2)));
        return toReturn;
    }

    @Override
    public void resetValues() {
        setPrice(100);
        setAmount(0);
        setCoinsPerSecond(0);
    }
}
