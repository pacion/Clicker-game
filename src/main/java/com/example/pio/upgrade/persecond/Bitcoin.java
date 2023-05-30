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
        if(getCoinsPerSecond() == 0.0)
            setCoinsPerSecond(0.1);

        setCoinsPerSecond(getCoinsPerSecond() * 1.41);

        setAmount(getAmount() + 1);
        var toReturn = getPrice();
        setPrice(getPrice() + (int)(getPrice() * 1.15));
        return toReturn;
    }
}
