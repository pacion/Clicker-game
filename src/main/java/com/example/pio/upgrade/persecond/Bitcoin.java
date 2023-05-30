package com.example.pio.upgrade.persecond;

public class Bitcoin extends UpgradePerSecond implements Cryptocurrency {

    public Bitcoin() {
        setPrice(200);
        setAmount(0);
        setCoinsPerSecond(1.4);
    }

    @Override
    public boolean isAvailableToBuy(double coins) {
        return coins >= getPrice();
    }

    @Override
    public double buyCrypto() {
        setCoinsPerSecond(getCoinsPerSecond() + getCoinsPerSecond() * 10 / 100);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice(getPrice() + (int)getCoinsPerSecond() * 95 / getAmount());
        return toReturn;
    }
}
