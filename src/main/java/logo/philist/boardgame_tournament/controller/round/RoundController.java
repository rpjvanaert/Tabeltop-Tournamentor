package logo.philist.boardgame_tournament.controller.round;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.Player;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.shuffle.*;
import logo.philist.boardgame_tournament.storage.Storage;

import java.util.List;

public class RoundController {

    @FXML
    private ComboBox<Shuffler> roundsComboBox;
    @FXML
    private ListView<GameGroup> gameGroupsListView;

    private List<Player> players;
    private List<Game> games;

    @FXML
    public void initialize() {
        gameGroupsListView.setCellFactory(_ -> new GameGroupListCell());
        roundsComboBox.setItems(FXCollections.observableArrayList(
                new PureShuffler(),
                new PairShuffler(),
                new BasicRankShuffler(),
                new WeightedRankShuffler(),
                new GroupShuffler()
        ));
    }

    public void setInfo(List<Player> players, int roundNumber) {
        this.roundsComboBox.getSelectionModel().select(determineShuffler(roundNumber));
        this.players = players;
        this.games = Storage.loadGames();
        this.reroll();
    }

    private void reroll() {
        List<GameGroup> gameGroups = this.roundsComboBox.getSelectionModel().getSelectedItem().shuffle(this.players, this.games);

        ObservableList<GameGroup> observableList = FXCollections.observableList(gameGroups);
        gameGroupsListView.setItems(observableList);
    }

    public Shuffler determineShuffler(int roundNumber) {
        switch (roundNumber%3) {
            case 1: return new PureShuffler();
            case 2: return new PairShuffler();
            case 0: return new WeightedRankShuffler();
        }
        return new PureShuffler();
    }

    public void onReroll() {
        this.reroll();
    }
}
