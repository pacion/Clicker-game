package com.example.pio.upgrade.persecond;

public class Ethereum extends UpgradePerSecond implements Cryptocurrency {

    public Ethereum() {
        setPrice(100);
        setAmount(0);
        setCoinsPerSecond(0.9);
    }

    @Override
    public boolean isAvailableToBuy(double coins) {
        return coins >= getPrice();
    }

    @Override
    public double buyCrypto() {
        setCoinsPerSecond(getCoinsPerSecond() + getCoinsPerSecond() * 7 / 100);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice(getPrice() + (int)getCoinsPerSecond() * 85 / getAmount());
        return toReturn;
    }
}
