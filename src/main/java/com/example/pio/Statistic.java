package com.example.pio;

import java.util.Objects;

class Statistic {
    String nickname;
    Double score;
    String counter;
    String seconds;

    public Statistic(Double score, String nickname, String counter, String seconds) {
        this.score = score;
        this.nickname = nickname;
        this.counter = counter;
        this.seconds = seconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistic statistic = (Statistic) o;
        return Objects.equals(nickname, statistic.nickname) && Objects.equals(score, statistic.score) && Objects.equals(counter, statistic.counter) && Objects.equals(seconds, statistic.seconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, score, counter, seconds);
    }
}
