package com.example.pio;

import java.util.Objects;

class Statistic {
    String nickname;
    Double score;

    public Statistic(Double score, String nickname) {
        this.score = score;
        this.nickname = nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistic statistic = (Statistic) o;
        return Objects.equals(nickname, statistic.nickname) && Objects.equals(score, statistic.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, score);
    }
}
