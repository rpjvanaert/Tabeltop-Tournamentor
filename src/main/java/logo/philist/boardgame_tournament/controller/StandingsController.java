package logo.philist.boardgame_tournament.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.PropertyValueFactory;
import logo.philist.boardgame_tournament.model.Player;

import java.util.Comparator;
import java.util.List;

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

        for (Player player : players) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(player.nameProperty().get());
            int cumulative = 0;
            series.getData().add(new XYChart.Data<>(0, 0));
            for (int r = 0; r < rounds; r++) {
                cumulative += player.getRoundScores().get(r).getValue().intValue();
                series.getData().add(new XYChart.Data<>(r + 1, cumulative));
            }
            pointsChart.getData().add(series);
        }
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
