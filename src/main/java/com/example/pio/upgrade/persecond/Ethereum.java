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
       // setCoinsPerSecond(0.25 + getCoinsPerSecond() + getCoinsPerSecond() * 321 / 624);
        setCoinsPerSecond(getCoinsPerSecond() * 1.32 + 0.1);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice(getPrice() + (int)(getPrice() * 1.15));
        return toReturn;
    }
}
