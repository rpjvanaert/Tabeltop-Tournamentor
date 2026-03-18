package logo.philist.boardgame_tournament.controller.distribution;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PointDistributionController {

    @FXML
    private TableView<PointsRow> pointsTable;

    @FXML
    private Text sharedPointsDisplay;

    private int playerAmount = 2;

    @FXML
    public void initialize() {
        pointsTable.setFixedCellSize(24);

        pointsTable.getSelectionModel().setCellSelectionEnabled(true);
        pointsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        pointsTable.getSelectionModel().getSelectedCells().addListener(
                (ListChangeListener<TablePosition>) c -> {
                    calculateSharedPoints(pointsTable.getSelectionModel().getSelectedCells());
                }
        );
    }

    @FXML
    private void onReroll() {
        Map<Integer, List<Integer>> distribution = generateCustomDistribution(playerAmount, 5, 4, 4);
        plotDistributionTable(distribution);
    }

    private void plotDistributionTable(Map<Integer, List<Integer>> distribution) {
        pointsTable.getItems().clear();
        pointsTable.getColumns().clear();

        TableColumn<PointsRow, Integer> playerCountCol = makePlayerCountCol();
        pointsTable.getColumns().add(playerCountCol);

        int maxRanks = distribution.values().stream().mapToInt(List::size).max().orElse(0);

        for (int i = 0; i < maxRanks; i++) {
            TableColumn<PointsRow, Integer> rankCol = makeRankCol(i);
            pointsTable.getColumns().add(rankCol);
        }

        TableColumn<PointsRow, Integer> sharedLosersCol = makeSharedLosersCol();
        pointsTable.getColumns().add(sharedLosersCol);

        List<PointsRow> rows = distribution.entrySet().stream()
                .map(e -> new PointsRow(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        pointsTable.setItems(FXCollections.observableArrayList(rows));

        pointsTable.prefHeightProperty().unbind();
        double headerOffset = 30;
        pointsTable.prefHeightProperty().bind(
                Bindings.size(pointsTable.getItems())
                        .multiply(pointsTable.fixedCellSizeProperty())
                        .add(headerOffset)
        );
    }

    private static TableColumn<PointsRow, Integer> makeRankCol(int i) {
        final int rank = i;
        TableColumn<PointsRow, Integer> rankCol = new TableColumn<>("#" + (rank + 1));
        rankCol.setCellValueFactory(data -> {
            List<Integer> points = data.getValue().getPoints();
            return new SimpleObjectProperty<>(rank < points.size() ? points.get(rank) : null);
        });
        rankCol.setReorderable(false);
        return rankCol;
    }

    private static TableColumn<PointsRow, Integer> makeSharedLosersCol() {
        TableColumn<PointsRow, Integer> sharedLosersCol = new TableColumn<>("SL");
        sharedLosersCol.setCellValueFactory(data -> {
            PointsRow row = data.getValue();
            List<Integer> points = row.getPoints();
            int players = row.getPlayerCount();
            if (points == null || players < 2) {
                return new SimpleObjectProperty<>(null);
            }
            int sumRest = 0;
            int limit = Math.min(points.size(), players);
            for (int i = 1; i < limit; i++) {
                sumRest += points.get(i);
            }
            int losersCount = players - 1;
            int avg = losersCount > 0 ? (sumRest / losersCount) : 0;
            return new SimpleObjectProperty<>(avg);
        });
        sharedLosersCol.setReorderable(false);
        sharedLosersCol.setCellFactory(_ -> styleColumn("-fx-background-color: #e0e0e0;"));
        return sharedLosersCol;
    }

    private static TableColumn<PointsRow, Integer> makePlayerCountCol() {
        TableColumn<PointsRow, Integer> playerCountCol = new TableColumn<>("#P");
        playerCountCol.setCellFactory(_ -> styleColumn("-fx-background-color: #606c38; -fx-font-weight: bold; -fx-text-fill: #ffffff;"));
        playerCountCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPlayerCount()));
        playerCountCol.setReorderable(false);
        return playerCountCol;
    }

    private static TableCell<PointsRow, Integer> styleColumn(final String css) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle(css);
                }
            }
        };
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

    private void calculateSharedPoints(ObservableList<TablePosition> selectedCells) {
        if (sharedPointsDisplay == null) return;

        if (isNothingSelected(selectedCells)) return;

        Map<Integer, List<TablePosition>> cellsByRow = selectedCells.stream()
                .collect(Collectors.groupingBy(TablePosition::getRow));

        if (cellsByRow.isEmpty()) {
            displaySelectRankText();
            return;
        }

        for (Map.Entry<Integer, List<TablePosition>> entry : cellsByRow.entrySet()) {
            List<TablePosition> positions = entry.getValue();

            if (positions.isEmpty()) continue;

            List<Integer> values = new ArrayList<>();
            List<String> columnNames = new ArrayList<>();

            for (TablePosition pos : positions) {
                TableColumn<PointsRow, ?> column = pos.getTableColumn();
                String columnHeader = column.getText();

                if (columnHeader.equals("#P") || columnHeader.equals("SL")) {
                    continue;
                }

                Object cellValue = column.getCellObservableValue(pos.getRow()).getValue();
                if (cellValue instanceof Integer) {
                    values.add((Integer) cellValue);
                    columnNames.add(columnHeader);
                }
            }

            if (!isValidSelection(values)) continue;

            int average = calculateAverage(values);

            String placesString = columnNames.getFirst() + " - " + columnNames.getLast();
            if (columnNames.size() == 1)
                placesString = columnNames.getFirst();

            sharedPointsDisplay.setText(String.format(
                    "(%s)\t\t %d points each",
                    placesString,
                    average
            ));
            break;
        }
    }

    private void displaySelectRankText() {
        sharedPointsDisplay.setText("Select rank cells (#1, #2, etc.)");
    }

    private boolean isNothingSelected(ObservableList<TablePosition> selectedCells) {
        if (selectedCells == null || selectedCells.isEmpty()) {
            sharedPointsDisplay.setText("Select cells to calculate shared points");
            return true;
        }
        return false;
    }

    private boolean isValidSelection(List<Integer> values) {
        if (values.isEmpty()) {
            displaySelectRankText();
            return false;
        }
        return true;
    }

    private static int calculateAverage(List<Integer> values) {
        int sum = values.stream().mapToInt(Integer::intValue).sum();
        int average = sum / values.size();
        return average;
    }
}