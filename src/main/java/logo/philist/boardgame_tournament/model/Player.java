package logo.philist.boardgame_tournament.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Player {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty totalScore = new SimpleIntegerProperty();
    private final ObservableList<IntegerProperty> roundScores = FXCollections.observableArrayList();

    public Player(String name) {
        this.name.set(name);
        roundScores.add(new SimpleIntegerProperty(0));
        recalculateTotal();
    }

    public StringProperty nameProperty() { return name; }
    public IntegerProperty totalScoreProperty() { return totalScore; }
    public ObservableList<IntegerProperty> getRoundScores() { return roundScores; }

    @JsonCreator
    public Player(@JsonProperty("name") String name, @JsonProperty("roundScores") int[] roundScores) {
        this.name.set(name);
        for (int score : roundScores) {
            this.roundScores.add(new SimpleIntegerProperty(score));
        }
        recalculateTotal();
    }

    @JsonProperty("name")
    public String getName() { return name.get(); }

    @JsonProperty("roundScores")
    public int[] getRoundScoresArray() {
        return roundScores.stream().mapToInt(IntegerProperty::get).toArray();
    }

    public int totalScore() { return totalScore.get(); }

    public void setRoundScore(int round, int score) {
        roundScores.get(round).set(score);
        recalculateTotal();
    }

    private void recalculateTotal() {
        int sum = roundScores.stream().mapToInt(IntegerProperty::get).sum();
        totalScore.set(sum);
    }

    public void addRound() {
        roundScores.add(new SimpleIntegerProperty(0));
        recalculateTotal();
    }

    public void removeLastRound() {
        if (!roundScores.isEmpty() && roundScores.size() > 1) {
            roundScores.removeLast();
            recalculateTotal();
        }
    }

    public void resetScores() {
        while (!roundScores.isEmpty()) {
            roundScores.removeLast();
        }
        roundScores.add(new SimpleIntegerProperty(0));
        recalculateTotal();
    }

    @Override
    public String toString() {
        return name.get();
    }
}
