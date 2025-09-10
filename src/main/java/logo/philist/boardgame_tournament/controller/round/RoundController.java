package logo.philist.boardgame_tournament.controller.round;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.Player;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.shuffle.BasicRankShuffler;
import logo.philist.boardgame_tournament.shuffle.PureShuffler;
import logo.philist.boardgame_tournament.shuffle.Shuffler;
import logo.philist.boardgame_tournament.shuffle.WeightedRankShuffler;
import logo.philist.boardgame_tournament.storage.Storage;

import java.util.List;

public class RoundController {

    @FXML
    private ListView<GameGroup> gameGroupsListView;

    private Shuffler shuffler;
    private List<Player> players;
    private List<Game> games;

    @FXML
    public void initialize() {
        gameGroupsListView.setCellFactory(param -> new GameGroupListCell());
    }

    public void setInfo(List<Player> players, int roundNumber) {
        this.shuffler = determineShuffler(roundNumber);
        this.players = players;
        this.games = Storage.loadGames();
        this.reroll();
    }

    private void reroll() {
        List<GameGroup> gameGroups = shuffler.shuffle(this.players, this.games);

        ObservableList<GameGroup> observableList = FXCollections.observableList(gameGroups);
        gameGroupsListView.setItems(observableList);
    }

    public Shuffler determineShuffler(int roundNumber) {
        return new WeightedRankShuffler();
    }

    public void onReroll() {
        this.reroll();
    }
}
