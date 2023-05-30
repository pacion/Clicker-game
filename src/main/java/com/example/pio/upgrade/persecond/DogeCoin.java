package com.example.pio.upgrade.persecond;

public class DogeCoin extends UpgradePerSecond implements Cryptocurrency {

    public DogeCoin() {
        setPrice(50);
        setAmount(0);
        setCoinsPerSecond(0.4);
    }

    @Override
    public boolean isAvailableToBuy(double coins) {
        return coins >= getPrice();
    }

    @Override
    public double buyCrypto() {
        setCoinsPerSecond(getCoinsPerSecond() + getCoinsPerSecond() * 4 / 100);
        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice(getPrice() + (int)getCoinsPerSecond() * 65 / getAmount());
        return toReturn;
    }
}
