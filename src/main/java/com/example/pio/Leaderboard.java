package com.example.pio;

import java.io.Serializable;
import java.util.*;

class Leaderboard implements Serializable {

    private Map<String, Statistic> playerScores = new HashMap<>();

    public void addOrUpdatePlayerScore(String key, Double value, String nickname, String counter, String seconds) {
        playerScores.put(key, new Statistic(value, nickname, counter, seconds));
    }

    private void sortMapByScore() {
        List<String> keys = new ArrayList<>(playerScores.keySet());

        Collections.sort(keys, (key1, key2) -> Double.compare(playerScores.get(key2).score, playerScores.get(key1).score));

        LinkedHashMap<String, Statistic> sortedMap = new LinkedHashMap<>();
        for (String key : keys) {
            sortedMap.put(key, playerScores.get(key));
        }

        playerScores = sortedMap;
    }

    @Override
    public String toString() {
        sortMapByScore();
        StringBuilder sb = new StringBuilder();
        boolean end = false;

        for (Map.Entry<String, Statistic> entry : playerScores.entrySet()) {
            if(Integer.valueOf(entry.getValue().seconds) == 10 ){
                end = true;
            }
        }

        for (Map.Entry<String, Statistic> entry : playerScores.entrySet()) {
            if (!end) {
                sb.append(entry.getValue().nickname).append(":").append(entry.getValue().score).append(":")
                        .append(entry.getValue().counter).append(":").append(entry.getValue().seconds).append("/");
            }else{
                sb.append(entry.getValue().nickname).append(":").append(entry.getValue().score).append(":")
                        .append(entry.getValue().counter).append(":").append("-1").append("/");
            }
        }
        return sb.toString();
    }
}
