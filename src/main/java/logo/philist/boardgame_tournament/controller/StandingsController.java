package logo.philist.boardgame_tournament.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import logo.philist.boardgame_tournament.model.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandingsController {

    @FXML
    private LineChart<Number, Number> pointsChart;

    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private List<Player> players;

    public void setPlayers(List<Player> players) {
        this.players = players;
        updateStandings();
        updateChart();

        int rounds = players.isEmpty() ? 0 : players.get(0).getRoundScores().size();

        int maxPoints = players.stream()
                .mapToInt(Player::totalScore)
                .max().orElse(0);

        xAxis.setLowerBound(0);
        xAxis.setUpperBound(rounds);
        xAxis.setTickUnit(1);

        yAxis.setLowerBound(0);
        yAxis.setUpperBound(Math.max(10, maxPoints));
        yAxis.setTickUnit(1);

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
    }

    private void updateStandings() {
        List<Player> sorted = players.stream()
                .sorted(Comparator.comparingInt(Player::totalScore).reversed())
                .toList();

        ObservableList<StandingRow> rows = FXCollections.observableArrayList();
        for (int i = 0; i < sorted.size(); i++) {
            Player p = sorted.get(i);
            rows.add(new StandingRow(i + 1, p.nameProperty().get(), p.totalScore()));
        }
    }

    private void updateChart() {
        pointsChart.getData().clear();
        int rounds = players.isEmpty() ? 0 : players.get(0).getRoundScores().size();

        Map<Player, Integer> rankMap = determineRanks();

        for (Player player : players) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(formatNamePlayer(player, rankMap, players));
            int cumulative = 0;
            series.getData().add(new XYChart.Data<>(0, 0));
            for (int r = 0; r < rounds; r++) {
                cumulative += player.getRoundScores().get(r).getValue().intValue();
                series.getData().add(new XYChart.Data<>(r + 1, cumulative));
            }
            pointsChart.getData().add(series);
        }
    }

    private static String formatNamePlayer(Player player, Map<Player, Integer> rankMap, List<Player> players) {
        int playerRank = rankMap.getOrDefault(player, players.indexOf(player) + 1);
        return String.format("#%d %s", playerRank, player.nameProperty().get());
    }

    private Map<Player, Integer> determineRanks() {
        List<Player> sorted = players.stream()
                .sorted(Comparator.comparingInt(Player::totalScore).reversed())
                .toList();
        Map<Player, Integer> rankMap = new HashMap<>();
        int rank = 1;
        int pos = 1;
        Integer prevScore = null;
        for (Player p : sorted) {
            int s = p.totalScore();
            if (prevScore != null && s < prevScore) {
                rank = pos;
            }
            rankMap.put(p, rank);
            prevScore = s;
            pos++;
        }
        return rankMap;
    }

    public static class StandingRow {
        private final Integer rank;
        private final String name;
        private final Integer score;

        public StandingRow(Integer rank, String name, Integer score) {
            this.rank = rank;
            this.name = name;
            this.score = score;
        }
    }
}
