package com.example.pio;

import java.io.Serializable;
import java.util.*;

class Leaderboard implements Serializable{

    private Map<String, Statistic> playerScores = new HashMap<>();

    public void addOrUpdatePlayerScore(String key, Double value, String nickname) {
        playerScores.put(key, new Statistic(value, nickname));
    }

    public Map<String, Statistic> getPlayerScores() {
        sortMapByScore();
        return playerScores;
    }

    private void sortMapByScore() {
        List<String> keys = new ArrayList<>(playerScores.keySet());

        Collections.sort(keys, new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                return Double.compare(playerScores.get(key2).score, playerScores.get(key1).score);
            }
        });

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

        for (Map.Entry<String, Statistic> entry : playerScores.entrySet()) {
            sb.append(entry.getValue().nickname).append(":").append(entry.getValue().score).append("/");
        }

        return sb.toString();
    }
}
