package com.example.pio.upgrade.persecond;

public class DogeCoin extends UpgradePerSecond implements Cryptocurrency {

    public DogeCoin() {
        setPrice(50);
        setAmount(0);
        setCoinsPerSecond(1);
    }

    @Override
    public boolean isAvailableToBuy(double coins) {
        return coins >= getPrice();
    }

    @Override
    public double buyCrypto() {
        setCoinsPerSecond(getCoinsPerSecond() + 1);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice(getPrice() * 2);
        return toReturn;
    }
}
