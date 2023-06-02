package com.example.pio.upgrade.persecond;

public class Bitcoin extends UpgradePerSecond implements Cryptocurrency {

    public Bitcoin() {
        setPrice(200);
        setAmount(0);
        setCoinsPerSecond(0);
    }

    @Override
    public boolean isAvailableToBuy(double coins) {
        return coins >= getPrice();
    }

    @Override
    public double buyCrypto() {
        setCoinsPerSecond(getCoinsPerSecond() * 1.41 + 0.2);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice((int)(getPrice() + ((getPrice() * 1.15)/2)));
        return toReturn;
    }
}
