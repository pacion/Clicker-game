package com.example.pio.upgrade.perclick;

import com.example.pio.upgrade.Upgrade;

class UpgradePerClick extends Upgrade {
    private double coinsPerClick;

    public double getCoinsPerClick() {
        return coinsPerClick;
    }

    public void setCoinsPerClick(double coinsPerClick) {
        this.coinsPerClick = coinsPerClick;
    }
}
