package logo.philist.boardgame_tournament.controller.distribution;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PointDistributionController {

    @FXML
    private TableView<PointsRow> pointsTable;

    private int playerAmount = 2;

    @FXML
    public void initialize() {
    }

    @FXML
    private void onReroll() {
        Map<Integer, List<Integer>> distribution = generateCustomDistribution(playerAmount, 5, 4, 4);
        plotDistributionTable(distribution);
    }

    private void plotDistributionTable(Map<Integer, List<Integer>> distribution) {
        pointsTable.getItems().clear();
        pointsTable.getColumns().clear();

        TableColumn<PointsRow, Integer> playerCountCol = new TableColumn<>("# Players");
        playerCountCol.setCellFactory(column -> new TableCell<PointsRow, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-background-color: #e0e0e0; -fx-font-weight: bold;");
                }
            }
        });
        playerCountCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPlayerCount()));
        pointsTable.getColumns().add(playerCountCol);

        int maxRanks = distribution.values().stream().mapToInt(List::size).max().orElse(0);

        for (int i = 0; i < maxRanks; i++) {
            final int rank = i;
            TableColumn<PointsRow, Integer> rankCol = new TableColumn<>("#" + (rank + 1));
            rankCol.setCellValueFactory(data -> {
                List<Integer> points = data.getValue().getPoints();
                return new SimpleObjectProperty<>(rank < points.size() ? points.get(rank) : null);
            });
            pointsTable.getColumns().add(rankCol);
        }

        List<PointsRow> rows = distribution.entrySet().stream()
                .map(e -> new PointsRow(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        pointsTable.setItems(FXCollections.observableArrayList(rows));
    }

    public void setPlayerAmount(int amount) {
        this.playerAmount = amount;
        Map<Integer, List<Integer>> distribution = generateCustomDistribution(amount, 5, 4, 4);
        plotDistributionTable(distribution);
    }

    private static Map<Integer, List<Integer>> generateVariableExponentialDistribution(int maxPlayers, int basePoints, int pointsPerPlayer) {
        Map<Integer, List<Integer>> distribution = new HashMap<>();

        for (int amountPlayers = 2; amountPlayers <= maxPlayers; amountPlayers++) {
            List<Integer> pointsDistribution = new ArrayList<>();
            double totalWeight = 0;
            int totalPoints = pointsPerPlayer * (amountPlayers - 2) + basePoints;

            for (int rank = 1; rank <= amountPlayers; rank++) {
                totalWeight += Math.pow(2, amountPlayers - rank);
            }

            int remainingPoints = totalPoints;

            for (int rank = 1; rank <= amountPlayers; rank++) {
                int points = (rank == amountPlayers) ? remainingPoints :
                        (int) Math.round(totalPoints * Math.pow(2, amountPlayers - rank) / totalWeight);

                pointsDistribution.add(points);
                remainingPoints -= points;
            }

            distribution.put(amountPlayers, pointsDistribution);
        }

        return distribution;
    }

    private static Map<Integer, List<Integer>> generateEqualDistribution(int maxPlayers, int maxPoints) {
        Map<Integer, List<Integer>> distribution = new HashMap<>();
        List<Integer> tempPoints = new ArrayList<>();

        for (int amountPlayers = 2; amountPlayers <= maxPlayers; amountPlayers++) {

            for (int ranking = 1; ranking <= amountPlayers; ranking++) {
                int points = (int) Math.round((double) (maxPoints * (amountPlayers - ranking)) / (amountPlayers - 1));
                tempPoints.add(points);
            }

            distribution.put(amountPlayers, new ArrayList<>(tempPoints));
            tempPoints.clear();
        }

        return distribution;
    }

    private static Map<Integer, List<Integer>> generateCustomDistribution(int maxPlayers, int pointPerPlayer, int basePointsVarExp, int maxPointsEqual) {
        Map<Integer, List<Integer>> varExp = generateVariableExponentialDistribution(maxPlayers, basePointsVarExp, pointPerPlayer);
        Map<Integer, List<Integer>> equal = generateEqualDistribution(maxPlayers, maxPointsEqual);

        return mergeMaps(varExp, equal);
    }

    private static Map<Integer, List<Integer>> mergeMaps(Map<Integer, List<Integer>> m1, Map<Integer, List<Integer>> m2) {
        Map<Integer, List<Integer>> merged = new HashMap<>(m1);

        for (Integer key : m1.keySet()) {
            List<Integer> list1 = m1.getOrDefault(key, new ArrayList<>());
            List<Integer> list2 = m2.getOrDefault(key, new ArrayList<>());
            List<Integer> mergedList = new ArrayList<>();

            int maxSize = Math.max(list1.size(), list2.size());
            for (int i = 0; i < maxSize; i++) {
                int value1 = i < list1.size() ? list1.get(i) : 0;
                int value2 = i < list2.size() ? list2.get(i) : 0;
                mergedList.add(value1 + value2);
            }

            merged.put(key, mergedList);
        }

        for (Integer key : m2.keySet()) {
            if (!merged.containsKey(key)) {
                merged.put(key, m2.get(key));
            }
        }

        return merged;
    }
}
